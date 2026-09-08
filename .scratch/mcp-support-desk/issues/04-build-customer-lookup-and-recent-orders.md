# 04: Build Customer Lookup and Recent Orders

**What to build:** a Support Agent or MCP tool can find Customers by exact email and retrieve recent Orders for a selected Customer.

**Blocked by:** 03: Seed Refund Case Data

**Status:** ready-for-agent

- [ ] Customer lookup accepts an exact email address and returns a short candidate list.
- [ ] Customer lookup does not silently choose between multiple matching Customers.
- [ ] Recent Orders can be retrieved for a selected Customer.
- [ ] Recent Orders include order ID, placed time, delivered time, status, total minor units, and currency.
- [ ] The behavior is implemented in application services that can be reused by both UI and MCP adapters.
