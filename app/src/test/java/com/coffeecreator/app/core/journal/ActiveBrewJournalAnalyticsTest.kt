package com.coffeecreator.app.core.journal

import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.data.BackupPayload
import com.coffeecreator.app.core.model.CoffeeBean
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Deterministic BDD/TDD unit tests for Epic S2-E04 (Stories S2-039 through S2-050).
 * Tests all pure Kotlin domain models, algorithms, and kinematics without Android SDK dependencies.
 */
class ActiveBrewJournalAnalyticsTest {

    // S2-039: Detailed brew journal
    @Test
    fun testDetailedBrewJournalCalculations() {
        val entry = BrewJournalEntry(
            id = "test-log-01",
            beanName = "Kenya Kamwangi Washed",
            brewMethod = "Pour Over (V60)",
            doseGrams = 15.0,
            waterGrams = 250.0,
            beverageMassGrams = 220.0,
            brewDurationSeconds = 195,
            measuredTdsPercent = 1.35,
            rating = 8.5,
            tasteNotes = listOf("Blackcurrant", "Bergamot", "Crisp Acidity"),
            photoUris = listOf("content://media/photo01.jpg")
        )

        // Brew ratio: 250 / 15 = 16.67
        assertEquals(16.67, entry.ratioDenominator, 0.01)
        // Extraction yield: (220 * 1.35) / 15 = 19.80%
        assertNotNull(entry.extractionYieldPercent)
        assertEquals(19.80, entry.extractionYieldPercent!!, 0.01)
        assertEquals(1, entry.photoUris.size)
        assertEquals(3, entry.tasteNotes.size)
    }

    // S2-040: Search and multi-dimensional filter
    @Test
    fun testSearchAndFilterCriteria() {
        val entries = listOf(
            BrewJournalEntry(id = "log-1", beanName = "Ethiopia Yirgacheffe", brewMethod = "V60", doseGrams = 15.0, waterGrams = 250.0, brewDurationSeconds = 180, rating = 9.0, tags = listOf("Fruity", "Morning")),
            BrewJournalEntry(id = "log-2", beanName = "Colombia Huila", brewMethod = "Espresso", doseGrams = 18.0, waterGrams = 36.0, brewDurationSeconds = 28, rating = 7.0, tags = listOf("Espresso")),
            BrewJournalEntry(id = "log-3", beanName = "Kenya Nyeri", brewMethod = "V60", doseGrams = 20.0, waterGrams = 300.0, brewDurationSeconds = 210, rating = 9.5, tags = listOf("Bright", "Clean")),
            BrewJournalEntry(id = "log-4", beanName = "Panama Geisha", brewMethod = "AeroPress", doseGrams = 14.0, waterGrams = 200.0, brewDurationSeconds = 120, rating = 8.0, tags = listOf("Floral"))
        )

        // Filter: method V60 and rating >= 8.5
        val v60HighRated = entries.filter { it.brewMethod.contains("V60") && (it.rating ?: 0.0) >= 8.5 }
        assertEquals(2, v60HighRated.size)
        assertTrue(v60HighRated.any { it.id == "log-1" })
        assertTrue(v60HighRated.any { it.id == "log-3" })

        // Filter: tag "Floral"
        val floral = entries.filter { "Floral" in it.tags }
        assertEquals(1, floral.size)
        assertEquals("log-4", floral.first().id)
    }

    // S2-041: Side-by-side comparison with deterministic numeric deltas
    @Test
    fun testSideBySideSessionComparison() {
        val sessionA = BrewJournalEntry(
            id = "brew-a",
            beanName = "Ethiopia Guji",
            brewMethod = "V60",
            doseGrams = 15.0,
            waterGrams = 250.0,
            beverageMassGrams = 220.0,
            brewDurationSeconds = 180,
            waterTemperatureCelsius = 92.0,
            measuredTdsPercent = 1.30,
            rating = 7.5
        )
        val sessionB = BrewJournalEntry(
            id = "brew-b",
            beanName = "Ethiopia Guji",
            brewMethod = "V60",
            doseGrams = 16.0,
            waterGrams = 250.0,
            beverageMassGrams = 218.0,
            brewDurationSeconds = 205,
            waterTemperatureCelsius = 94.0,
            measuredTdsPercent = 1.42,
            rating = 8.8
        )

        val comparison = BrewAnalyticsEngine.compareSessions(sessionA, sessionB)
        assertEquals(1.00, comparison.doseDeltaGrams, 0.001)
        assertEquals(0.00, comparison.waterDeltaGrams, 0.001)
        assertEquals(25, comparison.durationDeltaSeconds)
        assertEquals(2.0, comparison.temperatureDeltaCelsius, 0.01)
        assertNotNull(comparison.tdsDeltaPercent)
        assertEquals(0.12, comparison.tdsDeltaPercent!!, 0.01)
        assertNotNull(comparison.eyDeltaPercent)
        assertEquals(0.27, comparison.eyDeltaPercent!!, 0.05)
        assertNotNull(comparison.ratingDelta)
        assertEquals(1.30, comparison.ratingDelta!!, 0.01)
    }

    // S2-042: Espresso target vs actual shot kinematics
    @Test
    fun testEspressoTargetVsActualKinematics() {
        val details = EspressoJournalDetails(
            targetDoseGrams = 18.0,
            actualDoseGrams = 18.2,
            targetYieldGrams = 36.0,
            actualYieldGrams = 40.5,
            targetDurationSeconds = 28,
            actualDurationSeconds = 22,
            puckPrepMethods = listOf("WDT", "Screen")
        )

        val doseDelta = details.actualDoseGrams - details.targetDoseGrams
        val yieldDelta = details.actualYieldGrams - details.targetYieldGrams
        val durationDelta = details.actualDurationSeconds - details.targetDurationSeconds

        assertEquals(0.2, doseDelta, 0.01)
        assertEquals(4.5, yieldDelta, 0.01)
        assertEquals(-6, durationDelta)
        // 40.5g / 22s = 1.84 g/s
        assertEquals(1.84, details.meanFlowRateGps, 0.02)
        // 36.0g / 28s = 1.29 g/s
        assertEquals(1.29, details.targetFlowRateGps, 0.02)
    }

    // S2-043: One-variable-at-a-time (OVAT) adjustment suggestions
    @Test
    fun testOvatChannelingRecommendation() {
        val entry = BrewJournalEntry(
            id = "shot-channeled",
            brewMethod = "Espresso",
            doseGrams = 18.0,
            waterGrams = 45.0,
            beverageMassGrams = 45.0,
            brewDurationSeconds = 16,
            espressoDetails = EspressoJournalDetails(
                targetDoseGrams = 18.0, actualDoseGrams = 18.0,
                targetYieldGrams = 36.0, actualYieldGrams = 45.0,
                targetDurationSeconds = 28, actualDurationSeconds = 16
            )
        )

        val rec = BrewAnalyticsEngine.evaluateOvat(entry, listOf("SOUR", "ASTRINGENT"))
        assertEquals(OvatTargetVariable.PUCK_PREPARATION, rec.targetVariable)
        assertEquals(AdjustmentDirection.MODIFY_TECHNIQUE, rec.direction)
        assertTrue(rec.physicalReasoning.contains("channeling", ignoreCase = true))
    }

    @Test
    fun testOvatUnderExtractedRecommendation() {
        val entry = BrewJournalEntry(
            id = "shot-sour",
            brewMethod = "Espresso",
            doseGrams = 18.0,
            waterGrams = 36.0,
            beverageMassGrams = 36.0,
            measuredTdsPercent = 8.0,
            brewDurationSeconds = 18
        )

        val rec = BrewAnalyticsEngine.evaluateOvat(entry, listOf("SOUR", "ACIDIC"))
        assertEquals(OvatTargetVariable.GRIND_SIZE, rec.targetVariable)
        assertEquals(AdjustmentDirection.FINER, rec.direction)
        assertTrue(rec.expectedImpact.contains("Extraction Yield", ignoreCase = true))
    }

    @Test
    fun testOvatOverExtractedRecommendation() {
        val entry = BrewJournalEntry(
            id = "shot-bitter",
            brewMethod = "Pour Over (V60)",
            doseGrams = 15.0,
            waterGrams = 250.0,
            beverageMassGrams = 220.0,
            measuredTdsPercent = 1.60, // EY = 23.47% > 22%
            brewDurationSeconds = 270
        )

        val rec = BrewAnalyticsEngine.evaluateOvat(entry, listOf("BITTER", "DRY"))
        assertEquals(OvatTargetVariable.GRIND_SIZE, rec.targetVariable)
        assertEquals(AdjustmentDirection.COARSER, rec.direction)
        assertTrue(rec.suggestedMagnitude.contains("coarser", ignoreCase = true))
    }

    // S2-044: Interactive charts data models & aggregation
    @Test
    fun testChartsControlChartQuadrants() {
        // High TDS and Low EY -> Under-extracted / Strong
        val tds = 1.60
        val ey = 17.0
        val quadrant = when {
            tds in 1.15..1.45 && ey in 18.0..22.0 -> "Optimal Gold Cup"
            tds > 1.45 && ey < 18.0 -> "Strong / Under-extracted"
            tds < 1.15 && ey < 18.0 -> "Weak / Under-extracted"
            tds > 1.45 && ey > 22.0 -> "Strong / Over-extracted"
            else -> "Weak / Over-extracted"
        }
        assertEquals("Strong / Under-extracted", quadrant)
    }

    // S2-045: Calendar, brew streak & local monthly statistics
    @Test
    fun testStreakAndMonthlyStats() {
        val today = LocalDate.of(2026, 10, 15)
        val dates = listOf(
            LocalDate.of(2026, 10, 12),
            LocalDate.of(2026, 10, 13),
            LocalDate.of(2026, 10, 14),
            LocalDate.of(2026, 10, 15)
        )

        val streak = BrewAnalyticsEngine.calculateStreak(dates, today)
        assertEquals(4, streak.currentStreakDays)
        assertTrue(streak.longestStreakDays >= 4)
        assertEquals(4, streak.activeDaysCount)

        val oct2026Millis = LocalDate.of(2026, 10, 15).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entries = listOf(
            BrewJournalEntry(id = "1", brewMethod = "V60", doseGrams = 15.0, waterGrams = 250.0, brewDurationSeconds = 180, rating = 9.0, extractionYieldPercent = 20.0, createdAt = oct2026Millis),
            BrewJournalEntry(id = "2", brewMethod = "V60", doseGrams = 15.0, waterGrams = 250.0, brewDurationSeconds = 180, rating = 8.0, extractionYieldPercent = 19.0, createdAt = oct2026Millis + 3600_000L),
            BrewJournalEntry(id = "3", brewMethod = "Espresso", doseGrams = 18.0, waterGrams = 36.0, brewDurationSeconds = 28, rating = 8.5, extractionYieldPercent = 20.5, createdAt = oct2026Millis + 7200_000L)
        )

        val stats = BrewAnalyticsEngine.calculateMonthlyStats(entries, 2026, 10)
        assertEquals(3, stats.totalBrewsCount)
        assertEquals(48.0, stats.totalDoseGrams, 0.1)
        assertEquals("V60", stats.topBrewMethod)
        assertEquals(8.5, stats.averageRating!!, 0.1)
        assertEquals(19.83, stats.averageExtractionYield!!, 0.1)
    }

    // S2-046: Brew template save/reuse and repeat last brew
    @Test
    fun testRepeatLastBrewExtraction() {
        val entries = listOf(
            BrewJournalEntry(id = "e1", brewMethod = "V60", doseGrams = 15.0, waterGrams = 250.0, brewDurationSeconds = 180, createdAt = 1000L),
            BrewJournalEntry(id = "e2", brewMethod = "Espresso", doseGrams = 18.0, waterGrams = 36.0, brewDurationSeconds = 28, createdAt = 2000L)
        )

        val last = entries.maxByOrNull { it.createdAt }
        assertNotNull(last)
        assertEquals("e2", last!!.id)
        assertEquals(18.0, last.doseGrams, 0.01)
        assertEquals(36.0, last.waterGrams, 0.01)
    }

    // S2-047: CSV export and backup conflict detection
    @Test
    fun testJournalCsvExportAndConflictStatus() {
        val entries = listOf(
            BrewJournalEntry(
                id = "csv-1",
                beanName = "Colombia Pink Bourbon",
                brewMethod = "Pour Over (V60)",
                doseGrams = 15.0,
                waterGrams = 250.0,
                brewDurationSeconds = 180,
                measuredTdsPercent = 1.35,
                rating = 9.0,
                generalNotes = "Crisp, tropical fruit"
            )
        )

        val csv = BrewAnalyticsEngine.exportJournalToCsv(entries)
        assertTrue(csv.contains("id,createdAt,beanName,brewMethod"))
        assertTrue(csv.contains("Colombia Pink Bourbon"))
        assertTrue(csv.contains("1:16.67"))

        // Conflict check
        val local = CoffeeBean(id = "b1", name = "Local Bean")
        val remoteNewer = CoffeeBean(id = "b1", name = "Remote Bean")
        val status = if (local.name == remoteNewer.name) ImportRecordStatus.IDENTICAL else ImportRecordStatus.MODIFIED_REMOTE_NEWER
        assertEquals(ImportRecordStatus.MODIFIED_REMOTE_NEWER, status)
    }

    // S2-048: Beanconqueror JSON parser
    @Test
    fun testBeanconquerorJsonMapping() {
        val mockJson = """
        {
          "BEANS": [
            {
              "id": "bc-bean-1",
              "name": "Ethiopia Yirgacheffe",
              "roaster": "Nordic Roastery",
              "origin": "Ethiopia",
              "processing": "Washed",
              "elevation": "1900 - 2100 m",
              "roastingDate": "2026-10-01"
            }
          ],
          "BREWS": [
            {
              "id": "bc-brew-1",
              "bean": "bc-bean-1",
              "method_of_preparation": "V60",
              "coffee_amount": 16.0,
              "water_amount": 250.0,
              "total_brew_time": 190,
              "grind_size": "20 clicks",
              "grinder": "Comandante",
              "tds": 1.38,
              "rating": 4.5,
              "note": "Bright jasmine and lemon"
            }
          ]
        }
        """.trimIndent()

        val (beans, brews) = BrewAnalyticsEngine.parseBeanconquerorJson(mockJson)
        assertEquals(1, beans.size)
        assertEquals("Ethiopia Yirgacheffe", beans.first().name)
        assertEquals("Nordic Roastery", beans.first().producer)
        assertEquals(1900, beans.first().altitudeMeters)

        assertEquals(1, brews.size)
        val brew = brews.first()
        assertEquals(16.0, brew.doseGrams, 0.01)
        assertEquals(250.0, brew.waterGrams, 0.01)
        assertEquals(1.38, brew.measuredTdsPercent!!, 0.01)
        // Beanconqueror 4.5/5 mapped to 9.0/10
        assertEquals(9.0, brew.rating!!, 0.01)
        assertEquals("Comandante", brew.grinderModel)
    }

    // S2-049: Cafe cup journal (manual entry, zero maps)
    @Test
    fun testCafeCupEntryPersistence() {
        val cafeCup = CafeCupEntry(
            id = "cafe-01",
            cafeName = "The Barn Berlin",
            roaster = "The Barn",
            drinkName = "Flat White",
            pricePaid = "4.50 EUR",
            rating = 9.0,
            notes = "Silky texture, sweet hazelnut finish",
            photoUris = listOf("content://media/cafe_photo.jpg")
        )

        assertEquals("The Barn Berlin", cafeCup.cafeName)
        assertEquals(9.0, cafeCup.rating, 0.01)
        assertEquals(1, cafeCup.photoUris.size)
    }

    // S2-050: Manual optional caffeine consumption estimate with uncertainty
    @Test
    fun testCaffeineEstimationAndUncertainty() {
        // 18.0g of 100% Arabica (1.20% dry caffeine, 85% nominal extraction)
        // Available: 18.0 * 0.0120 * 1000 = 216.0 mg
        // Nominal (85%): 216.0 * 0.85 = 183.6 mg
        // Min (80%): 216.0 * 0.80 = 172.8 mg
        // Max (90%): 216.0 * 0.90 = 194.4 mg
        val arabicaEstimate = BrewAnalyticsEngine.estimateCaffeine(18.0, 1.0)
        assertEquals(183.6, arabicaEstimate.estimatedCaffeineMg, 0.1)
        assertEquals(172.8, arabicaEstimate.uncertaintyRangeMg.first, 0.1)
        assertEquals(194.4, arabicaEstimate.uncertaintyRangeMg.second, 0.1)
        assertTrue(arabicaEstimate.speciesRatioDescription.contains("Arabica"))
        assertTrue(arabicaEstimate.disclaimer.contains("Heuristic estimation"))

        // 20.0g of 50/50 Blend
        // Species fraction: 0.5 * 1.20% + 0.5 * 2.20% = 1.70%
        // Available: 20.0 * 0.0170 * 1000 = 340.0 mg
        // Nominal (85%): 340.0 * 0.85 = 289.0 mg
        val blendEstimate = BrewAnalyticsEngine.estimateCaffeine(20.0, 0.5)
        assertEquals(289.0, blendEstimate.estimatedCaffeineMg, 0.1)
        assertTrue(blendEstimate.speciesRatioDescription.contains("50% Arabica"))
    }
}
