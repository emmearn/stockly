package com.tuna.stockly.config;

import java.time.LocalDateTime;

import com.tuna.stockly.entity.Item;
import com.tuna.stockly.repository.ItemRepository;
import com.tuna.stockly.entity.OrderItem;
import com.tuna.stockly.entity.OrderStatus;
import com.tuna.stockly.entity.OrderStatusEvent;
import com.tuna.stockly.repository.OrderStatusEventRepository;
import com.tuna.stockly.entity.StockOrder;
import com.tuna.stockly.repository.StockOrderRepository;
import com.tuna.stockly.entity.WarehouseItem;
import com.tuna.stockly.repository.WarehouseItemRepository;
import com.tuna.stockly.entity.Warehouse;
import com.tuna.stockly.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({ "local", "poc", "test" })
public class DemoDataSeeder implements CommandLineRunner {

	private final WarehouseRepository warehouseRepository;
	private final ItemRepository itemRepository;
	private final WarehouseItemRepository warehouseItemRepository;
	private final StockOrderRepository stockOrderRepository;
	private final OrderStatusEventRepository orderStatusEventRepository;

	public DemoDataSeeder(WarehouseRepository warehouseRepository, ItemRepository itemRepository,
			WarehouseItemRepository warehouseItemRepository, StockOrderRepository stockOrderRepository,
			OrderStatusEventRepository orderStatusEventRepository) {
		this.warehouseRepository = warehouseRepository;
		this.itemRepository = itemRepository;
		this.warehouseItemRepository = warehouseItemRepository;
		this.stockOrderRepository = stockOrderRepository;
		this.orderStatusEventRepository = orderStatusEventRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (warehouseRepository.count() > 0 || itemRepository.count() > 0 || stockOrderRepository.count() > 0) {
			return;
		}

		Warehouse elmas = warehouseRepository.save(new Warehouse("Elmas", "Zona Industriale, Elmas (CA)"));
		Warehouse quartucciu = warehouseRepository.save(new Warehouse("Quartucciu", "Via delle Serre, Quartucciu (CA)"));

		Item bolt = itemRepository.save(new Item("800000000001", "Bullone M8", "Ferramenta Nord", "Bulloneria"));
		Item hammer = itemRepository.save(new Item("800000000002", "Martello", "Utensili Pro", "Utensili"));
		Item pipe = itemRepository.save(new Item("800000000003", "Tubo PVC", "IdroLine", "Tubature"));
		Item safetyGloves = itemRepository.save(new Item("800000000004", "Guanti antitaglio", "SafeHands", "DPI"));
		Item safetyGlasses = itemRepository.save(new Item("800000000005", "Occhiali protettivi", "SafeView", "DPI"));
		Item drillBit = itemRepository.save(new Item("800000000006", "Punta trapano 6 mm", "Foratura Sarda", "Utensili"));
		Item screw = itemRepository.save(new Item("800000000007", "Vite autofilettante 4x40", "Ferramenta Nord", "Viteria"));
		Item washer = itemRepository.save(new Item("800000000008", "Rondella zincata M8", "Ferramenta Nord", "Bulloneria"));
		Item cableTie = itemRepository.save(new Item("800000000009", "Fascetta nylon 200 mm", "CableFix", "Materiale elettrico"));
		Item electricalTape = itemRepository.save(new Item("800000000010", "Nastro isolante nero", "ElettroPro", "Materiale elettrico"));
		Item silicone = itemRepository.save(new Item("800000000011", "Silicone trasparente", "SealPro", "Sigillanti"));
		Item paintRoller = itemRepository.save(new Item("800000000012", "Rullo pittura 20 cm", "ColorLine", "Verniciatura"));
		Item brush = itemRepository.save(new Item("800000000013", "Pennello piatto 50 mm", "ColorLine", "Verniciatura"));
		Item cementBag = itemRepository.save(new Item("800000000014", "Cemento rapido 5 kg", "EdilSud", "Edilizia"));
		Item workLight = itemRepository.save(new Item("800000000015", "Lampada da cantiere LED", "LucePro", "Illuminazione"));
		Item extensionCord = itemRepository.save(new Item("800000000016", "Prolunga 10 m", "ElettroPro", "Materiale elettrico"));
		Item tapeMeasure = itemRepository.save(new Item("800000000017", "Metro a nastro 5 m", "MisuraPro", "Utensili"));
		Item level = itemRepository.save(new Item("800000000018", "Livella 40 cm", "MisuraPro", "Utensili"));
		Item cuttingDisc = itemRepository.save(new Item("800000000019", "Disco taglio metallo", "CutMax", "Utensili"));
		Item pvcElbow = itemRepository.save(new Item("800000000020", "Gomito PVC 90 gradi", "IdroLine", "Tubature"));

		warehouseItemRepository.save(new WarehouseItem(elmas, bolt, 45));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, bolt, 20));
		warehouseItemRepository.save(new WarehouseItem(elmas, hammer, 8));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, hammer, 6));
		warehouseItemRepository.save(new WarehouseItem(elmas, pipe, 12));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, pipe, 30));
		warehouseItemRepository.save(new WarehouseItem(elmas, safetyGloves, 80));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, safetyGloves, 50));
		warehouseItemRepository.save(new WarehouseItem(elmas, safetyGlasses, 35));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, safetyGlasses, 24));
		warehouseItemRepository.save(new WarehouseItem(elmas, drillBit, 60));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, drillBit, 18));
		warehouseItemRepository.save(new WarehouseItem(elmas, screw, 300));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, screw, 180));
		warehouseItemRepository.save(new WarehouseItem(elmas, washer, 240));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, washer, 160));
		warehouseItemRepository.save(new WarehouseItem(elmas, cableTie, 500));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, cableTie, 300));
		warehouseItemRepository.save(new WarehouseItem(elmas, electricalTape, 40));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, electricalTape, 25));
		warehouseItemRepository.save(new WarehouseItem(elmas, silicone, 22));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, paintRoller, 16));
		warehouseItemRepository.save(new WarehouseItem(elmas, brush, 28));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, cementBag, 14));
		warehouseItemRepository.save(new WarehouseItem(elmas, workLight, 9));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, extensionCord, 11));
		warehouseItemRepository.save(new WarehouseItem(elmas, tapeMeasure, 19));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, level, 13));
		warehouseItemRepository.save(new WarehouseItem(elmas, cuttingDisc, 45));
		warehouseItemRepository.save(new WarehouseItem(quartucciu, pvcElbow, 55));

		LocalDateTime now = LocalDateTime.now();

		StockOrder requiredOrder = saveOrder(OrderStatus.REQUIRED, bolt, elmas, 5);
		recordEvent(requiredOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusDays(2));

		StockOrder approvedOrder = saveOrder(OrderStatus.APPROVED, hammer, elmas, 2);
		recordEvent(approvedOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusDays(1));
		recordEvent(approvedOrder, OrderStatus.REQUIRED, OrderStatus.APPROVED, "demo.manager", now.minusHours(20));

		StockOrder canceledOrder = saveOrder(OrderStatus.CANCELED, pipe, quartucciu, 4);
		recordEvent(canceledOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(10));
		recordEvent(canceledOrder, OrderStatus.REQUIRED, OrderStatus.CANCELED, "demo.user", now.minusHours(8));

		StockOrder glovesOrder = saveOrder(OrderStatus.REQUIRED, safetyGloves, elmas, 12);
		recordEvent(glovesOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(7));

		StockOrder drillBitOrder = saveOrder(OrderStatus.REQUIRED, drillBit, quartucciu, 6);
		recordEvent(drillBitOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(6));

		StockOrder tapeOrder = saveOrder(OrderStatus.REQUIRED, electricalTape, elmas, 5);
		recordEvent(tapeOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(5));

		StockOrder screwsOrder = saveOrder(OrderStatus.APPROVED, screw, quartucciu, 40);
		recordEvent(screwsOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(4));
		recordEvent(screwsOrder, OrderStatus.REQUIRED, OrderStatus.APPROVED, "demo.manager", now.minusHours(3));

		StockOrder workLightOrder = saveOrder(OrderStatus.APPROVED, workLight, elmas, 2);
		recordEvent(workLightOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(3));
		recordEvent(workLightOrder, OrderStatus.REQUIRED, OrderStatus.APPROVED, "demo.manager", now.minusHours(2));

		StockOrder cementOrder = saveOrder(OrderStatus.CANCELED, cementBag, quartucciu, 3);
		recordEvent(cementOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusHours(2));
		recordEvent(cementOrder, OrderStatus.REQUIRED, OrderStatus.CANCELED, "demo.user", now.minusMinutes(90));

		StockOrder levelOrder = saveOrder(OrderStatus.CANCELED, level, quartucciu, 2);
		recordEvent(levelOrder, null, OrderStatus.REQUIRED, "demo.user", now.minusMinutes(80));
		recordEvent(levelOrder, OrderStatus.REQUIRED, OrderStatus.CANCELED, "demo.user", now.minusMinutes(60));
	}

	private StockOrder saveOrder(OrderStatus status, Item item, Warehouse warehouse, int quantity) {
		StockOrder order = new StockOrder(status);
		order.addItem(new OrderItem(item, warehouse, quantity));
		return stockOrderRepository.save(order);
	}

	private void recordEvent(StockOrder order, OrderStatus fromStatus, OrderStatus toStatus,
			String authorizedByUserId, LocalDateTime authorizedAt) {
		orderStatusEventRepository.save(
				new OrderStatusEvent(order, fromStatus, toStatus, authorizedByUserId, authorizedAt));
	}
}
