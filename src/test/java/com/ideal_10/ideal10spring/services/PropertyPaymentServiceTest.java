package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PropertyPaymentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyPaymentResponse;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.entities.PropertyPayment;
import com.ideal_10.ideal10spring.enums.AssessmentStatus;
import com.ideal_10.ideal10spring.enums.PaymentMethod;
import com.ideal_10.ideal10spring.enums.PaymentStatus;
import com.ideal_10.ideal10spring.repositories.PropertyPaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyPaymentServiceTest {

    @Mock
    private PropertyPaymentRepository paymentRepository;

    @Mock
    private PropertyAssessmentService assessmentService;

    @InjectMocks
    private PropertyPaymentService paymentService;

    @Test
    void registerPaymentSavesPaymentAndUpdatesAssessmentBalance() {
        PropertyAssessment assessment = assessment(1L, "500000.00", AssessmentStatus.PENDING);
        PropertyPaymentRequest request = new PropertyPaymentRequest(
                new BigDecimal("300000.00"),
                PaymentMethod.TRANSFER,
                "BANK-123"
        );

        when(assessmentService.getEntity(1L)).thenReturn(assessment);
        when(paymentRepository.save(any(PropertyPayment.class))).thenAnswer(invocation -> {
            PropertyPayment payment = invocation.getArgument(0);
            payment.setId(9L);
            return payment;
        });

        PropertyPaymentResponse response = paymentService.registerPayment(1L, request);

        assertEquals(9L, response.id());
        assertEquals(1L, response.assessmentId());
        assertEquals(new BigDecimal("300000.00"), response.amount());
        assertEquals(PaymentMethod.TRANSFER, response.paymentMethod());
        assertEquals("BANK-123", response.reference());
        assertEquals(PaymentStatus.REGISTERED, response.status());
        assertNotNull(response.paymentDate());
        verify(assessmentService).updateAfterPayment(assessment, new BigDecimal("300000.00"));
    }

    @Test
    void registerPaymentRejectsAmountGreaterThanBalance() {
        PropertyAssessment assessment = assessment(1L, "500000.00", AssessmentStatus.PENDING);
        PropertyPaymentRequest request = new PropertyPaymentRequest(
                new BigDecimal("600000.00"),
                PaymentMethod.CASH,
                "CASH-1"
        );

        when(assessmentService.getEntity(1L)).thenReturn(assessment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.registerPayment(1L, request)
        );

        assertEquals("Payment amount cannot exceed pending balance", exception.getMessage());
        verify(paymentRepository, never()).save(any());
        verify(assessmentService, never()).updateAfterPayment(any(), any());
    }

    @Test
    void registerPaymentRejectsPaidAssessment() {
        PropertyAssessment assessment = assessment(1L, "0.00", AssessmentStatus.PAID);
        PropertyPaymentRequest request = new PropertyPaymentRequest(
                new BigDecimal("1000.00"),
                PaymentMethod.CASH,
                "CASH-2"
        );

        when(assessmentService.getEntity(1L)).thenReturn(assessment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.registerPayment(1L, request)
        );

        assertEquals("Assessment is already paid", exception.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    private PropertyAssessment assessment(Long id, String balance, AssessmentStatus status) {
        PropertyAssessment assessment = new PropertyAssessment();
        assessment.setId(id);
        assessment.setBalance(new BigDecimal(balance));
        assessment.setStatus(status);
        return assessment;
    }
}
