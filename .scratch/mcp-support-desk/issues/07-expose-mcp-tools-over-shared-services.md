# 07: Expose MCP Tools Over Shared Services

**What to build:** an external AI assistant can use MCP tools for Customer lookup, recent Orders, and refund eligibility, with the tool adapter calling the same application services as the Support Agent workspace.

**Blocked by:** 05: Build Refund Eligibility Rules

**Status:** resolved

- [x] The MCP adapter exposes `lookupCustomer`.
- [x] The MCP adapter exposes `getRecentOrders`.
- [x] The MCP adapter exposes `checkRefundEligibility`.
- [x] `checkRefundEligibility` requires both Customer ID and Order ID.
- [x] MCP outputs use structured DTOs matching the first-slice spec.
- [x] MCP tool methods call shared application services, consistent with ADR-0002.
- [x] The UI does not call the MCP layer internally.

## Answer

Added the `SupportDeskTools` MCP adapter with `lookupCustomer`, `getRecentOrders`, and `checkRefundEligibility` methods annotated for Spring AI MCP. The generated MCP schemas require the expected inputs, including both IDs for refund eligibility. The adapter returns structured DTOs and delegates to the same application services used by the MVC workspace.
