package com.tuna.stockly.entity;

public enum OrderStatus {
	REQUIRED,
	APPROVED,
	REJECTED,
	CANCELED;

	public boolean isFinal() {
		return this == APPROVED || this == REJECTED || this == CANCELED;
	}
}
