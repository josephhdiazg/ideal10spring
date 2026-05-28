package com.ideal_10.ideal10spring.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String ADMINISTRADOR = "ADMINISTRADOR";
	private static final String FUNCIONARIO_HACIENDA = "FUNCIONARIO_HACIENDA";
	private static final String TESORERIA = "TESORERIA";

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthEntryPoint jwtAuthEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			JwtAuthEntryPoint jwtAuthEntryPoint,
			JwtAccessDeniedHandler jwtAccessDeniedHandler
	) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(jwtAuthEntryPoint)
						.accessDeniedHandler(jwtAccessDeniedHandler)
				)
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
						.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
						.requestMatchers("/api/v1/usuarios/**").hasRole(ADMINISTRADOR)
						.requestMatchers("/api/v1/fiscal-years/**").hasAnyRole(ADMINISTRADOR, TESORERIA)
						.requestMatchers("/api/v1/tax-rates/**").hasAnyRole(ADMINISTRADOR, TESORERIA)
						.requestMatchers("/api/v1/charge-types/**").hasAnyRole(ADMINISTRADOR, TESORERIA)
						.requestMatchers("/api/v1/tax-benefits/**").hasAnyRole(ADMINISTRADOR, TESORERIA)
						.requestMatchers(HttpMethod.GET, "/api/v1/predios/**")
						.hasAnyRole(ADMINISTRADOR, FUNCIONARIO_HACIENDA, TESORERIA)
						.requestMatchers(HttpMethod.POST, "/api/v1/predios/**")
						.hasAnyRole(ADMINISTRADOR, FUNCIONARIO_HACIENDA)
						.requestMatchers(HttpMethod.PUT, "/api/v1/predios/**")
						.hasAnyRole(ADMINISTRADOR, FUNCIONARIO_HACIENDA)
						.requestMatchers(HttpMethod.GET, "/api/v1/liquidaciones/**")
						.hasAnyRole(ADMINISTRADOR, FUNCIONARIO_HACIENDA, TESORERIA)
						.requestMatchers(HttpMethod.POST, "/api/v1/liquidaciones/**")
						.hasAnyRole(ADMINISTRADOR, FUNCIONARIO_HACIENDA)
						.requestMatchers("/api/v1/pagos/**").hasAnyRole(ADMINISTRADOR, TESORERIA)
						.requestMatchers("/api/v1/paz-salvo/**").hasAnyRole(ADMINISTRADOR, TESORERIA)
						.requestMatchers("/api/v1/dashboard/**")
						.hasAnyRole(ADMINISTRADOR, FUNCIONARIO_HACIENDA, TESORERIA)
						.requestMatchers("/api/v1/auditoria/**").hasRole(ADMINISTRADOR)
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
