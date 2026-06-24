package com.tuna.stockly.service;

import com.tuna.stockly.dto.Permissions;
import com.tuna.stockly.dto.SimulatedUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class RoleSimulationService {

	private static final String SESSION_ROLE = "stockly.simulatedRole";

	public SimulatedRole getCurrentRole(HttpSession session) {
		Object value = session.getAttribute(SESSION_ROLE);
		if (value instanceof SimulatedRole role) {
			return role;
		}
		return SimulatedRole.ADMIN;
	}

	public void setCurrentRole(HttpSession session, SimulatedRole role) {
		session.setAttribute(SESSION_ROLE, role);
	}

	public SimulatedUser getCurrentUser(HttpSession session) {
		SimulatedRole role = getCurrentRole(session);
		return new SimulatedUser(role.getUserId(), role.getLabel(), role);
	}

	public Permissions getPermissions(HttpSession session) {
		return permissionsFor(getCurrentRole(session));
	}

	private Permissions permissionsFor(SimulatedRole role) {
		return switch (role) {
			case ADMIN -> new Permissions(true, true, true, true, true, true);
			case STORE_MANAGER -> new Permissions(true, true, true, true, true, true);
			case USER -> new Permissions(true, false, false, false, true, false);
		};
	}
}
