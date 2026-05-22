package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PropertyClassification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TaxRateRequest(
        @NotNull Long fiscalYearId,
        @NotNull PropertyClassification classification,
        @NotNull @Positive BigDecimal ratePerThousand,
        Boolean active
) {
}
