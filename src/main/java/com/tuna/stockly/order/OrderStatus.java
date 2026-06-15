package com.tuna.stockly.order;

public enum OrderStatus {
	REQUIRED,
	APPROVED,
	REJECTED,
	CANCELED;

	public boolean isFinal() {
		return this == APPROVED || this == REJECTED || this == CANCELED;
	}
}
