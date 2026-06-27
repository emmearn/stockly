package com.tuna.stockly.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tuna.stockly.entity.Role;
import com.tuna.stockly.entity.User;
import com.tuna.stockly.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class OrderSecurityTests {

	private static final String USERNAME = "orders.user";
	private static final String PASSWORD = "orders-password";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void createUser() {
		if (!userRepository.existsByUsername(USERNAME)) {
			userRepository.save(new User(USERNAME, "Orders User", passwordEncoder.encode(PASSWORD), Role.USER, true));
		}
	}

	@Test
	void anonymousUsersAreRedirectedToLoginForOrders() throws Exception {
		mockMvc.perform(get("/orders"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string("Location", containsString("/login")));
	}

	@Test
	void userRoleCanOpenOrdersPage() throws Exception {
		mockMvc.perform(get("/orders").session((org.springframework.mock.web.MockHttpSession) loginSession()))
				.andExpect(status().isOk());
	}

	@Test
	void userRoleCannotApproveOrders() throws Exception {
		mockMvc.perform(post("/orders/1/approve").session((org.springframework.mock.web.MockHttpSession) loginSession()))
				.andExpect(status().isForbidden());
	}

	private HttpSession loginSession() throws Exception {
		MvcResult result = mockMvc.perform(post("/login")
						.param("username", USERNAME)
						.param("password", PASSWORD))
				.andExpect(status().is3xxRedirection())
				.andReturn();

		HttpSession session = result.getRequest().getSession(false);
		assertThat(session).isNotNull();
		return session;
	}
}
