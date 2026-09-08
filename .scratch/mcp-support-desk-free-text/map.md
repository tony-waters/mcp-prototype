# MCP Support Desk Free Text Slice

## Notes

- This slice extends the first MCP Support Desk slice with free-text interrogation from the frontend.
- The key architecture decision is to keep the browser thin and run the AI-agent boundary on the Spring Boot server, so model credentials are not exposed to the browser.
- The agent should interrogate MCP tools rather than bypassing the MCP layer for free-text answers.

## Decisions-so-far

- The free-text requirement was first captured as `.scratch/mcp-support-desk/issues/10-add-free-text-mcp-interrogation-ui.md`.
- This slice has its own spec at `.scratch/mcp-support-desk-free-text/spec.md`.

## Fog

- Confirm the exact Spring AI client APIs during implementation.
- Decide the default model name and provider-specific environment variables.
- Decide whether the server-side agent calls the local `/mcp` HTTP endpoint or an in-process MCP client abstraction that still exercises the MCP adapter contract.
