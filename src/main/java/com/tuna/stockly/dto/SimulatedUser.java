package com.tuna.stockly.dto;

import com.tuna.stockly.service.SimulatedRole;

public class SimulatedUser {

	private final String userId;

	private final String displayName;

	private final SimulatedRole role;

	public SimulatedUser(String userId, String displayName, SimulatedRole role) {
		this.userId = userId;
		this.displayName = displayName;
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public SimulatedRole getRole() {
		return role;
	}
}
