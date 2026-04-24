package com.company.ordermanagement.model.dto.response;

import com.company.ordermanagement.model.entity.Customer;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CustomerResponse {
    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Customer.CustomerTier tier;
    private final Instant createdAt;
}
