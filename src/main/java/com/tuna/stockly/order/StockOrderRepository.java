package com.tuna.stockly.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {

	List<StockOrder> findByStatus(OrderStatus status);

	@Query("""
			select distinct stockOrder
			from StockOrder stockOrder
			left join fetch stockOrder.items orderItem
			left join fetch orderItem.item
			left join fetch orderItem.warehouse
			order by stockOrder.id desc
			""")
	List<StockOrder> findAllForOrderList();

	@Query("""
			select stockOrder
			from StockOrder stockOrder
			left join fetch stockOrder.items orderItem
			left join fetch orderItem.item
			left join fetch orderItem.warehouse
			where stockOrder.id = :id
			""")
	Optional<StockOrder> findByIdWithItems(Long id);
}
