package com.tuna.stockly.dto;

public class Permissions {

	private final boolean canCreateOrder;

	private final boolean canManageStock;

	private final boolean canApproveOrders;

	private final boolean canCancelAnyOrder;

	private final boolean canCancelOwnOrder;

	private final boolean canViewAllOrders;

	public Permissions(boolean canCreateOrder, boolean canManageStock, boolean canApproveOrders,
			boolean canCancelAnyOrder, boolean canCancelOwnOrder, boolean canViewAllOrders) {
		this.canCreateOrder = canCreateOrder;
		this.canManageStock = canManageStock;
		this.canApproveOrders = canApproveOrders;
		this.canCancelAnyOrder = canCancelAnyOrder;
		this.canCancelOwnOrder = canCancelOwnOrder;
		this.canViewAllOrders = canViewAllOrders;
	}

	public boolean canCreateOrder() {
		return canCreateOrder;
	}

	public boolean canManageStock() {
		return canManageStock;
	}

	public boolean canApproveOrders() {
		return canApproveOrders;
	}

	public boolean canCancelAnyOrder() {
		return canCancelAnyOrder;
	}

	public boolean canCancelOwnOrder() {
		return canCancelOwnOrder;
	}

	public boolean canViewAllOrders() {
		return canViewAllOrders;
	}
}
