# BDD acceptance scenarios

## Scenario: catalog the official Gemma 4 E2B IT LiteRT-LM artifact

Given the Google AI Edge Gallery allowlist identifies the Android artifact at an immutable Hugging Face revision
When the built-in catalog is read
Then it exposes the exact artifact filename, immutable URL, size, SHA-256, model family, and license source
And existing GGUF assets retain their prior metadata and capability.

## Scenario: download-only model cannot be mistaken for a loadable model

Given Gemma 4 E2B IT is catalogued for SAF download
When runtime capability is queried before or after installation
Then the catalog reports download support and no inference/load support
And a UI can hide or disable its Load action without inferring capability from status or file format.

## Scenario: reject tampered manifest metadata

Given an upstream Gemma 4 catalog entry
When its pinned revision, URL, expected size, or SHA-256 is changed
Then catalog validation rejects the manifest before any transfer or installed state is recorded.

## Scenario: reject bytes that violate the upstream size/hash contract

Given a downloaded artifact with an incorrect size or SHA-256
When the existing verified publication path stages or publishes it
Then it remains not-installed and no final SAF model manifest is published.

## Scenario: accept only exact transfer size and SHA-256

Given the pinned artifact contract and complete transfer bytes
When size and SHA-256 match the manifest
Then the existing SAF publication path may mark it installed after verifying the published URI contents.
