# 04: Add Free Text Workspace UI

**What to build:** the Support Agent workspace includes a free-text question form and renders the answer and MCP trace.

**Blocked by:** 03: Build MCP-Backed Question Service

**Status:** todo

- [ ] `/support` renders a free-text input and submit control.
- [ ] Blank questions show validation feedback.
- [ ] Missing credentials show a non-crashing disabled state.
- [ ] Successful answers show the assistant response.
- [ ] Successful answers show tool calls, arguments, and compact results.
- [ ] Tool failures show an actionable error state.
- [ ] The existing guided lookup/order/eligibility workflow remains available.
- [ ] MVC tests cover the form and response states.

## Notes

This should remain Spring MVC + Thymeleaf. A separate React/Vite frontend is still out of scope.
