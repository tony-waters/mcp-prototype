# 04: Add Free Text Workspace UI

**What to build:** the Support Agent workspace includes a free-text question form and renders the answer and MCP trace.

**Blocked by:** 03: Build MCP-Backed Question Service

**Status:** resolved

- [x] `/support` renders a free-text input and submit control.
- [x] Blank questions show validation feedback.
- [x] Missing credentials show a non-crashing disabled state.
- [x] Successful answers show the assistant response.
- [x] Successful answers show tool calls, arguments, and compact results.
- [x] Tool failures show an actionable error state.
- [x] The existing guided lookup/order/eligibility workflow remains available.
- [x] MVC tests cover the form and response states.

## Notes

This should remain Spring MVC + Thymeleaf. A separate React/Vite frontend is still out of scope.

## Answer

Extended the existing Thymeleaf workspace with an `Ask with AI` section, disabled no-credentials state, validation feedback, answer rendering, and a visible compact MCP trace table. Added `/support/free-text` POST handling while preserving the original guided lookup/order/eligibility workflow.
