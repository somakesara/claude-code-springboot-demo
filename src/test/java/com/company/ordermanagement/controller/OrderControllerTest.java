package com.company.ordermanagement.controller;

import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Activity 1 — add tests for POST /api/v1/orders here after implementing the endpoint

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean OrderService orderService;

    @Test
    @WithMockUser(roles = "USER")
    void getOrder_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getOrder(id)).thenReturn(new OrderResponse());

        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrder_unknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getOrder(id))
                .thenThrow(new ResourceNotFoundException("Order", id));

        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelOrder_validId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.cancelOrder(id)).thenReturn(new OrderResponse());

        mockMvc.perform(delete("/api/v1/orders/{id}", id).with(csrf()))
                .andExpect(status().isOk());
    }
}
