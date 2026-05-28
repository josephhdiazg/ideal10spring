package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.AssessmentDetailResponse;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentResponse;
import com.ideal_10.ideal10spring.entities.AssessmentDetail;
import com.ideal_10.ideal10spring.entities.FiscalYear;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.entities.TaxRate;
import com.ideal_10.ideal10spring.enums.AssessmentMovementType;
import com.ideal_10.ideal10spring.enums.AssessmentStatus;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.PropertyUse;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.AssessmentDetailRepository;
import com.ideal_10.ideal10spring.repositories.FiscalYearRepository;
import com.ideal_10.ideal10spring.repositories.PropertyAssessmentRepository;
import com.ideal_10.ideal10spring.repositories.PropertyOwnerRepository;
import com.ideal_10.ideal10spring.repositories.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyAssessmentService {

    private static final BigDecimal ONE_THOUSAND = new BigDecimal("1000");

    private final PropertyAssessmentRepository assessmentRepository;
    private final AssessmentDetailRepository detailRepository;
    private final PropertyOwnerRepository propertyOwnerRepository;
    private final PropertyService propertyService;
    private final FiscalYearRepository fiscalYearRepository;
    private final TaxRateRepository taxRateRepository;

    @Transactional(readOnly = true)
    public List<PropertyAssessmentResponse> findAll() {
        return assessmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PropertyAssessmentResponse> findAll(Long propertyId) {
        if (propertyId != null) {
            return findByProperty(propertyId);
        }
        return findAll();
    }

    @Transactional(readOnly = true)
    public PropertyAssessmentResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<PropertyAssessmentResponse> findByProperty(Long propertyId) {
        propertyService.getEntity(propertyId);
        return assessmentRepository.findByPropertyId(propertyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PropertyAssessmentResponse create(PropertyAssessmentRequest request) {
        Property property = propertyService.getEntity(request.propertyId());
        validatePropertyCanBeLiquidated(property);
        validateUniqueAssessment(request.propertyId(), request.fiscalYear());

        FiscalYear fiscalYear = getActiveFiscalYear(request.fiscalYear());
        TaxRate taxRate = getActiveTaxRate(fiscalYear, classifyProperty(property));

        BigDecimal subtotal = money(
                property.getCadastralValue()
                        .multiply(taxRate.getRatePerThousand())
                        .divide(ONE_THOUSAND, 2, RoundingMode.HALF_UP)
        );
        BigDecimal discount = money(defaultZero(request.discountAmount()));
        BigDecimal interest = money(defaultZero(request.interestAmount()));
        BigDecimal total = subtotal.subtract(discount).add(interest);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Assessment total cannot be negative");
        }

        PropertyAssessment assessment = new PropertyAssessment();
        assessment.setProperty(property);
        assessment.setFiscalYear(request.fiscalYear());
        assessment.setIssueDate(LocalDate.now());
        assessment.setDueDate(request.dueDate() == null ? LocalDate.now().plusDays(30) : request.dueDate());
        assessment.setSubtotal(subtotal);
        assessment.setDiscountAmount(discount);
        assessment.setInterestAmount(interest);
        assessment.setTotalAmount(money(total));
        assessment.setBalance(money(total));
        assessment.setStatus(AssessmentStatus.PENDING);

        PropertyAssessment saved = assessmentRepository.save(assessment);
        createDetail(saved, AssessmentMovementType.CHARGE, "Base property tax", subtotal);
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            createDetail(saved, AssessmentMovementType.DISCOUNT, "Applied discount", discount);
        }
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            createDetail(saved, AssessmentMovementType.INTEREST, "Applied interest", interest);
        }
        return toResponse(saved);
    }

    @Transactional
    public PropertyAssessment updateAfterPayment(PropertyAssessment assessment, BigDecimal paymentAmount) {
        BigDecimal newBalance = money(assessment.getBalance().subtract(paymentAmount));
        assessment.setBalance(newBalance);
        assessment.setStatus(newBalance.compareTo(BigDecimal.ZERO) == 0
                ? AssessmentStatus.PAID
                : AssessmentStatus.PARTIAL);
        return assessment;
    }

    public PropertyAssessment getEntity(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + id));
    }

    public PropertyAssessmentResponse toResponse(PropertyAssessment assessment) {
        List<AssessmentDetailResponse> details = detailRepository.findByAssessmentId(assessment.getId()).stream()
                .map(this::toDetailResponse)
                .toList();
        return new PropertyAssessmentResponse(
                assessment.getId(),
                assessment.getProperty().getId(),
                assessment.getProperty().getCadastralCode(),
                assessment.getFiscalYear(),
                assessment.getIssueDate(),
                assessment.getDueDate(),
                assessment.getSubtotal(),
                assessment.getDiscountAmount(),
                assessment.getInterestAmount(),
                assessment.getTotalAmount(),
                assessment.getBalance(),
                assessment.getStatus(),
                details
        );
    }

    private void validatePropertyCanBeLiquidated(Property property) {
        if (property.getStatus() != PropertyStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active properties can be assessed");
        }
        if (propertyOwnerRepository.findByPropertyId(property.getId()).isEmpty()) {
            throw new IllegalArgumentException("Property must have at least one owner before assessment");
        }
    }

    private void validateUniqueAssessment(Long propertyId, Integer fiscalYear) {
        assessmentRepository.findByPropertyIdAndFiscalYear(propertyId, fiscalYear)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Assessment already exists for this property and fiscal year");
                });
    }

    private FiscalYear getActiveFiscalYear(Integer year) {
        FiscalYear fiscalYear = fiscalYearRepository.findByYear(year)
                .orElseThrow(() -> new ResourceNotFoundException("Fiscal year not found for year " + year));
        if (!Boolean.TRUE.equals(fiscalYear.getActive())) {
            throw new IllegalArgumentException("Fiscal year must be active to create an assessment");
        }
        return fiscalYear;
    }

    private TaxRate getActiveTaxRate(FiscalYear fiscalYear, PropertyClassification classification) {
        return taxRateRepository.findByFiscalYearIdAndClassification(fiscalYear.getId(), classification)
                .filter(taxRate -> Boolean.TRUE.equals(taxRate.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active tax rate not found for fiscal year and property classification"
                ));
    }

    private PropertyClassification classifyProperty(Property property) {
        if (property.getPropertyUse() == PropertyUse.RURAL) {
            return PropertyClassification.RURAL;
        }
        return PropertyClassification.URBAN;
    }

    private void createDetail(
            PropertyAssessment assessment,
            AssessmentMovementType movementType,
            String concept,
            BigDecimal amount
    ) {
        AssessmentDetail detail = new AssessmentDetail();
        detail.setAssessment(assessment);
        detail.setMovementType(movementType);
        detail.setConcept(concept);
        detail.setAmount(money(amount));
        detailRepository.save(detail);
    }

    private AssessmentDetailResponse toDetailResponse(AssessmentDetail detail) {
        return new AssessmentDetailResponse(
                detail.getId(),
                detail.getMovementType(),
                detail.getConcept(),
                detail.getAmount()
        );
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
