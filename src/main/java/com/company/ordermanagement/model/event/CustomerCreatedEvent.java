package com.company.ordermanagement.model.event;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
public class CustomerCreatedEvent extends BaseEvent {
    private final UUID customerId;
    private final String firstName;
    private final String lastName;
}
