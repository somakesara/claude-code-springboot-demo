# Claude Code Demo — Spring Boot Order Management Service

A realistic Spring Boot microservice used as the live demo codebase for the
**Claude Code for Spring Boot Engineers** session.

This is the codebase your audience clones, runs Claude Code against, and uses
to walk through five hands-on SDLC activities.

---

## What This Is

A mid-scale Order Management microservice built with Java 17, Spring Boot 3.2,
PostgreSQL 15, and Kafka.

It intentionally includes:

- Correct patterns in the customer and order flows so Claude Code can learn repo conventions
- A deliberate bug in `PaymentServiceImpl.processRefund()` for Activity 2
- A flawed retry/error-handling design in `RetryablePaymentService` for Activity 3
- Undocumented inventory endpoints for the Activity 5 documentation exercise
- A `CLAUDE.md` that defines coding standards Claude Code enforces automatically

---

## The 5 Activities

| Activity | What Happens | Key Demo Point | Prompt |
|---|---|---|---|
| **1 - Feature Development** | Implement `POST /api/v1/orders` from scratch | BigDecimal, `@TransactionalEventListener`, configurable limit | [activity1-feature-development.md](docs/prompts/activity1-feature-development.md) |
| **2 - Bug Fixing** | Debug `processRefund()` crashing with `LazyInitializationException` | Lazy loading, transaction boundaries, codebase-wide scan | [activity2-bug-fix.md](docs/prompts/activity2-bug-fix.md) |
| **3 - Code Review** | Review the retry/error-handling design in `RetryablePaymentService` | AI finds implementation risks; engineers judge the trade-offs | [activity3-code-review.md](docs/prompts/activity3-code-review.md) |
| **4 - Migration** | Java 11 + Spring Boot 2.7 to Java 17 + Spring Boot 3.2 | `javax` to `jakarta`, security migration checks, compile/test loop | [activity4-migration.md](docs/prompts/activity4-migration.md) |
| **5 - Documentation** | Generate API docs for 8 undocumented endpoints | AI generates scaffold; engineer adds why, limitations, and dependencies | [activity5-documentation.md](docs/prompts/activity5-documentation.md) |

---

## Quick Start

### Prerequisites

- Java 17+ (only requirement for local demo)
- Docker (only needed for integration tests with Testcontainers)

### Run locally (Java only — no Docker, no Kafka)

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`.  
H2 console at `http://localhost:8080/h2-console`.

Demo auth uses HTTP Basic:
- `engineer` / `demo-pass`
- `admin` / `admin-pass`

### Run tests

```bash
mvn clean test
```

Unit tests run with H2. Integration tests use Testcontainers and require Docker.

### Start Claude Code on this project

```bash
cd claude-code-springboot-demo
claude
```

`CLAUDE.md` is loaded automatically at session start.

---

## Project Structure

```text
com.company.ordermanagement/
|-- controller/
|   |-- CustomerController.java
|   |-- OrderController.java
|   |-- InventoryController.java
|   `-- PaymentController.java
|
|-- service/
|   |-- impl/
|   |   |-- CustomerServiceImpl.java
|   |   |-- OrderServiceImpl.java
|   |   |-- OrderEventPublisher.java
|   |   |-- PaymentServiceImpl.java        ← Activity 2 bug here
|   |   |-- InventoryServiceImpl.java
|   |   `-- RetryablePaymentService.java   ← Activity 3 review target
|
|-- config/
|   |-- KafkaRetryConfig.java
|   |-- KafkaConfig.java
|   |-- SecurityConfig.java
|   `-- JpaConfig.java
|
|-- model/
|   |-- entity/
|   |-- dto/
|   `-- event/
|
|-- mapper/
|-- repository/
`-- exception/
```

---

## Database

Uses **H2 in-memory** by default — no setup required, starts with Java only.

Migrations in `src/main/resources/db/migration/h2/`:
- `V1__CreateSchema.sql` — full schema (customers, orders, inventory, payments)
- `V2__SeedDemoData.sql` — 3 demo customers + 4 inventory items

For production or integration tests, PostgreSQL 15 migrations are in `db/migration/postgresql/`.

---

## Reset Between Activities

```bash
scripts/reset-demo.bat 1    # Windows
scripts/reset-demo.sh  1    # Mac/Linux
```

Argument is the activity number (1–5). Stashes local changes, checks out `demo-baseline`, and starts the app.

---

## Activity 2 — Bug Detail

`PaymentServiceImpl.processRefund()` is missing `@Transactional`. This causes a
`LazyInitializationException` when `order.getLineItems()` is accessed outside an
active session.

The fix Claude Code should suggest:
- Add `@Transactional` to `processRefund()`
- Keep the `@EntityGraph` fetch (do not switch to `FetchType.EAGER`)

---

## The CLAUDE.md Contract

[CLAUDE.md](CLAUDE.md) defines the conventions Claude Code reads at session start.

| Rule | Enforcement |
|---|---|
| BigDecimal for all money | CLAUDE.md (guides the model) |
| `@TransactionalEventListener` after DB commit | CLAUDE.md (guides the model) |
| Constructor injection only — no `@Autowired` on fields | Pre-commit hook (hard block) |
| `@PreAuthorize` on every endpoint | Pre-commit hook (hard block) |
| No PII in logs | Pre-commit hook (hard block) |
| Testcontainers for integration tests | CLAUDE.md (guides the model) |

---

## API Reference

All endpoints require authentication. Role requirements:

| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/api/v1/customers` | USER | Create customer |
| GET | `/api/v1/customers/{id}` | USER | Get customer |
| GET | `/api/v1/customers` | ADMIN | List all customers |
| PATCH | `/api/v1/customers/{id}/tier` | ADMIN | Update tier |
| POST | `/api/v1/orders` | USER | Create order |
| GET | `/api/v1/orders/{id}` | USER | Get order |
| GET | `/api/v1/orders/customer/{customerId}` | USER | Orders by customer |
| DELETE | `/api/v1/orders/{id}` | USER | Cancel order |
| POST | `/api/v1/payments/{paymentId}/refund` | ADMIN | Process refund |
| GET | `/api/v1/inventory/sku/{sku}` | USER | Inventory by SKU |
| GET | `/api/v1/inventory/{id}` | USER | Inventory by ID |
| GET | `/api/v1/inventory` | USER | All inventory |
| GET | `/api/v1/inventory/low-stock` | USER | Below reorder threshold |
| GET | `/api/v1/inventory/warehouse/{loc}` | USER | By warehouse |
| POST | `/api/v1/inventory/{id}/adjust` | ADMIN | Adjust quantity |
| POST | `/api/v1/inventory/{id}/receive` | ADMIN | Receive stock |
| GET | `/api/v1/inventory/{id}/audit-log` | ADMIN | Audit history |
| GET | `/actuator/health` | none | Health check |
