# Free Text Agent Calls MCP Over HTTP

The server-side free-text agent will call the application's MCP tools through the real `/mcp` Streamable HTTP endpoint at runtime, with tests allowed to fake the MCP-client boundary. This proves the feature is genuinely interrogating MCP rather than bypassing the protocol through direct service calls, while keeping automated tests deterministic and free of live protocol or model dependencies.
