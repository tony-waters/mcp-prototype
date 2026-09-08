# 08: Add First-Slice Verification

**What to build:** automated verification proves the first slice is complete and protects the key refund-case behavior.

**Blocked by:** 06: Build Support Agent Workspace; 07: Expose MCP Tools Over Shared Services

**Status:** ready-for-agent

- [ ] Repository-backed service tests cover the eligible refund scenario.
- [ ] Repository-backed service tests cover the already-refunded Order scenario.
- [ ] Repository-backed service tests cover the out-of-window Order scenario.
- [ ] Repository-backed service tests cover the blocked Customer scenario.
- [ ] Repository-backed service tests cover watch Customer warning behavior.
- [ ] An MVC smoke test verifies the Support Agent workspace route renders.
- [ ] Direct Spring tests verify MCP tool bean methods are wired and callable.
- [ ] Verification does not require full protocol-level MCP client tests.
