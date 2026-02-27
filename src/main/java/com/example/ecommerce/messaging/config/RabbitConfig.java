package com.example.ecommerce.messaging.config;

import java.util.Map;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
  @Bean TopicExchange domainExchange(){ return new TopicExchange("domain.events"); }
  private Queue q(String n){ return QueueBuilder.durable(n).withArguments(Map.of("x-dead-letter-exchange", n+".dlx")).build(); }
  @Bean Queue receiptQueue(){return q("receipt.queue");}
  @Bean Queue fulfillmentQueue(){return q("fulfillment.queue");}
  @Bean Queue notificationQueue(){return q("notification.queue");}
  @Bean Queue accountingQueue(){return q("accounting.queue");}
  @Bean Queue reportingQueue(){return q("reporting.queue");}
  @Bean Queue inventoryQueue(){return q("inventory.queue");}
  @Bean Binding b1(){return BindingBuilder.bind(receiptQueue()).to(domainExchange()).with("payment.succeeded");}
  @Bean Binding b2(){return BindingBuilder.bind(fulfillmentQueue()).to(domainExchange()).with("payment.succeeded");}
  @Bean Binding b3(){return BindingBuilder.bind(notificationQueue()).to(domainExchange()).with("payment.failed");}
  @Bean Binding b4(){return BindingBuilder.bind(accountingQueue()).to(domainExchange()).with("payment.succeeded");}
  @Bean Binding b5(){return BindingBuilder.bind(reportingQueue()).to(domainExchange()).with("payment.succeeded");}
  @Bean Binding b6(){return BindingBuilder.bind(inventoryQueue()).to(domainExchange()).with("payment.*");}
}
