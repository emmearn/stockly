package com.tuna.stockly.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.tuna.stockly.entity.Role;
import com.tuna.stockly.entity.User;
import com.tuna.stockly.repository.UserRepository;

@Configuration
public class BootstrapAdminConfig {

	private static final Logger log = LoggerFactory.getLogger(BootstrapAdminConfig.class);

	@Bean
	CommandLineRunner bootstrapAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder,
			@Value("${stockly.bootstrap-admin.username:}") String username,
			@Value("${stockly.bootstrap-admin.password:}") String password,
			@Value("${stockly.bootstrap-admin.display-name:}") String displayName) {
		return args -> {
			if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
				log.warn("Bootstrap admin credentials are not configured; no initial admin user was created");
				return;
			}

			if (userRepository.existsByUsername(username)) {
				return;
			}

			String resolvedDisplayName = StringUtils.hasText(displayName) ? displayName : username;
			userRepository.save(new User(username, resolvedDisplayName, passwordEncoder.encode(password), Role.ADMIN, true));
			log.info("Bootstrap admin user created");
		};
	}
}
