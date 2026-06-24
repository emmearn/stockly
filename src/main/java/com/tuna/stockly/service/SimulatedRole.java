package com.tuna.stockly.service;

public enum SimulatedRole {

	ADMIN("Admin", "demo.admin"),
	STORE_MANAGER("Store manager", "demo.manager"),
	USER("User base", "demo.user");

	private final String label;

	private final String userId;

	SimulatedRole(String label, String userId) {
		this.label = label;
		this.userId = userId;
	}

	public String getLabel() {
		return label;
	}

	public String getUserId() {
		return userId;
	}
}
