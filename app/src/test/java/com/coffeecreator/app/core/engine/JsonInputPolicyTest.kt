package com.coffeecreator.app.core.engine

import org.junit.Assert.assertThrows
import org.junit.Test

class JsonInputPolicyTest {
    @Test fun givenBracesInsideString_whenValidated_thenDataDoesNotChangeDepth() {
        JsonInputPolicy.validateDepth("{\"notes\":\"{untrusted} [source]\"}")
    }

    @Test fun givenDeepOrUnclosedJson_whenValidated_thenRejectedBeforeParsing() {
        assertThrows(IllegalArgumentException::class.java) { JsonInputPolicy.validateDepth("[".repeat(21) + "]".repeat(21)) }
        assertThrows(IllegalArgumentException::class.java) { JsonInputPolicy.validateDepth("{\"incomplete\":") }
    }
}
