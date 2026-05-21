package com.ideal_10.ideal10spring.mapper;

import com.ideal_10.ideal10spring.dtos.CurrentUserDto;
import com.ideal_10.ideal10spring.dtos.LoginRequestDto;
import com.ideal_10.ideal10spring.dtos.LoginResponseDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Mapper(componentModel = "spring")
public interface AuthMapper {

	default UsernamePasswordAuthenticationToken toAuthenticationToken(LoginRequestDto request) {
		return new UsernamePasswordAuthenticationToken(request.username(), request.password());
	}

	@Mapping(target = "type", constant = "Bearer")
	LoginResponseDto toLoginResponse(String token, String username, List<String> roles);

	CurrentUserDto toCurrentUser(String username, List<String> roles);

	default List<String> toRoleNames(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(authority -> authority.startsWith("ROLE_"))
				.toList();
	}
}
