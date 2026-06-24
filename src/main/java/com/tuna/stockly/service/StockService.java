package com.tuna.stockly.service;

import com.tuna.stockly.dto.CreateAvailabilityCommand;
import com.tuna.stockly.dto.UpdateAvailabilityCommand;
import com.tuna.stockly.entity.Item;
import com.tuna.stockly.entity.Warehouse;
import com.tuna.stockly.entity.WarehouseItem;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

	private final WarehouseItemRepository warehouseItemRepository;
	private final WarehouseRepository warehouseRepository;
	private final ItemRepository itemRepository;

	public StockService(WarehouseItemRepository warehouseItemRepository, WarehouseRepository warehouseRepository,
			ItemRepository itemRepository) {
		this.warehouseItemRepository = warehouseItemRepository;
		this.warehouseRepository = warehouseRepository;
		this.itemRepository = itemRepository;
	}

	@Transactional
	public WarehouseItem updateAvailability(UpdateAvailabilityCommand command) {
		if (command.quantity() < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
		if (command.itemId() == null) {
			throw new IllegalArgumentException("Item is required");
		}
		if (command.warehouseId() == null) {
			throw new IllegalArgumentException("Warehouse is required");
		}

		Item item = itemRepository.findById(command.itemId())
				.orElseThrow(() -> new EntityNotFoundException("Item not found: " + command.itemId()));
		Warehouse warehouse = warehouseRepository.findById(command.warehouseId())
				.orElseThrow(() -> new EntityNotFoundException("Warehouse not found: " + command.warehouseId()));

		WarehouseItem stock = warehouseItemRepository.findByWarehouseAndItem(warehouse, item)
				.orElseGet(() -> new WarehouseItem(warehouse, item, 0));
		stock.setQuantity(command.quantity());
		return warehouseItemRepository.save(stock);
	}

	@Transactional
	public WarehouseItem createAvailability(CreateAvailabilityCommand command) {
		if (command.quantity() < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
		if (command.warehouseId() == null) {
			throw new IllegalArgumentException("Warehouse is required");
		}

		Warehouse warehouse = warehouseRepository.findById(command.warehouseId())
				.orElseThrow(() -> new EntityNotFoundException("Warehouse not found: " + command.warehouseId()));
		Item item = itemRepository.findByBarcode(command.barcode())
				.map(existingItem -> validateExistingItem(existingItem, command))
				.orElseGet(() -> itemRepository.save(new Item(command.barcode(), command.itemName(), command.brand(),
						command.type())));

		if (warehouseItemRepository.findByWarehouseAndItem(warehouse, item).isPresent()) {
			throw new IllegalArgumentException("Availability already exists for selected item and warehouse");
		}

		return warehouseItemRepository.save(new WarehouseItem(warehouse, item, command.quantity()));
	}

	private Item validateExistingItem(Item item, CreateAvailabilityCommand command) {
		if (!item.getName().equals(command.itemName()) || !item.getBrand().equals(command.brand())
				|| !item.getType().equals(command.type())) {
			throw new IllegalArgumentException("Barcode already exists with different item data");
		}
		return item;
	}

	@Transactional(readOnly = true)
	public WarehouseItem getAvailability(Long stockId) {
		return warehouseItemRepository.findByIdForStockView(stockId)
				.orElseThrow(() -> new EntityNotFoundException("Availability not found: " + stockId));
	}

	@Transactional
	public void deleteAvailability(Long stockId) {
		WarehouseItem stock = getAvailability(stockId);
		warehouseItemRepository.delete(stock);
	}
}
