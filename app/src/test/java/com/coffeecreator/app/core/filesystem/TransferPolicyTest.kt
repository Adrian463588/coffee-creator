package com.coffeecreator.app.core.filesystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

private const val GEMMA4_LITERT_SIZE_BYTES = 2_588_147_712L
private const val GEMMA4_LITERT_SHA256 = "181938105e0eefd105961417e8da75903eacda102c4fce9ce90f50b97139a63c"

class TransferPolicyTest {
    @Test fun givenMatchingRangeAndIdentity_whenResuming_thenKeepExistingBytes() {
        assertEquals(100L, TransferPolicy.resumeOffset(100, 500, "\"a\"", 206, "bytes 100-499/500", "\"a\""))
    }

    @Test fun givenServerIgnoresRange_whenResuming_thenRestartSafely() {
        assertEquals(0L, TransferPolicy.resumeOffset(100, 500, "\"a\"", 200, null, "\"a\""))
    }

    @Test fun givenChangedIdentityOrWrongRange_whenResuming_thenRejectPartialResponse() {
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.resumeOffset(100, 500, "\"a\"", 206, "bytes 100-499/500", "\"b\"")
        }
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.resumeOffset(100, 500, "\"a\"", 206, "bytes 99-499/500", "\"a\"")
        }
    }

    @Test fun givenNoStrongIdentity_whenResuming_thenRejectUnsafeRange() {
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.resumeOffset(100, 500, null, 206, "bytes 100-499/500", null)
        }
    }

    @Test fun givenPathTraversalOrUnknownHash_whenValidatingManifest_thenReject() {
        assertThrows(IllegalArgumentException::class.java) { TransferPolicy.safeIdentifier("../weights") }
        assertThrows(IllegalArgumentException::class.java) { TransferPolicy.validateHash("not-a-hash") }
        assertEquals("local-model-1", TransferPolicy.safeIdentifier("local-model-1"))
    }

    @Test
    fun givenExactGemmaLiteRtSizeAndSha_whenValidatingCompletedTransfer_thenAcceptLongByteCount() {
        TransferPolicy.validateCompletedTransfer(
            expectedSizeBytes = GEMMA4_LITERT_SIZE_BYTES,
            actualSizeBytes = GEMMA4_LITERT_SIZE_BYTES,
            expectedSha256 = GEMMA4_LITERT_SHA256,
            actualSha256 = GEMMA4_LITERT_SHA256
        )
    }

    @Test
    fun givenWrongTransferSizeOrDigest_whenValidatingCompletedTransfer_thenReject() {
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.validateCompletedTransfer(
                GEMMA4_LITERT_SIZE_BYTES,
                GEMMA4_LITERT_SIZE_BYTES - 1,
                GEMMA4_LITERT_SHA256,
                GEMMA4_LITERT_SHA256
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            TransferPolicy.validateCompletedTransfer(
                GEMMA4_LITERT_SIZE_BYTES,
                GEMMA4_LITERT_SIZE_BYTES,
                GEMMA4_LITERT_SHA256,
                "0".repeat(64)
            )
        }
    }
}
