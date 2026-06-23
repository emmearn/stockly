package com.tuna.stockly.config;

import java.time.LocalDateTime;

import com.tuna.stockly.entity.Item;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.entity.OrderItem;
import com.tuna.stockly.entity.OrderStatus;
import com.tuna.stockly.entity.OrderStatusEvent;
import com.tuna.stockly.repository.OrderStatusEventRepository;
import com.tuna.stockly.entity.StockOrder;
import com.tuna.stockly.repository.StockOrderRepository;
import com.tuna.stockly.entity.WarehouseItem;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.entity.Warehouse;
import com.tuna.stockly.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("poc")
public class DemoDataSeeder implements CommandLineRunner {

	private final WarehouseRepository warehouseRepository;
	private final ItemRepository itemRepository;
	private final WarehouseItemRepository warehouseItemRepository;
	private final StockOrderRepository stockOrderRepository;
	private final OrderStatusEventRepository orderStatusEventRepository;

	public DemoDataSeeder(WarehouseRepository warehouseRepository, ItemRepository itemRepository,
			WarehouseItemRepository warehouseItemRepository, StockOrderRepository stockOrderRepository,
			OrderStatusEventRepository orderStatusEventRepository) {
		this.warehouseRepository = warehouseRepository;
		this.itemRepository = itemRepository;
		this.warehouseItemRepository = warehouseItemRepository;
		this.stockOrderRepository = stockOrderRepository;
		this.orderStatusEventRepository = orderStatusEventRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (warehouseRepository.count() > 0 || itemRepository.count() > 0 || stockOrderRepository.count() > 0) {
			return;
		}

		Warehouse milan = warehouseRepository.save(new Warehouse("Milano", "Via Torino 12, Milano"));
		Warehouse rome = warehouseRepository.save(new Warehouse("Roma", "Via Appia 45, Roma"));

		Item bolt = itemRepository.save(new Item("800000000001", "Bullone M8", "Ferramenta Nord", "Bulloneria"));
		Item hammer = itemRepository.save(new Item("800000000002", "Martello", "Utensili Pro", "Utensili"));
		Item pipe = itemRepository.save(new Item("800000000003", "Tubo PVC", "IdroLine", "Tubature"));

		warehouseItemRepository.save(new WarehouseItem(milan, bolt, 45));
		warehouseItemRepository.save(new WarehouseItem(rome, bolt, 20));
		warehouseItemRepository.save(new WarehouseItem(milan, hammer, 8));
		warehouseItemRepository.save(new WarehouseItem(rome, pipe, 30));

		LocalDateTime now = LocalDateTime.now();

		StockOrder requiredOrder = new StockOrder(OrderStatus.REQUIRED);
		requiredOrder.addItem(new OrderItem(bolt, milan, 5));
		requiredOrder = stockOrderRepository.save(requiredOrder);
		recordEvent(requiredOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusDays(2));

		StockOrder approvedOrder = new StockOrder(OrderStatus.APPROVED);
		approvedOrder.addItem(new OrderItem(hammer, milan, 2));
		approvedOrder = stockOrderRepository.save(approvedOrder);
		recordEvent(approvedOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusDays(1));
		recordEvent(approvedOrder, OrderStatus.REQUIRED, OrderStatus.APPROVED, "demo.manager", now.minusHours(20));

		StockOrder canceledOrder = new StockOrder(OrderStatus.CANCELED);
		canceledOrder.addItem(new OrderItem(pipe, rome, 4));
		canceledOrder = stockOrderRepository.save(canceledOrder);
		recordEvent(canceledOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(10));
		recordEvent(canceledOrder, OrderStatus.REQUIRED, OrderStatus.CANCELED, "demo.user", now.minusHours(8));
	}

	private void recordEvent(StockOrder order, OrderStatus fromStatus, OrderStatus toStatus,
			String authorizedByUserId, LocalDateTime authorizedAt) {
		orderStatusEventRepository.save(
				new OrderStatusEvent(order, fromStatus, toStatus, authorizedByUserId, authorizedAt));
	}
}
