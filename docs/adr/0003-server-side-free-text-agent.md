# Run the Free Text Agent Server-Side

The free-text assistant will run inside the Spring Boot application, not in the browser or a separate local service. This keeps model credentials out of the frontend, fits the current single-app prototype, and gives the implementation one backend boundary for model calls, MCP orchestration, disabled-state handling, and tests.
