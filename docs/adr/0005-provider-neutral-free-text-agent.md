# Keep the Free Text Agent Provider-Neutral

The free-text agent will be implemented behind provider-neutral Spring AI abstractions rather than binding the slice directly to one model provider. This keeps the prototype open to different local or hosted model backends while preserving the requirement that credentials come from environment-specific configuration and that default tests use deterministic fakes instead of live paid model calls.
