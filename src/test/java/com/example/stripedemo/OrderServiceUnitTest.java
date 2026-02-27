package com.example.stripedemo;

import com.example.stripedemo.catalog.Product;
import com.example.stripedemo.catalog.ProductRepository;
import com.example.stripedemo.inventory.InventoryRepository;
import com.example.stripedemo.inventory.InventoryReservationRepository;
import com.example.stripedemo.order.Order;
import com.example.stripedemo.order.OrderRepository;
import com.example.stripedemo.order.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class OrderServiceUnitTest {
    @Test
    void shouldCalculateOrderTotalOnServer() {
        OrderRepository orderRepo = Mockito.mock(OrderRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        InventoryRepository inventoryRepo = Mockito.mock(InventoryRepository.class);
        InventoryReservationRepository reservationRepo = Mockito.mock(InventoryReservationRepository.class);

        Product p = new Product();
        p.setSku("SKU"); p.setName("N"); p.setPrice(750L); p.setCurrency("TWD"); p.setActive(true);
        Mockito.when(productRepo.findById("SKU")).thenReturn(Optional.of(p));
        Mockito.when(inventoryRepo.reserveAtomic("SKU", 2L)).thenReturn(1);
        Mockito.when(orderRepo.save(any())).thenAnswer(a -> a.getArgument(0));

        OrderService service = new OrderService(orderRepo, productRepo, inventoryRepo, reservationRepo);
        Order o = service.createOrder(new OrderService.CreateOrderRequest("a@b.com", java.util.List.of(new OrderService.CreateOrderItem("SKU", 2L))));
        assertEquals(1500L, o.getTotalAmount());
    }
}
