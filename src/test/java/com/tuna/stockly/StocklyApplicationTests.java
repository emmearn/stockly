package com.tuna.stockly;

import static org.assertj.core.api.Assertions.assertThat;

import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.repository.OrderStatusEventRepository;
import com.tuna.stockly.repository.StockOrderRepository;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
	void loadsDemoData() {
		assertThat(warehouseRepository.count()).isEqualTo(2);
		assertThat(itemRepository.count()).isEqualTo(10);
		assertThat(warehouseItemRepository.count()).isEqualTo(18);
		assertThat(stockOrderRepository.count()).isEqualTo(10);
		assertThat(orderStatusEventRepository.count()).isEqualTo(16);
	}

}
