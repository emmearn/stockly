package com.tuna.stockly.order;

public record CreateOrderCommand(
		String requesterUserId,
		Long itemId,
		Long warehouseId,
		int quantity
) {
}
