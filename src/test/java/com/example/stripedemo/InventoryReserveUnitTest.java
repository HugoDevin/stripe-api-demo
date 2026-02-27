package com.example.stripedemo;

import com.example.stripedemo.catalog.ProductRepository;
import com.example.stripedemo.common.error.ApiException;
import com.example.stripedemo.inventory.InventoryRepository;
import com.example.stripedemo.inventory.InventoryReservationRepository;
import com.example.stripedemo.order.OrderRepository;
import com.example.stripedemo.order.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InventoryReserveUnitTest {
    @Test
    void shouldFailWhenAtomicReserveReturnsZero() {
        OrderRepository orderRepo = Mockito.mock(OrderRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        InventoryRepository inventoryRepo = Mockito.mock(InventoryRepository.class);
        InventoryReservationRepository reservationRepo = Mockito.mock(InventoryReservationRepository.class);
        Mockito.when(inventoryRepo.reserveAtomic("SKU", 1L)).thenReturn(0);
        OrderService service = new OrderService(orderRepo, productRepo, inventoryRepo, reservationRepo);
        assertThrows(ApiException.class, () -> service.createOrder(new OrderService.CreateOrderRequest("a@b.com", java.util.List.of(new OrderService.CreateOrderItem("SKU", 1L)))));
    }
}
