package com.ideal_10.ideal10spring.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MunicipalityRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String department,
        Boolean active
) {
}
