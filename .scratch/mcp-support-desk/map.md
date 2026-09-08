# MCP Support Desk

## Notes

- First slice covers a local support desk prototype backed by PostgreSQL, Spring MVC, Thymeleaf, and Spring AI MCP.

## Decisions-so-far

- Tickets 03-08 are resolved together in commit `b1e15de`, adding stable refund case data, shared customer/order/refund services, a server-rendered workspace, MCP tools over the same services, and first-slice verification. See `.scratch/mcp-support-desk/issues/03-seed-refund-case-data.md`, `.scratch/mcp-support-desk/issues/04-build-customer-lookup-and-recent-orders.md`, `.scratch/mcp-support-desk/issues/05-build-refund-eligibility-rules.md`, `.scratch/mcp-support-desk/issues/06-build-support-agent-workspace.md`, `.scratch/mcp-support-desk/issues/07-expose-mcp-tools-over-shared-services.md`, and `.scratch/mcp-support-desk/issues/08-add-first-slice-verification.md`.
- Ticket 09 is resolved with a top-level README that documents the local demo path, workspace walkthrough, seeded scenarios, MCP endpoint usage, configuration, and test command. See `.scratch/mcp-support-desk/issues/09-add-prototype-readme.md` and `README.md`.

## Fog

- Future slices still need refund draft creation and any full protocol-level MCP client coverage the project decides to add.
