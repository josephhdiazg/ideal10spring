package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.CurrentUserDto;
import com.ideal_10.ideal10spring.dtos.LoginRequestDto;
import com.ideal_10.ideal10spring.dtos.LoginResponseDto;
import com.ideal_10.ideal10spring.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
		return authService.login(request);
	}

	@GetMapping("/me")
	public CurrentUserDto me(Authentication authentication) {
		return authService.getCurrentUser(authentication);
	}
}
