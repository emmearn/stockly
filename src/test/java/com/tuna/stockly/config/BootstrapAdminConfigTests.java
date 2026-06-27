package com.tuna.stockly.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.tuna.stockly.entity.Role;
import com.tuna.stockly.entity.User;
import com.tuna.stockly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(properties = {
		"stockly.bootstrap-admin.username=admin.test",
		"stockly.bootstrap-admin.password=change-me-in-env",
		"stockly.bootstrap-admin.display-name=Test Admin"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BootstrapAdminConfigTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void createsInitialAdminFromConfiguredCredentials() {
		User admin = userRepository.findByUsername("admin.test").orElseThrow();

		assertThat(admin.getDisplayName()).isEqualTo("Test Admin");
		assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
		assertThat(admin.isEnabled()).isTrue();
		assertThat(admin.getPasswordHash()).isNotEqualTo("change-me-in-env");
		assertThat(passwordEncoder.matches("change-me-in-env", admin.getPasswordHash())).isTrue();
	}
}
