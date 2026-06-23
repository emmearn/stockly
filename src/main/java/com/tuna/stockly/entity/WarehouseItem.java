package com.tuna.stockly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "warehouse_items", uniqueConstraints = {
		@UniqueConstraint(name = "uk_warehouse_items_warehouse_item", columnNames = { "warehouse_id", "item_id" })
})
public class WarehouseItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@Column(nullable = false)
	private int quantity;

	protected WarehouseItem() {
	}

	public WarehouseItem(Warehouse warehouse, Item item, int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
		this.warehouse = warehouse;
		this.item = item;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public Item getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
		this.quantity = quantity;
	}

	public void reserve(int requestedQuantity) {
		if (requestedQuantity <= 0) {
			throw new IllegalArgumentException("Requested quantity must be positive");
		}
		if (quantity < requestedQuantity) {
			throw new IllegalArgumentException("Insufficient stock");
		}
		quantity -= requestedQuantity;
	}

	public void replenish(int returnedQuantity) {
		if (returnedQuantity <= 0) {
			throw new IllegalArgumentException("Returned quantity must be positive");
		}
		quantity += returnedQuantity;
	}
}
