package com.tuna.stockly.order;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tuna.stockly.item.ItemRepository;
import com.tuna.stockly.stock.WarehouseItemRepository;
import com.tuna.stockly.warehouse.WarehouseRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

	private static final String DEMO_USER_ID = "demo.user";

	private final OrderService orderService;
	private final StockOrderRepository stockOrderRepository;
	private final OrderStatusEventRepository orderStatusEventRepository;
	private final ItemRepository itemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseItemRepository warehouseItemRepository;

	public OrderController(OrderService orderService, StockOrderRepository stockOrderRepository,
			OrderStatusEventRepository orderStatusEventRepository, ItemRepository itemRepository,
			WarehouseRepository warehouseRepository, WarehouseItemRepository warehouseItemRepository) {
		this.orderService = orderService;
		this.stockOrderRepository = stockOrderRepository;
		this.orderStatusEventRepository = orderStatusEventRepository;
		this.itemRepository = itemRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseItemRepository = warehouseItemRepository;
	}

	@GetMapping("/orders")
	public String orders(Model model) {
		List<StockOrder> orders = stockOrderRepository.findAllForOrderList();
		model.addAttribute("orders", toRows(orders));
		return "orders";
	}

	@GetMapping("/orders/new")
	public String newOrder(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new CreateOrderForm());
		}
		addFormOptions(model);
		return "order-form";
	}

	@PostMapping("/orders")
	public String create(@Valid @ModelAttribute("form") CreateOrderForm form, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			addFormOptions(model);
			return "order-form";
		}

		try {
			StockOrder order = orderService.createOrder(
					new CreateOrderCommand(DEMO_USER_ID, form.getItemId(), form.getWarehouseId(), form.getQuantity()));
			redirectAttributes.addFlashAttribute("success", "Ordine #" + order.getId() + " creato.");
			return "redirect:/orders";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			addFormOptions(model);
			return "order-form";
		}
	}

	@PostMapping("/orders/{id}/approve")
	public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			orderService.approveOrder(id, DEMO_USER_ID);
			redirectAttributes.addFlashAttribute("success", "Ordine #" + id + " approvato.");
		}
		catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/orders";
	}

	@PostMapping("/orders/{id}/cancel")
	public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			orderService.cancelOrder(id, DEMO_USER_ID);
			redirectAttributes.addFlashAttribute("success", "Ordine #" + id + " cancellato.");
		}
		catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/orders";
	}

	private void addFormOptions(Model model) {
		model.addAttribute("items", itemRepository.findAll());
		model.addAttribute("warehouses", warehouseRepository.findAll());
		model.addAttribute("stockRows", warehouseItemRepository.findAll());
	}

	private List<OrderListRow> toRows(List<StockOrder> orders) {
		if (orders.isEmpty()) {
			return List.of();
		}
		List<Long> orderIds = orders.stream()
				.map(StockOrder::getId)
				.toList();
		Map<Long, List<OrderStatusEvent>> eventsByOrderId = orderStatusEventRepository
				.findByOrderIdInOrderByAuthorizedAtAsc(orderIds)
				.stream()
				.collect(Collectors.groupingBy(event -> event.getOrder().getId()));

		return orders.stream()
				.map(order -> toRow(order, eventsByOrderId.getOrDefault(order.getId(), List.of())))
				.sorted(Comparator.comparing((OrderListRow row) -> row.getRequestedEvent().getAuthorizedAt()).reversed())
				.toList();
	}

	private OrderListRow toRow(StockOrder order, List<OrderStatusEvent> events) {
		if (events.isEmpty()) {
			throw new IllegalStateException("Order has no status events: " + order.getId());
		}
		return new OrderListRow(order, events.getFirst(), events.getLast());
	}
}
