package com.tuna.stockly.web;

import com.tuna.stockly.dto.UpdateAvailabilityCommand;
import com.tuna.stockly.dto.UpdateAvailabilityForm;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import com.tuna.stockly.service.StockService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StockController {

	private final StockService stockService;
	private final WarehouseItemRepository warehouseItemRepository;
	private final ItemRepository itemRepository;
	private final WarehouseRepository warehouseRepository;

	public StockController(StockService stockService, WarehouseItemRepository warehouseItemRepository,
			ItemRepository itemRepository, WarehouseRepository warehouseRepository) {
		this.stockService = stockService;
		this.warehouseItemRepository = warehouseItemRepository;
		this.itemRepository = itemRepository;
		this.warehouseRepository = warehouseRepository;
	}

	@GetMapping({ "/", "/stock" })
	public String stock(Model model) {
		model.addAttribute("stocks", warehouseItemRepository.findAllForStockView());
		return "stock";
	}

	@GetMapping("/stock/availability")
	public String editAvailability(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new UpdateAvailabilityForm());
		}
		addAvailabilityOptions(model);
		return "availability-form";
	}

	@PostMapping("/stock/availability")
	public String saveAvailability(@Valid @ModelAttribute("form") UpdateAvailabilityForm form,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			addAvailabilityOptions(model);
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
			addAvailabilityOptions(model);
			return "availability-form";
		}
	}

	private void addAvailabilityOptions(Model model) {
		model.addAttribute("items", itemRepository.findAll());
		model.addAttribute("warehouses", warehouseRepository.findAll());
		model.addAttribute("stockRows", warehouseItemRepository.findAllForStockView());
	}
}
