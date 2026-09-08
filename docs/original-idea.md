# MCP Support Desk Prototype

## Summary

Build a Spring Boot WebMVC MCP server that gives an AI assistant safe, useful tools for investigating customer support issues over Streamable HTTP. The prototype should feel like a small slice of a real internal support platform: customer lookup, order history, ticket context, refund eligibility, and refund draft creation.

The goal is not to build a full CRM. The goal is to demonstrate how MCP can expose bounded business capabilities to an agent while keeping the domain logic, data access, and side effects inside a normal Spring Boot service.

## Demo Scenario

A support agent asks:

> A customer emailed about a missing refund. Can you look up their account, summarize recent tickets, check the order, and draft a refund request if they are eligible?

The assistant uses MCP tools exposed by the Spring Boot app to:

1. Find the customer by email.
2. Pull recent orders.
3. Review recent ticket history.
4. Check whether a specific order is eligible for a refund.
5. Create a draft refund request for human review.

## MCP Tools

### `lookupCustomer`

Finds customers by email address. If more than one customer matches, the tool returns a short candidate list rather than picking silently.

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

### `getRecentOrders`

Returns recent orders for a customer.

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
      "status": "delivered",
      "total": 79.99,
      "currency": "GBP"
    }
  ]
}
```

### `summarizeTicketHistory`

Summarizes recent support tickets for a customer. In this prototype, the summary is produced deterministically from stored ticket fields by application code; the Spring Boot app does not call an LLM to generate this summary.

Input:

```json
{
  "customerId": "cus_123",
  "limit": 10
}
```

Output:

```json
{
  "summary": "Customer contacted support twice about delayed delivery and once about refund timing.",
  "ticketCount": 3,
  "latestTicketId": "tic_789"
}
```

### `checkRefundEligibility`

Applies business rules to decide whether an order is eligible for refund.

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
  "orderId": "ord_456",
  "eligible": true,
  "reason": "Order was delivered within the refund window and has no previous refund.",
  "maxRefundAmount": 79.99,
  "currency": "GBP",
  "checks": [
    {
      "name": "within_refund_window",
      "passed": true,
      "message": "Order was placed within the 30 day refund window."
    }
  ]
}
```

### `createRefundDraft`

Creates a draft refund request. This should not issue money automatically.

Input:

```json
{
  "orderId": "ord_456",
  "reason": "Customer reports item was not received.",
  "amount": 79.99
}
```

Output:

```json
{
  "refundDraftId": "refdraft_001",
  "status": "pending_review",
  "requiresHumanApproval": true
}
```

## Spring Boot Shape

Use a conventional layered Spring Boot application:

- `mcp` package: MCP tool definitions and request/response DTOs
- `customer` package: customer lookup domain and repository
- `order` package: order history and refund eligibility rules
- `ticket` package: support ticket retrieval and summary service
- `refund` package: refund draft creation and event publishing

Keep the MCP layer thin. It should validate inputs, call application services, and return structured outputs. Business decisions belong in services such as `RefundEligibilityService`, not inside the MCP tool methods.

The MCP endpoint should use Spring AI's WebMVC Streamable HTTP support. The server-rendered UI should not call the MCP layer internally; the UI and MCP tools should be separate adapters over the same application services.

## Data Model

Core tables:

- `customers`
  - `id`
  - `name`
  - `email`
  - `status`
  - `risk_level`

- `orders`
  - `id`
  - `customer_id`
  - `placed_at`
  - `delivered_at`
  - `status`
  - `total_minor`
  - `currency`
  - `refunded_at`

- `support_tickets`
  - `id`
  - `customer_id`
  - `subject`
  - `body`
  - `status`
  - `created_at`

- `refund_drafts`
  - `id`
  - `order_id`
  - `reason`
  - `amount`
  - `status`
  - `created_at`

## Useful System Components

### PostgreSQL

Use PostgreSQL as the system of record for customers, orders, tickets, and refund drafts. This makes the prototype feel close to a production Spring Boot service.

### Redis

Use Redis for caching hot reads:

- customer lookup by email
- recent orders by customer ID
- refund eligibility results for short periods

This is useful because support workflows repeatedly inspect the same customer and order while handling a ticket.

### Message Queue

Use RabbitMQ or Kafka for refund draft events:

- `refund-draft.created`
- `refund-draft.reviewed`
- `refund-draft.rejected`

For the prototype, publishing `refund-draft.created` is enough. A simple consumer can log the event or update a mock review queue.

## Business Rules

Initial refund eligibility rules:

- Order must exist.
- Order must belong to an active customer.
- Order must be delivered.
- Order must be within a configurable refund window, for example 30 days from `delivered_at`.
- Order must not already be refunded.
- Customer risk level must not be `blocked`.
- Customer risk level `watch` does not block eligibility, but should produce a warning check.
- Refund amount cannot exceed the order total.

These rules give the agent something meaningful to ask about while keeping sensitive action behind a draft-and-review boundary.

## First Build Slice

Build this first:

1. Spring Boot app with MCP server support.
2. Spring MVC + Thymeleaf guided refund-case workspace.
3. PostgreSQL schema and seed data.
4. `lookupCustomer`, `getRecentOrders`, and `checkRefundEligibility`.
5. Integration tests for the refund eligibility rules.
6. A sample prompt/script showing the assistant using the tools.

Seed the first database with four refund-case scenarios:

- eligible refund
- already-refunded order
- out-of-window order
- blocked Customer

The first slice is done when the app runs locally with Docker PostgreSQL, the Support Agent workspace can search for a Customer and check refund eligibility, MCP tool bean methods are wired to the same application services as the UI, and the automated tests pass.

Then add:

1. `summarizeTicketHistory`.
2. `createRefundDraft`.
3. Redis caching.
4. Queue event publishing.

## Why This Prototype Works

This is a good MCP demo because the agent is not just reading static data. It is moving through a real workflow with business constraints:

- It has to identify the right customer.
- It has to inspect related records.
- It has to apply domain rules.
- It can prepare an action, but not execute a sensitive financial change directly.

That separation makes the prototype useful for showing how MCP can connect an AI assistant to production-style systems without handing the assistant unrestricted access.
