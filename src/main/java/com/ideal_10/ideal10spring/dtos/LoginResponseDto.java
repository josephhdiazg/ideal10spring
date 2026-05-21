package com.ideal_10.ideal10spring.dtos;

import java.util.List;

public record LoginResponseDto(
		String token,
		String type,
		String username,
		List<String> roles
) {
	public LoginResponseDto(String token, String username, List<String> roles) {
		this(token, "Bearer", username, roles);
	}
}
