# 01: Design Free Text Agent Boundary

**What to build:** a concrete architecture decision for where the AI agent runs and how it interrogates MCP tools.

**Blocked by:** None

**Status:** resolved

- [x] Decide whether the agent calls `/mcp` over Streamable HTTP or uses an in-process MCP client abstraction.
- [x] Define the server-side request and response model for a free-text question.
- [x] Define the tool trace model shown to the Support Agent.
- [x] Define the missing-credentials behavior.
- [x] Record the decision in an ADR or the slice map.

## Notes

The default direction is a thin browser and a server-side Spring Boot agent. Browser-side model credentials are out of scope.

## Answer

Added a server-side free-text boundary in the Spring Boot app. `FreeTextQuestionService` accepts a single question and returns `FreeTextQuestionResponse`, `FreeTextQuestionStatus`, and compact `FreeTextToolTrace` values. Runtime MCP calls go through `HttpFreeTextMcpClient` against `/mcp`; tests use fakes at the `FreeTextMcpClient` boundary. ADRs 0003-0008 record the settled architecture decisions.
