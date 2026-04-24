package com.company.ordermanagement.model.event;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@SuperBuilder
public abstract class BaseEvent {
    private final UUID eventId;
    private final Instant timestamp;
    private final String eventType;
    private final UUID correlationId;
}
