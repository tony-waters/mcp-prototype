# 02: Add AI Configuration and Disabled State

**What to build:** the app can detect whether free-text AI configuration is present and expose a clear disabled state when it is not.

**Blocked by:** 01: Design Free Text Agent Boundary

**Status:** todo

- [ ] Model provider credentials are read from environment variables or external configuration.
- [ ] Secrets are not committed to the repository.
- [ ] The default Docker Compose path starts without credentials.
- [ ] The UI clearly explains that free-text AI is unavailable when credentials are missing.
- [ ] Tests cover missing credentials without making live model calls.

## Notes

This ticket should not wire paid calls into the default test suite.
