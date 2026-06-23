package com.tuna.stockly.service;

import java.time.LocalDateTime;

import com.tuna.stockly.dto.CreateOrderCommand;
import com.tuna.stockly.entity.Item;
import com.tuna.stockly.entity.OrderItem;
import com.tuna.stockly.entity.OrderStatus;
import com.tuna.stockly.entity.OrderStatusEvent;
import com.tuna.stockly.entity.StockOrder;
import com.tuna.stockly.entity.Warehouse;
import com.tuna.stockly.entity.WarehouseItem;
import com.tuna.stockly.exception.InsufficientStockException;
import com.tuna.stockly.exception.InvalidOrderTransitionException;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.repository.OrderStatusEventRepository;
import com.tuna.stockly.repository.StockOrderRepository;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

	private final StockOrderRepository stockOrderRepository;
	private final OrderStatusEventRepository orderStatusEventRepository;
	private final WarehouseRepository warehouseRepository;
	private final ItemRepository itemRepository;
	private final WarehouseItemRepository warehouseItemRepository;

	public OrderService(StockOrderRepository stockOrderRepository, OrderStatusEventRepository orderStatusEventRepository,
			WarehouseRepository warehouseRepository, ItemRepository itemRepository,
			WarehouseItemRepository warehouseItemRepository) {
		this.stockOrderRepository = stockOrderRepository;
		this.orderStatusEventRepository = orderStatusEventRepository;
		this.warehouseRepository = warehouseRepository;
		this.itemRepository = itemRepository;
		this.warehouseItemRepository = warehouseItemRepository;
	}

	@Transactional
	public StockOrder createOrder(CreateOrderCommand command) {
		if (command.quantity() <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		if (command.requesterUserId() == null || command.requesterUserId().isBlank()) {
			throw new IllegalArgumentException("Requester user id is required");
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
				.orElseThrow(() -> new InsufficientStockException("Item is not available in selected warehouse"));

		if (stock.getQuantity() < command.quantity()) {
			throw new InsufficientStockException("Requested quantity exceeds available stock");
		}

		stock.reserve(command.quantity());

		LocalDateTime now = LocalDateTime.now();
		StockOrder order = new StockOrder(OrderStatus.REQUIRED);
		order.addItem(new OrderItem(item, warehouse, command.quantity()));
		StockOrder savedOrder = stockOrderRepository.save(order);
		recordStatusEvent(savedOrder, null, OrderStatus.REQUIRED, command.requesterUserId(), now);
		return savedOrder;
	}

	@Transactional
	public StockOrder approveOrder(Long orderId, String authorizedByUserId) {
		StockOrder order = findOrder(orderId);
		transition(order, () -> {
			OrderStatus previousStatus = order.approve();
			recordStatusEvent(order, previousStatus, order.getStatus(), authorizedByUserId, LocalDateTime.now());
		});
		return order;
	}

	@Transactional
	public StockOrder cancelOrder(Long orderId, String authorizedByUserId) {
		StockOrder order = findOrder(orderId);
		transition(order, () -> {
			for (OrderItem item : order.getItems()) {
				WarehouseItem stock = warehouseItemRepository.findByWarehouseAndItem(item.getWarehouse(), item.getItem())
						.orElseThrow(() -> new EntityNotFoundException("Stock row not found for order item " + item.getId()));
				stock.replenish(item.getQuantity());
			}
			OrderStatus previousStatus = order.cancel();
			recordStatusEvent(order, previousStatus, order.getStatus(), authorizedByUserId, LocalDateTime.now());
		});
		return order;
	}

	private StockOrder findOrder(Long orderId) {
		return stockOrderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
	}

	private void transition(StockOrder order, Runnable change) {
		try {
			change.run();
		}
		catch (IllegalStateException ex) {
			throw new InvalidOrderTransitionException(ex.getMessage());
		}
	}

	private void recordStatusEvent(StockOrder order, OrderStatus fromStatus, OrderStatus toStatus,
			String authorizedByUserId, LocalDateTime authorizedAt) {
		if (authorizedByUserId == null || authorizedByUserId.isBlank()) {
			throw new IllegalArgumentException("Authorized user id is required");
		}
		orderStatusEventRepository.save(
				new OrderStatusEvent(order, fromStatus, toStatus, authorizedByUserId, authorizedAt));
	}
}
