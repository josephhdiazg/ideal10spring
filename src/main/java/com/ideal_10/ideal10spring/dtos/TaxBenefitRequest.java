package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PropertyClassification;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TaxBenefitRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 100) String name,
        String description,
        @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal discountPercentage,
        PropertyClassification applicableClassification,
        Boolean active
) {
}
