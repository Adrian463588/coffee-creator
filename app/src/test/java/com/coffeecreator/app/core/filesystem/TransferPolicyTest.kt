package com.coffeecreator.app.core.filesystem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

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
}
