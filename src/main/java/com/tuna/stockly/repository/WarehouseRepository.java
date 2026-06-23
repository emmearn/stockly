package com.tuna.stockly.repository;

import java.util.Optional;

import com.tuna.stockly.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

	Optional<Warehouse> findByName(String name);
}
