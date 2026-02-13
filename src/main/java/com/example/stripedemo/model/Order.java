package com.example.stripedemo.model;

public class Order {
    private String id;
    private String product;
    private Long amount;
    private String currency;
    private String status;
    private String paymentIntentId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
}
