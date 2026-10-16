# Implementation and verification tasks

- [x] Verify the allowlist's pinned commit, file size, model SHA-256, and Gemma 4 terms using primary Google/Hugging Face sources.
- [x] Add the distinct Gemma 4 E2B IT LiteRT-LM catalog entry without changing existing GGUF assets.
- [x] Add an explicit runtime capability contract that marks the new entry download-only.
- [x] Validate the exact pinned catalog transfer metadata before network or installed-state changes.
- [x] Add BDD unit tests for catalog contract, download-only capability, tampered metadata, and exact size/hash transfer validation.
- [ ] Run focused and required Android quality gates after the integrator grants the Gradle verification window.
- [ ] Review diff for unchanged SAF ownership, no partial final publication, and no GGUF regressions.
