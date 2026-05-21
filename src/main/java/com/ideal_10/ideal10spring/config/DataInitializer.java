package com.ideal_10.ideal10spring.config;

import com.ideal_10.ideal10spring.entities.User;
import com.ideal_10.ideal10spring.enums.RoleName;
import com.ideal_10.ideal10spring.repositories.UserRepository;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

	@Bean
	@Transactional
	public CommandLineRunner seedUsers(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		return args -> {
			Map<RoleName, String> testUsers = Map.of(
					RoleName.ADMINISTRADOR, "admin@ideal10.com",
					RoleName.FUNCIONARIO_HACIENDA, "hacienda@ideal10.com",
					RoleName.TESORERIA, "tesoreria@ideal10.com",
					RoleName.CONTRIBUYENTE, "contribuyente@ideal10.com"
			);

			for (RoleName roleName : RoleName.values()) {
				String username = testUsers.get(roleName);

				if (!userRepository.existsByUsername(username)) {
					User user = new User();
					user.setUsername(username);
					user.setPassword(passwordEncoder.encode(defaultPassword(roleName)));
					user.setEnabled(true);
					user.setRole(roleName);
					userRepository.save(user);
				}
			}
		};
	}

	private String defaultPassword(RoleName roleName) {
		return switch (roleName) {
			case ADMINISTRADOR -> "admin123";
			case FUNCIONARIO_HACIENDA -> "hacienda123";
			case TESORERIA -> "tesoreria123";
			case CONTRIBUYENTE -> "contribuyente123";
		};
	}
}
