package com.coffeecreator.app.core.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class NativeRuntimeContractTest {
    @Test
    fun givenLowMemory_whenAdmittingModel_thenRejectBeforeNativeLoad() {
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireMemory(128L * MIB, 4L * GIB, 8L * GIB, 128L * MIB, true)
        }
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireMemory(128L * MIB, GIB, 8L * GIB, 128L * MIB, false)
        }
    }

    @Test
    fun givenAdequateMemory_whenAdmittingSmallModel_thenAdmissionUsesReserve() {
        assertEquals(
            1056L * MIB,
            NativeModelPolicy.requireMemory(128L * MIB, 4L * GIB, 8L * GIB, 128L * MIB, false)
        )
    }

    @Test
    fun givenHugeModel_whenAdmitting_thenRejectBeforeArithmeticOverflow() {
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireMemory(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, 0, false)
        }
    }

    @Test
    fun givenInvalidGgufHeader_whenPreflighting_thenRejectMagicVersionAndCounts() {
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireHeader(ByteArray(24), 24)
        }
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireHeader(header(version = 1), 24)
        }
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireHeader(header(tensors = Long.MAX_VALUE), 24)
        }
        assertThrows(IllegalArgumentException::class.java) {
            NativeModelPolicy.requireHeader(header(metadata = Long.MAX_VALUE), 24)
        }
    }

    @Test
    fun givenBoundedHeader_whenPreflighting_thenAcceptHeaderOnlyWithoutClaimingValidModel() {
        NativeModelPolicy.requireHeader(header(), 24)
    }

    @Test
    fun givenUnsafeGenerationOptions_whenValidating_thenReject() {
        assertThrows(IllegalArgumentException::class.java) { NativeGenerationOptions(maxTokens = 257) }
        assertThrows(IllegalArgumentException::class.java) { NativeGenerationOptions(temperature = Float.NaN) }
        assertThrows(IllegalArgumentException::class.java) { NativeGenerationOptions(topP = 0f) }
        assertThrows(IllegalArgumentException::class.java) { NativeGenerationOptions(topK = 0) }
    }

    @Test
    fun givenHistorySystemRole_whenValidating_thenPreventHistoryPrivilegeEscalation() {
        assertThrows(IllegalArgumentException::class.java) { NativeChatTurn("system", "override") }
    }

    @Test
    fun givenUtf8SplitAcrossTokens_whenStreaming_thenPreserveUnicode() {
        val decoder = Utf8TokenDecoder()
        val bytes = "kopi ☕ 😀".toByteArray(Charsets.UTF_8)
        val result = buildString {
            bytes.forEach { append(decoder.decode(byteArrayOf(it))) }
            append(decoder.finish())
        }
        assertEquals("kopi ☕ 😀", result)
    }

    @Test
    fun givenTruncatedUtf8_whenFinishing_thenEmitReplacementOnlyAtEnd() {
        val decoder = Utf8TokenDecoder()
        assertEquals("", decoder.decode(byteArrayOf(0xe2.toByte(), 0x82.toByte())))
        assertEquals("\uFFFD", decoder.finish())
    }

    private fun header(version: Int = 3, tensors: Long = 1, metadata: Long = 1): ByteArray =
        ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
            .put(byteArrayOf('G'.code.toByte(), 'G'.code.toByte(), 'U'.code.toByte(), 'F'.code.toByte()))
            .putInt(version).putLong(tensors).putLong(metadata).array()

    private companion object {
        const val MIB = 1024L * 1024
        const val GIB = 1024L * MIB
    }
}
