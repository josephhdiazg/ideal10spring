package com.ideal_10.ideal10spring.services.impl;

import com.ideal_10.ideal10spring.dtos.CurrentUserDto;
import com.ideal_10.ideal10spring.dtos.LoginRequestDto;
import com.ideal_10.ideal10spring.dtos.LoginResponseDto;
import com.ideal_10.ideal10spring.mapper.AuthMapper;
import com.ideal_10.ideal10spring.security.JwtTokenProvider;
import com.ideal_10.ideal10spring.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthMapper authMapper;

	public AuthServiceImpl(
			AuthenticationManager authenticationManager,
			JwtTokenProvider jwtTokenProvider,
			AuthMapper authMapper
	) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.authMapper = authMapper;
	}

	@Override
	public LoginResponseDto login(LoginRequestDto request) {
		Authentication authentication = authenticationManager.authenticate(authMapper.toAuthenticationToken(request));
		String token = jwtTokenProvider.generateToken(authentication);

		return authMapper.toLoginResponse(token, authentication.getName(), authMapper.toRoleNames(authentication));
	}

	@Override
	public CurrentUserDto getCurrentUser(Authentication authentication) {
		return authMapper.toCurrentUser(authentication.getName(), authMapper.toRoleNames(authentication));
	}
}
