# Activity 2 - Bug Fix Demo Prompt

## The Historical Symptom

Users reported that payment refunds crashed for orders with multiple line items.
It only affected partially shipped orders.

**Historical stack trace:**

```text
ERROR PaymentServiceImpl.java:87
  java.lang.NullPointerException: Cannot invoke method size() on null

  at com.company.ordermanagement.service.impl.PaymentServiceImpl.processRefund(PaymentServiceImpl.java:87)
  at com.company.ordermanagement.messaging.PaymentRefundRetryConsumer.handle(PaymentRefundRetryConsumer.java:45)
  at ...
```

---

## The Demo Prompt

Give Claude Code this prompt when walking through the historical bug:

```text
There's a NullPointerException in PaymentServiceImpl.processRefund() at line 87.
Stack trace:
  java.lang.NullPointerException at PaymentServiceImpl.java:87
  order.getLineItems() returns null for multi-item partially-shipped orders.

Investigate the root cause and fix it. Also scan the codebase for any other place
where lazy-loaded collections are accessed outside a @Transactional boundary.
```

---

## What Claude Code Should Do

1. Read `PaymentServiceImpl.java` and identify the missing transaction boundary in the historical version.
2. Read `PaymentRepository` and confirm `findByIdWithOrderAndLineItems` already uses `@EntityGraph`.
3. Read `Order.java` and confirm `lineItems` is `FetchType.LAZY`.
4. Diagnose the root cause: lazy collection access outside an active transaction in an async retry path.
5. Fix `PaymentServiceImpl` by adding `@Transactional` to `processRefund`.
6. Scan the codebase for similar lazy-loading access patterns.
7. Report any additional findings.

Note for this demo repo: `PaymentServiceImpl` already contains the fix. This
activity is a guided debugging walkthrough rather than a currently broken code
path.

---

## Root Cause Summary

The historical issue was not the repository query itself. The failure came from
touching `order.getLineItems()` after the original Hibernate session had ended.

The correct fix is:

```java
@Transactional
public Payment processRefund(UUID paymentId) {
    Payment payment = paymentRepository.findByIdWithOrderAndLineItems(paymentId)
            .orElseThrow(...);
    // lineItems are now accessed inside an active transaction
}
```

Avoid changing the association to `FetchType.EAGER`, because that would widen
the performance impact across normal code paths.

---

## Metrics to Track

| Metric | Target | How to measure |
|---|---|---|
| Root Cause Accuracy | > 80% | Did it identify lazy loading without hints? |
| Preventive Findings | Trending up | Related lazy-load risks found in the same session |
| Recurrence Rate | Trending down | Same bug type in the next 30-day window |
| MTTR | Baseline vs with Claude | Time from stack trace to merged fix |
