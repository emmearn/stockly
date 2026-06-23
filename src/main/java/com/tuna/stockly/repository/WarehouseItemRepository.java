package com.tuna.stockly.repository;

import java.util.List;
import java.util.Optional;

import com.tuna.stockly.entity.Item;
import com.tuna.stockly.entity.Warehouse;
import com.tuna.stockly.entity.WarehouseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WarehouseItemRepository extends JpaRepository<WarehouseItem, Long> {

	List<WarehouseItem> findByItem(Item item);

	Optional<WarehouseItem> findByWarehouseAndItem(Warehouse warehouse, Item item);

	@Query("""
			select wi
			from WarehouseItem wi
			join fetch wi.item item
			join fetch wi.warehouse warehouse
			order by item.name asc, warehouse.name asc
			""")
	List<WarehouseItem> findAllForStockView();
}
