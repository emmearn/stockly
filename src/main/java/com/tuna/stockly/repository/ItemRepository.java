package com.tuna.stockly.repository;

import java.util.Optional;

import com.tuna.stockly.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

	Optional<Item> findByBarcode(String barcode);
}
