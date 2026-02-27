package com.example.stripedemo.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {
    public static final String EXCHANGE = "domain.events";

    @Bean TopicExchange exchange() { return new TopicExchange(EXCHANGE); }
    @Bean Queue receiptQueue() { return QueueBuilder.durable("receipt.queue").build(); }
    @Bean Queue fulfillmentQueue() { return QueueBuilder.durable("fulfillment.queue").build(); }
    @Bean Queue notificationQueue() { return QueueBuilder.durable("notification.queue").build(); }
    @Bean Queue accountingQueue() { return QueueBuilder.durable("accounting.queue").build(); }
    @Bean Queue reportingQueue() { return QueueBuilder.durable("reporting.queue").build(); }
    @Bean Queue inventoryQueue() { return QueueBuilder.durable("inventory.queue").build(); }

    @Bean Binding b1(Queue receiptQueue, TopicExchange exchange) { return BindingBuilder.bind(receiptQueue).to(exchange).with("payment.succeeded"); }
    @Bean Binding b2(Queue fulfillmentQueue, TopicExchange exchange) { return BindingBuilder.bind(fulfillmentQueue).to(exchange).with("payment.succeeded"); }
    @Bean Binding b3(Queue notificationQueue, TopicExchange exchange) { return BindingBuilder.bind(notificationQueue).to(exchange).with("payment.succeeded"); }
    @Bean Binding b4(Queue accountingQueue, TopicExchange exchange) { return BindingBuilder.bind(accountingQueue).to(exchange).with("payment.succeeded"); }
    @Bean Binding b5(Queue reportingQueue, TopicExchange exchange) { return BindingBuilder.bind(reportingQueue).to(exchange).with("payment.succeeded"); }
    @Bean Binding b6(Queue inventoryQueue, TopicExchange exchange) { return BindingBuilder.bind(inventoryQueue).to(exchange).with("payment.failed"); }
}
