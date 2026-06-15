package com.tuna.stockly.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.tuna.stockly.item.Item;
import com.tuna.stockly.item.ItemRepository;
import com.tuna.stockly.stock.WarehouseItem;
import com.tuna.stockly.stock.WarehouseItemRepository;
import com.tuna.stockly.warehouse.Warehouse;
import com.tuna.stockly.warehouse.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("poc")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class OrderServiceTests {

	@Autowired
	private OrderService orderService;

	@Autowired
	private StockOrderRepository stockOrderRepository;

	@Autowired
	private OrderStatusEventRepository orderStatusEventRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private WarehouseItemRepository warehouseItemRepository;

	@Test
	void createOrderReservesStock() {
		Item bolt = item("800000000001");
		Warehouse milan = warehouse("Milano");
		WarehouseItem stock = stock(milan, bolt);
		int initialQuantity = stock.getQuantity();

		StockOrder order = orderService.createOrder(new CreateOrderCommand("test.user", bolt.getId(), milan.getId(), 3));

		assertThat(order.getStatus()).isEqualTo(OrderStatus.REQUIRED);
		assertThat(order.getItems()).hasSize(1);
		assertThat(stock.getQuantity()).isEqualTo(initialQuantity - 3);

		List<OrderStatusEvent> events = orderStatusEventRepository.findByOrderIdOrderByAuthorizedAtAsc(order.getId());
		assertThat(events).hasSize(1);
		assertThat(events.getFirst().getFromStatus()).isNull();
		assertThat(events.getFirst().getToStatus()).isEqualTo(OrderStatus.REQUIRED);
		assertThat(events.getFirst().getAuthorizedByUserId()).isEqualTo("test.user");
	}

	@Test
	void createOrderRejectsInsufficientStock() {
		Item hammer = item("800000000002");
		Warehouse milan = warehouse("Milano");
		WarehouseItem stock = stock(milan, hammer);
		int initialQuantity = stock.getQuantity();

		assertThatThrownBy(() -> orderService.createOrder(
				new CreateOrderCommand("test.user", hammer.getId(), milan.getId(), initialQuantity + 1)))
				.isInstanceOf(InsufficientStockException.class);

		assertThat(stock.getQuantity()).isEqualTo(initialQuantity);
	}

	@Test
	void createOrderRequiresWarehouse() {
		Item bolt = item("800000000001");

		assertThatThrownBy(() -> orderService.createOrder(
				new CreateOrderCommand("test.user", bolt.getId(), null, 1)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Warehouse is required");
	}

	@Test
	void approveOrderDoesNotChangeStock() {
		Item bolt = item("800000000001");
		Warehouse milan = warehouse("Milano");
		WarehouseItem stock = stock(milan, bolt);
		int afterReservationQuantity = stock.getQuantity();
		StockOrder order = orderService.createOrder(new CreateOrderCommand("test.user", bolt.getId(), milan.getId(), 4));
		int reservedQuantity = stock.getQuantity();

		StockOrder approved = orderService.approveOrder(order.getId(), "approver.user");

		assertThat(approved.getStatus()).isEqualTo(OrderStatus.APPROVED);
		assertThat(stock.getQuantity()).isEqualTo(reservedQuantity);
		assertThat(stock.getQuantity()).isEqualTo(afterReservationQuantity - 4);

		List<OrderStatusEvent> events = orderStatusEventRepository.findByOrderIdOrderByAuthorizedAtAsc(order.getId());
		assertThat(events).hasSize(2);
		assertThat(events.getLast().getFromStatus()).isEqualTo(OrderStatus.REQUIRED);
		assertThat(events.getLast().getToStatus()).isEqualTo(OrderStatus.APPROVED);
		assertThat(events.getLast().getAuthorizedByUserId()).isEqualTo("approver.user");
	}

	@Test
	void cancelOrderReplenishesStock() {
		Item pipe = item("800000000003");
		Warehouse rome = warehouse("Roma");
		WarehouseItem stock = stock(rome, pipe);
		int initialQuantity = stock.getQuantity();
		StockOrder order = orderService.createOrder(new CreateOrderCommand("test.user", pipe.getId(), rome.getId(), 6));

		StockOrder canceled = orderService.cancelOrder(order.getId(), "cancel.user");

		assertThat(canceled.getStatus()).isEqualTo(OrderStatus.CANCELED);
		assertThat(stock.getQuantity()).isEqualTo(initialQuantity);

		List<OrderStatusEvent> events = orderStatusEventRepository.findByOrderIdOrderByAuthorizedAtAsc(order.getId());
		assertThat(events).hasSize(2);
		assertThat(events.getLast().getFromStatus()).isEqualTo(OrderStatus.REQUIRED);
		assertThat(events.getLast().getToStatus()).isEqualTo(OrderStatus.CANCELED);
		assertThat(events.getLast().getAuthorizedByUserId()).isEqualTo("cancel.user");
	}

	@Test
	void finalOrdersCannotChangeStatus() {
		StockOrder approvedOrder = stockOrderRepository.findByStatus(OrderStatus.APPROVED).getFirst();

		assertThatThrownBy(() -> orderService.cancelOrder(approvedOrder.getId(), "test.user"))
				.isInstanceOf(InvalidOrderTransitionException.class);
	}

	private Item item(String barcode) {
		return itemRepository.findByBarcode(barcode).orElseThrow();
	}

	private Warehouse warehouse(String name) {
		return warehouseRepository.findByName(name).orElseThrow();
	}

	private WarehouseItem stock(Warehouse warehouse, Item item) {
		return warehouseItemRepository.findByWarehouseAndItem(warehouse, item).orElseThrow();
	}
}
