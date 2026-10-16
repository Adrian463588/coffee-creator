# Change: Gemma 4 E2B IT SAF download catalog

## Purpose

Offer the official Gemma 4 E2B IT LiteRT-LM artifact as an explicitly user-initiated download into the selected SAF tree, with immutable provenance and the existing verified staging, SHA-256, size, and publish safeguards.

## Scope

- Add the Google AI Edge Gallery allowlisted LiteRT-LM artifact as a distinct catalog entry beside the existing GGUF models.
- Mark runtime compatibility explicitly. This change provides download and verified SAF persistence only; it does not implement LiteRT-LM inference.
- Reject catalog or transfer metadata that does not match the pinned upstream artifact contract.
- Preserve existing GGUF catalog, download, import, and inference behavior.

## Invariants

- Model bytes remain user-owned under the user-selected SAF directory; no URI is converted into a filesystem path.
- Final `INSTALLED` state requires exact manifest size and SHA-256 verification on staged bytes and published SAF bytes.
- The model URL is pinned to an immutable Hugging Face revision and the file is not bundled or downloaded without an explicit user action.
- The LiteRT-LM entry is download-only until an actual compatible runtime adapter and runtime tests exist. Existing GGUF entries retain their current load capability.
- Size values use `Long`; the 2,588,147,712-byte contract must not overflow 32-bit arithmetic.
- The artifact's license identifier and official license URL are surfaced as metadata; the app does not redistribute weights.

## Dependencies and boundaries

- Owns `BuiltInAssets.kt`, narrowly scoped manifest/transfer validation if necessary, and filesystem unit tests.
- Does not edit Compose screens or `CoffeeViewModel`; the integrator/UI owner must use the new capability predicate to hide or disable `Load` for this entry.
- Uses Google AI Edge Gallery's pinned Android allowlist values and the matching immutable Hugging Face file revision.

## Source evidence

- [Google AI Edge Gallery Android allowlist](https://github.com/google-ai-edge/gallery/blob/main/model_allowlists/1_0_16.json) identifies the model, artifact filename, 2,588,147,712-byte size, 8 GB minimum memory, and pinned revision `6e5c4f1e395deb959c494953478fa5cec4b8008f`.
- The [pinned Hugging Face file page](https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm/blob/6e5c4f1e395deb959c494953478fa5cec4b8008f/gemma-4-E2B-it.litertlm) reports SHA-256 `181938105e0eefd105961417e8da75903eacda102c4fce9ce90f50b97139a63c`.
- Google's [Gemma 4 model card](https://ai.google.dev/gemma/docs/core/model_card_4) lists Apache 2.0, linking to the [official Apache license](https://ai.google.dev/gemma/apache_2).

## Rollback

Revert this change's model catalog entry and focused contract tests. Existing catalog models and SAF transfer implementation remain unchanged.
