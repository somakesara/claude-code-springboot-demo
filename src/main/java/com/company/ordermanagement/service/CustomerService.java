package com.company.ordermanagement.service;

import com.company.ordermanagement.model.dto.request.CreateCustomerRequest;
import com.company.ordermanagement.model.dto.response.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse getCustomer(UUID customerId);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse updateCustomerTier(UUID customerId, String tier);
}
