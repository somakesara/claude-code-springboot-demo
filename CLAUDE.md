# CLAUDE.md — Order Management Service

## Project Overview
Enterprise Spring Boot microservice managing orders, customers, inventory, and payments.
Mid-scale: ~50 classes, PostgreSQL 15, Kafka for domain events.

## Stack
- Java 17, Spring Boot 3.2.x
- PostgreSQL 15 (production), H2 (local dev only)
- Apache Kafka (domain events)
- MapStruct (entity↔DTO mapping — never map manually)
- Lombok (@Data, @Builder, @RequiredArgsConstructor)
- Flyway (versioned migrations)
- JUnit 5 + Mockito + Testcontainers

## Package Structure
```
com.company.ordermanagement/
├── controller/         REST endpoints — @RestController only, no business logic
├── service/            interfaces + impl/ — all business logic lives here
├── repository/         Spring Data JPA — named params only
├── model/
│   ├── entity/         JPA @Entity classes — all extend BaseEntity
│   ├── dto/
│   │   ├── request/    Inbound validated DTOs
│   │   └── response/   Outbound response DTOs
│   └── event/          Kafka event POJOs — all extend BaseEvent
├── mapper/             MapStruct interfaces — one per entity
├── config/             SecurityConfig, KafkaConfig, KafkaRetryConfig
├── exception/          GlobalExceptionHandler + domain exceptions
└── util/               Shared utilities
```

## API Conventions (non-negotiable)
- All responses wrapped: `ResponseEntity<ApiResponse<T>>`
- All endpoints versioned: `/api/v1/`
- Success: 200 (GET), 201 (POST), 204 (DELETE)
- Validation failure: 400
- Not found: 404
- Business rule violation: 422
- Never return raw entities — always map to response DTOs via MapStruct

## Entity Conventions
- All entities extend BaseEntity (id: UUID, createdAt, updatedAt, createdBy)
- Soft delete via `isDeleted` boolean — never hard delete
- UUIDs for all primary keys
- All timestamps in UTC

## Business Rules (enforced in service layer)
- Order total must not exceed `${order.max-total:50000}` (BigDecimal, dollars)
- Use BigDecimal for all monetary values — never double or float
- Kafka events published ONLY after DB commit: use `ApplicationEventPublisher` + `@TransactionalEventListener`
- Never publish Kafka directly inside `@Transactional` methods

## Kafka Conventions
- Topic naming: `{service-name}.{entity}.{event-type}` (e.g. `order-service.order.created`)
- All events extend BaseEvent (eventId UUID, timestamp Instant, eventType String, correlationId UUID)
- Consumer groups: `{service-name}-{event-type}-group`
- DLQ suffix: `.dlq` (e.g. `order-service.order.created.dlq`)

## Code Style (violations block pre-commit)
- Constructor injection only — `@Autowired` on fields is banned
- `Optional.orElseThrow(ResourceNotFoundException::new)` — never `Optional.get()`
- `@PreAuthorize` on every controller method — no method without it
- No raw SQL string concatenation — named params or Criteria API only
- No `RestTemplate` — use `RestClient` (Spring 6+)
- No PII in logs: email, password, ssn, dob, phone, creditCard

## Testing Standards
- Repository tests: Testcontainers (real PostgreSQL) — no H2 for @DataJpaTest
- Service tests: Mockito unit tests — no Spring context
- Controller tests: @WebMvcTest with MockMvc
- Test naming: `methodName_scenario_expectedResult`
- Never mock what you can test with a real container

## Flyway
- File naming: `V{version}__{PascalCaseDescription}.sql`
- Never modify existing migrations
- Always test migrations run clean in Testcontainers
