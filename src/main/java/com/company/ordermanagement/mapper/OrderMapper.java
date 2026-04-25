package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Order;
import org.springframework.stereotype.Component;

// Activity 1 — replace with a @Mapper MapStruct interface once OrderResponse fields are implemented
@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        return new OrderResponse();
    }
}
