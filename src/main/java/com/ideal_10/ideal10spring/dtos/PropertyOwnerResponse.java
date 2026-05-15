package com.ideal_10.ideal10spring.dtos;

import java.math.BigDecimal;

public record PropertyOwnerResponse(
        Long id,
        Long propertyId,
        OwnerResponse owner,
        BigDecimal ownershipPercentage
) {
}
