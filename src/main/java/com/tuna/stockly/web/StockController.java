package com.tuna.stockly.web;

import com.tuna.stockly.dto.CreateAvailabilityCommand;
import com.tuna.stockly.dto.CreateAvailabilityForm;
import com.tuna.stockly.dto.UpdateAvailabilityCommand;
import com.tuna.stockly.dto.UpdateAvailabilityForm;
import com.tuna.stockly.entity.WarehouseItem;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import com.tuna.stockly.service.StockService;
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
public class StockController {

	private final StockService stockService;
	private final WarehouseItemRepository warehouseItemRepository;
	private final WarehouseRepository warehouseRepository;

	public StockController(StockService stockService, WarehouseItemRepository warehouseItemRepository,
			WarehouseRepository warehouseRepository) {
		this.stockService = stockService;
		this.warehouseItemRepository = warehouseItemRepository;
		this.warehouseRepository = warehouseRepository;
	}

	@GetMapping({ "/", "/stock" })
	public String stock(Model model) {
		model.addAttribute("stocks", warehouseItemRepository.findAllForStockView());
		return "stock";
	}

	@GetMapping("/stock/availability")
	public String availability() {
		return "redirect:/stock/availability/new";
	}

	@GetMapping("/stock/availability/new")
	public String newAvailability(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new CreateAvailabilityForm());
		}
		addWarehouseOptions(model);
		return "new-availability-form";
	}

	@PostMapping("/stock/availability/new")
	public String createAvailability(@Valid @ModelAttribute("form") CreateAvailabilityForm form,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			addWarehouseOptions(model);
			return "new-availability-form";
		}

		try {
			stockService.createAvailability(new CreateAvailabilityCommand(form.getBarcode(), form.getItemName(),
					form.getBrand(), form.getType(), form.getWarehouseId(), form.getQuantity()));
			redirectAttributes.addFlashAttribute("success", "Disponibilita creata.");
			return "redirect:/stock";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			addWarehouseOptions(model);
			return "new-availability-form";
		}
	}

	@GetMapping("/stock/availability/{stockId}/edit")
	public String editAvailability(@PathVariable Long stockId, Model model) {
		WarehouseItem stock = stockService.getAvailability(stockId);
		if (!model.containsAttribute("form")) {
			UpdateAvailabilityForm form = new UpdateAvailabilityForm();
			form.setItemId(stock.getItem().getId());
			form.setWarehouseId(stock.getWarehouse().getId());
			form.setQuantity(stock.getQuantity());
			model.addAttribute("form", form);
		}
		model.addAttribute("stock", stock);
		return "availability-form";
	}

	@PostMapping("/stock/availability/{stockId}/edit")
	public String updateAvailability(@PathVariable Long stockId,
			@Valid @ModelAttribute("form") UpdateAvailabilityForm form, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		WarehouseItem stock = stockService.getAvailability(stockId);
		if (bindingResult.hasErrors()) {
			model.addAttribute("stock", stock);
			return "availability-form";
		}

		try {
			stockService.updateAvailability(
					new UpdateAvailabilityCommand(form.getItemId(), form.getWarehouseId(), form.getQuantity()));
			redirectAttributes.addFlashAttribute("success", "Disponibilita aggiornata.");
			return "redirect:/stock";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("stock", stock);
			return "availability-form";
		}
	}

	@PostMapping("/stock/availability/{stockId}/delete")
	public String deleteAvailability(@PathVariable Long stockId, RedirectAttributes redirectAttributes) {
		stockService.deleteAvailability(stockId);
		redirectAttributes.addFlashAttribute("success", "Disponibilita eliminata.");
		return "redirect:/stock";
	}

	private void addWarehouseOptions(Model model) {
		model.addAttribute("warehouses", warehouseRepository.findAll());
	}
}
