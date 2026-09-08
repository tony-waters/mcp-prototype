# 08: Add First-Slice Verification

**What to build:** automated verification proves the first slice is complete and protects the key refund-case behavior.

**Blocked by:** 06: Build Support Agent Workspace; 07: Expose MCP Tools Over Shared Services

**Status:** resolved

- [x] Repository-backed service tests cover the eligible refund scenario.
- [x] Repository-backed service tests cover the already-refunded Order scenario.
- [x] Repository-backed service tests cover the out-of-window Order scenario.
- [x] Repository-backed service tests cover the blocked Customer scenario.
- [x] Repository-backed service tests cover watch Customer warning behavior.
- [x] An MVC smoke test verifies the Support Agent workspace route renders.
- [x] Direct Spring tests verify MCP tool bean methods are wired and callable.
- [x] Verification does not require full protocol-level MCP client tests.

## Answer

Added PostgreSQL-backed Spring integration tests using Testcontainers, covering Customer lookup, recent Orders, all required refund eligibility scenarios, MVC rendering, and direct MCP tool bean calls. Verified the full suite with `./mvnw test`. Also smoke-tested the Compose app with `/support`, MCP `initialize`, `tools/list`, and `tools/call` for `checkRefundEligibility`.
