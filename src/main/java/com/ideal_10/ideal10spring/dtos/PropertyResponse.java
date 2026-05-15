package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.PropertyUse;

import java.math.BigDecimal;

public record PropertyResponse(
        Long id,
        String cadastralCode,
        String address,
        PropertyUse propertyUse,
        PropertyStatus status,
        BigDecimal cadastralValue,
        MunicipalityResponse municipality
) {
}
