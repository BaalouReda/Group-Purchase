# Group Purchase Platform - MVP

A Spring Boot application implementing a group purchasing system where customers can create or join group purchases, benefiting from progressive discounts as more participants join.

## User Stories

> **As a customer**, I can create or join a group purchase for a product.  
> Group purchases have a minimum and maximum number of participants and an expiration deadline.

> **As a customer**, the price decreases as more people join the group.  
> Price tiers are predefined, and the purchase is finalized only if minimum participation is met before the deadline.

---

## Technical Architecture

### Core Design Decisions

#### 1. Deadline Expiration with RabbitMQ Delayed Messages

Instead of polling or scheduled tasks, I chose RabbitMQ's delayed message mechanism to handle deadline expiration:

- When a group purchase is created, a delayed message is published with delay matching the deadline duration
- When the message is consumed (at deadline time), the system checks if minimum participation was reached
- If yes → status becomes `FINALIZED`; if no → status becomes `CANCELLED`

**Why this approach?**
- No database polling overhead
- Precise timing without cron drift
- Scales horizontally (multiple consumers can process expirations)
- Messages survive application restarts

#### 2. Real-Time Updates with Server-Sent Events (SSE)

Customers receive live updates when:
- A new group purchase is created
- A participant joins an existing group purchase
- Price tier changes due to participant count

**Implementation flow:**
```
[Service Layer] → Spring ApplicationEvent → [EventListener] → SSE Broadcaster → [Connected Clients]
```

This decouples business logic from notification concerns using Spring's event system.

#### 3. Price Tier System

Products have configurable price tiers based on participant thresholds:

| Threshold | Discount |
|-----------|----------|
| 5 participants | 10% off |
| 10 participants | 20% off |
| 20 participants | 30% off |

The `currentPrice` is dynamically calculated based on the current participant count.

---

## Database Schema

**PostgreSQL** with **Flyway** migrations:

- `V0_0_1__mvp_first_migration.sql` — DDL schema creation
- `R__data_generation.sql` — Repeatable migration for sample data

### Entity Relationships

```
Customer (1) ──creates──> (N) GroupPurchase
Customer (N) <──joins───> (N) GroupPurchase  [via Participation table]
Product (1) ──────────────> (N) GroupPurchase
Product (1) ──────────────> (N) PriceTier
```

---

## Project Structure

```
src/main/java/org/kata/grouppurchase/
├── config/                 # configuration 
│   └──  propertie/          # configuration properties
├── dao/                    # Entities and domain logic
├── dto/                    # Request/Response DTOs
├── enums/                  # enums Pojos
├── event/                  # event DTOS
├── helper/                 # helepr class's (Utilities)
├── listener/               # spring event listener & rabbit consumer
├── mapper/                 # DTO/DAO mapper's
├── repository/             # DAO repositories
├── service/
│   ├── Impl/               # service Layer implementation 
│   └── /                   # service Layer definition ( interfaces )
├── web/                   
│   ├──  Exception/         # Exception handling (GlobalExceptionHandler) 
│   └── /                   # controller's
```

---

## Prerequisites

- **JDK 25**
- **Maven 3.9+**
- **Docker Desktop** (or any Docker daemon) — for PostgreSQL and RabbitMQ

---

## Running the Application

### 1. Run the Application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. Access the Application

- **API Base URL:** `http://localhost:8080/kata`
- **RabbitMQ Management:** `http://localhost:15672` (rba/rba)
---

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Authenticate user |
| POST | `/auth/logout` | End session |
| GET | `/api/products` | List products (paginated) |
| GET | `/api/products/{id}` | Get product with price tiers |
| POST | `/api/group-purchase` | Create a group purchase |
| GET | `/api/group-purchase` | List group purchases (filter by status) |
| GET | `/api/group-purchase/{id}` | Get group purchase details |
| POST | `/api/group-purchase/{id}/join` | Join a group purchase |
| GET | `/sse/group-purchases` | Subscribe to real-time updates |
| GET | `/sse/status` | Get SSE connection stats |

---
## Postman Collection
A complete Postman collection is provided in the gitlab/ folder for API testing.
Files

```
gitlab/
├── group purchase.postman_collection.json   # API collection with all endpoints
└── dev.postman_environment.json             # Development environment variables
```

### Group Purchase Statuses

- `PENDING` — Open for participants, deadline not reached
- `FULL` — Maximum participants reached
- `FINALIZED` — Deadline passed with minimum participants met
- `CANCELLED` — Deadline passed without minimum participants

---

## Error Handling

The API uses **RFC 7807 Problem Details** format via `GlobalExceptionHandler`.

### Business Exceptions (GroupPurchaseException)

| Exception | HTTP Status | When |
|-----------|-------------|------|
| `productNotActive()` | 400 Bad Request | Creating group purchase for inactive product |
| `invalidParticipantRange()` | 400 Bad Request | min >= max participants |
| `alreadyJoined()` | 409 Conflict | Customer already in this group |
| `groupFull()` | 409 Conflict | Maximum participants reached |
| `deadlinePassed()` | 410 Gone | Attempting to join after deadline |
| `invalidStatus(status)` | 409 Conflict | Invalid operation for current status |

### Validation Errors

Returns field-level errors for invalid request payloads:

```json
{
  "type": "about:blank",
  "title": "Validation Error",
  "status": 400,
  "errors": {
    "minParticipants": "must be greater than 0",
    "deadline": "must be in the future"
  }
}
```
---
