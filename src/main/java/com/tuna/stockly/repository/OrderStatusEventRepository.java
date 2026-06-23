package com.tuna.stockly.repository;

import java.util.Collection;
import java.util.List;

import com.tuna.stockly.entity.OrderStatusEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusEventRepository extends JpaRepository<OrderStatusEvent, Long> {

	List<OrderStatusEvent> findByOrderIdOrderByAuthorizedAtAsc(Long orderId);

	List<OrderStatusEvent> findByOrderIdInOrderByAuthorizedAtAsc(Collection<Long> orderIds);
}
