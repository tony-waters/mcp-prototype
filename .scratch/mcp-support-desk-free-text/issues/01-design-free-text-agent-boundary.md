# 01: Design Free Text Agent Boundary

**What to build:** a concrete architecture decision for where the AI agent runs and how it interrogates MCP tools.

**Blocked by:** None

**Status:** todo

- [ ] Decide whether the agent calls `/mcp` over Streamable HTTP or uses an in-process MCP client abstraction.
- [ ] Define the server-side request and response model for a free-text question.
- [ ] Define the tool trace model shown to the Support Agent.
- [ ] Define the missing-credentials behavior.
- [ ] Record the decision in an ADR or the slice map.

## Notes

The default direction is a thin browser and a server-side Spring Boot agent. Browser-side model credentials are out of scope.
