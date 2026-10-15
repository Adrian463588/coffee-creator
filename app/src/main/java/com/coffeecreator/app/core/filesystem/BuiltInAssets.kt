package com.coffeecreator.app.core.filesystem

import com.coffeecreator.app.core.model.ModelAsset

object BuiltInAssets {
    val qwen = ModelAsset(
        id = "qwen25-05b-q4", name = "Qwen2.5 0.5B Instruct Q4_K_M",
        revision = "9217f5db79a29953eb74d5343926648285ec7e67", format = "GGUF Q4_K_M",
        expectedSizeBytes = 491400032,
        sha256 = "74a4da8c9fdbcd15bd1f6d01d621410d31c6fc00986f5eb687824e7b93d7a9db",
        downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/" +
            "9217f5db79a29953eb74d5343926648285ec7e67/qwen2.5-0.5b-instruct-q4_k_m.gguf",
        license = "Apache-2.0; official Qwen GGUF model card. Optional model, not bundled.",
        runtime = "llama.cpp", tokenizerSupport = "Embedded GGUF tokenizer and chat template"
    )
}
