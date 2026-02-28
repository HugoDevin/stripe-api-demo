package com.example.ecommerce.messaging.consumer;

import com.example.ecommerce.accounting.*;
import com.example.ecommerce.common.idempotency.*;
import com.example.ecommerce.fulfillment.*;
import com.example.ecommerce.inventory.*;
import com.example.ecommerce.invoicing.*;
import com.example.ecommerce.notification.*;
import com.example.ecommerce.order.*;
import com.example.ecommerce.reporting.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentEventConsumers {
  private final ObjectMapper om = new ObjectMapper();
  private final ProcessedEventRepository processed; private final ReceiptRepository receiptRepo; private final FulfillmentRepository fulfillmentRepo;
  private final NotificationRepository notificationRepo; private final AccountingEntryRepository accountingRepo; private final SalesDailyFactRepository salesRepo;
  private final InventoryReservationRepository reservationRepo; private final InventoryRepository inventoryRepo; private final OrderRepository orderRepo;
  public PaymentEventConsumers(ProcessedEventRepository p, ReceiptRepository r, FulfillmentRepository f, NotificationRepository n, AccountingEntryRepository a, SalesDailyFactRepository s, InventoryReservationRepository rr, InventoryRepository i, OrderRepository o){processed=p;receiptRepo=r;fulfillmentRepo=f;notificationRepo=n;accountingRepo=a;salesRepo=s;reservationRepo=rr;inventoryRepo=i;orderRepo=o;}

  @RabbitListener(queues = "receipt.queue") @Transactional
  public void receipt(String body) throws Exception {
    var m=parse(body); if(done(m,"receipt")) return;
    UUID oid = UUID.fromString((String)m.get("orderId"));
    if (receiptRepo.existsByOrderId(oid)) { mark(m,"receipt"); return; }
    Receipt r = new Receipt(); r.orderId=oid; r.receiptNo="R-"+System.currentTimeMillis(); receiptRepo.save(r); mark(m,"receipt");
  }

  @RabbitListener(queues = "fulfillment.queue") @Transactional
  public void fulfill(String body) throws Exception { var m=parse(body); if(done(m,"fulfillment")) return; UUID oid=UUID.fromString((String)m.get("orderId"));
    reservationRepo.findByOrderId(oid).ifPresent(res->{ if(res.status==ReservationStatus.RESERVED){ inventoryRepo.commit(res.sku,res.qty); res.status=ReservationStatus.COMMITTED; reservationRepo.save(res);} });
    if (!fulfillmentRepo.existsByOrderId(oid)) {
      Fulfillment f = new Fulfillment(); f.orderId=oid; f.status="ACTIVATED"; f.detailsJson="{\"type\":\"digital\"}"; fulfillmentRepo.save(f);
    }
    orderRepo.findById(oid).ifPresent(o->{o.status=OrderStatus.FULFILLED; orderRepo.save(o);}); mark(m,"fulfillment"); }

  @RabbitListener(queues = "notification.queue") @Transactional
  public void notification(String body) throws Exception { var m=parse(body); if(done(m,"notification")) return; NotificationEntity n = new NotificationEntity(); n.orderId=UUID.fromString((String)m.get("orderId")); n.channel="EMAIL"; n.toAddr="customer@example.com"; n.subject="Order Update"; n.body=body; n.status="SENT"; n.sentAt=Instant.now(); notificationRepo.save(n); mark(m,"notification"); }

  @RabbitListener(queues = "accounting.queue") @Transactional
  public void accounting(String body) throws Exception { var m=parse(body); if(done(m,"accounting")) return; UUID oid=UUID.fromString((String)m.get("orderId"));
    if (accountingRepo.existsByOrderId(oid)) { mark(m,"accounting"); return; }
    AccountingEntry e = new AccountingEntry(); e.orderId=oid; e.debitAccount="Cash"; e.creditAccount="Revenue"; e.amount=new java.math.BigDecimal(m.get("amount").toString()); e.currency=(String)m.get("currency"); accountingRepo.save(e); mark(m,"accounting"); }

  @RabbitListener(queues = "reporting.queue") @Transactional
  public void reporting(String body) throws Exception { var m=parse(body); if(done(m,"reporting")) return; LocalDate d = Instant.parse((String)m.get("occurredAt")).atZone(java.time.ZoneOffset.UTC).toLocalDate();
    SalesDailyFact f = salesRepo.findById(d).orElseGet(()->{var x=new SalesDailyFact();x.date=d;return x;});
    f.totalAmount=f.totalAmount.add(new java.math.BigDecimal(m.get("amount").toString())); f.orderCount+=1; f.updatedAt=Instant.now(); salesRepo.save(f); mark(m,"reporting"); }

  @RabbitListener(queues = "inventory.queue") @Transactional
  public void inventory(String body) throws Exception { var m=parse(body); if(!"payment.failed".equals(m.getOrDefault("eventType","payment.succeeded")) && body.contains("payment.failed")){} UUID oid=UUID.fromString((String)m.get("orderId"));
    if (body.contains("payment.failed")) reservationRepo.findByOrderId(oid).ifPresent(res->{ if(res.status==ReservationStatus.RESERVED){ inventoryRepo.release(res.sku,res.qty); res.status=ReservationStatus.RELEASED; reservationRepo.save(res);} }); }

  private Map<String,Object> parse(String body) throws Exception { return om.readValue(body, Map.class); }
  private boolean done(Map<String,Object> m, String c){ return processed.existsByEventIdAndConsumerName((String)m.get("eventId"), c); }
  private void mark(Map<String,Object> m, String c){ ProcessedEvent p=new ProcessedEvent(); p.eventId=(String)m.get("eventId"); p.consumerName=c; processed.save(p); }
}
