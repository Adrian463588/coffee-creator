# Third-party assets and runtime licenses

The Android app sends no documents to these services. URLs below are development
asset sources or an explicit user-requested model transfer.

| Component | Revision / version | License |
|---|---|---|
| llama.cpp | 3fb47677eaf0ebb569a1f94c756dcf58b8584cc0 | MIT |
| ONNX Runtime Android | 1.21.1 | MIT |
| all-MiniLM-L6-v2 (Xenova quantized ONNX) | 751bff37182d3f1213fa05d7196b954e230abad9 | Apache-2.0 (model card) |
| Tesseract4Android | 4.9.0 | Apache-2.0; bundled Leptonica/image libraries have their own notices |
| tessdata_fast English | 87416418657359cb625c412a48b6e1d6d41c29bd | Apache-2.0 |
| PDFBox Android | 2.0.27.0 | Apache-2.0 |
| Qwen2.5-0.5B-Instruct GGUF (optional; not bundled) | 9217f5db79a29953eb74d5343926648285ec7e67 | Apache-2.0 |
| Kafi Llama3.2-1B GGUF (optional; not bundled) | 13e635d1d4f86c95eaeb90d99decb45845afedfe | Llama3.2 Community License and Acceptable Use Policy; Built with Llama |
| Gemma4 E2B IT GGUF (optional; not bundled) | 0314792d7f1f7e229411f620751375812bb9faf2 | Apache-2.0 |
| Google Gemma4 text chat-template reference | 3e22461f65e89153144f8adb70e3b8c2cc9845a7 | Apache-2.0; text-only specialization in native adapter |
| AndroidX / Compose / Room / WorkManager / DataStore | Gradle version catalog | Apache-2.0 |
| Dagger Hilt / Gson | 2.52 / 2.11.0 | Apache-2.0 |
| OkHttp | 4.12.0 | Apache-2.0 |
| ObjectBox Java bindings | 4.3.1 | Apache-2.0 |
| ObjectBox native database | 4.3.1 | ObjectBox Binary Licence; includes FlatBuffers (Apache-2.0), LMDB (OpenLDAP-2.8) |
| ObjectBox Gradle generator | 4.3.1; build tool only | AGPL-3.0 (generator repository); not an application component |
| Robolectric | 4.14.1; test tool only | MIT; not an application component |

Native and OCR weights are not an SCA graphics or vocabulary license. Coffee
Creator uses application-owned descriptive labels, with no proprietary wheel.
General catalog references identify taxonomy only and are not real supplier lots.
No SCA manual or measurement is bundled as an invented document.

Asset sizes and SHA-256 receipts are in `app/src/main/assets/bundled-assets.json`.
The developer fetch script checks the immutable model hash before bundling.
Each downloadable user asset must declare its license, size and SHA-256.

## Primary license references

- [ObjectBox component licensing FAQ](https://objectbox.io/faq/#license-pricing)
  and [native binary agreement](https://objectbox.io/0209-ob-binary-license/).
  Native ObjectBox is separately licensed proprietary software; do not label the
  entire application or dependency tree Apache-2.0 or F-Droid-compatible.
  No ObjectBox Sync component is included. Build-time tools are distinct from
  offline application operation.
- [ObjectBox Java](https://github.com/objectbox/objectbox-java),
  [generator](https://github.com/objectbox/objectbox-java-generator),
  [LMDB notices](https://www.openldap.org/software/release/license.html).
- [llama.cpp](https://github.com/ggml-org/llama.cpp),
  [ONNX Runtime](https://github.com/microsoft/onnxruntime),
  [MiniLM model card](https://huggingface.co/Xenova/all-MiniLM-L6-v2),
  [Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android),
  [tessdata_fast](https://github.com/tesseract-ocr/tessdata_fast),
  [PDFBox Android](https://github.com/TomRoush/PdfBox-Android),
  [Qwen optional model card](https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF).
- [Kafi optional model card](https://huggingface.co/tristiyadi/kafi-barista-llama3.2-1b-gguf),
  [Llama3.2 license](https://github.com/meta-llama/llama-models/blob/main/models/llama3_2/LICENSE),
  [Acceptable Use Policy](https://github.com/meta-llama/llama-models/blob/main/models/llama3_2/USE_POLICY.md),
  [Gemma4 optional GGUF card](https://huggingface.co/unsloth/gemma-4-E2B-it-GGUF),
  [Google text-template reference](https://huggingface.co/google/gemma-4-E2B-it/blob/3e22461f65e89153144f8adb70e3b8c2cc9845a7/chat_template.jinja).
- [Robolectric local testing](https://robolectric.org/getting-started/) and
  [Windows native-graphics support](https://github.com/robolectric/robolectric/discussions/8954).

Dataset source-specific restrictions and per-record attribution are documented
separately in DATASET.md. The app does not bundle a copyrighted coffee manual.
