package com.tuna.stockly.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tuna.stockly.dto.UpdateAvailabilityCommand;
import com.tuna.stockly.entity.Item;
import com.tuna.stockly.entity.Warehouse;
import com.tuna.stockly.entity.WarehouseItem;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class StockServiceTests {

	@Autowired
	private StockService stockService;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private WarehouseItemRepository warehouseItemRepository;

	@Test
	void updateAvailabilityChangesExistingStockQuantity() {
		Item bolt = item("800000000001");
		Warehouse elmas = warehouse("Elmas");

		WarehouseItem stock = stockService.updateAvailability(
				new UpdateAvailabilityCommand(bolt.getId(), elmas.getId(), 77));

		assertThat(stock.getQuantity()).isEqualTo(77);
		assertThat(warehouseItemRepository.findByWarehouseAndItem(elmas, bolt).orElseThrow().getQuantity())
				.isEqualTo(77);
	}

	@Test
	void updateAvailabilityCreatesMissingStockRow() {
		Item silicone = item("800000000011");
		Warehouse quartucciu = warehouse("Quartucciu");

		assertThat(warehouseItemRepository.findByWarehouseAndItem(quartucciu, silicone)).isEmpty();

		WarehouseItem stock = stockService.updateAvailability(
				new UpdateAvailabilityCommand(silicone.getId(), quartucciu.getId(), 9));

		assertThat(stock.getQuantity()).isEqualTo(9);
		assertThat(warehouseItemRepository.findByWarehouseAndItem(quartucciu, silicone)).isPresent();
	}

	@Test
	void updateAvailabilityRejectsNegativeQuantity() {
		Item bolt = item("800000000001");
		Warehouse elmas = warehouse("Elmas");

		assertThatThrownBy(() -> stockService.updateAvailability(
				new UpdateAvailabilityCommand(bolt.getId(), elmas.getId(), -1)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Quantity cannot be negative");
	}

	private Item item(String barcode) {
		return itemRepository.findByBarcode(barcode).orElseThrow();
	}

	private Warehouse warehouse(String name) {
		return warehouseRepository.findByName(name).orElseThrow();
	}
}
