package com.tuna.stockly.web;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tuna.stockly.dto.CreateOrderCommand;
import com.tuna.stockly.dto.CreateOrderForm;
import com.tuna.stockly.dto.OrderListRow;
import com.tuna.stockly.dto.Permissions;
import com.tuna.stockly.dto.SimulatedUser;
import com.tuna.stockly.entity.OrderStatusEvent;
import com.tuna.stockly.entity.StockOrder;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.repository.OrderStatusEventRepository;
import com.tuna.stockly.repository.StockOrderRepository;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import com.tuna.stockly.service.OrderService;
import com.tuna.stockly.service.RoleSimulationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
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

	private final OrderService orderService;
	private final StockOrderRepository stockOrderRepository;
	private final OrderStatusEventRepository orderStatusEventRepository;
	private final ItemRepository itemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseItemRepository warehouseItemRepository;
	private final RoleSimulationService roleSimulationService;

	public OrderController(OrderService orderService, StockOrderRepository stockOrderRepository,
			OrderStatusEventRepository orderStatusEventRepository, ItemRepository itemRepository,
			WarehouseRepository warehouseRepository, WarehouseItemRepository warehouseItemRepository,
			RoleSimulationService roleSimulationService) {
		this.orderService = orderService;
		this.stockOrderRepository = stockOrderRepository;
		this.orderStatusEventRepository = orderStatusEventRepository;
		this.itemRepository = itemRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseItemRepository = warehouseItemRepository;
		this.roleSimulationService = roleSimulationService;
	}

	@GetMapping("/orders")
	public String orders(Model model, HttpSession session) {
		List<StockOrder> orders = stockOrderRepository.findAllForOrderList();
		model.addAttribute("orders", visibleRows(toRows(orders), session));
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
			RedirectAttributes redirectAttributes, HttpSession session) {
		if (bindingResult.hasErrors()) {
			addFormOptions(model);
			return "order-form";
		}

		try {
			SimulatedUser currentUser = roleSimulationService.getCurrentUser(session);
			StockOrder order = orderService.createOrder(
					new CreateOrderCommand(currentUser.getUserId(), form.getItemId(), form.getWarehouseId(),
							form.getQuantity()));
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
	public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
		if (!roleSimulationService.getPermissions(session).canApproveOrders()) {
			redirectAttributes.addFlashAttribute("error", "Ruolo simulato non abilitato ad approvare ordini.");
			return "redirect:/orders";
		}
		try {
			orderService.approveOrder(id, roleSimulationService.getCurrentUser(session).getUserId());
			redirectAttributes.addFlashAttribute("success", "Ordine #" + id + " approvato.");
		}
		catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/orders";
	}

	@PostMapping("/orders/{id}/cancel")
	public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
		try {
			StockOrder order = stockOrderRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
			OrderListRow row = toRow(order, orderStatusEventRepository.findByOrderIdOrderByAuthorizedAtAsc(id));
			if (!canCancel(row, session)) {
				redirectAttributes.addFlashAttribute("error", "Ruolo simulato non abilitato a cancellare questo ordine.");
				return "redirect:/orders";
			}
			orderService.cancelOrder(id, roleSimulationService.getCurrentUser(session).getUserId());
			redirectAttributes.addFlashAttribute("success", "Ordine #" + id + " cancellato.");
		}
		catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/orders";
	}

	private List<OrderListRow> visibleRows(List<OrderListRow> rows, HttpSession session) {
		Permissions permissions = roleSimulationService.getPermissions(session);
		if (permissions.canViewAllOrders()) {
			return rows;
		}
		String currentUserId = roleSimulationService.getCurrentUser(session).getUserId();
		return rows.stream()
				.filter(row -> row.getRequestedEvent().getAuthorizedByUserId().equals(currentUserId))
				.toList();
	}

	private boolean canCancel(OrderListRow row, HttpSession session) {
		Permissions permissions = roleSimulationService.getPermissions(session);
		if (permissions.canCancelAnyOrder()) {
			return true;
		}
		String currentUserId = roleSimulationService.getCurrentUser(session).getUserId();
		return permissions.canCancelOwnOrder()
				&& row.getRequestedEvent().getAuthorizedByUserId().equals(currentUserId);
	}

	private void addFormOptions(Model model) {
		model.addAttribute("items", itemRepository.findAll());
		model.addAttribute("warehouses", warehouseRepository.findAll());
		model.addAttribute("stockRows", warehouseItemRepository.findAllForStockView());
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
