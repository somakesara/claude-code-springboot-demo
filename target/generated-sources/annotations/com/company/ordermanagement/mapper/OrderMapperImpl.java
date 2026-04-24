package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Customer;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.model.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T06:32:00+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.customerId( orderCustomerId( order ) );
        orderResponse.createdAt( order.getCreatedAt() );
        orderResponse.id( order.getId() );
        orderResponse.lineItems( orderItemListToLineItemResponseList( order.getLineItems() ) );
        orderResponse.status( order.getStatus() );
        orderResponse.total( order.getTotal() );
        orderResponse.updatedAt( order.getUpdatedAt() );

        orderResponse.customerName( order.getCustomer().getFirstName() + ' ' + order.getCustomer().getLastName() );

        return orderResponse.build();
    }

    @Override
    public OrderResponse.LineItemResponse toLineItemResponse(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        OrderResponse.LineItemResponse.LineItemResponseBuilder lineItemResponse = OrderResponse.LineItemResponse.builder();

        lineItemResponse.id( item.getId() );
        lineItemResponse.productId( item.getProductId() );
        lineItemResponse.productName( item.getProductName() );
        lineItemResponse.productSku( item.getProductSku() );
        lineItemResponse.quantity( item.getQuantity() );
        lineItemResponse.unitPrice( item.getUnitPrice() );

        lineItemResponse.lineTotal( item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())) );

        return lineItemResponse.build();
    }

    private UUID orderCustomerId(Order order) {
        if ( order == null ) {
            return null;
        }
        Customer customer = order.getCustomer();
        if ( customer == null ) {
            return null;
        }
        UUID id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<OrderResponse.LineItemResponse> orderItemListToLineItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderResponse.LineItemResponse> list1 = new ArrayList<OrderResponse.LineItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toLineItemResponse( orderItem ) );
        }

        return list1;
    }
}
