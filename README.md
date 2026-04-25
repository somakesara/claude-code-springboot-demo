# Claude Code Demo — Spring Boot Order Management Service

A realistic Spring Boot microservice used as the live demo codebase for the
**Claude Code for Spring Boot Engineers** session.

Audience members clone this repo, run Claude Code against it, and walk through
five hands-on SDLC activities.

---

## Branches

| Branch | Purpose |
|---|---|
| `master` | **Demo starting state** — clone this. Activity 1 files missing, Activity 2 bug present, Activity 3 review targets in place. |
| `solution` | Full working reference implementation. Switch here to show the expected end result after each activity. |

---

## Quick Start

**Prerequisites:** Java 17, 21, or 25. Maven 3.8+. No Docker required for the basic demo.

```bash
git clone https://github.com/somakesara/claude-code-springboot-demo
cd claude-code-springboot-demo

# Verify the build is green before the demo
mvn clean test

# Start the app (H2 in-memory, Kafka disabled — no external services needed)
mvn spring-boot:run

# Open Claude Code
claude
```

App runs at `http://localhost:8080`.  
Health check: `http://localhost:8080/actuator/health`

Demo credentials (HTTP Basic):

| Username | Password | Roles |
|---|---|---|
| `engineer` | `demo-pass` | USER |
| `admin` | `admin-pass` | USER, ADMIN |

---

## The 5 Activities

| # | Activity | Starting state on `master` | Prompt file |
|---|---|---|---|
| 1 | Feature Development | `CreateOrderRequest`, `OrderResponse`, `OrderMapper`, `createOrder()` all deleted — Claude builds from scratch | [activity1-feature-development.md](docs/prompts/activity1-feature-development.md) |
| 2 | Bug Fix | `PaymentServiceImpl.processRefund()` missing `@Transactional` — causes `LazyInitializationException` | [activity2-bug-fix.md](docs/prompts/activity2-bug-fix.md) |
| 3 | Code Review | `KafkaRetryConfig` + `RetryablePaymentService` have live bugs (hardcoded partition, no idempotency, PII log, field injection) | [activity3-code-review.md](docs/prompts/activity3-code-review.md) |
| 4 | Migration | Conceptual walkthrough using this codebase as the migration target reference | [activity4-migration.md](docs/prompts/activity4-migration.md) |
| 5 | Documentation | `InventoryController` has 8 endpoints, zero docs — Claude generates the full API reference | [activity5-documentation.md](docs/prompts/activity5-documentation.md) |

---

## Demo Flow — Per Activity

### Reset between activities

**Windows:**
```bat
scripts\reset-demo.bat 1
```

**Mac / Linux:**
```bash
chmod +x scripts/reset-demo.sh
./scripts/reset-demo.sh 1
```

Replace `1` with `2`, `3`, `4`, or `5`. The script kills any running instance on port 8080,
resets `master` to clean state, runs `mvn clean`, and starts the app.

### Manual reset (without the script)

```bash
git checkout master
git checkout -- .
mvn clean
mvn spring-boot:run
```

### Show the solution

```bash
git checkout solution
# open the relevant file, discuss with audience
git checkout master
git checkout -- .
```

---

## Activity Prompts (copy-paste ready)

### Activity 1 — Feature Development

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

**What to watch for:** Does Claude use `ApplicationEventPublisher` + `@TransactionalEventListener(AFTER_COMMIT)`
instead of calling `kafkaTemplate.send()` directly inside `@Transactional`?

---

### Activity 2 — Bug Fix

```
There's a NullPointerException in PaymentServiceImpl.processRefund() at line 87.
Stack trace:
  java.lang.NullPointerException at PaymentServiceImpl.java:87
  order.getLineItems() returns null for multi-item partially-shipped orders.

Investigate the root cause and fix it. Also scan the codebase for any other place
where lazy-loaded collections are accessed outside a @Transactional boundary.
```

**What to watch for:** Does Claude identify the missing `@Transactional` as the root cause
rather than suggesting `FetchType.EAGER`?

---

### Activity 3 — Code Review

```
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

Files to review: KafkaRetryConfig.java, RetryablePaymentService.java, README.md, CLAUDE.md.
```

**What to watch for:** Claude finds the implementation bugs (field injection, hardcoded partition,
PII log). The architectural risk it *misses* — duplicate charges when gateway has its own retry —
is the human judgment point.

---

### Activity 4 — Migration

```
Migrate this Spring Boot project from Java 11 / Spring Boot 2.7 to Java 17 / Spring Boot 3.2.

Work in phases. After each phase, compile and report errors before moving to the next phase.

Phase 1 — pom.xml: Update Java version to 17, Spring Boot parent to 3.2.4.
Update any dependency versions that changed in Spring Boot 3.x.

Phase 2 — javax → jakarta: Replace all javax.* imports with jakarta.*
EXCEPT: do NOT change javax.sql.*, javax.crypto.*, or javax.net.* — those are JDK packages,
not Jakarta EE packages.

Phase 3 — Security config: WebSecurityConfigurerAdapter was removed in Spring Security 6.
Migrate to SecurityFilterChain bean pattern. Count the antMatchers() calls in the old config —
your new requestMatchers() count MUST be equal. If counts differ, stop and report.

Phase 4 — Deprecated APIs: Fix any remaining deprecation warnings.
RestTemplate → RestClient. spring.security.oauth2 property key changes.

Phase 5 — Tests: Run mvn clean test. Fix any test compilation or runtime failures.

Phase 6 — Smoke test: Start the application and confirm /actuator/health returns 200.
```

**What to watch for:** Does Claude count `antMatchers()` correctly when migrating to
`requestMatchers()`? A mismatch is a security vulnerability (endpoints silently become public).

---

### Activity 5 — Documentation

```
Generate complete API documentation for InventoryController in Markdown format.

For each of the 8 endpoints, include:
- HTTP method and full path
- Description (what it does, not just what the method name says)
- Authentication: which role is required
- Path parameters and query parameters — name, type, required/optional, constraints
- Request body schema (if applicable) with a realistic example
- Response schema — full field list with types and descriptions
- Example response with realistic data (not placeholder UUIDs)
- Error responses — every possible status code with example body
- Business rules enforced by this endpoint

Output as a single Markdown file suitable for a developer portal.
```

**What to watch for:** Claude generates all 8 endpoints with accurate schemas derived from the code.
The engineer still needs to add the *why* — known limitations, cross-service dependencies,
and planned changes.

---

## Tests and JaCoCo Coverage

```bash
# Run tests + generate HTML coverage report
mvn clean test

# View report (after mvn test)
open target/site/jacoco/index.html          # Mac/Linux
start target\site\jacoco\index.html         # Windows
```

Starting branch coverage on `master` is intentionally low — Activity 1 asks Claude to write tests,
which grows the coverage metric visibly during the demo.

The `solution` branch enforces a 75% branch coverage gate: `mvn verify` fails below that threshold.

---

## Java Version Compatibility

```bash
mvn clean test                       # Java 17 (default)
mvn clean test -Djava.version=21     # Java 21
mvn clean test -Djava.version=25     # Java 25
```

Spring Boot 3.5.0 supports Java 17, 21, and 25.

---

## Project Structure

```
com.company.ordermanagement/
├── controller/
│   ├── CustomerController.java
│   ├── OrderController.java          ← Activity 1: POST endpoint missing
│   ├── InventoryController.java      ← Activity 5: 8 undocumented endpoints
│   └── PaymentController.java
├── service/impl/
│   ├── OrderServiceImpl.java         ← Activity 1: createOrder() missing
│   ├── PaymentServiceImpl.java       ← Activity 2: @Transactional missing on processRefund()
│   ├── RetryablePaymentService.java  ← Activity 3: field injection + PII log bugs
│   └── OrderEventPublisher.java      ← reference: correct @TransactionalEventListener pattern
├── config/
│   ├── KafkaRetryConfig.java         ← Activity 3: hardcoded partition 0, no idempotency
│   ├── SecurityConfig.java
│   └── KafkaConfig.java
├── model/
│   ├── entity/                       ← all extend BaseEntity, UUID PKs, soft delete
│   ├── dto/request/                  ← Activity 1: CreateOrderRequest missing
│   ├── dto/response/                 ← Activity 1: OrderResponse missing
│   └── event/
├── mapper/                           ← Activity 1: OrderMapper missing
└── repository/
```

---

## The CLAUDE.md Contract

[CLAUDE.md](CLAUDE.md) is loaded automatically by Claude Code at session start.

| Rule | Enforcement |
|---|---|
| BigDecimal for all monetary values | CLAUDE.md |
| Kafka events only after DB commit via `@TransactionalEventListener` | CLAUDE.md |
| Constructor injection only — no `@Autowired` on fields | CLAUDE.md + pre-commit hook |
| `@PreAuthorize` on every controller method | CLAUDE.md + pre-commit hook |
| No PII in logs (email, password, card number, etc.) | CLAUDE.md + pre-commit hook |
| `Optional.orElseThrow()` — never `Optional.get()` | CLAUDE.md |
| Testcontainers for repository tests — no H2 mocks | CLAUDE.md |

---

## API Reference

| Method | Path | Role | Notes |
|---|---|---|---|
| POST | `/api/v1/customers` | USER | Create customer |
| GET | `/api/v1/customers/{id}` | USER | Get customer |
| GET | `/api/v1/customers` | ADMIN | List all customers |
| PATCH | `/api/v1/customers/{id}/tier` | ADMIN | Update tier |
| POST | `/api/v1/orders` | USER | **Activity 1** — not implemented on `master` |
| GET | `/api/v1/orders/{id}` | USER | Get order |
| GET | `/api/v1/orders/customer/{id}` | USER | Orders by customer |
| DELETE | `/api/v1/orders/{id}` | USER | Cancel order |
| POST | `/api/v1/payments/{id}/refund` | ADMIN | **Activity 2** — bug present on `master` |
| GET | `/api/v1/inventory/sku/{sku}` | USER | Inventory by SKU |
| GET | `/api/v1/inventory/{id}` | USER | Inventory by ID |
| GET | `/api/v1/inventory` | USER | All inventory |
| GET | `/api/v1/inventory/low-stock` | USER | Below reorder threshold |
| GET | `/api/v1/inventory/warehouse/{loc}` | USER | By warehouse |
| POST | `/api/v1/inventory/{id}/adjust` | ADMIN | Adjust quantity |
| POST | `/api/v1/inventory/{id}/receive` | ADMIN | Receive stock |
| GET | `/api/v1/inventory/{id}/audit-log` | ADMIN | **Activity 5** — undocumented on `master` |
| GET | `/actuator/health` | none | Health check |
