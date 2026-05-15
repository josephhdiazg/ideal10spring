package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.enums.PropertyUse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PropertyRequest(
        @NotBlank @Size(max = 40) String cadastralCode,
        @NotBlank @Size(max = 180) String address,
        @NotNull PropertyUse propertyUse,
        PropertyStatus status,
        @NotNull @DecimalMin(value = "0.01") BigDecimal cadastralValue,
        @NotNull Long municipalityId
) {
}
