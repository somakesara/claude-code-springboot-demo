package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.CustomerResponse;
import com.company.ordermanagement.model.entity.Customer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T06:32:00+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerResponse toResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerResponse.CustomerResponseBuilder customerResponse = CustomerResponse.builder();

        customerResponse.createdAt( customer.getCreatedAt() );
        customerResponse.email( customer.getEmail() );
        customerResponse.firstName( customer.getFirstName() );
        customerResponse.id( customer.getId() );
        customerResponse.lastName( customer.getLastName() );
        customerResponse.tier( customer.getTier() );

        return customerResponse.build();
    }
}
