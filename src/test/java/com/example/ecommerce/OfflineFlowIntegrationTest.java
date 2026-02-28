package com.example.ecommerce;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecommerce.accounting.AccountingEntryRepository;
import com.example.ecommerce.catalog.*;
import com.example.ecommerce.fulfillment.FulfillmentRepository;
import com.example.ecommerce.inventory.*;
import com.example.ecommerce.invoicing.ReceiptRepository;
import com.example.ecommerce.messaging.consumer.PaymentEventConsumers;
import com.example.ecommerce.notification.NotificationRepository;
import com.example.ecommerce.order.*;
import com.example.ecommerce.payment.PaymentService;
import com.example.ecommerce.reporting.SalesDailyFactRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OfflineFlowIntegrationTest {
  @Autowired ProductRepository productRepo; @Autowired InventoryRepository inventoryRepo; @Autowired OrderService orderService;
  @Autowired PaymentService paymentService; @Autowired PaymentEventConsumers consumers;
  @Autowired ReceiptRepository receiptRepo; @Autowired FulfillmentRepository fulfillmentRepo; @Autowired NotificationRepository notificationRepo; @Autowired AccountingEntryRepository accountingRepo; @Autowired SalesDailyFactRepository salesRepo; @Autowired OrderRepository orderRepo; @Autowired InventoryReservationRepository reservationRepo;

  @BeforeEach void setup(){
    if(productRepo.findById("SKU").isEmpty()){ Product p=new Product(); p.sku="SKU"; p.name="book"; p.price=new BigDecimal("10.00"); p.currency="USD"; productRepo.save(p); Inventory i=new Inventory(); i.sku="SKU"; i.availableQty=10; i.reservedQty=0; inventoryRepo.save(i);} }

  @Test void concurrentReserveOnlyOneSuccess() throws Exception {
    ExecutorService ex = Executors.newFixedThreadPool(2);
    Callable<Boolean> task=()->{try{orderService.create("a@a.com", List.of(new OrderService.ItemRequest("SKU",6)));return true;}catch(Exception e){return false;}};
    var f1=ex.submit(task); var f2=ex.submit(task); boolean r1=f1.get(); boolean r2=f2.get();
    assertTrue(r1 ^ r2);
  }

  @Test void simulateSuccessCreatesProjections() throws Exception {
    var o = orderService.create("b@a.com", List.of(new OrderService.ItemRequest("SKU",1))); var p = paymentService.createPayment(o.id); paymentService.markSuccess(o.id, "evt-1");
    String payload = "{\"eventId\":\"evt-1\",\"orderId\":\"%s\",\"paymentId\":\"%s\",\"provider\":\"FAKE\",\"providerIntentId\":\"pi\",\"amount\":10.00,\"currency\":\"USD\",\"occurredAt\":\"2025-01-01T00:00:00Z\"}".formatted(o.id,p.id);
    consumers.receipt(payload); consumers.fulfill(payload); consumers.notification(payload); consumers.accounting(payload); consumers.reporting(payload);
    assertFalse(receiptRepo.findAll().isEmpty()); assertFalse(fulfillmentRepo.findAll().isEmpty()); assertFalse(notificationRepo.findAll().isEmpty()); assertFalse(accountingRepo.findAll().isEmpty()); assertFalse(salesRepo.findAll().isEmpty());
  }

  @Test void simulateFailReleasesReservation(){
    var o = orderService.create("c@a.com", List.of(new OrderService.ItemRequest("SKU",1))); paymentService.createPayment(o.id); paymentService.markFailed(o.id, "evt-f");
    reservationRepo.findByOrderId(o.id).ifPresent(r->{ if(r.status==ReservationStatus.RESERVED){ inventoryRepo.release(r.sku,r.qty); r.status=ReservationStatus.RELEASED; reservationRepo.save(r);} });
    assertEquals(OrderStatus.CANCELED, orderRepo.findById(o.id).orElseThrow().status);
  }
}
