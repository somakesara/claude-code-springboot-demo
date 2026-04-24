# Activity 3 - Code Review Demo Prompt

## The Review Scope

Review the retry and error-handling design already present in this demo service.

Primary files:
- `KafkaRetryConfig.java` - exponential backoff with DLQ routing
- `PaymentServiceImpl.java` - payment and refund logic affected by retry flows
- `README.md` - documents the architectural concern called out in the demo
- `CLAUDE.md` - conventions and guardrails the review should check against

---

## The Demo Prompt

Give Claude Code this prompt:

```text
Review the existing payment retry design in this repository.

Provide a structured review covering:

SUMMARY: What the current retry/error-handling design does in 2-3 sentences.

ARCHITECTURE: Does this fit the existing patterns? Any design concerns?

BUGS: Concrete bugs or risks with file:line references.

EDGE CASES: Scenarios the current design has not handled.

CONVENTION VIOLATIONS: Check against CLAUDE.md rules.

TEST GAPS: What's not tested that should be?

CONFIGURATION: Any configuration that should be externalized?

SECURITY: Any security concerns?

Files to review: KafkaRetryConfig.java, PaymentServiceImpl.java, README.md, CLAUDE.md.
```

---

## What Claude Code Finds (AI strength - systematic checking)

### Bugs and risks it catches:
- `KafkaRetryConfig` assumes the downstream payment gateway honors an idempotency or correlation key, but that contract is not enforced in code.
- DLQ routing is hardcoded to partition `0`, which is simple but creates an operational hot spot and should be called out in review.
- Retry behavior is configured, but there are no tests proving the backoff or DLQ behavior works as intended.

### Convention checks it catches:
- Retry behavior should be reviewed against the CLAUDE.md rule that Kafka publishing must not happen directly inside transactional service methods.
- Reviewers should verify that retry flows do not reintroduce entity leakage or broad exception handling patterns.

### Test gaps it finds:
- No test for retry backoff and dead-letter routing behavior.
- No test documenting replay behavior for `processRefund`.
- No integration test covering the intended async retry scenario.

---

## What Claude Code Misses (architectural judgment - human strength)

**The critical issue:** the payment gateway may already have built-in retry logic. This service adds a second retry layer at the consumer side.

When the gateway returns a transient error and the consumer retries:
1. Consumer sends payment attempt #1 -> gateway times out and retries internally.
2. Consumer backoff fires -> consumer sends payment attempt #2.
3. Gateway internal retry succeeds for attempt #1.
4. Both charges can go through -> duplicate payment risk.

**This is a system-contract decision, not just a code bug.** Claude Code can flag the risk, but a human reviewer still has to know the gateway's actual idempotency behavior and whether correlation IDs are honored end to end.

**Key point for the presentation:** AI review is strong at policy checks and implementation consistency. Human review is still needed for architecture and external integration semantics.

---

## Metrics to Track

| Metric | Target | How to measure |
|---|---|---|
| Review Coverage | > 90% | Issues found by AI / total issues identified in review |
| Time per Review | Baseline vs with Claude | Timer from prompt to review notes |
| False Positive Rate | < 15% | Comments dismissed as not real issues |
| Critical Miss Rate | Trending down | Material issues merged that AI did not flag |
