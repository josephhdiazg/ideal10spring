package com.ideal_10.ideal10spring.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
		@NotBlank String username,
		@NotBlank String password
) {
}
