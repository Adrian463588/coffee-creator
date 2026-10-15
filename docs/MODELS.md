# Downloadable local models

Verified from primary Hugging Face metadata/cards, immutable revisions and HTTP
headers on2026-10-15. These are optional downloads, not APK-bundled weights.

| Model | Quantization / actual bytes | Revision | License |
|---|---|---|---|
| [Qwen2.5 0.5B Instruct](https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF) | Q4_K_M /491,400,032 | 9217f5db79a29953eb74d5343926648285ec7e67 | Apache-2.0 |
| [Kafi — Llama3.2 1B](https://huggingface.co/tristiyadi/kafi-barista-llama3.2-1b-gguf) | Q4_K_M /807,694,080 | 13e635d1d4f86c95eaeb90d99decb45845afedfe | Llama3.2 Community License + Acceptable Use Policy |
| [Gemma4 E2B IT, Unsloth conversion](https://huggingface.co/unsloth/gemma-4-E2B-it-GGUF) | Q4_K_M /3,106,738,272 | 0314792d7f1f7e229411f620751375812bb9faf2 | Apache-2.0 |

The Gemma GGUF artifact is **3.11GB decimal**, approximately2.89GiB. The advertised
approximately2.58GB mobile download belongs to a different LiteRT variant; this
application uses the verified GGUF compatible with its native backend. It does
not rename or mislabel a LiteRT file as GGUF.

Kafi's publisher describes Indonesian synthetic cafe/menu/order training. That
does not establish extraction-science accuracy, supported ingredient limits or
grounded citation quality. All calculations remain typed Kotlin operations;
model-proposed writes and quantities still require user review. See
[Meta's model license](https://github.com/meta-llama/llama-models/blob/main/models/llama3_2/LICENSE)
and [Google's Gemma4 license](https://ai.google.dev/gemma/apache_2).

## Using the SAF folder

1. Open Models and choose a directory with the Android system picker.
2. Select the desired model's Download action. Networking starts only from this
   explicit action. Without a selected folder the download action is disabled.
3. Transfer stages private partial bytes and identity/hash checkpoints. Only a
   verified complete file is copied into the chosen SAF tree, re-read and checked
   before its manifest is published and the model becomes Installed.
4. Load creates a separately verified private inference copy and checks actual
   RAM/runtime support. Installed and loaded are distinct states. Large models
   may be rejected recoverably on insufficient-RAM devices.
5. After reinstall, choose that same folder again and use Recover bundles.
   Restore a separate explicit backup for records/chat. Never assume grants,
   private partial downloads or the internal database survive uninstall.

Allow storage for staging, the published SAF copy and a private inference copy,
especially when the chosen folder shares the device's internal storage. Transfer
and native admission retain bounded4GiB file size and Android memory reserves.
Text-only sessions use at most1024 context/output256 tokens, not the model card's
maximum long context. No image/audio projector or multimodal input is enabled.

## Integrity and evidence

Exact filenames, immutable download URLs, sizes and hashes for the newly requested
models are retained in [catalog receipts](verification/model-catalog-receipts.json)
and `BuiltInAssets.kt`. Metadata and two bounded64KiB GGUF prefix requests were
checked; the multi-GB weights were not downloaded during this no-ADB phase.
Full Kafi/Gemma download, SAF publication and native inference remain device
acceptance work. The earlier physical Qwen/SAF suite is separate evidence.
