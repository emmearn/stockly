package com.tuna.stockly.stock;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockController {

	private final WarehouseItemRepository warehouseItemRepository;

	public StockController(WarehouseItemRepository warehouseItemRepository) {
		this.warehouseItemRepository = warehouseItemRepository;
	}

	@GetMapping({ "/", "/stock" })
	public String stock(Model model) {
		model.addAttribute("stocks", warehouseItemRepository.findAllForStockView());
		return "stock";
	}
}
