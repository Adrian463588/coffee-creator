package com.coffeecreator.app.core.filesystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BagPhotoInputPolicyTest {
    @Test
    fun givenSupportedPhotoWithinBounds_whenValidated_thenAcceptedAndSampled() {
        BagPhotoInputPolicy.validate("image/jpeg", 12 * 1024 * 1024, 6400, 4800)

        assertEquals(4, BagPhotoInputPolicy.sampleSize(6400, 4800))
        assertEquals(4, BagPhotoInputPolicy.sampleSize(3201, 1600))
    }

    @Test
    fun givenUnsupportedMime_whenValidated_thenRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            BagPhotoInputPolicy.validate("application/pdf", 10, 1200, 800)
        }
    }

    @Test
    fun givenOversizedOrMalformedImage_whenValidated_thenRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            BagPhotoInputPolicy.validate("image/png", BagPhotoInputPolicy.MAX_BYTES + 1, 100, 100)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BagPhotoInputPolicy.validate("image/png", 1024, 0, 800)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BagPhotoInputPolicy.validate("image/png", 1024, 10_001, 10_000)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BagPhotoInputPolicy.validate("image/png", 1024, 30_001, 10)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BagPhotoInputPolicy.sampleSize(Int.MAX_VALUE, 1)
        }
    }
}
