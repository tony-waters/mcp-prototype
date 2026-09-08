# 03: Seed Refund Case Data

**What to build:** the local database contains stable Customers and Orders for the first refund-case scenarios so the Support Agent workspace and MCP tools have realistic data to inspect.

**Blocked by:** 02: Run the Complete System in Docker Compose

**Status:** ready-for-agent

- [ ] The database schema stores Customers with status and Customer Risk Level.
- [ ] The database schema stores Orders with status, delivered time, refund time, total minor units, and currency.
- [ ] Seed data includes an eligible refund scenario.
- [ ] Seed data includes an already-refunded Order scenario.
- [ ] Seed data includes an out-of-window Order scenario.
- [ ] Seed data includes a blocked Customer scenario.
- [ ] Money is represented as minor units plus currency code.
