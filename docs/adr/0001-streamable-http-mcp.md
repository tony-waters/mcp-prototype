# Use Streamable HTTP for the MCP Server

The prototype exposes its MCP server over HTTP from the Spring Boot WebMVC application, and will use Spring AI's Streamable HTTP protocol rather than SSE. SSE was considered because it is common in older MCP examples, but Spring AI 2.0 marks SSE as deprecated in favor of Streamable HTTP, so using Streamable HTTP keeps the prototype aligned with the current Spring Boot MCP path while still allowing local MCP clients to connect over a URL.
