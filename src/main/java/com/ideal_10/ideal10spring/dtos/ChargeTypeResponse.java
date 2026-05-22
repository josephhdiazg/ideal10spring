package com.ideal_10.ideal10spring.dtos;

public record ChargeTypeResponse(
        Long id,
        String code,
        String name,
        String description,
        Boolean active
) {
}
