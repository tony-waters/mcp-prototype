# 06: Build Support Agent Workspace

**What to build:** a Support Agent can use a server-rendered workspace to search for a Customer, inspect recent Orders, and view refund eligibility for a selected Order.

**Blocked by:** 05: Build Refund Eligibility Rules

**Status:** ready-for-agent

- [ ] The workspace supports exact email search for Customers.
- [ ] The workspace displays Customer candidates when a search matches.
- [ ] The workspace lets the Support Agent select a Customer.
- [ ] The workspace displays recent Orders for the selected Customer.
- [ ] The workspace lets the Support Agent select an Order.
- [ ] The workspace displays refund eligibility and the rule breakdown.
- [ ] The workspace shows a disabled `Create refund draft` button after eligibility is checked.
- [ ] The UI uses Spring MVC and Thymeleaf, with no separate frontend build system.
