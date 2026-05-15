package com.ideal_10.ideal10spring.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OwnerRequest(
        @NotBlank @Size(max = 30) String identificationNumber,
        @NotBlank @Size(max = 120) String fullName,
        @Size(max = 30) String phone,
        @Email @Size(max = 120) String email,
        Boolean active
) {
}
