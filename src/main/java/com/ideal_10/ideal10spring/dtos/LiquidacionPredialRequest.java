package com.ideal_10.ideal10spring.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LiquidacionPredialRequest(
        @NotNull Long propertyId,
        @NotNull @Positive Integer fiscalYear,
        @DecimalMin("0.00") BigDecimal discountAmount,
        @DecimalMin("0.00") BigDecimal interestAmount,
        LocalDate dueDate
) {
}
