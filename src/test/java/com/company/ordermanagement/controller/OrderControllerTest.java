package com.company.ordermanagement.controller;

import com.company.ordermanagement.exception.OrderTotalExceededException;
import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean OrderService orderService;

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_validRequest_returns201() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .id(UUID.randomUUID())
                .status(Order.OrderStatus.PENDING)
                .total(new BigDecimal("49.95"))
                .lineItems(List.of())
                .build();
        when(orderService.createOrder(any())).thenReturn(response);

        String body = """
                {
                  "customerId": "%s",
                  "lineItems": [{
                    "productId": "%s",
                    "productSku": "WIDGET-001",
                    "productName": "Standard Widget",
                    "quantity": 5,
                    "unitPrice": 9.99
                  }]
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void createOrder_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_emptyLineItems_returns400() throws Exception {
        String body = """
                {"customerId": "%s", "lineItems": []}
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_unknownCustomer_returns404() throws Exception {
        when(orderService.createOrder(any()))
                .thenThrow(new ResourceNotFoundException("Customer", UUID.randomUUID()));

        String body = """
                {"customerId": "%s", "lineItems": [{
                  "productId": "%s", "productSku": "SKU-1", "productName": "P",
                  "quantity": 1, "unitPrice": 10.00
                }]}
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_totalExceeded_returns422() throws Exception {
        when(orderService.createOrder(any()))
                .thenThrow(new OrderTotalExceededException(
                        new BigDecimal("60000"), new BigDecimal("50000")));

        String body = """
                {"customerId": "%s", "lineItems": [{
                  "productId": "%s", "productSku": "EXP-1", "productName": "Expensive",
                  "quantity": 1, "unitPrice": 60000.00
                }]}
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrder_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getOrder(id)).thenReturn(
                OrderResponse.builder().id(id).status(Order.OrderStatus.CONFIRMED).build());

        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelOrder_validId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.cancelOrder(id)).thenReturn(
                OrderResponse.builder().id(id).status(Order.OrderStatus.CANCELLED).build());

        mockMvc.perform(delete("/api/v1/orders/{id}", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }
}
