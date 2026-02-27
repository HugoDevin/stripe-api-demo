package com.example.stripedemo;

import com.example.stripedemo.catalog.CatalogController;
import com.example.stripedemo.inventory.InventoryController;
import com.example.stripedemo.order.OrderController;
import com.example.stripedemo.payment.api.PaymentController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.rabbitmq.listener.simple.auto-startup=false"})
@AutoConfigureMockMvc
@ActiveProfiles("dev-offline")
class CheckoutFlowIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @BeforeEach
    void seed() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(new CatalogController.CreateProductRequest("CON-SKU", "Concurrent", 100L, "TWD"))))
                .andReturn();
        mvc.perform(post("/api/inventory/CON-SKU/adjust").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(new InventoryController.AdjustRequest(10L))));
    }

    @Test
    void concurrentOrders_oneShouldConflict() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(2);
        Callable<Integer> c = () -> mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new OrderController.CreateOrderApiRequest("u@e.com", java.util.List.of(new OrderController.Item("CON-SKU", 6L))))))
                .andReturn().getResponse().getStatus();
        int s1 = es.submit(c).get();
        int s2 = es.submit(c).get();
        es.shutdown();
        org.junit.jupiter.api.Assertions.assertTrue((s1 == 200 && s2 == 409) || (s1 == 409 && s2 == 200));
    }

    @Test
    void simulateSuccessAndFailEndpointsProtected() throws Exception {
        mvc.perform(post("/internal/payments/any/simulate-success")).andExpect(status().isUnauthorized());
    }
}
