package com.company.ordermanagement.controller;

import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.model.entity.Customer;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Activity 1 — add tests for POST /api/v1/orders here after implementing the endpoint

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean OrderService orderService;

    @Test
    @WithMockUser(roles = "USER")
    void getOrder_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        Order order = Order.builder()
                .customer(Customer.builder().firstName("A").lastName("B").build())
                .status(Order.OrderStatus.PENDING)
                .total(BigDecimal.TEN)
                .build();
        when(orderService.getOrderEntity(id)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrder_unknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getOrderEntity(id))
                .thenThrow(new ResourceNotFoundException("Order", id));

        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelOrder_validId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        Order order = Order.builder()
                .customer(Customer.builder().firstName("A").lastName("B").build())
                .status(Order.OrderStatus.CANCELLED)
                .total(BigDecimal.TEN)
                .build();
        when(orderService.cancelOrder(id)).thenReturn(order);

        mockMvc.perform(delete("/api/v1/orders/{id}", id).with(csrf()))
                .andExpect(status().isOk());
    }
}
