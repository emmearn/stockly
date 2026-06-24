package com.tuna.stockly.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

class RoleSimulationServiceTests {

	private final RoleSimulationService roleSimulationService = new RoleSimulationService();

	@Test
	void defaultsToAdminRole() {
		MockHttpSession session = new MockHttpSession();

		assertThat(roleSimulationService.getCurrentRole(session)).isEqualTo(SimulatedRole.ADMIN);
		assertThat(roleSimulationService.getPermissions(session).canManageStock()).isTrue();
		assertThat(roleSimulationService.getPermissions(session).canApproveOrders()).isTrue();
	}

	@Test
	void storesSelectedRoleInSession() {
		MockHttpSession session = new MockHttpSession();

		roleSimulationService.setCurrentRole(session, SimulatedRole.USER);

		assertThat(roleSimulationService.getCurrentRole(session)).isEqualTo(SimulatedRole.USER);
		assertThat(roleSimulationService.getCurrentUser(session).getUserId()).isEqualTo("demo.user");
		assertThat(roleSimulationService.getPermissions(session).canManageStock()).isFalse();
		assertThat(roleSimulationService.getPermissions(session).canCancelOwnOrder()).isTrue();
	}
}
