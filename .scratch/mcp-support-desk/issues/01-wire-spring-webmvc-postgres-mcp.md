# 01: Wire Spring WebMVC, PostgreSQL, and MCP

**What to build:** the application can start as a Spring Boot WebMVC service with Thymeleaf support, connect to local Docker PostgreSQL, and expose the Spring AI MCP server using Streamable HTTP.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] The application includes the dependencies needed for Spring WebMVC, Thymeleaf, PostgreSQL access, database migrations or schema initialization, tests, and Spring AI MCP server support.
- [x] A local Docker PostgreSQL setup is available for development.
- [x] Application configuration connects to PostgreSQL in local development.
- [x] The MCP server is configured for WebMVC Streamable HTTP, consistent with ADR-0001.
- [x] The app starts successfully against the local PostgreSQL database.

## Answer

Added the Spring Boot WebMVC, Thymeleaf, JDBC, Flyway, PostgreSQL, and Spring AI MCP WebMVC dependencies. Added Docker Compose PostgreSQL 17, local datasource/MCP configuration, a baseline Flyway migration, and a placeholder Support Agent workspace route/template. Verified `./mvnw test`, Docker PostgreSQL health, `/support`, and MCP `initialize` over `/mcp` with Streamable HTTP headers.
