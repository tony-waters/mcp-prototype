# Use Shared Application Services Behind UI and MCP

The Spring MVC support UI and the MCP tools are separate adapters over the same application services. The UI will not call the MCP layer internally, because MCP is the integration boundary for external assistants and should not become the app's internal API; keeping both adapters thin preserves one place for refund-case rules and makes the demo architecture easier to test.
