# Third-party assets and runtime licenses

The Android app sends no documents to these services. URLs below are development
asset sources or an explicit user-requested model transfer.

| Component | Revision / version | License |
|---|---|---|
| llama.cpp | 3fb47677eaf0ebb569a1f94c756dcf58b8584cc0 | MIT |
| ONNX Runtime Android | 1.19.2 | MIT |
| all-MiniLM-L6-v2 (Xenova quantized ONNX) | 751bff37182d3f1213fa05d7196b954e230abad9 | Apache-2.0 (model card) |
| Tesseract4Android | 4.9.0 | Apache-2.0; bundled Leptonica/image libraries have their own notices |
| tessdata_fast English | 87416418657359cb625c412a48b6e1d6d41c29bd | Apache-2.0 |
| PDFBox Android | 2.0.27.0 | Apache-2.0 |
| Qwen2.5-0.5B-Instruct GGUF (optional; not bundled) | 9217f5db79a29953eb74d5343926648285ec7e67 | Apache-2.0 |
| AndroidX / Compose / Room / WorkManager / DataStore | Gradle version catalog | Apache-2.0 |
| Dagger Hilt / Gson | 2.52 / 2.11.0 | Apache-2.0 |
| OkHttp | 4.12.0 | Apache-2.0 |

Native and OCR weights are not an SCA graphics or vocabulary license. Coffee
Creator uses application-owned descriptive labels, with no proprietary wheel.
General catalog references identify taxonomy only and are not real supplier lots.
No SCA manual or measurement is bundled as an invented document.

Asset sizes and SHA-256 receipts are in `app/src/main/assets/bundled-assets.json`.
The developer fetch script checks the immutable model hash before bundling.
Each downloadable user asset must declare its license, size and SHA-256.
