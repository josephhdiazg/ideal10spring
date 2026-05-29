package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.EstadoPago;
import com.ideal_10.ideal10spring.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PropertyPaymentResponse(
        Long id,
        Long assessmentId,
        BigDecimal amount,
        MetodoPago paymentMethod,
        String reference,
        LocalDateTime paymentDate,
        EstadoPago status
) {
}
