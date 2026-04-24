package com.company.ordermanagement.controller;

import com.company.ordermanagement.model.dto.request.CreateCustomerRequest;
import com.company.ordermanagement.model.dto.response.ApiResponse;
import com.company.ordermanagement.model.dto.response.CustomerResponse;
import com.company.ordermanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(customerService.createCustomer(request), "Customer created"));
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCustomer(customerId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAllCustomers()));
    }

    @PatchMapping("/{customerId}/tier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerTier(
            @PathVariable UUID customerId,
            @RequestParam String tier) {
        return ResponseEntity.ok(ApiResponse.ok(
                customerService.updateCustomerTier(customerId, tier), "Tier updated"));
    }
}
