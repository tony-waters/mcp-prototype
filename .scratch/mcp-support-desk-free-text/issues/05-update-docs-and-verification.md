# 05: Update Docs and Verification

**What to build:** README and automated verification explain and protect the free-text MCP interrogation slice.

**Blocked by:** 04: Add Free Text Workspace UI

**Status:** resolved

- [x] README documents how to run the free-text flow without credentials.
- [x] README documents how to configure model credentials locally.
- [x] README states when external AI provider costs may apply.
- [x] README includes at least one free-text question example.
- [x] The default test suite passes without paid model calls.
- [x] Any optional live-model smoke test is opt-in and skipped by default.

## Notes

The documentation should preserve the first-slice guided workflow instructions.

## Answer

Updated `README.md` with free-text usage, the default no-cost disabled path, Anthropic/Spring AI configuration, cost behavior, and an example question. Added deterministic service and MVC tests that do not make live model calls.
