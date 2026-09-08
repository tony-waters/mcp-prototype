# 06: Build Support Agent Workspace

**What to build:** a Support Agent can use a server-rendered workspace to search for a Customer, inspect recent Orders, and view refund eligibility for a selected Order.

**Blocked by:** 05: Build Refund Eligibility Rules

**Status:** resolved

- [x] The workspace supports exact email search for Customers.
- [x] The workspace displays Customer candidates when a search matches.
- [x] The workspace lets the Support Agent select a Customer.
- [x] The workspace displays recent Orders for the selected Customer.
- [x] The workspace lets the Support Agent select an Order.
- [x] The workspace displays refund eligibility and the rule breakdown.
- [x] The workspace shows a disabled `Create refund draft` button after eligibility is checked.
- [x] The UI uses Spring MVC and Thymeleaf, with no separate frontend build system.

## Answer

Replaced the placeholder workspace with a server-rendered Spring MVC and Thymeleaf flow. `/support` supports email search, candidate selection, recent Order display, Order selection, refund eligibility summary, rule breakdown, and the disabled `Create refund draft` control for the later slice.
