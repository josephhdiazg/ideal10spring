package com.ideal_10.ideal10spring.dtos;

import java.util.List;

public record CurrentUserDto(
		String username,
		List<String> roles
) {
}
