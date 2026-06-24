package com.tuna.stockly.web;

import com.tuna.stockly.dto.Permissions;
import com.tuna.stockly.dto.SimulatedUser;
import com.tuna.stockly.service.RoleSimulationService;
import com.tuna.stockly.service.SimulatedRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

	private final RoleSimulationService roleSimulationService;

	public GlobalModelAttributes(RoleSimulationService roleSimulationService) {
		this.roleSimulationService = roleSimulationService;
	}

	@ModelAttribute("simulatedRoles")
	public SimulatedRole[] simulatedRoles() {
		return SimulatedRole.values();
	}

	@ModelAttribute("currentUser")
	public SimulatedUser currentUser(HttpSession session) {
		return roleSimulationService.getCurrentUser(session);
	}

	@ModelAttribute("permissions")
	public Permissions permissions(HttpSession session) {
		return roleSimulationService.getPermissions(session);
	}
}
