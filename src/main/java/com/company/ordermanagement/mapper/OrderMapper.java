package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(order.getCustomer().getFirstName() + ' ' + order.getCustomer().getLastName())")
    OrderResponse toResponse(Order order);

    @Mapping(target = "lineTotal", expression = "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    OrderResponse.LineItemResponse toLineItemResponse(OrderItem item);
}
