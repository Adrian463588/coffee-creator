package com.coffeecreator.app.core.catalog

import java.math.BigDecimal
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveCatalogEquipmentTest {

    // ========================================================================
    // S2-001: Bean CRUD with Full Metadata
    // ========================================================================
    @Test
    fun testBeanRecordCreationAndMetadata() {
        val roastDate = LocalDate.of(2026, 10, 1)
        val bean = DetailedBeanRecord(
            id = "bean-sey-worka",
            roaster = "Sey Coffee",
            name = "Worka Sakaro",
            originCountry = "Ethiopia",
            region = "Gedeb, Yirgacheffe",
            farm = "Halo Hartume",
            producer = "Mijane Woresa",
            varietal = "Kurume, Dega",
            process = ProcessMethod.WASHED,
            altitudeMeters = 2100,
            harvestDate = "2026-02-15",
            roastDate = roastDate,
            roastLevel = RoastLevel.LIGHT,
            tastingNotes = listOf("Peach", "Jasmine", "Bergamot"),
            tags = listOf("Floral", "High-Altitude", "African"),
            isFavorite = true
        )

        assertEquals("Sey Coffee", bean.roaster)
        assertEquals("Worka Sakaro", bean.name)
        assertEquals("Ethiopia", bean.originCountry)
        assertEquals(ProcessMethod.WASHED, bean.process)
        assertEquals(2100, bean.altitudeMeters)
        assertEquals(RoastLevel.LIGHT, bean.roastLevel)
        assertTrue(bean.isFavorite)
        assertEquals(3, bean.tastingNotes.size)
    }

    // ========================================================================
    // S2-002: Bag Inventory Lifecycle & Mass Balance Deduction
    // ========================================================================
    @Test
    fun testBagInventoryDeductionAndStatusTransitions() {
        val bag = BagInventoryItem(
            id = "bag-001",
            beanId = "bean-sey-worka",
            packageMassGrams = BigDecimal("250.00"),
            remainingMassGrams = BigDecimal("250.00"),
            status = BagStatus.PURCHASED
        )

        // First brew: 18.0g consumes and activates bag
        val result1 = InventoryDeductionEngine.deductDose(bag, BigDecimal("18.00"))
        assertTrue(result1 is InventoryDeductionResult.Success)
        val s1 = result1 as InventoryDeductionResult.Success
        assertEquals(BigDecimal("232.00"), s1.updatedBag.remainingMassGrams)
        assertEquals(BagStatus.ACTIVE, s1.updatedBag.status)
        assertNotNull(s1.updatedBag.openedAtEpochMs)
        assertFalse(s1.wasDepleted)

        // Final depletion to zero
        val almostEmptyBag = s1.updatedBag.copy(remainingMassGrams = BigDecimal("15.00"))
        val result2 = InventoryDeductionEngine.deductDose(almostEmptyBag, BigDecimal("15.00"))
        assertTrue(result2 is InventoryDeductionResult.Success)
        val s2 = result2 as InventoryDeductionResult.Success
        assertEquals(BigDecimal("0.00"), s2.updatedBag.remainingMassGrams)
        assertEquals(BagStatus.FINISHED, s2.updatedBag.status)
        assertNotNull(s2.updatedBag.finishedAtEpochMs)
        assertTrue(s2.wasDepleted)
    }

    @Test
    fun testFrozenBagCannotBeBrewedUntilThawed() {
        val bag = BagInventoryItem(
            id = "bag-002",
            beanId = "bean-sey-worka",
            packageMassGrams = BigDecimal("250.00"),
            remainingMassGrams = BigDecimal("200.00"),
            status = BagStatus.ACTIVE
        )

        val frozenBag = InventoryDeductionEngine.freezeBag(bag)
        assertEquals(BagStatus.FROZEN, frozenBag.status)
        assertEquals(1, frozenBag.freezeHistory.size)
        assertTrue(frozenBag.freezeHistory.first().isOngoing)

        // Deduction fails
        val failResult = InventoryDeductionEngine.deductDose(frozenBag, BigDecimal("20.00"))
        assertTrue(failResult is InventoryDeductionResult.Error)
        assertTrue((failResult as InventoryDeductionResult.Error).reason.contains("FROZEN_BAG_CANNOT_BREW"))

        // Thaw bag
        val thawedBag = InventoryDeductionEngine.thawBag(frozenBag)
        assertEquals(BagStatus.ACTIVE, thawedBag.status)
        assertFalse(thawedBag.freezeHistory.first().isOngoing)

        // Deduction now succeeds
        val successResult = InventoryDeductionEngine.deductDose(thawedBag, BigDecimal("20.00"))
        assertTrue(successResult is InventoryDeductionResult.Success)
    }

    // ========================================================================
    // S2-003: Roast-Rest Calculator & Freshness Heuristic
    // ========================================================================
    @Test
    fun testRoastRestFreshnessHeuristic() {
        val roastDate = LocalDate.of(2026, 10, 1)

        // Day 3: Too fresh
        val analysisDay3 = RoastRestCalculator.calculateFreshness(
            roastDate = roastDate,
            roastLevel = RoastLevel.LIGHT,
            process = ProcessMethod.WASHED,
            evaluationDate = LocalDate.of(2026, 10, 4)
        )
        assertEquals(3L, analysisDay3.effectiveRestDays)
        assertEquals(FreshnessState.TOO_FRESH_DEGASSING, analysisDay3.state)

        // Day 14: Peak flavor
        val analysisDay14 = RoastRestCalculator.calculateFreshness(
            roastDate = roastDate,
            roastLevel = RoastLevel.LIGHT,
            process = ProcessMethod.WASHED,
            evaluationDate = LocalDate.of(2026, 10, 15)
        )
        assertEquals(14L, analysisDay14.effectiveRestDays)
        assertEquals(FreshnessState.PEAK_FLAVOR, analysisDay14.state)
        assertTrue(analysisDay14.heuristicRationale.contains("Optimal aromatic expression"))

        // Day 45: Stable mature
        val analysisDay45 = RoastRestCalculator.calculateFreshness(
            roastDate = roastDate,
            roastLevel = RoastLevel.LIGHT,
            process = ProcessMethod.WASHED,
            evaluationDate = LocalDate.of(2026, 11, 15)
        )
        assertEquals(45L, analysisDay45.effectiveRestDays)
        assertEquals(FreshnessState.STABLE_MATURE, analysisDay45.state)

        // Day 70: Stale
        val analysisDay70 = RoastRestCalculator.calculateFreshness(
            roastDate = roastDate,
            roastLevel = RoastLevel.LIGHT,
            process = ProcessMethod.WASHED,
            evaluationDate = LocalDate.of(2026, 12, 10)
        )
        assertEquals(FreshnessState.STALE_OXIDIZED, analysisDay70.state)
    }

    // ========================================================================
    // S2-004: Grinder Records & Calibration Unit
    // ========================================================================
    @Test
    fun testGrinderRecordsAndBurrGeometry() {
        val grinder = GrinderRecord(
            id = "grinder-c40",
            brand = "Comandante",
            model = "C40 MK4 Nitro Blade",
            calibrationUnit = CalibrationUnit.CLICKS,
            burrProfile = BurrProfile(
                burrType = BurrType.CONICAL,
                diameterMm = BigDecimal("39.00"),
                material = BurrMaterial.STAINLESS_STEEL,
                burrModelName = "Nitro Blade"
            ),
            zeroPointOffset = BigDecimal.ZERO,
            clicksPerRotation = 12,
            isFavorite = true
        )

        assertEquals("Comandante", grinder.brand)
        assertEquals(CalibrationUnit.CLICKS, grinder.calibrationUnit)
        assertEquals(BurrType.CONICAL, grinder.burrProfile.burrType)
        assertEquals(BigDecimal("39.00"), grinder.burrProfile.diameterMm)
    }

    // ========================================================================
    // S2-005: Equipment Catalog Categories
    // ========================================================================
    @Test
    fun testEquipmentCatalogHierarchy() {
        val dripper = EquipmentItem(
            id = "eq-v60",
            category = EquipmentCategory.DRIPPER,
            brand = "Hario",
            model = "V60 Ceramic 02",
            parameters = mapOf("Material" to "Ceramic", "Angle" to "60deg"),
            isFavorite = true
        )
        val basket = EquipmentItem(
            id = "eq-vst-18",
            category = EquipmentCategory.PORTAFILTER_BASKET,
            brand = "VST",
            model = "18g Precision Basket",
            parameters = mapOf("Diameter" to "58.5mm", "Holes" to "660")
        )

        assertEquals(EquipmentCategory.DRIPPER, dripper.category)
        assertEquals(EquipmentCategory.PORTAFILTER_BASKET, basket.category)
        assertEquals("Ceramic", dripper.parameters["Material"])
    }

    // ========================================================================
    // S2-006: Water Mineral Profile & Hardness/Alkalinity Conversions
    // ========================================================================
    @Test
    fun testWaterChemistryCalculationsAndPresets() {
        // GH: 20 * 2.4973 + 10 * 4.1179 = 49.946 + 41.179 = 91.125 -> 91.13
        // KH: 48.77 * 0.82016 = 39.999 -> 40.00
        val profile = WaterChemistryCalculator.formulateWater(
            name = "Test Custom Water",
            calciumPpm = BigDecimal("20.00"),
            magnesiumPpm = BigDecimal("10.00"),
            sodiumPpm = BigDecimal("5.00"),
            bicarbonatePpm = BigDecimal("48.77"),
            ph = BigDecimal("7.0")
        )

        assertEquals(BigDecimal("91.13"), profile.totalHardnessPpmCaCO3)
        assertEquals(BigDecimal("40.00"), profile.totalAlkalinityPpmCaCO3)
        assertEquals(BigDecimal("83.77"), profile.calculatedTdsPpm)

        // Verify SCA preset
        val sca = WaterChemistryCalculator.PRESET_SCA
        assertEquals("SCA Standard Water", sca.name)
        assertTrue(sca.totalHardnessPpmCaCO3 > BigDecimal("60.00"))
        assertEquals(BigDecimal("40.00"), sca.totalAlkalinityPpmCaCO3)
    }

    // ========================================================================
    // S2-007: Food-Grade Ingredient Catalog
    // ========================================================================
    @Test
    fun testFoodGradeIngredientValidation() {
        val ingredient = FoodGradeIngredientItem(
            id = "ing-citric",
            name = "Food-Grade Citric Acid (10% Solution)",
            densityGramsPerMl = BigDecimal("1.04"),
            allergens = emptyList(),
            supplierMaxPercent = BigDecimal("2.50"),
            isFoodGradeVerified = true,
            provenance = "USP Food Chemicals Codex"
        )

        assertEquals(BigDecimal("1.04"), ingredient.densityGramsPerMl)
        assertTrue(ingredient.isFoodGradeVerified)
        assertEquals(BigDecimal("2.50"), ingredient.supplierMaxPercent)
    }

    // ========================================================================
    // S2-008: Bag Offline OCR Parsing
    // ========================================================================
    @Test
    fun testOfflineBagOcrParser() {
        val ocrText = """
            SEY COFFEE
            WORKA SAKARO
            ETHIOPIA
            VARIETY: KURUME
            WASHED PROCESS
            ALTITUDE: 2100 MASL
            ROASTED ON 2026-10-01
            NET WT: 250G
        """.trimIndent()

        val draft = OfflineBagOcrParser.parseBagText("content://mock/bag.jpg", ocrText)

        assertEquals("ETHIOPIA", draft.origin.value)
        assertEquals(ProcessMethod.WASHED, draft.process.value)
        assertEquals(2100, draft.altitudeMeters.value)
        assertEquals(LocalDate.of(2026, 10, 1), draft.roastDate.value)
        assertEquals(BigDecimal("250.00"), draft.packageMassGrams.value)
        assertNull("Heuristic text parsing is not calibrated OCR confidence.", draft.origin.confidence)
    }

    @Test
    fun givenBagTextWithoutMassOrRoastDate_whenParsed_thenUnknownFieldsStayUnknown() {
        val draft = OfflineBagOcrParser.parseBagText("content://user/bag.jpg", "ETHIOPIA\nWASHED")

        assertNull(draft.packageMassGrams.value)
        assertNull(draft.roastDate.value)
        assertNull(draft.packageMassGrams.confidence)
        assertNull(draft.process.confidence)
    }

    @Test
    fun givenNonPositiveBagMass_whenParsed_thenNoDefaultInventoryMassIsInvented() {
        val draft = OfflineBagOcrParser.parseBagText("content://user/bag.jpg", "NET WT: 0G")

        assertNull(draft.packageMassGrams.value)
        assertNull(draft.packageMassGrams.confidence)
    }

    // ========================================================================
    // S2-009: Bulk Import/Export with Conflict Resolution
    // ========================================================================
    @Test
    fun testCatalogImportExportWithConflictStrategies() {
        val bean1 = DetailedBeanRecord(
            id = "bean-101",
            roaster = "Sey Coffee",
            name = "Worka Sakaro",
            originCountry = "Ethiopia",
            roastDate = LocalDate.of(2026, 10, 1),
            roastLevel = RoastLevel.LIGHT
        )

        val payload = CatalogExportPayload(
            beans = listOf(bean1),
            equipment = listOf(
                EquipmentItem("eq-01", EquipmentCategory.DRIPPER, "Hario", "V60 02")
            )
        )

        val json = CatalogImportExportEngine.exportToJson(payload)
        assertTrue(json.contains("Worka Sakaro"))

        // Preview dry run
        val preview = CatalogImportExportEngine.previewImport(json, listOf(bean1), emptyList())
        assertEquals(2, preview.totalRecords)
        assertEquals(1, preview.conflictingRecordsCount)

        // Strategy OVERWRITE: update in place
        val (receiptOverwrite, updatedData) = CatalogImportExportEngine.executeImport(
            json,
            ConflictResolutionStrategy.OVERWRITE,
            listOf(bean1),
            emptyList()
        )
        assertEquals(1, receiptOverwrite.updatedCount)
        assertEquals(1, receiptOverwrite.createdCount) // eq-01 was new

        // Strategy KEEP_BOTH_RENAME
        val (receiptRename, renamedData) = CatalogImportExportEngine.executeImport(
            json,
            ConflictResolutionStrategy.KEEP_BOTH_RENAME,
            listOf(bean1),
            emptyList()
        )
        assertEquals(1, receiptRename.renamedCount)
        assertEquals(2, renamedData.first.size)
        assertTrue(renamedData.first.any { it.name.contains("(Imported)") })

        // Strategy SKIP_DUPLICATE
        val (receiptSkip, skippedData) = CatalogImportExportEngine.executeImport(
            json,
            ConflictResolutionStrategy.SKIP_DUPLICATE,
            listOf(bean1),
            emptyList()
        )
        assertEquals(1, receiptSkip.skippedCount)
        assertEquals(1, skippedData.first.size)
    }

    // ========================================================================
    // S2-010: Global Offline Search, Tags & Favorites
    // ========================================================================
    @Test
    fun testSearchFilteringAndFavorites() {
        val b1 = DetailedBeanRecord(
            id = "b1",
            roaster = "Sey Coffee",
            name = "Worka Sakaro",
            originCountry = "Ethiopia",
            roastDate = LocalDate.of(2026, 10, 1),
            tags = listOf("Floral", "Light"),
            isFavorite = true
        )
        val b2 = DetailedBeanRecord(
            id = "b2",
            roaster = "Tim Wendelboe",
            name = "Caballero Geisha",
            originCountry = "Honduras",
            roastDate = LocalDate.of(2026, 10, 2),
            tags = listOf("Jasmine", "Clean"),
            isFavorite = false
        )
        val beans = listOf(b1, b2)

        // Filter favorite
        val favs = beans.filter { it.isFavorite }
        assertEquals(1, favs.size)
        assertEquals("b1", favs.first().id)

        // Filter by tag
        val florals = beans.filter { it.tags.contains("Floral") }
        assertEquals(1, florals.size)

        // Search text
        val query = "Honduras"
        val searched = beans.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.originCountry.contains(query, ignoreCase = true) ||
                it.roaster.contains(query, ignoreCase = true)
        }
        assertEquals(1, searched.size)
        assertEquals("b2", searched.first().id)
    }

    // ========================================================================
    // S2-011: Green Coffee Physical QC & Defect Points
    // ========================================================================
    @Test
    fun testGreenCoffeePhysicalQcAndDefects() {
        val sample = GreenCoffeeLot(
            id = "green-eth-01",
            lotNumber = "LOT-2026-ETH-004",
            originCountry = "Ethiopia",
            producer = "Mijane Woresa",
            harvestYear = "2026",
            moisturePercent = BigDecimal("10.80"),
            bulkDensityGramsPerLiter = BigDecimal("695.00"),
            waterActivityAw = BigDecimal("0.54"),
            category1DefectCount = 0,
            category2DefectCount = 3,
            screenDistributionPercent = mapOf(17 to BigDecimal("70.00"), 18 to BigDecimal("30.00")),
            sampleStatus = SampleLotStatus.OFFER_SAMPLE
        )

        // Total defects = 0 * 1.0 + 3 * 0.20 = 0.60
        assertEquals(BigDecimal("0.60"), sample.totalDefectPoints)
        assertTrue(sample.isSpecialtyGradeEligible)

        // Fail case: Category 1 defect present
        val defectiveSample = sample.copy(category1DefectCount = 1)
        assertFalse(defectiveSample.isSpecialtyGradeEligible)
    }

    // ========================================================================
    // S2-012: Roast Batch Curves, DTR & Blend Formulation
    // ========================================================================
    @Test
    fun testRoastBatchDtrAndBlendFormulation() {
        // DTR: total 600s, first crack 480s -> dev = 120s -> 120/600 = 20.00%
        val dtr = RoastBatchRecord.calculateDtr(600, 480)
        assertEquals(BigDecimal("20.00"), dtr)

        // RoR calculation
        val rawCurve = listOf(
            0 to 200.0,
            60 to 120.0,
            120 to 140.0,
            180 to 160.0
        )
        val points = RoastBatchRecord.calculateRorSeries(rawCurve)
        assertEquals(4, points.size)
        // From 60s to 120s: (140 - 120) / 60 * 60 = 20.00 C/min
        assertEquals(BigDecimal("20.00"), points[2].rateOfRiseCelsiusPerMinute)

        // Blend formulation summing to 100%
        val validBlend = listOf(
            BlendComponentSpec("bean-eth", BigDecimal("60.00")),
            BlendComponentSpec("bean-col", BigDecimal("40.00"))
        )
        val batch = RoastBatchRecord(
            id = "roast-001",
            batchNumber = "B-2026-101",
            chargeTemperatureCelsius = BigDecimal("200.00"),
            dropTemperatureCelsius = BigDecimal("212.00"),
            totalRoastDurationSeconds = 600,
            firstCrackTimeSeconds = 480,
            developmentTimeRatioPercent = dtr,
            blendComponents = validBlend
        )
        assertEquals(BigDecimal("20.00"), batch.developmentTimeRatioPercent)
        assertEquals(2, batch.blendComponents.size)
    }
}
