package com.tuna.stockly.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class StockOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	protected StockOrder() {
	}

	public StockOrder(OrderStatus status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public List<OrderItem> getItems() {
		return Collections.unmodifiableList(items);
	}

	public boolean isFinalStatus() {
		return status.isFinal();
	}

	public void addItem(OrderItem item) {
		items.add(item);
		item.attachTo(this);
	}

	public OrderStatus approve() {
		ensureRequired();
		OrderStatus previousStatus = status;
		status = OrderStatus.APPROVED;
		return previousStatus;
	}

	public OrderStatus cancel() {
		ensureRequired();
		OrderStatus previousStatus = status;
		status = OrderStatus.CANCELED;
		return previousStatus;
	}

	private void ensureRequired() {
		if (status != OrderStatus.REQUIRED) {
			throw new IllegalStateException("Only REQUIRED orders can change status");
		}
	}
}
