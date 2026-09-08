# MCP Support Desk Free Text Slice

## Notes

- This slice extends the first MCP Support Desk slice with free-text interrogation from the frontend.
- The key architecture decision is to keep the browser thin and run the AI-agent boundary on the Spring Boot server, so model credentials are not exposed to the browser.
- The agent should interrogate MCP tools rather than bypassing the MCP layer for free-text answers.

## Decisions-so-far

- The free-text requirement was first captured as `.scratch/mcp-support-desk/issues/10-add-free-text-mcp-interrogation-ui.md`.
- This slice has its own spec at `.scratch/mcp-support-desk-free-text/spec.md`.
- The free-text assistant runs inside the Spring Boot application rather than in the browser or a separate local service. See `docs/adr/0003-server-side-free-text-agent.md`.
- At runtime, the server-side free-text agent calls the application's MCP tools through `/mcp` over Streamable HTTP. Tests may fake the MCP-client boundary. See `docs/adr/0004-free-text-agent-calls-mcp-over-http.md`.
- The free-text agent should use provider-neutral Spring AI abstractions rather than an OpenAI-only implementation path. See `docs/adr/0005-provider-neutral-free-text-agent.md`.
- Without configured model credentials, the workspace disables free-text submission and shows a clear unavailable state. See `docs/adr/0006-disable-free-text-without-model-credentials.md`.
- Free-text answers should show a compact MCP trace by default: tool name, arguments, status, and short result summary. See `docs/adr/0007-show-compact-mcp-trace.md`.
- The free-text assistant supports single-turn refund eligibility questions only for this slice. See `docs/adr/0008-single-turn-refund-eligibility-free-text.md`.
- The slice should bundle one default model provider while keeping application code behind Spring AI abstractions. See `docs/adr/0009-bundle-one-default-model-provider.md`.
- Anthropic is the bundled default live-model provider for the free-text assistant slice. See `docs/adr/0010-use-anthropic-as-default-model-provider.md`.
- The Anthropic demo path should use Spring AI's standard Anthropic configuration properties and environment variable mapping. See `docs/adr/0011-use-standard-spring-ai-anthropic-configuration.md`.
- Free-text answers must ground factual customer, order, refund eligibility, and refund amount claims in MCP tool results. See `docs/adr/0012-ground-free-text-answers-in-mcp-results.md`.

## Fog

- Confirm the exact Spring AI client APIs during implementation.
- Confirm the exact Spring AI Anthropic property and environment variable names during implementation.
