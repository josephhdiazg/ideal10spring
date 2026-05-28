package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PaymentStatus;
import com.ideal_10.ideal10spring.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PropertyPaymentResponse(
        Long id,
        Long assessmentId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String reference,
        LocalDateTime paymentDate,
        PaymentStatus status
) {
}
