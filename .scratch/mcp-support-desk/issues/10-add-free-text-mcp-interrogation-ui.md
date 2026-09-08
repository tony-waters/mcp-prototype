# 10: Add Free Text MCP Interrogation UI

**What to build:** the frontend can accept a Support Agent's free-text question and use MCP tools to answer it against the prototype's customer, order, and refund eligibility data.

**Blocked by:** 09: Add Prototype README

**Status:** resolved

- [ ] The Support Agent workspace includes a free-text input for asking refund-case questions.
- [ ] The free-text flow can call `lookupCustomer`, `getRecentOrders`, and `checkRefundEligibility` through MCP.
- [ ] The UI displays the assistant's answer and the MCP tool calls or source data used to produce it.
- [ ] The implementation defines where the AI agent runs: in the browser, in the Spring Boot app, or in a separate local service.
- [ ] The implementation defines how model credentials are configured and keeps them out of the repository.
- [ ] The implementation handles missing credentials with a clear non-crashing UI state.
- [ ] The implementation preserves the existing deterministic guided workflow.
- [ ] Automated verification covers the free-text request path without requiring paid model calls in the default test suite.

## Notes

This is a new requirement beyond the first slice. It changes the architecture because the original first-slice spec treats MCP as the external assistant boundary and says the UI must not call the MCP layer internally.

Before implementation, decide whether the prototype should:

- embed an AI agent in the Spring Boot app and have it call MCP tools,
- add a separate local AI-agent service that calls the MCP server,
- or keep the browser thin and proxy free-text requests to one of those backends.

The default automated tests should use deterministic fakes or mocks rather than making live paid model calls.

## Answer

Split the requirement into a dedicated follow-up slice at `.scratch/mcp-support-desk-free-text/`. The new slice includes a full spec, map, and implementation tickets for the agent boundary, AI configuration, MCP-backed question service, workspace UI, and documentation/verification.
