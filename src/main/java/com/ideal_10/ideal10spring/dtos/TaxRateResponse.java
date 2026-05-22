package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PropertyClassification;

import java.math.BigDecimal;

public record TaxRateResponse(
        Long id,
        Long fiscalYearId,
        Integer year,
        PropertyClassification classification,
        BigDecimal ratePerThousand,
        Boolean active
) {
}
