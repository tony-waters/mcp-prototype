# 06: Expose MCP Tools Over Shared Services

**What to build:** an external AI assistant can use MCP tools for Customer lookup, recent Orders, and refund eligibility, with the tool adapter calling the same application services as the Support Agent workspace.

**Blocked by:** 04: Build Refund Eligibility Rules

**Status:** ready-for-agent

- [ ] The MCP adapter exposes `lookupCustomer`.
- [ ] The MCP adapter exposes `getRecentOrders`.
- [ ] The MCP adapter exposes `checkRefundEligibility`.
- [ ] `checkRefundEligibility` requires both Customer ID and Order ID.
- [ ] MCP outputs use structured DTOs matching the first-slice spec.
- [ ] MCP tool methods call shared application services, consistent with ADR-0002.
- [ ] The UI does not call the MCP layer internally.
