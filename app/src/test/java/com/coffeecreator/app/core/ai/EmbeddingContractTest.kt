package com.coffeecreator.app.core.ai

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EmbeddingContractTest {
    private val vocabulary = listOf("[PAD]", "[UNK]", "[CLS]", "[SEP]", "[MASK]", "coffee", "cafe", ",", "brew", "##ing", "中")

    @Test
    fun givenUncasedAccentedText_whenTokenizing_thenNormalizeAndAddSpecialTokens() {
        val tokens = BertWordPieceTokenizer(vocabulary).encode("COFFEE Café, brewing")
        assertArrayEquals(longArrayOf(2, 5, 6, 7, 8, 9, 3), tokens.ids)
        assertArrayEquals(LongArray(7) { 1 }, tokens.attentionMask)
    }

    @Test
    fun givenUnknownAndCjkText_whenTokenizing_thenPreserveUnknownAndCjkBoundary() {
        assertArrayEquals(
            longArrayOf(2, 1, 10, 3),
            BertWordPieceTokenizer(vocabulary).encode("unavailable中").ids
        )
    }

    @Test
    fun givenInputBeyondBudget_whenTokenizing_thenKeepClsSepAndBoundLength() {
        val tokens = BertWordPieceTokenizer(vocabulary).encode("coffee ".repeat(300), 128)
        assertEquals(128, tokens.ids.size)
        assertEquals(2L, tokens.ids.first())
        assertEquals(3L, tokens.ids.last())
    }

    @Test
    fun givenFixedShape_whenPadding_thenMaskPadTokens() {
        val tokens = BertWordPieceTokenizer(vocabulary).encode("coffee", 8, padToMax = true)
        assertArrayEquals(longArrayOf(2, 5, 3, 0, 0, 0, 0, 0), tokens.ids)
        assertArrayEquals(longArrayOf(1, 1, 1, 0, 0, 0, 0, 0), tokens.attentionMask)
    }

    @Test
    fun givenMissingSpecialTokens_whenCreatingTokenizer_thenReject() {
        assertThrows(IllegalArgumentException::class.java) { BertWordPieceTokenizer(listOf("coffee")) }
    }

    @Test
    fun givenMaskedTokenEmbeddings_whenPooling_thenIgnorePaddingAndNormalize() {
        val pooled = EmbeddingPooling.meanNormalize(
            floatArrayOf(3f, 4f, 3f, 4f, 1000f, 1000f),
            3,
            2,
            longArrayOf(1, 1, 0)
        )
        assertArrayEquals(floatArrayOf(0.6f, 0.8f), pooled, 0.000001f)
    }

    @Test
    fun givenInvalidVector_whenNormalizing_thenRejectRatherThanFabricate() {
        assertThrows(IllegalArgumentException::class.java) { EmbeddingPooling.normalize(floatArrayOf(0f, 0f)) }
        assertThrows(IllegalArgumentException::class.java) { EmbeddingPooling.normalize(floatArrayOf(Float.NaN, 1f)) }
        assertThrows(IllegalArgumentException::class.java) {
            EmbeddingPooling.meanNormalize(floatArrayOf(1f, 2f), 1, 2, longArrayOf(0))
        }
    }
}
