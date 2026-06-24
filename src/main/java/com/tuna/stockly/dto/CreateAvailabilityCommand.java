package com.tuna.stockly.dto;

public record CreateAvailabilityCommand(
		String barcode,
		String itemName,
		String brand,
		String type,
		Long warehouseId,
		int quantity
) {
}
