# Activity 1 — Feature Development Demo Prompt

## The Task

Engineer gives Claude Code this single prompt:

```
Implement POST /api/v1/orders.

Requirements:
- Accept customerId (UUID) and lineItems (productId, productSku, productName, quantity, unitPrice)
- Validate: customerId not null, lineItems not empty, quantity 1–1000, unitPrice > 0
- Business rule: total must not exceed ${order.max-total} (configurable, default $50,000)
- Return 201 on success, 400 on validation failure, 404 if customer not found, 422 if total exceeded
- Publish an OrderCreatedEvent to Kafka ONLY after the DB transaction commits
- All monetary values must use BigDecimal — never double or float
- Follow all conventions in CLAUDE.md
```

---

## What Claude Code Does (correct behavior)

1. **Reads CLAUDE.md** — loads conventions (BigDecimal, @TransactionalEventListener, @PreAuthorize, constructor injection)
2. **Reads CustomerRepository** — finds `findById` method
3. **Creates CreateOrderRequest** — with `@NotEmpty` on lineItems, `@NotNull` on customerId, `@DecimalMin` on unitPrice
4. **Creates OrderServiceImpl** — uses `ApplicationEventPublisher.publishEvent()` inside @Transactional
5. **Creates OrderEventPublisher** — `@TransactionalEventListener(phase = AFTER_COMMIT)` sends to Kafka
6. **Creates OrderController** — `@PreAuthorize("hasRole('USER')")`, returns `ResponseEntity<ApiResponse<OrderResponse>>`
7. **Writes unit tests** — verifies total calculation, 404 case, 422 case, event published once

---

## Common Bugs Claude Code Makes (show these to the audience)

### Bug 1 — Kafka inside @Transactional
```java
// WRONG — message sent even if DB rolls back
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    Order saved = orderRepository.save(order);
    kafkaTemplate.send("order-service.order.created", event);  // BUG
    return mapper.toResponse(saved);
}
```

**Fix prompt:**
```
The Kafka publish should only fire after the DB transaction commits.
Use ApplicationEventPublisher + @TransactionalEventListener(phase = AFTER_COMMIT)
instead of calling kafkaTemplate.send() directly inside @Transactional.
```

---

### Bug 2 — double instead of BigDecimal
```java
// WRONG — floating-point precision errors
double total = lineItems.stream()
    .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
    .sum();
```

**Fix:** CLAUDE.md already says "Use BigDecimal for all monetary values". If Claude ignores this, the pre-commit hook won't catch it — it's a bug the engineer must spot in review.

---

### Bug 3 — hardcoded limit
```java
// WRONG — not configurable
if (total.compareTo(new BigDecimal("50000")) > 0) {
```

**Fix prompt:**
```
The max order total should be configurable via application properties.
Use @Value("${order.max-total:50000}") instead of a hardcoded literal.
```

---

## Metrics to Track

| Metric | Target | How to measure |
|---|---|---|
| AI Acceptance Rate | > 70% | Lines merged / lines generated |
| Quality Gate Pass Rate | > 85% | Pre-commit hooks + CI pass without manual fixes |
| Time to working feature | Baseline vs with Claude | Stopwatch from prompt to green CI |
