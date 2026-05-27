package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.AssessmentDetailResponse;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentResponse;
import com.ideal_10.ideal10spring.entities.AssessmentDetail;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.TipoMovimientoLiquidacion;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.AssessmentDetailRepository;
import com.ideal_10.ideal10spring.repositories.PropertyAssessmentRepository;
import com.ideal_10.ideal10spring.repositories.PropertyOwnerRepository;
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

    private static final BigDecimal TEMPORARY_TAX_RATE = new BigDecimal("0.01");

    private final PropertyAssessmentRepository assessmentRepository;
    private final AssessmentDetailRepository detailRepository;
    private final PropertyOwnerRepository propertyOwnerRepository;
    private final PropertyService propertyService;

    @Transactional(readOnly = true)
    public List<PropertyAssessmentResponse> findAll() {
        return assessmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
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
        validateUniqueLiquidation(request.propertyId(), request.fiscalYear());

        BigDecimal subtotal = money(property.getCadastralValue().multiply(TEMPORARY_TAX_RATE));
        BigDecimal discount = money(defaultZero(request.discountAmount()));
        BigDecimal interest = money(defaultZero(request.interestAmount()));
        BigDecimal total = subtotal.subtract(discount).add(interest);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Liquidation total cannot be negative");
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
        assessment.setStatus(EstadoLiquidacion.PENDIENTE);

        PropertyAssessment saved = assessmentRepository.save(assessment);
        createDetail(saved, TipoMovimientoLiquidacion.CARGO, "Temporary base property tax 1% assessed value", subtotal);
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            createDetail(saved, TipoMovimientoLiquidacion.DESCUENTO, "Applied discount", discount);
        }
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            createDetail(saved, TipoMovimientoLiquidacion.INTERES, "Applied interest", interest);
        }
        return toResponse(saved);
    }

    @Transactional
    public PropertyAssessment updateAfterPayment(PropertyAssessment assessment, BigDecimal paymentAmount) {
        BigDecimal newBalance = money(assessment.getBalance().subtract(paymentAmount));
        assessment.setBalance(newBalance);
        assessment.setStatus(newBalance.compareTo(BigDecimal.ZERO) == 0
                ? EstadoLiquidacion.PAGADA
                : EstadoLiquidacion.PARCIAL);
        return assessment;
    }

    public PropertyAssessment getEntity(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidation not found with id " + id));
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
            throw new IllegalArgumentException("Only active properties can be liquidated");
        }
        if (propertyOwnerRepository.findByPropertyId(property.getId()).isEmpty()) {
            throw new IllegalArgumentException("Property must have at least one owner before liquidation");
        }
    }

    private void validateUniqueLiquidation(Long propertyId, Integer fiscalYear) {
        assessmentRepository.findByPropertyIdAndFiscalYear(propertyId, fiscalYear)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Liquidation already exists for this property and fiscal year");
                });
    }

    private void createDetail(
            PropertyAssessment assessment,
            TipoMovimientoLiquidacion movementType,
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
