# 03: Build MCP-Backed Question Service

**What to build:** a server-side service accepts one free-text support question and answers it by using the available MCP tools.

**Blocked by:** 01: Design Free Text Agent Boundary; 02: Add AI Configuration and Disabled State

**Status:** todo

- [ ] The service accepts a non-blank question.
- [ ] The service constrains answers to refund-case support data.
- [ ] Factual claims about Customers, Orders, refund eligibility, and refund amounts are grounded in MCP tool results.
- [ ] The service can call `lookupCustomer`.
- [ ] The service can call `getRecentOrders`.
- [ ] The service can call `checkRefundEligibility`.
- [ ] The service does not silently choose between duplicate Customer matches.
- [ ] The service returns an answer and a tool trace.
- [ ] Tests use deterministic fakes or mocks instead of a live paid model.

## Notes

For the first implementation, single-turn questions are enough. Persisted chat history is out of scope.
