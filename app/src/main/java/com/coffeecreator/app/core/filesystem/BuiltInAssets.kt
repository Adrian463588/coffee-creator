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

    val kafi = ModelAsset(
        id = "kafi-llama32-1b-q4", name = "Kafi · Llama 3.2 1B Q4_K_M",
        revision = "13e635d1d4f86c95eaeb90d99decb45845afedfe", format = "GGUF Q4_K_M",
        expectedSizeBytes = 807694080L,
        sha256 = "8b2023ef1c79a7e6c5b4de61db1d720160f12ab2b4db08dbc2ea111c5da0c8bb",
        downloadUrl = "https://huggingface.co/tristiyadi/kafi-barista-llama3.2-1b-gguf/resolve/" +
            "13e635d1d4f86c95eaeb90d99decb45845afedfe/unsloth.Q4_K_M.gguf",
        license = "Llama 3.2 Community License + Acceptable Use Policy; Built with Llama. " +
            "https://github.com/meta-llama/llama-models/blob/main/models/llama3_2/LICENSE",
        runtime = "llama.cpp", tokenizerSupport = "Embedded Llama GGUF tokenizer and chat template"
    )

    val gemma4 = ModelAsset(
        id = "gemma4-e2b-it-q4", name = "Gemma 4 E2B IT Q4_K_M · text",
        revision = "0314792d7f1f7e229411f620751375812bb9faf2", format = "GGUF Q4_K_M",
        expectedSizeBytes = 3106738272L,
        sha256 = "740185b21d22ceb83a11c3aa62ad5842ef32c70f6096d756bbee85a1e4ec34b8",
        downloadUrl = "https://huggingface.co/unsloth/gemma-4-E2B-it-GGUF/resolve/" +
            "0314792d7f1f7e229411f620751375812bb9faf2/gemma-4-E2B-it-Q4_K_M.gguf",
        license = "Apache-2.0; Google Gemma 4, Unsloth GGUF conversion. https://ai.google.dev/gemma/apache_2",
        runtime = "llama.cpp", tokenizerSupport = "Embedded Gemma 4 tokenizer; text-only, bounded 1024-token context"
    )

    val catalog = listOf(qwen, kafi, gemma4)

    fun guidance(asset: ModelAsset): String? {
        val expected = catalog.find { it.id == asset.id } ?: return null
        if (asset.revision != expected.revision || asset.expectedSizeBytes != expected.expectedSizeBytes ||
            !asset.sha256.equals(expected.sha256, true) || asset.downloadUrl != expected.downloadUrl
        ) {
            return null
        }
        return when (asset.id) {
            qwen.id -> "Small general assistant. Optional download; no weights bundled."
            kafi.id ->
                "Publisher fine-tune for Indonesian cafe/menu dialogue using synthetic conversations. " +
                    "Not independently validated for extraction physics or formulation. Kotlin tools remain authoritative."
            gemma4.id ->
                "Multilingual general assistant; text only here. This verified GGUF is 3.11 GB, " +
                    "not the approximately 2.58 GB LiteRT variant. Needs substantial free RAM and space for SAF plus " +
                    "private staging/inference copies. Image/audio and the model's advertised long context are not enabled."
            else -> null
        }
    }

    fun purpose(asset: ModelAsset): String = if (guidance(asset) == null) {
        "Custom manifest. Review its source and license before downloading."
    } else {
        when (asset.id) {
            kafi.id -> "Indonesian cafe and menu dialogue. Use Kotlin tools for exact coffee calculations."
            gemma4.id -> "Multilingual text chat. Requires substantial free RAM and storage; image/audio are not enabled."
            else -> "Compact general text assistant. The smallest download in this catalog."
        }
    }
}
