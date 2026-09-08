# MCP Support Desk

First runnable slice of a Spring Boot support desk prototype. It exposes the same refund-case application services through two adapters:

- a server-rendered Support Agent workspace at `/support`
- Spring AI MCP tools over Streamable HTTP at `/mcp`

The prototype is intentionally local and deterministic. It uses PostgreSQL seed data for realistic customer and order scenarios, but it does not issue real refunds or create refund drafts.

## Requirements

- Docker and Docker Compose
- Java 25, only needed when running Maven commands directly

## Run the Prototype

Start the complete local system:

```bash
docker compose up --build
```

Open the Support Agent workspace:

```text
http://localhost:8080/support
```

If port `8080` is already in use:

```bash
APP_PORT=18080 docker compose up --build
```

Then open:

```text
http://localhost:18080/support
```

Stop the system:

```bash
docker compose down
```

Remove the local database volume as well:

```bash
docker compose down --volumes
```

## Use the Workspace

1. Search for an exact customer email.
2. Select one of the matching customers.
3. Select an order from the recent orders table.
4. Read the refund eligibility summary and rule breakdown.

The `Create refund draft` button is intentionally disabled. Refund draft creation is outside this slice.

Useful seeded scenarios:

| Scenario | Email | Customer ID | Order ID | Expected result |
| --- | --- | --- | --- | --- |
| Eligible refund | `sam@example.com` | `cus_eligible` | `ord_eligible` | Eligible |
| Already refunded | `priya@example.com` | `cus_refunded` | `ord_refunded` | Ineligible |
| Outside refund window | `jon@example.com` | `cus_out_of_window` | `ord_out_of_window` | Ineligible |
| Blocked customer | `marta@example.com` | `cus_blocked` | `ord_blocked_customer` | Ineligible |
| Watch customer | `alex@example.com` | `cus_watch` | `ord_watch_customer` | Eligible with warning |
| Duplicate email lookup | `duplicate@example.com` | `cus_duplicate_a`, `cus_duplicate_b` | none | Multiple candidates |

## MCP Endpoint

The MCP server is exposed over Streamable HTTP:

```text
POST http://localhost:8080/mcp
```

Available tools:

- `lookupCustomer`
- `getRecentOrders`
- `checkRefundEligibility`

Example initialize request:

```bash
curl -i http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "initialize",
    "params": {
      "protocolVersion": "2025-03-26",
      "capabilities": {},
      "clientInfo": {
        "name": "local-curl",
        "version": "0.0.1"
      }
    }
  }'
```

The server may return an `Mcp-Session-Id` response header. Include that value on later MCP requests when present.

Send the initialized notification after a successful initialize response:

```bash
curl http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Mcp-Session-Id: <session-id>' \
  -d '{
    "jsonrpc": "2.0",
    "method": "notifications/initialized",
    "params": {}
  }'
```

List tools:

```bash
curl http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Mcp-Session-Id: <session-id>' \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/list",
    "params": {}
  }'
```

Check refund eligibility:

```bash
curl http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Mcp-Session-Id: <session-id>' \
  -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
      "name": "checkRefundEligibility",
      "arguments": {
        "customerId": "cus_eligible",
        "orderId": "ord_eligible"
      }
    }
  }'
```

## Configuration

Compose defaults:

| Variable | Default | Purpose |
| --- | --- | --- |
| `APP_PORT` | `8080` | Host port for the Spring Boot app |
| `POSTGRES_PORT` | `5432` | Host port for PostgreSQL |

Application environment:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/mcp_support_desk` | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `mcp_support_desk` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | `mcp_support_desk` | Database password |
| `REFUND_CLOCK_INSTANT` | `2026-09-08T12:00:00Z` | Fixed demo clock for repeatable seeded refund scenarios |

Set `REFUND_CLOCK_INSTANT=` or set `refund.clock.instant` to an empty value if you want the app to use the real system clock.

## Run Tests

```bash
./mvnw test
```

The test suite uses Testcontainers PostgreSQL, so Docker must be available.
