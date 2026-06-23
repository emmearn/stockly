package com.tuna.stockly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String barcode;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(nullable = false, length = 100)
	private String brand;

	@Column(nullable = false, length = 100)
	private String type;

	protected Item() {
	}

	public Item(String barcode, String name, String brand, String type) {
		this.barcode = barcode;
		this.name = name;
		this.brand = brand;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public String getType() {
		return type;
	}
}
