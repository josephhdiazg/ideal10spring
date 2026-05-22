package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.EstadoPago;
import com.ideal_10.ideal10spring.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoPredialResponse(
        Long id,
        Long liquidacionId,
        BigDecimal amount,
        MetodoPago paymentMethod,
        String reference,
        LocalDateTime paymentDate,
        EstadoPago status
) {
}
