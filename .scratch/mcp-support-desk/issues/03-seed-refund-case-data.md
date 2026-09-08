# 03: Seed Refund Case Data

**What to build:** the local database contains stable Customers and Orders for the first refund-case scenarios so the Support Agent workspace and MCP tools have realistic data to inspect.

**Blocked by:** 02: Run the Complete System in Docker Compose

**Status:** resolved

- [x] The database schema stores Customers with status and Customer Risk Level.
- [x] The database schema stores Orders with status, delivered time, refund time, total minor units, and currency.
- [x] Seed data includes an eligible refund scenario.
- [x] Seed data includes an already-refunded Order scenario.
- [x] Seed data includes an out-of-window Order scenario.
- [x] Seed data includes a blocked Customer scenario.
- [x] Money is represented as minor units plus currency code.

## Answer

Added Flyway migration `V2__refund_case_schema_and_seed.sql` with `customers` and `customer_orders` tables. Seed data covers eligible, already-refunded, out-of-window, blocked Customer, watch Customer, duplicate-email Customer lookup, and cancelled Order scenarios. Order totals are stored as `total_minor` plus a three-letter `currency`.
