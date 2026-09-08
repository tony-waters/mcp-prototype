# 04: Build Customer Lookup and Recent Orders

**What to build:** a Support Agent or MCP tool can find Customers by exact email and retrieve recent Orders for a selected Customer.

**Blocked by:** 03: Seed Refund Case Data

**Status:** resolved

- [x] Customer lookup accepts an exact email address and returns a short candidate list.
- [x] Customer lookup does not silently choose between multiple matching Customers.
- [x] Recent Orders can be retrieved for a selected Customer.
- [x] Recent Orders include order ID, placed time, delivered time, status, total minor units, and currency.
- [x] The behavior is implemented in application services that can be reused by both UI and MCP adapters.

## Answer

Added repository-backed `CustomerLookupService` and `RecentOrderService`, with reusable repositories and domain records for Customers and Orders. Lookup returns all exact email matches up to a bounded candidate limit, including duplicate-email candidates, and recent Orders are sorted by most recent `placed_at`.
