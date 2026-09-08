# MCP Support Desk First Slice Spec

## Goal

Build the first runnable slice of MCP Support Desk: a Spring Boot WebMVC application that exposes MCP tools over Streamable HTTP and provides a server-rendered Support Agent workspace for checking refund eligibility.

The prototype should prove that an external AI assistant can safely help a frontline Support Agent resolve refund cases by using bounded tools over realistic business data.

## Scope

In scope:

- Spring Boot WebMVC application.
- Spring AI MCP server using Streamable HTTP.
- PostgreSQL as the development database.
- Docker Compose as the local runtime for the complete first-slice system, including the Spring Boot app and PostgreSQL.
- Spring MVC + Thymeleaf Support Agent workspace.
- Shared application services used by both the UI and MCP tool adapter.
- Seeded refund-case data.
- Automated tests for refund eligibility, MVC wiring, and MCP tool bean methods.

Out of scope for this slice:

- Issuing real refunds.
- Creating Refund Drafts.
- Support Manager approval queue.
- Ticket history summary.
- Redis caching.
- Message queue integration.
- In-app LLM calls.
- Separate React/Vite frontend.

## Architecture

The application has two adapters over one application-service layer:

- Spring MVC + Thymeleaf UI for the human Support Agent.
- MCP tool beans for the external AI assistant.

The UI must not call the MCP layer internally. Both adapters call the same application services so domain rules stay in one place.

Use package boundaries that match the domain:

- `mcp`: MCP tool definitions and MCP DTOs.
- `support`: MVC controller and view model for the Support Agent workspace.
- `customer`: Customer domain, repository, and lookup service.
- `order`: Order domain, repository, and recent-order service.
- `refund`: refund eligibility domain and service.

## Domain Model

### Customer

A Customer has:

- `id`
- `name`
- `email`
- `status`
- `risk_level`

Risk levels:

- `normal`: no warning.
- `watch`: does not block eligibility, but returns a warning check.
- `blocked`: prevents refund eligibility.

### Order

An Order has:

- `id`
- `customer_id`
- `placed_at`
- `delivered_at`
- `status`
- `total_minor`
- `currency`
- `refunded_at`

Order statuses for this slice:

- `paid`
- `delivered`
- `refunded`
- `cancelled`

Money is represented as minor units plus a currency code. Do not use floating-point values for domain or persistence amounts.

## MCP Tools

### `lookupCustomer`

Finds Customers by exact email address.

Input:

```json
{
  "email": "sam@example.com"
}
```

Output:

```json
{
  "matches": [
    {
      "customerId": "cus_123",
      "name": "Sam Rivera",
      "email": "sam@example.com",
      "status": "active",
      "riskLevel": "normal"
    }
  ]
}
```

If more than one Customer matches, return a short candidate list. Do not pick silently.

### `getRecentOrders`

Returns recent Orders for a Customer.

Input:

```json
{
  "customerId": "cus_123",
  "limit": 5
}
```

Output:

```json
{
  "orders": [
    {
      "orderId": "ord_456",
      "placedAt": "2026-09-01T10:15:00Z",
      "deliveredAt": "2026-09-03T10:15:00Z",
      "status": "delivered",
      "totalMinor": 7999,
      "currency": "GBP"
    }
  ]
}
```

### `checkRefundEligibility`

Checks whether an Order is eligible for this refund workflow.

Input:

```json
{
  "customerId": "cus_123",
  "orderId": "ord_456"
}
```

Output:

```json
{
  "customerId": "cus_123",
  "orderId": "ord_456",
  "eligible": true,
  "reason": "Order was delivered within the refund window and has no previous refund.",
  "maxRefundMinor": 7999,
  "currency": "GBP",
  "checks": [
    {
      "name": "within_refund_window",
      "passed": true,
      "severity": "info",
      "message": "Order was delivered within the 30 day refund window."
    }
  ]
}
```

The tool must require both `customerId` and `orderId`, and must verify that the Order belongs to the Customer.

## Refund Eligibility Rules

An Order is eligible when all blocking checks pass:

- Order exists.
- Customer exists.
- Order belongs to the Customer.
- Customer is active.
- Customer risk level is not `blocked`.
- Order status is `delivered`.
- Order has `delivered_at`.
- Order was delivered within the configured refund window.
- Order has not already been refunded.

The refund window is calculated from `delivered_at`. Use a 30-day default.

`watch` risk level does not block eligibility. It should add a warning check to the result.

The result should include:

- final eligibility boolean
- human-readable reason
- max refundable amount in minor units
- currency
- passed/failed/warning checks

## UI

Build a single guided Refund Case workspace for the Support Agent.

The first slice UI should support:

- exact email search for a Customer
- displaying matching Customers
- selecting a Customer
- displaying recent Orders
- selecting an Order
- displaying refund eligibility result and rule breakdown
- showing a disabled `Create refund draft` button after eligibility, making it clear that draft creation belongs to a later slice

The UI should be server-rendered with Thymeleaf. Use small progressive JavaScript only if it materially improves the flow.

## Local Runtime

The complete first-slice system must run through Docker Compose. Compose should include:

- PostgreSQL.
- The Spring Boot application, built from the repo source.

The application container should connect to PostgreSQL over the Compose network. Host-local `localhost` database configuration can exist for direct `./mvnw spring-boot:run`, but the Compose path is the canonical demo path.

## Database Seed Data

Seed four refund-case scenarios:

- eligible refund
- already-refunded Order
- out-of-window Order
- blocked Customer

The seed data should be stable enough for tests and for a repeatable local demo.

## Tests

Required tests:

- Repository-backed service tests for each eligibility scenario.
- MVC smoke test for the Support Agent workspace.
- Direct Spring tests of MCP tool bean methods, verifying the MCP adapter calls the same application services as the UI path.

Full protocol-level MCP client tests are out of scope for this slice.

## Done

This slice is done when:

- Docker Compose starts PostgreSQL and the Spring Boot app locally.
- The Support Agent workspace can search for a Customer and check refund eligibility.
- The MCP tool bean methods are wired and callable in tests.
- Refund eligibility tests cover the seeded scenarios.
- MVC smoke test passes.
- MCP tool bean tests pass.
