package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.CustomerResponse;
import com.company.ordermanagement.model.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
}
