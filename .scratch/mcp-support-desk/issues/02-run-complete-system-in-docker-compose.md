# 02: Run the Complete System in Docker Compose

**What to build:** the complete first-slice system can run through Docker Compose, including the Spring Boot application and PostgreSQL, so later tickets preserve a one-command local demo path as features are added.

**Blocked by:** 01: Wire Spring WebMVC, PostgreSQL, and MCP

**Status:** resolved

- [x] Docker Compose includes a Spring Boot application service as well as PostgreSQL.
- [x] The application service builds from the repo source.
- [x] The application service connects to PostgreSQL over the Compose network, not `localhost`.
- [x] The application service waits for or tolerates PostgreSQL startup before serving requests.
- [x] Port 8080 is exposed for the Support Agent workspace and MCP endpoint.
- [x] `docker compose up --build` starts the complete first-slice system.
- [x] `/support` is reachable from the host after Compose startup.
- [x] `/mcp` responds to an MCP `initialize` request after Compose startup.

## Answer

Completed the Docker Compose runtime path for the first slice. The app service builds from the repository Dockerfile, runs alongside PostgreSQL, uses `jdbc:postgresql://postgres:5432/mcp_support_desk` on the Compose network, gates app startup on PostgreSQL health, and restarts on failure to tolerate transient database/DNS startup timing.

The default host ports remain `8080` for the app and `5432` for PostgreSQL. They can be overridden with `APP_PORT` and `POSTGRES_PORT` when another local service already owns the defaults, for example `APP_PORT=18080 docker compose up --build -d`.

Verified `./mvnw test`, `APP_PORT=18080 docker compose up --build -d`, `GET /support`, and a Streamable HTTP MCP `initialize` request to `POST /mcp`.
