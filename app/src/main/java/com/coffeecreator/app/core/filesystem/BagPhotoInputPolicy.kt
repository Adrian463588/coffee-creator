package com.coffeecreator.app.core.filesystem

object BagPhotoInputPolicy {
    const val MAX_BYTES = 15 * 1024 * 1024
    const val MAX_DECODED_EDGE = 1600
    private const val MAX_SOURCE_EDGE = 30_000
    private const val MAX_SOURCE_PIXELS = 100_000_000L

    private val supportedMimeTypes = setOf("image/jpeg", "image/png", "image/webp")

    fun validate(mimeType: String?, byteCount: Int, width: Int, height: Int) {
        require(mimeType?.lowercase() in supportedMimeTypes) {
            "Choose a JPEG, PNG, or WebP image."
        }
        require(byteCount in 1..MAX_BYTES) { "Image exceeds the 15 MB processing limit or is empty." }
        require(width > 0 && height > 0) { "Image could not be decoded. Select another photo." }
        require(width <= MAX_SOURCE_EDGE && height <= MAX_SOURCE_EDGE) {
            "Image dimensions exceed the supported limit. Choose a smaller photo."
        }
        require(width.toLong() * height.toLong() <= MAX_SOURCE_PIXELS) {
            "Image dimensions exceed the supported limit. Choose a smaller photo."
        }
    }

    fun sampleSize(width: Int, height: Int): Int {
        require(width > 0 && height > 0) { "Image dimensions must be positive." }
        require(width <= MAX_SOURCE_EDGE && height <= MAX_SOURCE_EDGE) {
            "Image dimensions exceed the supported limit. Choose a smaller photo."
        }
        require(width.toLong() * height.toLong() <= MAX_SOURCE_PIXELS) {
            "Image dimensions exceed the supported limit. Choose a smaller photo."
        }
        var sampleSize = 1
        while (maxOf(ceilDiv(width, sampleSize), ceilDiv(height, sampleSize)) > MAX_DECODED_EDGE) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private fun ceilDiv(value: Int, divisor: Int): Int = (value - 1) / divisor + 1
}
