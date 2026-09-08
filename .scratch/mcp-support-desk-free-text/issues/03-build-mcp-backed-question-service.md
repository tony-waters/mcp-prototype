# 03: Build MCP-Backed Question Service

**What to build:** a server-side service accepts one free-text support question and answers it by using the available MCP tools.

**Blocked by:** 01: Design Free Text Agent Boundary; 02: Add AI Configuration and Disabled State

**Status:** resolved

- [x] The service accepts a non-blank question.
- [x] The service constrains answers to refund-case support data.
- [x] Factual claims about Customers, Orders, refund eligibility, and refund amounts are grounded in MCP tool results.
- [x] The service can call `lookupCustomer`.
- [x] The service can call `getRecentOrders`.
- [x] The service can call `checkRefundEligibility`.
- [x] The service does not silently choose between duplicate Customer matches.
- [x] The service returns an answer and a tool trace.
- [x] Tests use deterministic fakes or mocks instead of a live paid model.

## Notes

For the first implementation, single-turn questions are enough. Persisted chat history is out of scope.

## Answer

Added `FreeTextQuestionService`, a provider-neutral `FreeTextQuestionInterpreter`, a Spring AI `ChatModel` interpreter for configured providers, and an MCP HTTP client boundary. The service validates blank questions, rejects unavailable model configuration, asks for an exact email when needed, avoids collapsing duplicate matches, chooses the latest delivered recent Order when no Order ID is supplied, checks refund eligibility, and returns a grounded answer plus compact MCP trace.
