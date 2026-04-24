# Software Engineer Productivity Framework - Demo Project

A realistic Spring Boot microservice used as the live demo codebase for the
Software Engineer Productivity Framework presentation.

This is the codebase your audience clones, runs Claude Code against, and uses
to walk through the five activities from the framework.

---

## What This Is

A mid-scale Order Management microservice built with Java 17, Spring Boot 3.2,
PostgreSQL 15, and Kafka.

It intentionally includes:

- Correct patterns in the customer and order flows so Claude Code can learn repo conventions
- A historical bug walkthrough in `PaymentServiceImpl.processRefund()` for Activity 2
- A controversial retry/error-handling design in `KafkaRetryConfig` for Activity 3
- Undocumented inventory endpoints for the Activity 5 documentation exercise
- A `CLAUDE.md` adapted from the PDF template for this concrete demo repo

---

## The 5 Activities

| Activity | What Happens | Key Demo Point | Prompt |
|---|---|---|---|
| **1 - Feature Development** | Implement `POST /api/v1/orders` from scratch | BigDecimal, `@TransactionalEventListener`, configurable limit | [activity1-feature-development.md](docs/prompts/activity1-feature-development.md) |
| **2 - Bug Fixing** | Guided analysis of the historical `processRefund()` bug | Lazy loading, transaction boundaries, codebase-wide scan | [activity2-bug-fix.md](docs/prompts/activity2-bug-fix.md) |
| **3 - Code Review** | Review the current retry/error-handling design | AI finds implementation risks; humans judge gateway/retry architecture | [activity3-code-review.md](docs/prompts/activity3-code-review.md) |
| **4 - Migration** | Java 11 + Spring Boot 2.7 to Java 17 + Spring Boot 3.2 | `javax` to `jakarta`, security migration checks, compile/test loop | [activity4-migration.md](docs/prompts/activity4-migration.md) |
| **5 - Documentation** | Generate API docs for 8 undocumented endpoints | AI generates scaffold; engineer adds why, limitations, and dependencies | [activity5-documentation.md](docs/prompts/activity5-documentation.md) |

---

## Project Structure

```text
com.company.ordermanagement/
|-- controller/
|   |-- CustomerController.java
|   |-- OrderController.java
|   `-- InventoryController.java
|
|-- service/
|   |-- impl/
|   |   |-- CustomerServiceImpl.java
|   |   |-- OrderServiceImpl.java
|   |   |-- OrderEventPublisher.java
|   |   |-- PaymentServiceImpl.java
|   |   `-- InventoryServiceImpl.java
|
|-- config/
|   |-- KafkaRetryConfig.java
|   |-- KafkaConfig.java
|   |-- SecurityConfig.java
|   `-- JpaConfig.java
|
|-- model/
|   |-- entity/
|   |   |-- BaseEntity.java
|   |   |-- Customer.java
|   |   |-- Order.java
|   |   |-- OrderItem.java
|   |   |-- InventoryItem.java
|   |   |-- InventoryAuditLog.java
|   |   `-- Payment.java
|   |-- dto/
|   |   |-- request/
|   |   `-- response/
|   `-- event/
|
|-- mapper/
|-- repository/
`-- exception/
```

---

## Quick Start

### Local dev

```bash
mvn spring-boot:run
```

API at `http://localhost:8080`.

Demo auth uses HTTP Basic:
- `engineer` / `demo-pass`
- `admin` / `admin-pass`

This keeps the local walkthrough simple. The framework document still assumes
JWT for production-grade services.

### Run tests

```bash
mvn clean test
```

Integration tests use Testcontainers and require Docker.

### Start Claude Code on this project

```bash
cd claude-code-springboot-demo
claude
```

`CLAUDE.md` is loaded automatically.

---

## Activity 2 Reference Scenario

`PaymentServiceImpl.processRefund()` now contains the correct fix:
`@Transactional`.

Activity 2 is a guided debugging walkthrough of the historical issue that led
to this implementation. The original failure mode was a lazy-loading problem in
an async retry path.

The applied fix was:
- Add `@Transactional` to `processRefund`
- Keep the repository fetch using `@EntityGraph`
- Do not switch to `FetchType.EAGER`

---

## The CLAUDE.md Contract

[CLAUDE.md](CLAUDE.md) contains the conventions Claude Code reads at session
start.

| Rule | Where Enforced |
|---|---|
| BigDecimal for money | CLAUDE.md (soft) |
| `@TransactionalEventListener` after DB commit | CLAUDE.md (soft) |
| Constructor injection only | Pre-commit hook (hard) |
| `@PreAuthorize` on every endpoint | Pre-commit hook (hard) |
| No PII in logs | Pre-commit hook (hard) |
| Testcontainers for integration tests | CLAUDE.md (soft) |

Soft constraints guide the model. Hard constraints are enforced regardless of
what the model remembers.

---

## API Reference

All application endpoints require authentication. Role requirements:

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
| GET | `/api/v1/inventory/sku/{sku}` | USER | Inventory by SKU |
| GET | `/api/v1/inventory/{id}` | USER | Inventory by ID |
| GET | `/api/v1/inventory` | USER | All inventory |
| GET | `/api/v1/inventory/low-stock` | USER | Below reorder threshold |
| GET | `/api/v1/inventory/warehouse/{loc}` | USER | By warehouse |
| POST | `/api/v1/inventory/{id}/adjust` | ADMIN | Adjust quantity |
| POST | `/api/v1/inventory/{id}/receive` | ADMIN | Receive stock |
| GET | `/api/v1/inventory/{id}/audit-log` | ADMIN | Audit history |
| GET | `/actuator/health` | none | Health check |
