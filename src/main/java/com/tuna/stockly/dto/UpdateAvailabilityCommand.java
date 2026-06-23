package com.tuna.stockly.dto;

public record UpdateAvailabilityCommand(
		Long itemId,
		Long warehouseId,
		int quantity
) {
}
