package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PropertyPaymentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyPaymentResponse;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.entities.PropertyPayment;
import com.ideal_10.ideal10spring.enums.AssessmentStatus;
import com.ideal_10.ideal10spring.enums.PaymentStatus;
import com.ideal_10.ideal10spring.repositories.PropertyPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyPaymentService {

    private final PropertyPaymentRepository paymentRepository;
    private final PropertyAssessmentService assessmentService;

    @Transactional(readOnly = true)
    public List<PropertyPaymentResponse> findByAssessment(Long assessmentId) {
        assessmentService.getEntity(assessmentId);
        return paymentRepository.findByAssessmentId(assessmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PropertyPaymentResponse registerPayment(Long assessmentId, PropertyPaymentRequest request) {
        PropertyAssessment assessment = assessmentService.getEntity(assessmentId);
        if (assessment.getStatus() == AssessmentStatus.PAID) {
            throw new IllegalArgumentException("Assessment is already paid");
        }
        if (assessment.getStatus() == AssessmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot register payment for cancelled assessment");
        }
        if (request.amount().compareTo(assessment.getBalance()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed pending balance");
        }

        PropertyPayment payment = new PropertyPayment();
        payment.setAssessment(assessment);
        payment.setAmount(request.amount());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setReference(request.reference());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.REGISTERED);
        PropertyPayment saved = paymentRepository.save(payment);

        assessmentService.updateAfterPayment(assessment, request.amount());
        return toResponse(saved);
    }

    public PropertyPaymentResponse toResponse(PropertyPayment payment) {
        return new PropertyPaymentResponse(
                payment.getId(),
                payment.getAssessment().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getReference(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }
}
