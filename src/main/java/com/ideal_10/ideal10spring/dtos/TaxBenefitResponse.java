package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PropertyClassification;

import java.math.BigDecimal;

public record TaxBenefitResponse(
        Long id,
        String code,
        String name,
        String description,
        BigDecimal discountPercentage,
        PropertyClassification applicableClassification,
        Boolean active
) {
}
