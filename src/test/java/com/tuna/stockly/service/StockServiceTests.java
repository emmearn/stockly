package com.tuna.stockly.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tuna.stockly.dto.CreateAvailabilityCommand;
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
		Item cementBag = item("800000000009");
		Warehouse elmas = warehouse("Elmas");

		assertThat(warehouseItemRepository.findByWarehouseAndItem(elmas, cementBag)).isEmpty();

		WarehouseItem stock = stockService.updateAvailability(
				new UpdateAvailabilityCommand(cementBag.getId(), elmas.getId(), 9));

		assertThat(stock.getQuantity()).isEqualTo(9);
		assertThat(warehouseItemRepository.findByWarehouseAndItem(elmas, cementBag)).isPresent();
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

	@Test
	void createAvailabilityCreatesNewItemAndStockRow() {
		Warehouse elmas = warehouse("Elmas");

		WarehouseItem stock = stockService.createAvailability(new CreateAvailabilityCommand("900000000001",
				"Guanti antitaglio", "Safety Pro", "DPI", elmas.getId(), 12));

		assertThat(stock.getQuantity()).isEqualTo(12);
		Item item = itemRepository.findByBarcode("900000000001").orElseThrow();
		assertThat(item.getName()).isEqualTo("Guanti antitaglio");
		assertThat(warehouseItemRepository.findByWarehouseAndItem(elmas, item)).isPresent();
	}

	@Test
	void createAvailabilityReusesExistingItemWhenBarcodeDataMatches() {
		Item cementBag = item("800000000009");
		Warehouse elmas = warehouse("Elmas");

		WarehouseItem stock = stockService.createAvailability(new CreateAvailabilityCommand(cementBag.getBarcode(),
				cementBag.getName(), cementBag.getBrand(), cementBag.getType(), elmas.getId(), 7));

		assertThat(stock.getItem().getId()).isEqualTo(cementBag.getId());
		assertThat(stock.getQuantity()).isEqualTo(7);
		assertThat(warehouseItemRepository.findByWarehouseAndItem(elmas, cementBag)).isPresent();
	}

	@Test
	void createAvailabilityRejectsExistingBarcodeWithDifferentItemData() {
		Item cementBag = item("800000000009");
		Warehouse elmas = warehouse("Elmas");

		assertThatThrownBy(() -> stockService.createAvailability(new CreateAvailabilityCommand(cementBag.getBarcode(),
				"Nome errato", cementBag.getBrand(), cementBag.getType(), elmas.getId(), 7)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Barcode already exists with different item data");
	}

	@Test
	void createAvailabilityRejectsDuplicateWarehouseItemRow() {
		Item bolt = item("800000000001");
		Warehouse elmas = warehouse("Elmas");

		assertThatThrownBy(() -> stockService.createAvailability(new CreateAvailabilityCommand(bolt.getBarcode(),
				bolt.getName(), bolt.getBrand(), bolt.getType(), elmas.getId(), 5)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Availability already exists for selected item and warehouse");
	}

	@Test
	void deleteAvailabilityRemovesStockRow() {
		Item cementBag = item("800000000009");
		Warehouse elmas = warehouse("Elmas");
		WarehouseItem stock = stockService.updateAvailability(
				new UpdateAvailabilityCommand(cementBag.getId(), elmas.getId(), 9));

		stockService.deleteAvailability(stock.getId());

		assertThat(warehouseItemRepository.findByWarehouseAndItem(elmas, cementBag)).isEmpty();
	}

	@Test
	void getAvailabilityLoadsItemAndWarehouseForEditView() {
		Item bolt = item("800000000001");
		Warehouse elmas = warehouse("Elmas");
		WarehouseItem existingStock = warehouseItemRepository.findByWarehouseAndItem(elmas, bolt).orElseThrow();

		WarehouseItem stock = stockService.getAvailability(existingStock.getId());

		assertThat(stock.getItem().getName()).isEqualTo("Bullone M8");
		assertThat(stock.getWarehouse().getName()).isEqualTo("Elmas");
	}

	private Item item(String barcode) {
		return itemRepository.findByBarcode(barcode).orElseThrow();
	}

	private Warehouse warehouse(String name) {
		return warehouseRepository.findByName(name).orElseThrow();
	}
}
