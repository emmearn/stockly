package com.tuna.stockly;

import static org.assertj.core.api.Assertions.assertThat;

import com.tuna.stockly.item.ItemRepository;
import com.tuna.stockly.order.OrderStatusEventRepository;
import com.tuna.stockly.order.StockOrderRepository;
import com.tuna.stockly.stock.WarehouseItemRepository;
import com.tuna.stockly.warehouse.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("poc")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class StocklyApplicationTests {

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private WarehouseItemRepository warehouseItemRepository;

	@Autowired
	private StockOrderRepository stockOrderRepository;

	@Autowired
	private OrderStatusEventRepository orderStatusEventRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void loadsPocDemoData() {
		assertThat(warehouseRepository.count()).isEqualTo(2);
		assertThat(itemRepository.count()).isEqualTo(3);
		assertThat(warehouseItemRepository.count()).isEqualTo(4);
		assertThat(stockOrderRepository.count()).isEqualTo(3);
		assertThat(orderStatusEventRepository.count()).isEqualTo(5);
	}

}
