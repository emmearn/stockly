package com.tuna.stockly.order;

import com.tuna.stockly.item.Item;
import com.tuna.stockly.warehouse.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private StockOrder order;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@Column(nullable = false)
	private int quantity;

	protected OrderItem() {
	}

	public OrderItem(Item item, Warehouse warehouse, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		this.item = item;
		this.warehouse = warehouse;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public StockOrder getOrder() {
		return order;
	}

	public Item getItem() {
		return item;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public int getQuantity() {
		return quantity;
	}

	void attachTo(StockOrder order) {
		this.order = order;
	}
}
