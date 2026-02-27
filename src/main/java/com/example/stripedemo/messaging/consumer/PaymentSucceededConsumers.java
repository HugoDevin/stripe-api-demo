package com.example.stripedemo.messaging.consumer;

import com.example.stripedemo.accounting.AccountingEntry;
import com.example.stripedemo.accounting.AccountingEntryRepository;
import com.example.stripedemo.common.idempotency.ConsumerIdempotencyService;
import com.example.stripedemo.fulfillment.Fulfillment;
import com.example.stripedemo.fulfillment.FulfillmentRepository;
import com.example.stripedemo.inventory.InventoryService;
import com.example.stripedemo.invoicing.Receipt;
import com.example.stripedemo.invoicing.ReceiptRepository;
import com.example.stripedemo.notification.NotificationEntity;
import com.example.stripedemo.notification.NotificationRepository;
import com.example.stripedemo.order.Order;
import com.example.stripedemo.order.OrderRepository;
import com.example.stripedemo.order.OrderStatus;
import com.example.stripedemo.reporting.SalesDailyFact;
import com.example.stripedemo.reporting.SalesDailyFactRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class PaymentSucceededConsumers {
    private final ObjectMapper om;
    private final ConsumerIdempotencyService idem;
    private final ReceiptRepository receiptRepository;
    private final FulfillmentRepository fulfillmentRepository;
    private final NotificationRepository notificationRepository;
    private final AccountingEntryRepository accountingEntryRepository;
    private final SalesDailyFactRepository salesDailyFactRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    public PaymentSucceededConsumers(ObjectMapper om, ConsumerIdempotencyService idem, ReceiptRepository receiptRepository, FulfillmentRepository fulfillmentRepository, NotificationRepository notificationRepository, AccountingEntryRepository accountingEntryRepository, SalesDailyFactRepository salesDailyFactRepository, OrderRepository orderRepository, InventoryService inventoryService) {
        this.om = om; this.idem = idem; this.receiptRepository = receiptRepository; this.fulfillmentRepository = fulfillmentRepository;
        this.notificationRepository = notificationRepository; this.accountingEntryRepository = accountingEntryRepository; this.salesDailyFactRepository = salesDailyFactRepository;
        this.orderRepository = orderRepository; this.inventoryService = inventoryService;
    }

    @RabbitListener(queues = "receipt.queue")
    public void receipt(String message) throws Exception {
        JsonNode n = om.readTree(message); String eventId = n.get("eventId").asText(); if (idem.alreadyProcessed(eventId, "receipt")) return;
        String orderId = n.get("orderId").asText(); if (receiptRepository.existsByOrderId(orderId)) return;
        Receipt r = new Receipt(); r.setOrderId(orderId); r.setReceiptNo("R-" + UUID.randomUUID().toString().substring(0, 8)); r.setIssuedAt(OffsetDateTime.now()); receiptRepository.save(r);
    }

    @RabbitListener(queues = "fulfillment.queue")
    public void fulfillment(String message) throws Exception {
        JsonNode n = om.readTree(message); String eventId = n.get("eventId").asText(); if (idem.alreadyProcessed(eventId, "fulfillment")) return;
        String orderId = n.get("orderId").asText(); inventoryService.commit(orderId);
        if (!fulfillmentRepository.existsByOrderId(orderId)) {
            Fulfillment f = new Fulfillment(); f.setOrderId(orderId); f.setStatus("FULFILLED"); f.setActivatedAt(OffsetDateTime.now()); f.setDetailsJson("{\"type\":\"digital\"}"); fulfillmentRepository.save(f);
        }
        Order o = orderRepository.findById(orderId).orElseThrow(); o.setStatus(OrderStatus.FULFILLED); o.setUpdatedAt(OffsetDateTime.now()); orderRepository.save(o);
    }

    @RabbitListener(queues = "notification.queue")
    public void notify(String message) throws Exception {
        JsonNode n = om.readTree(message); String eventId = n.get("eventId").asText(); if (idem.alreadyProcessed(eventId, "notification")) return;
        String orderId = n.get("orderId").asText();
        Order order = orderRepository.findById(orderId).orElseThrow();
        NotificationEntity e = new NotificationEntity(); e.setOrderId(orderId); e.setChannel("EMAIL"); e.setToAddr(order.getCustomerEmail());
        e.setSubject("Payment Success"); e.setBody("Order " + orderId + " paid."); e.setStatus("SENT"); e.setCreatedAt(OffsetDateTime.now()); e.setSentAt(OffsetDateTime.now());
        notificationRepository.save(e);
    }

    @RabbitListener(queues = "accounting.queue")
    public void accounting(String message) throws Exception {
        JsonNode n = om.readTree(message); String eventId = n.get("eventId").asText(); if (idem.alreadyProcessed(eventId, "accounting")) return;
        String orderId = n.get("orderId").asText(); if (accountingEntryRepository.existsByOrderId(orderId)) return;
        AccountingEntry e = new AccountingEntry(); e.setOrderId(orderId); e.setDebitAccount("Cash"); e.setCreditAccount("Revenue");
        e.setAmount(n.get("amount").asLong()); e.setCurrency(n.get("currency").asText()); e.setCreatedAt(OffsetDateTime.now()); accountingEntryRepository.save(e);
    }

    @RabbitListener(queues = "reporting.queue")
    public void reporting(String message) throws Exception {
        JsonNode n = om.readTree(message); String eventId = n.get("eventId").asText(); if (idem.alreadyProcessed(eventId, "reporting")) return;
        LocalDate d = OffsetDateTime.parse(n.get("occurredAt").asText()).toLocalDate();
        SalesDailyFact f = salesDailyFactRepository.findById(d).orElseGet(() -> { SalesDailyFact nf = new SalesDailyFact(); nf.setDate(d); nf.setOrderCount(0L); nf.setTotalAmount(0L); return nf; });
        f.setOrderCount(f.getOrderCount() + 1); f.setTotalAmount(f.getTotalAmount() + n.get("amount").asLong()); f.setUpdatedAt(OffsetDateTime.now());
        salesDailyFactRepository.save(f);
    }

    @RabbitListener(queues = "inventory.queue")
    public void onPaymentFailed(String message) throws Exception {
        JsonNode n = om.readTree(message); String eventId = n.get("eventId").asText(); if (idem.alreadyProcessed(eventId, "inventory-release")) return;
        inventoryService.release(n.get("orderId").asText(), false);
    }
}
