package com.ideal_10.ideal10spring.dtos;

public record MunicipalityResponse(
        Long id,
        String name,
        String department,
        Boolean active
) {
}
