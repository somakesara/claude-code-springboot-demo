package com.company.ordermanagement.service.impl;

import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.mapper.CustomerMapper;
import com.company.ordermanagement.model.dto.request.CreateCustomerRequest;
import com.company.ordermanagement.model.dto.response.CustomerResponse;
import com.company.ordermanagement.model.entity.Customer;
import com.company.ordermanagement.model.event.CustomerCreatedEvent;
import com.company.ordermanagement.repository.CustomerRepository;
import com.company.ordermanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .tier(request.getTier() != null ? request.getTier() : Customer.CustomerTier.STANDARD)
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: customerId={}", saved.getId());

        eventPublisher.publishEvent(CustomerCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(Instant.now())
                .eventType("CUSTOMER_CREATED")
                .correlationId(UUID.randomUUID())
                .customerId(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .build());

        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAllActive().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomerTier(UUID customerId, String tier) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
        customer.setTier(Customer.CustomerTier.valueOf(tier.toUpperCase()));
        log.info("Customer tier updated: customerId={}, tier={}", customerId, tier);
        return customerMapper.toResponse(customerRepository.save(customer));
    }
}
