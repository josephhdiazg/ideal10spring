package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PagoPredialRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull MetodoPago paymentMethod,
        @Size(max = 80) String reference
) {
}
