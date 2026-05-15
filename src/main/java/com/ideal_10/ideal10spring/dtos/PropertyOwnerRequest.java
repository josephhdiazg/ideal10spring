package com.ideal_10.ideal10spring.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PropertyOwnerRequest(
        @NotNull Long ownerId,
        @NotNull @DecimalMin(value = "0.01") @DecimalMax(value = "100.00") BigDecimal ownershipPercentage
) {
}
