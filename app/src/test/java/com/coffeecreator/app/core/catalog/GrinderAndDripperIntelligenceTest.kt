package com.coffeecreator.app.core.catalog

import com.coffeecreator.app.core.ai.ToolExecutor
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GrinderAndDripperIntelligenceTest {

    // ========================================================================
    // TC-301 & GR-01: 44-Model Grinder Catalog & 26-Dripper Catalog Lossless
    // ========================================================================
    @Test
    fun testAll44GrindersAnd26DrippersImportedLosslessly() {
        assertEquals(44, GrinderIntelligenceCatalog.ALL_44_GRINDERS.size)
        assertEquals(26, DripperIntelligenceCatalog.ALL_26_DRIPPERS.size)

        // Verify key models exist
        assertNotNull(GrinderIntelligenceCatalog.getGrinderById("grinder-comandante-c40-mk4"))
        assertNotNull(GrinderIntelligenceCatalog.getGrinderById("grinder-1zpresso-j-ultra"))
        assertNotNull(GrinderIntelligenceCatalog.getGrinderById("grinder-1zpresso-zp6-special"))
        assertNotNull(GrinderIntelligenceCatalog.getGrinderById("grinder-fellow-ode-gen-2"))
        assertNotNull(GrinderIntelligenceCatalog.getGrinderById("grinder-kingrinder-k6"))
        assertNotNull(GrinderIntelligenceCatalog.getGrinderById("grinder-timemore-c2"))

        // Verify drippers
        assertNotNull(DripperIntelligenceCatalog.getDripperById("dripper-hario-v60"))
        assertNotNull(DripperIntelligenceCatalog.getDripperById("dripper-hario-switch"))
        assertNotNull(DripperIntelligenceCatalog.getDripperById("dripper-hario-v60-mugen"))
        assertNotNull(DripperIntelligenceCatalog.getDripperById("dripper-kalita-wave-155"))
        assertNotNull(DripperIntelligenceCatalog.getDripperById("dripper-orea-v4"))
        assertNotNull(DripperIntelligenceCatalog.getDripperById("dripper-timemore-b75"))
    }

    // ========================================================================
    // TC-302: Hand vs Electric Classification
    // ========================================================================
    @Test
    fun testElectricGrindersExcludedFromHandFilter() {
        val electricGrinders = GrinderIntelligenceCatalog.searchGrinders("", GrinderType.ELECTRIC)
        val handGrinders = GrinderIntelligenceCatalog.searchGrinders("", GrinderType.HAND)

        assertEquals(3, electricGrinders.size)
        val electricNames = electricGrinders.map { it.modelName }
        assertTrue(electricNames.contains("Breville Smart Grinder Pro"))
        assertTrue(electricNames.contains("Fellow Ode Gen 2"))
        assertTrue(electricNames.contains("Timemore Sculptor 064S / 078S"))

        // Hand grinder filter should not include electric grinders
        assertEquals(41, handGrinders.size)
        assertFalse(handGrinders.any { it.modelName.contains("Fellow Ode") })
        assertFalse(handGrinders.any { it.modelName.contains("Breville") })
    }

    // ========================================================================
    // TC-303 & DRIP-002: Unknown Flow Handling Without Fabrication
    // ========================================================================
    @Test
    fun testUnknownDripperFlowDoesNotFabricateNumericRate() {
        val unknownDripper = DripperIntelligenceCatalog.getDripperById("dripper-1zpresso-01")
        assertNotNull(unknownDripper)
        assertEquals(FlowClass.UNKNOWN, unknownDripper!!.flowClass)

        val advice = DripperIntelligenceCatalog.getDripperFlowAdvice(unknownDripper.id)
        assertEquals(FlowClass.UNKNOWN, advice.flowClass)
        assertTrue(advice.qualitativeDrainSpeed.contains("unverified", ignoreCase = true))
        assertFalse(advice.qualitativeDrainSpeed.contains("mL/s"))
    }

    // ========================================================================
    // TC-304 & DRIP-008: Typed Units & Zero Cross-Model Click Equivalence
    // ========================================================================
    @Test
    fun testGrinderSettingsAreTypedAndIndependent() {
        val c40 = GrinderIntelligenceCatalog.getGrinderById("grinder-comandante-c40-mk4")!!
        val jUltra = GrinderIntelligenceCatalog.getGrinderById("grinder-1zpresso-j-ultra")!!
        val ode = GrinderIntelligenceCatalog.getGrinderById("grinder-fellow-ode-gen-2")!!

        assertEquals(AdjustmentUnit.CLICK, c40.adjustmentUnit)
        assertEquals(AdjustmentUnit.ROTATION, jUltra.adjustmentUnit)
        assertEquals(AdjustmentUnit.DIAL_NUMBER, ode.adjustmentUnit)

        // Validate typed inputs
        assertTrue(GrinderIntelligenceCatalog.validateSetting(c40.id, "24 Klik"))
        assertTrue(GrinderIntelligenceCatalog.validateSetting(jUltra.id, "3.0 Putaran"))
        assertTrue(GrinderIntelligenceCatalog.validateSetting(ode.id, "5.0"))
        assertFalse(GrinderIntelligenceCatalog.validateSetting(c40.id, ""))
    }

    // ========================================================================
    // TC-307 & GR-10: Incompatible Grinder Warning
    // ========================================================================
    @Test
    fun testFilterGrinderEspressoWarning() {
        val ode = GrinderIntelligenceCatalog.checkCompatibility("grinder-fellow-ode-gen-2", "Espresso")
        assertFalse(ode.isCompatible)
        assertNotNull(ode.warningMessage)
        assertTrue(ode.warningMessage!!.contains("not recommended for espresso", ignoreCase = true))

        val zp6 = GrinderIntelligenceCatalog.checkCompatibility("grinder-1zpresso-zp6-special", "Espresso")
        assertFalse(zp6.isCompatible)
        assertTrue(zp6.warningMessage!!.contains("not recommended for espresso", ignoreCase = true))

        val jUltra = GrinderIntelligenceCatalog.checkCompatibility("grinder-1zpresso-j-ultra", "Espresso")
        assertTrue(jUltra.isCompatible)
    }

    // ========================================================================
    // TC-308 & TC-309 & TC-310: Deterministic Scaling & Pour Calculations
    // ========================================================================
    @Test
    fun testDeterministicStagedPours() {
        // 15g / 250g
        val stages15 = DripperIntelligenceCatalog.calculateStagedPours(
            BigDecimal("15.0"),
            BigDecimal("250.0"),
            pourCount = 3
        )
        assertEquals(3, stages15.size)
        assertEquals(BigDecimal("250.0"), stages15.last().cumulativeMassGrams)

        // 20g / 300g
        val stages20 = DripperIntelligenceCatalog.calculateStagedPours(
            BigDecimal("20.0"),
            BigDecimal("300.0"),
            pourCount = 4
        )
        assertEquals(4, stages20.size)
        assertEquals(BigDecimal("300.0"), stages20.last().cumulativeMassGrams)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testZeroDoseThrowsException() {
        DripperIntelligenceCatalog.calculateStagedPours(BigDecimal.ZERO, BigDecimal("250.0"))
    }

    // ========================================================================
    // DRIP-003, DRIP-004, DRIP-005, DRIP-006: Dripper Specific Flow Behavior
    // ========================================================================
    @Test
    fun testMugenCoarserThanV60() {
        val mugen = DripperIntelligenceCatalog.getDripperById("dripper-hario-v60-mugen")!!
        assertEquals(FlowClass.RESTRICTED, mugen.flowClass)
        assertTrue(mugen.grindBaselineRelativeToV60.contains("Coarser than V60", ignoreCase = true))
    }

    @Test
    fun testSwitchValveOpenClosedModes() {
        val switchDripper = DripperIntelligenceCatalog.getDripperById("dripper-hario-switch")!!
        assertTrue(switchDripper.isValveEquipped)

        val closedAdvice = DripperIntelligenceCatalog.getDripperFlowAdvice(switchDripper.id, ValveState.CLOSED)
        assertTrue(closedAdvice.valveNotice!!.contains("CLOSED", ignoreCase = true))
        assertTrue(closedAdvice.qualitativeDrainSpeed.contains("Held", ignoreCase = true))

        val openAdvice = DripperIntelligenceCatalog.getDripperFlowAdvice(switchDripper.id, ValveState.OPEN)
        assertTrue(openAdvice.valveNotice!!.contains("OPEN", ignoreCase = true))
        assertTrue(openAdvice.qualitativeDrainSpeed.contains("percolation", ignoreCase = true))
    }

    @Test
    fun testOreaV4InterchangeableBottoms() {
        val orea = DripperIntelligenceCatalog.getDripperById("dripper-orea-v4")!!
        assertTrue(orea.supportedBottomVariants.contains("FAST"))
        assertTrue(orea.supportedBottomVariants.contains("CLASSIC"))
        assertTrue(orea.supportedBottomVariants.contains("APEX"))

        val classicAdvice = DripperIntelligenceCatalog.getDripperFlowAdvice(orea.id, bottomVariant = "CLASSIC")
        assertTrue(classicAdvice.relativeGrindAdjustmentNote.contains("CLASSIC bottom: Medium-coarse", ignoreCase = true))
    }

    @Test
    fun testB75GeometryFlatAndFast() {
        val b75 = DripperIntelligenceCatalog.getDripperById("dripper-timemore-b75")!!
        assertEquals(BrewerGeometry.FLAT_BOTTOM, b75.geometry)
        assertEquals(FlowClass.FAST, b75.flowClass)
    }

    // ========================================================================
    // GR-08: Dial-In Assistant Reversible Recommendations
    // ========================================================================
    @Test
    fun testDialInAssistantReversibleRecommendations() {
        val bitterAdvice = GrinderIntelligenceCatalog.dialInRecommendation(
            brewTimeSec = 250,
            targetTimeSec = 180,
            sensoryFeedback = "Bitter, astringent, dry finish"
        )
        assertTrue(bitterAdvice.proposedAction.contains("coarser", ignoreCase = true))
        assertFalse(bitterAdvice.isDeterministic)

        val sourAdvice = GrinderIntelligenceCatalog.dialInRecommendation(
            brewTimeSec = 110,
            targetTimeSec = 180,
            sensoryFeedback = "Sour, watery, fast drawdown"
        )
        assertTrue(sourAdvice.proposedAction.contains("finer", ignoreCase = true))
        assertFalse(sourAdvice.isDeterministic)
    }

    // ========================================================================
    // GR-12: Calibration Transfer Export & Import
    // ========================================================================
    @Test
    fun testGrinderProfileTransferJson() {
        val profile = UserGrinderProfile(
            id = "custom-profile-1",
            grinderModelId = "grinder-comandante-c40-mk4",
            customName = "My Filter C40",
            selectedVariantId = "c40-std",
            zeroCalibrationOffset = -1.0,
            burrCondition = "SEASONED",
            calibrationNotes = "Clicks start at -1"
        )

        val json = GrinderIntelligenceCatalog.exportProfileToJson(profile)
        assertTrue(json.contains("\"schemaVersion\": 1"))
        assertTrue(json.contains("grinder-comandante-c40-mk4"))
        assertTrue(json.contains("My Filter C40"))

        val imported = GrinderIntelligenceCatalog.importProfileFromJson(json)
        assertEquals(profile.id, imported.id)
        assertEquals(profile.grinderModelId, imported.grinderModelId)
        assertEquals(profile.customName, imported.customName)
        assertEquals(-1.0, imported.zeroCalibrationOffset, 0.001)
    }

    // ========================================================================
    // BR-15: ToolExecutor AI Tools
    // ========================================================================
    @Test
    fun testToolExecutorGrinderAndDripperTools() = runBlocking {
        val executor = ToolExecutor(
            beans = { emptyList() },
            ingredients = { emptyList() },
            recipes = { emptyList() },
            search = { error("No RAG needed") }
        )

        val grinderResult = executor.execute(
            "lookup_grinder_range",
            mapOf("grinderModel" to "Comandante C40", "method" to "V60")
        )
        assertTrue(grinderResult.isSuccess)
        assertTrue(grinderResult.output.contains("18 - 24 Klik"))

        val dripperResult = executor.execute(
            "lookup_dripper_geometry",
            mapOf("dripperName" to "Hario V60 Mugen")
        )
        assertTrue(dripperResult.isSuccess)
        assertTrue(dripperResult.output.contains("RESTRICTED"))

        val dialInResult = executor.execute(
            "recommend_dial_in_step",
            mapOf("actualDurationSeconds" to 250.0, "targetDurationSeconds" to 180.0, "sensoryFeedback" to "bitter")
        )
        assertTrue(dialInResult.isSuccess)
        assertTrue(dialInResult.output.contains("coarser"))
    }
}
