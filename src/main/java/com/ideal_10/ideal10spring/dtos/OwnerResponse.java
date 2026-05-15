package com.ideal_10.ideal10spring.dtos;

public record OwnerResponse(
        Long id,
        String identificationNumber,
        String fullName,
        String phone,
        String email,
        Boolean active
) {
}
