# 02: Run the Complete System in Docker Compose

**What to build:** the complete first-slice system can run through Docker Compose, including the Spring Boot application and PostgreSQL, so later tickets preserve a one-command local demo path as features are added.

**Blocked by:** 01: Wire Spring WebMVC, PostgreSQL, and MCP

**Status:** claimed

- [ ] Docker Compose includes a Spring Boot application service as well as PostgreSQL.
- [ ] The application service builds from the repo source.
- [ ] The application service connects to PostgreSQL over the Compose network, not `localhost`.
- [ ] The application service waits for or tolerates PostgreSQL startup before serving requests.
- [ ] Port 8080 is exposed for the Support Agent workspace and MCP endpoint.
- [ ] `docker compose up --build` starts the complete first-slice system.
- [ ] `/support` is reachable from the host after Compose startup.
- [ ] `/mcp` responds to an MCP `initialize` request after Compose startup.
