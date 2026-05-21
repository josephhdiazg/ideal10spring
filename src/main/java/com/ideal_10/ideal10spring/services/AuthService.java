package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.LoginRequestDto;
import com.ideal_10.ideal10spring.dtos.LoginResponseDto;
import com.ideal_10.ideal10spring.dtos.CurrentUserDto;
import org.springframework.security.core.Authentication;

public interface AuthService {

	LoginResponseDto login(LoginRequestDto request);

	CurrentUserDto getCurrentUser(Authentication authentication);
}
