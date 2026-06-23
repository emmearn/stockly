package com.tuna.stockly.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_status_events")
public class OrderStatusEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private StockOrder order;

	@Enumerated(EnumType.STRING)
	@Column(name = "from_status", length = 20)
	private OrderStatus fromStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "to_status", nullable = false, length = 20)
	private OrderStatus toStatus;

	@Column(name = "authorized_by_user_id", nullable = false, length = 100)
	private String authorizedByUserId;

	@Column(name = "authorized_at", nullable = false)
	private LocalDateTime authorizedAt;

	@Column(length = 500)
	private String reason;

	protected OrderStatusEvent() {
	}

	public OrderStatusEvent(StockOrder order, OrderStatus fromStatus, OrderStatus toStatus,
			String authorizedByUserId, LocalDateTime authorizedAt) {
		this.order = order;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.authorizedByUserId = authorizedByUserId;
		this.authorizedAt = authorizedAt;
	}

	public OrderStatusEvent(StockOrder order, OrderStatus fromStatus, OrderStatus toStatus,
			String authorizedByUserId, LocalDateTime authorizedAt, String reason) {
		this(order, fromStatus, toStatus, authorizedByUserId, authorizedAt);
		this.reason = reason;
	}

	public Long getId() {
		return id;
	}

	public StockOrder getOrder() {
		return order;
	}

	public OrderStatus getFromStatus() {
		return fromStatus;
	}

	public OrderStatus getToStatus() {
		return toStatus;
	}

	public String getAuthorizedByUserId() {
		return authorizedByUserId;
	}

	public LocalDateTime getAuthorizedAt() {
		return authorizedAt;
	}

	public String getReason() {
		return reason;
	}
}
