# MCP Support Desk Free Text Slice Spec

## Goal

Add a free-text assistant experience to MCP Support Desk so a Support Agent can ask a natural-language refund-case question from the frontend and receive an answer produced by interrogating the prototype's MCP tools.

The slice should prove the end-to-end MCP value proposition inside the demo: a human asks in plain English, an AI agent chooses bounded MCP tools, and the UI shows both the answer and the evidence used.

## Scope

In scope:

- A free-text question box in the existing Spring MVC + Thymeleaf Support Agent workspace.
- A server-side AI agent boundary in the Spring Boot app.
- MCP client calls from the agent boundary to the app's own MCP server tools.
- Tool-use trace display in the UI.
- Configuration for model credentials through environment variables only.
- A deterministic no-credentials state for local demos and tests.
- Automated tests that do not require paid model calls.
- README updates showing how to run the slice with and without model credentials.

Out of scope:

- Browser-side model credentials.
- Persisting chat history.
- Multi-turn conversation memory beyond one submitted question.
- Real refund creation.
- Replacing the existing guided deterministic workflow.
- Full production authentication or authorization.
- Full protocol-level coverage for every MCP transport edge case.

## Architecture

The browser remains thin. It submits a free-text question to the Spring Boot application, and the Spring Boot application owns the AI-agent boundary.

Preferred runtime flow:

1. Support Agent opens `/support`.
2. Support Agent enters a question such as `Can Sam Rivera's latest delivered order be refunded?`.
3. Browser submits the question to a Spring MVC endpoint.
4. Server-side agent receives the question.
5. Agent calls MCP tools over Streamable HTTP:
   - `lookupCustomer`
   - `getRecentOrders`
   - `checkRefundEligibility`
6. Server returns an answer plus a trace of tool calls and relevant source data.
7. UI renders the answer without removing the existing guided workflow.

The server-side agent may use an LLM when credentials are configured. Without credentials, it must return a clear disabled or demo-only state rather than crashing.

## Model and Cost Requirements

- Model credentials must be configured through environment variables and must not be committed.
- The UI must clearly indicate when free-text AI is unavailable because credentials are missing.
- The default `docker compose up --build` path must still run without paid model calls.
- Automated tests must use deterministic fakes, mocks, or a local rule-based test double rather than a live paid model.
- Documentation must state that AI provider usage may incur external costs only when credentials are configured and free-text questions are submitted.

## UI Requirements

The Support Agent workspace should include:

- a free-text question input
- a submit control
- loading, success, missing-credentials, validation-error, and tool-error states
- the assistant's answer
- a readable trace of MCP tools called, arguments used, and summarized results

The existing guided workflow must remain usable:

- exact email search
- Customer selection
- recent Orders
- Order selection
- refund eligibility rule breakdown

## Agent Behavior

The agent should be constrained to the available MCP tools and the refund-case workflow.

It should:

- ask for an email address when the question does not identify a Customer strongly enough
- avoid silently choosing between duplicate Customer matches
- use `getRecentOrders` before checking eligibility if the Order ID is not explicit
- require both Customer ID and Order ID before calling `checkRefundEligibility`
- present eligibility answers with the reason, max refundable amount, currency, and failed or warning checks
- avoid claiming that a refund has been created

## MCP Tool Trace

Each free-text response should expose enough trace information for a human Support Agent to audit the answer:

- tool name
- arguments
- success or failure status
- compact result summary

The trace is user-facing diagnostic evidence, not hidden debug logs.

## Local Runtime

The first-slice Docker Compose path remains canonical. This slice may add environment variables for AI configuration, but Compose must still start successfully when they are absent.

## Tests

Required tests:

- free-text endpoint rejects blank questions
- missing model credentials produce a clear non-crashing response
- agent orchestration calls the expected MCP tools for a known eligible refund question
- duplicate Customer matches are not silently collapsed
- UI renders the free-text form and response states
- default test suite avoids live paid model calls

## Done

This slice is done when:

- `/support` includes a free-text MCP interrogation form
- a configured AI agent can answer a refund-case question by calling MCP tools
- missing credentials are handled cleanly
- the UI displays answer and tool trace
- the guided workflow still works
- tests pass without paid model calls
- README explains setup, usage, and cost behavior
