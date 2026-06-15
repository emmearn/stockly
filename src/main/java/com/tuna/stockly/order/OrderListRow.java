package com.tuna.stockly.order;

public class OrderListRow {

	private final StockOrder order;
	private final OrderStatusEvent requestedEvent;
	private final OrderStatusEvent latestEvent;

	public OrderListRow(StockOrder order, OrderStatusEvent requestedEvent, OrderStatusEvent latestEvent) {
		this.order = order;
		this.requestedEvent = requestedEvent;
		this.latestEvent = latestEvent;
	}

	public StockOrder getOrder() {
		return order;
	}

	public OrderStatusEvent getRequestedEvent() {
		return requestedEvent;
	}

	public OrderStatusEvent getLatestEvent() {
		return latestEvent;
	}
}
