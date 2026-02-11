package com.example.stripedemo.controller;

import com.example.stripedemo.service.PaymentService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void shouldRenderOrdersPageWithSampleData() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("sample-1001")));
    }

    @Test
    void shouldCreateOrderOnCheckout() throws Exception {
        PaymentIntent mockIntent = new PaymentIntent();
        mockIntent.setClientSecret("cs_test_123");
        when(paymentService.createPayment(anyLong(), eq("usd"), eq("Book"))).thenReturn(mockIntent);

        mockMvc.perform(post("/checkout")
                        .param("product", "Book")
                        .param("amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attribute("clientSecret", "cs_test_123"));
    }
}
