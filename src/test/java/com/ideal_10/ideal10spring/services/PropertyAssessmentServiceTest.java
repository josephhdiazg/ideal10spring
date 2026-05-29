package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PropertyAssessmentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentResponse;
import com.ideal_10.ideal10spring.entities.AssessmentDetail;
import com.ideal_10.ideal10spring.entities.FiscalYear;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.entities.PropertyOwner;
import com.ideal_10.ideal10spring.entities.TaxRate;
import com.ideal_10.ideal10spring.enums.AssessmentMovementType;
import com.ideal_10.ideal10spring.enums.AssessmentStatus;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.PropertyUse;
import com.ideal_10.ideal10spring.repositories.AssessmentDetailRepository;
import com.ideal_10.ideal10spring.repositories.FiscalYearRepository;
import com.ideal_10.ideal10spring.repositories.PropertyAssessmentRepository;
import com.ideal_10.ideal10spring.repositories.PropertyOwnerRepository;
import com.ideal_10.ideal10spring.repositories.TaxRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyAssessmentServiceTest {

    @Mock
    private PropertyAssessmentRepository assessmentRepository;

    @Mock
    private AssessmentDetailRepository detailRepository;

    @Mock
    private PropertyOwnerRepository propertyOwnerRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private FiscalYearRepository fiscalYearRepository;

    @Mock
    private TaxRateRepository taxRateRepository;

    @InjectMocks
    private PropertyAssessmentService assessmentService;

    @Test
    void createUsesActiveFiscalYearAndTaxRate() {
        Property property = activeProperty(PropertyUse.RESIDENTIAL);
        FiscalYear fiscalYear = activeFiscalYear();
        TaxRate taxRate = activeTaxRate(fiscalYear, PropertyClassification.URBAN, "8.0");

        when(propertyService.getEntity(10L)).thenReturn(property);
        when(propertyOwnerRepository.findByPropertyId(10L)).thenReturn(List.of(new PropertyOwner()));
        when(assessmentRepository.findByPropertyIdAndFiscalYear(10L, 2026)).thenReturn(Optional.empty());
        when(fiscalYearRepository.findByYear(2026)).thenReturn(Optional.of(fiscalYear));
        when(taxRateRepository.findByFiscalYearIdAndClassification(5L, PropertyClassification.URBAN))
                .thenReturn(Optional.of(taxRate));
        when(assessmentRepository.save(any(PropertyAssessment.class))).thenAnswer(invocation -> {
            PropertyAssessment assessment = invocation.getArgument(0);
            assessment.setId(1L);
            return assessment;
        });
        when(detailRepository.findByAssessmentId(1L)).thenReturn(List.of());

        PropertyAssessmentRequest request = new PropertyAssessmentRequest(
                10L,
                2026,
                new BigDecimal("10000"),
                new BigDecimal("5000"),
                LocalDate.of(2026, 6, 30)
        );

        PropertyAssessmentResponse response = assessmentService.create(request);

        assertEquals(new BigDecimal("960000.00"), response.subtotal());
        assertEquals(new BigDecimal("10000.00"), response.discountAmount());
        assertEquals(new BigDecimal("5000.00"), response.interestAmount());
        assertEquals(new BigDecimal("955000.00"), response.totalAmount());
        assertEquals(new BigDecimal("955000.00"), response.balance());
        assertEquals(AssessmentStatus.PENDING, response.status());

        ArgumentCaptor<AssessmentDetail> detailCaptor = ArgumentCaptor.forClass(AssessmentDetail.class);
        verify(detailRepository, times(3)).save(detailCaptor.capture());
        List<AssessmentDetail> details = detailCaptor.getAllValues();
        assertEquals(AssessmentMovementType.CHARGE, details.get(0).getMovementType());
        assertEquals(new BigDecimal("960000.00"), details.get(0).getAmount());
        assertEquals(AssessmentMovementType.DISCOUNT, details.get(1).getMovementType());
        assertEquals(new BigDecimal("10000.00"), details.get(1).getAmount());
        assertEquals(AssessmentMovementType.INTEREST, details.get(2).getMovementType());
        assertEquals(new BigDecimal("5000.00"), details.get(2).getAmount());
    }

    @Test
    void createRejectsInactiveFiscalYear() {
        Property property = activeProperty(PropertyUse.RESIDENTIAL);
        FiscalYear fiscalYear = activeFiscalYear();
        fiscalYear.setActive(false);

        when(propertyService.getEntity(10L)).thenReturn(property);
        when(propertyOwnerRepository.findByPropertyId(10L)).thenReturn(List.of(new PropertyOwner()));
        when(assessmentRepository.findByPropertyIdAndFiscalYear(10L, 2026)).thenReturn(Optional.empty());
        when(fiscalYearRepository.findByYear(2026)).thenReturn(Optional.of(fiscalYear));

        PropertyAssessmentRequest request = new PropertyAssessmentRequest(10L, 2026, null, null, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> assessmentService.create(request)
        );

        assertEquals("Fiscal year must be active to create an assessment", exception.getMessage());
        verify(taxRateRepository, never()).findByFiscalYearIdAndClassification(any(), any());
    }

    @Test
    void createRejectsPropertyWithoutOwner() {
        Property property = activeProperty(PropertyUse.RESIDENTIAL);

        when(propertyService.getEntity(10L)).thenReturn(property);
        when(propertyOwnerRepository.findByPropertyId(10L)).thenReturn(List.of());

        PropertyAssessmentRequest request = new PropertyAssessmentRequest(10L, 2026, null, null, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> assessmentService.create(request)
        );

        assertEquals("Property must have at least one owner before assessment", exception.getMessage());
        verify(fiscalYearRepository, never()).findByYear(any());
    }

    @Test
    void updateAfterPaymentMarksPaidWhenBalanceIsCleared() {
        PropertyAssessment assessment = assessmentWithBalance("500000.00", AssessmentStatus.PENDING);

        assessmentService.updateAfterPayment(assessment, new BigDecimal("500000.00"));

        assertEquals(new BigDecimal("0.00"), assessment.getBalance());
        assertEquals(AssessmentStatus.PAID, assessment.getStatus());
    }

    @Test
    void updateAfterPaymentMarksPartialWhenBalanceRemains() {
        PropertyAssessment assessment = assessmentWithBalance("500000.00", AssessmentStatus.PENDING);

        assessmentService.updateAfterPayment(assessment, new BigDecimal("125000.00"));

        assertEquals(new BigDecimal("375000.00"), assessment.getBalance());
        assertEquals(AssessmentStatus.PARTIAL, assessment.getStatus());
    }

    private Property activeProperty(PropertyUse propertyUse) {
        Property property = new Property();
        property.setId(10L);
        property.setCadastralCode("0102030405");
        property.setPropertyUse(propertyUse);
        property.setStatus(PropertyStatus.ACTIVE);
        property.setCadastralValue(new BigDecimal("120000000.00"));
        return property;
    }

    private FiscalYear activeFiscalYear() {
        FiscalYear fiscalYear = new FiscalYear();
        fiscalYear.setId(5L);
        fiscalYear.setYear(2026);
        fiscalYear.setActive(true);
        return fiscalYear;
    }

    private TaxRate activeTaxRate(FiscalYear fiscalYear, PropertyClassification classification, String rate) {
        TaxRate taxRate = new TaxRate();
        taxRate.setId(2L);
        taxRate.setFiscalYear(fiscalYear);
        taxRate.setClassification(classification);
        taxRate.setRatePerThousand(new BigDecimal(rate));
        taxRate.setActive(true);
        return taxRate;
    }

    private PropertyAssessment assessmentWithBalance(String balance, AssessmentStatus status) {
        PropertyAssessment assessment = new PropertyAssessment();
        assessment.setBalance(new BigDecimal(balance));
        assessment.setStatus(status);
        return assessment;
    }
}
