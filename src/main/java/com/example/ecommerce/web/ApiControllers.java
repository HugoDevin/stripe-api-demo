package com.example.ecommerce.web;

import com.example.ecommerce.catalog.*;
import com.example.ecommerce.common.error.AppException;
import com.example.ecommerce.inventory.*;
import com.example.ecommerce.order.*;
import com.example.ecommerce.payment.*;
import com.example.ecommerce.reporting.SalesDailyFactRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api")
class CatalogController {
  private final ProductService service; private final InventoryRepository inventory; @Value("${stripe.publishable-key:}") String publishableKey;
  CatalogController(ProductService s, InventoryRepository i){service=s;inventory=i;}
  @GetMapping("/config") Map<String,Object> config(){ return Map.of("currency","USD","publishableKey",publishableKey == null ? "" : publishableKey); }
  @PostMapping("/products") Product create(@RequestBody Product p){ return service.create(p); }
  @GetMapping("/products") List<Product> list(){ return service.list(); }
  @GetMapping("/products/{sku}") Product get(@PathVariable String sku){ return service.get(sku); }
  @GetMapping("/inventory/{sku}") Inventory inv(@PathVariable String sku){ return inventory.findById(sku).orElseThrow(); }
  @PostMapping("/inventory/{sku}/adjust") Inventory adjust(@PathVariable String sku,@RequestParam int qty){
    Inventory i = inventory.findById(sku).orElseGet(()->{Inventory n=new Inventory();n.sku=sku;return n;});
    if (i.availableQty + qty < 0) throw new AppException(HttpStatus.CONFLICT,"negative stock"); i.availableQty += qty; return inventory.save(i);
  }
}

@RestController @RequestMapping("/api/orders")
class OrderController {
  private final OrderService service; private final OrderRepository orderRepository;
  OrderController(OrderService s, OrderRepository orderRepository){service=s;this.orderRepository=orderRepository;}
  @PostMapping Map<String,Object> create(@Valid @RequestBody CreateOrderRequest req){
    var o=service.create(req.customerEmail, req.items.stream().map(i->new OrderService.ItemRequest(i.sku,i.qty)).toList());
    return Map.of("orderId",o.id,"status",o.status,"totalAmount",o.totalAmount,"currency",o.currency);
  }
  @GetMapping List<OrderEntity> list(){ return orderRepository.findAll(); }
  @GetMapping("/{id}") OrderEntity get(@PathVariable UUID id){return service.get(id);}  
  record CreateOrderRequest(@Email String customerEmail, @NotEmpty List<Item> items){}
  record Item(@NotBlank String sku, @Min(1) int qty){}
}

@RestController @RequestMapping("/api/payments")
class PaymentController {
  private final PaymentService service; private final PaymentRepository repo;
  PaymentController(PaymentService s, PaymentRepository r){service=s;repo=r;}
  @PostMapping("/create") Map<String,Object> create(@RequestParam UUID orderId){ var p=service.createPayment(orderId); return Map.of("paymentId",p.id,"clientSecret",p.clientSecret); }
  @GetMapping("/{id}") PaymentEntity get(@PathVariable UUID id){ return repo.findById(id).orElseThrow(); }
}

@RestController @RequestMapping("/api/stripe") @Profile({"staging","prod"})
class StripeWebhookController {
  private final PaymentService paymentService; private final WebhookEventRepository webhookRepo; @Value("${stripe.webhook-secret:}") String secret;
  StripeWebhookController(PaymentService p, WebhookEventRepository w){paymentService=p;webhookRepo=w;}
  @PostMapping("/webhook")
  public void webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sig){
    try {
      Event e = Webhook.constructEvent(payload, sig, secret);
      if (webhookRepo.existsByProviderEventId(e.getId())) return;
      WebhookEventEntity we = new WebhookEventEntity(); we.providerEventId=e.getId(); we.type=e.getType(); we.payloadJson=payload; webhookRepo.save(we);
      PaymentIntent pi = (PaymentIntent) e.getDataObjectDeserializer().getObject().orElseThrow();
      String oid = pi.getMetadata().get("orderId");
      if ("payment_intent.succeeded".equals(e.getType())) paymentService.markSuccess(UUID.fromString(oid), e.getId());
      if ("payment_intent.payment_failed".equals(e.getType())) paymentService.markFailed(UUID.fromString(oid), e.getId());
    } catch (SignatureVerificationException ex){ throw new AppException(HttpStatus.BAD_REQUEST,"bad signature"); }
  }
}

@RestController @RequestMapping("/internal/payments") @Profile("dev-offline")
class DevPaymentSimulationController {
  private final PaymentService paymentService; @Value("${dev.internal-token}") String token;
  DevPaymentSimulationController(PaymentService p){paymentService=p;}
  private void check(String t){if(!token.equals(t)) throw new AppException(HttpStatus.UNAUTHORIZED,"bad token");}
  @PostMapping("/{orderId}/simulate-success") void success(@PathVariable UUID orderId, @RequestHeader("X-DEV-TOKEN") String t){check(t); paymentService.markSuccess(orderId, UUID.randomUUID().toString());}
  @PostMapping("/{orderId}/simulate-fail") void fail(@PathVariable UUID orderId, @RequestHeader("X-DEV-TOKEN") String t){check(t); paymentService.markFailed(orderId, UUID.randomUUID().toString());}
}

@RestController @RequestMapping("/api/reporting")
class ReportingController {
  private final SalesDailyFactRepository repo; ReportingController(SalesDailyFactRepository r){repo=r;}
  @GetMapping("/sales-daily") List<?> daily(@RequestParam LocalDate from,@RequestParam LocalDate to){ return repo.findByDateBetweenOrderByDate(from,to); }
}
