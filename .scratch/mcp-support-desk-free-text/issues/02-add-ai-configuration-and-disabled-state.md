# 02: Add AI Configuration and Disabled State

**What to build:** the app can detect whether free-text AI configuration is present and expose a clear disabled state when it is not.

**Blocked by:** 01: Design Free Text Agent Boundary

**Status:** resolved

- [x] Model provider credentials are read from environment variables or external configuration.
- [x] Secrets are not committed to the repository.
- [x] The default Docker Compose path starts without credentials.
- [x] The UI clearly explains that free-text AI is unavailable when credentials are missing.
- [x] Tests cover missing credentials without making live model calls.

## Notes

This ticket should not wire paid calls into the default test suite.

## Answer

Added the Anthropic Spring AI starter as the bundled provider while defaulting `SPRING_AI_MODEL_CHAT` to `none`. The default UI disables the free-text form and shows a missing-credentials state. Compose passes Spring AI Anthropic configuration through only from environment variables; no secrets are committed.
