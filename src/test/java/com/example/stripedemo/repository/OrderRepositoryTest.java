package com.example.stripedemo.repository;

import com.example.stripedemo.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldLoadSampleOrdersFromDataSql() {
        List<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSizeGreaterThanOrEqualTo(3);
        assertThat(orders)
                .extracting(Order::getId)
                .contains("sample-1001", "sample-1002", "sample-1003");
    }

    @Test
    void shouldSaveNewOrder() {
        Order order = new Order();
        order.setId("sample-new-order");
        order.setProduct("Marker");
        order.setAmount(250L);
        order.setCurrency("USD");
        order.setStatus("pending");

        orderRepository.save(order);

        assertThat(orderRepository.findById("sample-new-order")).isPresent();
    }
}
