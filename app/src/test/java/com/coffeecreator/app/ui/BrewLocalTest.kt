package com.coffeecreator.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.CoffeeApplication
import com.coffeecreator.app.MainActivity
import com.coffeecreator.app.core.ai.personalization.AcidityTolerance
import com.coffeecreator.app.core.ai.personalization.BitternessSensitivity
import com.coffeecreator.app.core.ai.personalization.BodyPreference
import com.coffeecreator.app.core.ai.personalization.ElevationUnit
import com.coffeecreator.app.core.ai.personalization.PalateProfile
import com.coffeecreator.app.core.ai.personalization.PalateQuizAnswers
import com.coffeecreator.app.core.ai.personalization.PalateVector
import com.coffeecreator.app.core.ai.personalization.RoastPreference
import com.coffeecreator.app.core.ai.personalization.SweetnessPriority
import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.BurrMaterial
import com.coffeecreator.app.core.catalog.BurrProfile
import com.coffeecreator.app.core.catalog.BurrType
import com.coffeecreator.app.core.catalog.CalibrationUnit
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.EquipmentCategory
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.FoodGradeIngredientItem
import com.coffeecreator.app.core.catalog.GreenCoffeeLot
import com.coffeecreator.app.core.catalog.GrinderRecord
import com.coffeecreator.app.core.catalog.ProcessMethod
import com.coffeecreator.app.core.catalog.RoastBatchRecord
import com.coffeecreator.app.core.catalog.RoastLevel
import com.coffeecreator.app.core.catalog.SampleLotStatus
import com.coffeecreator.app.core.catalog.WaterChemistryCalculator
import com.coffeecreator.app.core.catalog.WaterMineralProfile
import com.coffeecreator.app.core.data.BackupCodec
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.journal.BrewJournalEntry
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.reliability.AppLanguage
import com.coffeecreator.app.core.reliability.MassUnit
import com.coffeecreator.app.core.reliability.TemperatureUnit
import com.coffeecreator.app.core.reliability.UserUnitPreferences
import com.coffeecreator.app.core.reliability.VolumeUnit
import com.coffeecreator.app.core.sensory.CuppingTemplateType
import com.coffeecreator.app.core.sensory.DegassingPhase
import com.coffeecreator.app.core.sensory.MaskingMode
import com.coffeecreator.app.core.sensory.PhysicalQcRecord
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.RecipeDraft
import com.coffeecreator.app.ui.viewmodel.addDetailedBean
import com.coffeecreator.app.ui.viewmodel.addEquipmentItem
import com.coffeecreator.app.ui.viewmodel.addFoodGradeIngredient
import com.coffeecreator.app.ui.viewmodel.addGreenLot
import com.coffeecreator.app.ui.viewmodel.addGrinder
import com.coffeecreator.app.ui.viewmodel.addRoastBatch
import com.coffeecreator.app.ui.viewmodel.addWaterProfile
import com.coffeecreator.app.ui.viewmodel.createCuppingSession
import com.coffeecreator.app.ui.viewmodel.deleteDetailedBean
import com.coffeecreator.app.ui.viewmodel.flushDraft
import com.coffeecreator.app.ui.viewmodel.generateInventoryRecommendations
import com.coffeecreator.app.ui.viewmodel.newRecipe
import com.coffeecreator.app.ui.viewmodel.recordRoastRestObservation
import com.coffeecreator.app.ui.viewmodel.saveCafeCup
import com.coffeecreator.app.ui.viewmodel.saveJournalEntry
import com.coffeecreator.app.ui.viewmodel.savePhysicalQc
import com.coffeecreator.app.ui.viewmodel.sendChat
import com.coffeecreator.app.ui.viewmodel.setUnitPreferences
import com.coffeecreator.app.ui.viewmodel.submitPalateQuiz
import com.coffeecreator.app.ui.viewmodel.updatePalateSliders
import com.coffeecreator.app.ui.viewmodel.updateWeatherParameters
import com.google.gson.Gson
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Local Android framework/Compose execution; does not prove physical-device behavior. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BrewLocalTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun givenRealLocalActivity_whenOpeningManualWorkbench_thenRenderHomeAndBrewWithoutModel() {
        compose.onNodeWithTag("page:Home").assertIsDisplayed()
        capture("home")
        compose.onNodeWithTag("nav:Formulate").performClick()
        compose.onNodeWithTag("page:Formulate").assertIsDisplayed()
        capture("formulation")
    }

    @Test
    fun givenManualDraft_whenSwitchingSectionsAndRecreating_thenValuesAndSelectedSectionSurvive() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Local manual brew")
        field("dose", "18")
        select("Measurements")
        compose.onNodeWithTag("form:dose").assertDoesNotExist()
        field("beverage", "270")
        field("tds", "1.30")
        select("Ingredients")
        compose.onNodeWithTag("form:tds").assertDoesNotExist()
        select("Tools")
        compose.onNodeWithTag("brew:section:Tools").assertIsSelected()
        select("Measurements")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("brew:section:Measurements").assertIsSelected()
        compose.onNodeWithTag("form:save").assertIsDisplayed().performClick()
        compose.waitUntil(20_000) { repository.recipes.value.any { it.name == "Local manual brew" } }
        val recipe = repository.recipes.value.first { it.name == "Local manual brew" }
        assertEquals(18.0, recipe.doseGrams, 0.0)
        assertEquals(270.0, recipe.beverageMassGrams ?: -1.0, 0.0)
        assertEquals(1.3, recipe.measuredTdsPercent ?: -1.0, 0.0)
        capture("measurements")
        compose.onNodeWithTag("nav:Library").performClick()
        compose.onNodeWithTag("page:Library").assertIsDisplayed()
        capture("library")
        compose.onNodeWithTag("nav:more").performClick()
        compose.onNodeWithTag("nav:Models").performClick()
        compose.onNodeWithTag("page:Models").assertIsDisplayed()
        capture("models")
    }

    private fun select(section: String) {
        try {
            compose.onNodeWithTag("page:Formulate").performScrollToIndex(0)
        } catch (_: Throwable) {}
        val node = compose.onNodeWithTag("brew:section:$section")
        try {
            node.performScrollTo()
        } catch (_: Throwable) {}
        node.performClick()
        compose.onNodeWithTag("brew:content:$section").assertIsDisplayed()
    }

    @Test
    fun givenUnsavedDraft_whenNewRecipeThenUndo_thenOriginalFieldsSurviveRecreation() {
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Keep my draft")
        field("dose", "22")
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:new"))
        compose.onNodeWithTag("form:new").performClick()
        compose.onNodeWithTag("form:new").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Undo draft change").assertIsDisplayed().performClick()
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:name"))
        compose.onNodeWithTag("form:name").assertTextContains("Keep my draft")
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:dose"))
        compose.onNodeWithTag("form:dose").assertTextContains("22")
        compose.onNodeWithText("Undo draft change").assertDoesNotExist()
    }

    @Test
    fun givenBackupDraft_whenRestoringOverActiveDraft_thenFlushAndRecreateKeepRestoredDraft() {
        val app = compose.activity.application as CoffeeApplication
        val repository = app.graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Backup draft")
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        val file = File(compose.activity.cacheDir, "owned-records-backup.json")
        awaitMain { model.state.value.ready && !model.state.value.busy }
        compose.runOnUiThread { model.exportBackup(Uri.fromFile(file), compose.activity.contentResolver) }
        awaitMain { !model.state.value.busy && file.exists() }
        assertEquals(null, model.state.value.error)
        val json = file.readText()
        assertEquals(
            "Backup draft",
            Gson().fromJson(BackupCodec.decode(json).payload.settings["draft"], RecipeDraft::class.java).fields["name"]
        )
        field("name", "Stale active draft")
        compose.runOnUiThread {
            model.newRecipe()
            model.flushDraft()
        }
        // Exercise the same record/draft restore used by the SAF action. Android
        // ObjectBox/JNI cache invalidation remains a separate device-only gate.
        val restore = compose.runOnUiThread { model.viewModelScope.async { model.restoreRecords(json) } }
        awaitMain { restore.isCompleted }
        runBlocking { restore.await() }
        compose.runOnUiThread { model.flushDraft() }
        runBlocking { app.awaitBackgroundWrites() }
        assertEquals("Backup draft", Gson().fromJson(repository.settings.value["draft"], RecipeDraft::class.java).fields["name"])
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Undo draft change").assertDoesNotExist()
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:name"))
        compose.onNodeWithTag("form:name").assertTextContains("Backup draft")
        check(file.delete())
    }

    @Test
    fun givenInvalidDraftInsideValidBackup_whenRestoring_thenExistingRecordsAndDraftRemain() {
        val app = compose.activity.application as CoffeeApplication
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }
        compose.onNodeWithTag("nav:Formulate").performClick()
        field("name", "Retain active draft")
        val before = app.graph.repository.beans.value
        val invalid = runBlocking {
            val payload = BackupCodec.decode(app.graph.repository.exportBackupJson()).payload
            BackupCodec.encode(payload.copy(settings = payload.settings + ("draft" to "{\"fields\":{}}")))
        }
        assertTrue(runBlocking { runCatching { model.restoreRecords(invalid) }.isFailure })
        assertEquals(before, app.graph.repository.beans.value)
        compose.onNodeWithTag("form:name").assertTextContains("Retain active draft")
    }

    @Test
    fun givenFreshPalateAndRatingOnlyJournal_whenRequestingRecommendations_thenAbstainWithoutAddingEvidence() {
        val app = compose.activity.application as CoffeeApplication
        val repository = app.graph.repository
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }
        val originalBackup = runBlocking { repository.exportBackupJson() }
        val freshProfile = PalateProfile()
        try {
            // A fresh profile has no learned preference (0 evidence/count), a neutral midpoint vector,
            // maximum per-dimension uncertainty, and no completion/update timestamp.
            assertEquals(0.0, freshProfile.cumulativeEvidenceWeight, 0.0)
            assertFalse(freshProfile.isQuizCompleted)
            assertEquals(0, freshProfile.qualifiedSessionCount)
            assertEquals(PalateVector(0.5, 0.5, 0.5, 0.5, 0.5), freshProfile.vector)
            assertEquals(PalateVector(1.0, 1.0, 1.0, 1.0, 1.0), freshProfile.varianceVector)
            assertEquals(0L, freshProfile.lastUpdatedEpochMs)
            compose.runOnUiThread {
                model.mutableState.update { it.copy(palateProfile = freshProfile, inventoryRecommendations = emptyList()) }
                model.saveJournalEntry(
                    BrewJournalEntry(
                        id = "qa-rating-only-${UUID.randomUUID()}",
                        beanName = "QA rating-only record",
                        brewMethod = "Pour Over (V60)",
                        doseGrams = 18.0,
                        waterGrams = 300.0,
                        brewDurationSeconds = 180,
                        rating = 8.0
                    )
                )
                model.generateInventoryRecommendations()
            }
            assertEquals(freshProfile, model.state.value.palateProfile)
            assertTrue(model.state.value.journalEntries.any { it.beanName == "QA rating-only record" })
            assertTrue(model.state.value.inventoryRecommendations.isEmpty())
            assertTrue(model.state.value.notice.orEmpty().startsWith("Set a palate preference"))
        } finally {
            restoreRecords(model, originalBackup)
        }
    }

    private data class DurableWorkflowRecords(
        val prefix: String,
        val bean: DetailedBeanRecord,
        val lot: GreenCoffeeLot,
        val qc: PhysicalQcRecord,
        val roastRest: RoastRestObservation,
        val grinder: GrinderRecord,
        val equipment: EquipmentItem,
        val water: WaterMineralProfile,
        val ingredient: FoodGradeIngredientItem,
        val roastBatch: RoastBatchRecord,
        val journal: BrewJournalEntry,
        val units: UserUnitPreferences
    )

    private val durableWorkflowSettingKeys = setOf(
        "journal_entries", "cafe_cups", "cuppings", "palate_profile", "weather_parameters", "user_units",
        "physical_qc_records", "roast_rest_observations", "detailed_beans", "grinders", "equipment_catalog",
        "water_profiles", "food_grade_ingredients", "green_coffee_lots", "roast_batches"
    )

    @Test
    fun givenRecordedDurableWorkflows_whenReopenedAndBackupRestored_thenAllFamiliesRemainAvailable() {
        val app = compose.activity.application as CoffeeApplication
        val repository = app.graph.repository
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }
        val originalBackup = runBlocking { repository.exportBackupJson() }
        try {
            val prefix = "qa-${UUID.randomUUID()}"
            val records = createDurableWorkflowRecords(prefix, LocalDate.now())
            recordDurableWorkflow(model, records)
            awaitMain { !model.state.value.busy && model.state.value.journalEntries.any { it.id == records.journal.id } }
            assertEquals(1, model.state.value.palateProfile.qualifiedSessionCount)
            assertTrue(model.state.value.palateProfile.cumulativeEvidenceWeight > 5.0)
            durableWorkflowSettingKeys.forEach { key -> awaitMain { repository.settings.value[key] != null } }
            exerciseDurableWorkflowRestore(app, repository, records)
        } finally {
            restoreRecords(model, originalBackup)
        }
    }

    private fun createDurableWorkflowRecords(prefix: String, today: LocalDate) = DurableWorkflowRecords(
        prefix = prefix,
        bean = durableWorkflowBean(prefix, today),
        lot = durableWorkflowLot(prefix),
        qc = durableWorkflowQc(prefix),
        roastRest = durableWorkflowRoastRest(prefix, today),
        grinder = durableWorkflowGrinder(prefix),
        equipment = durableWorkflowEquipment(prefix),
        water = durableWorkflowWater(prefix),
        ingredient = durableWorkflowIngredient(prefix),
        roastBatch = durableWorkflowRoastBatch(prefix),
        journal = durableWorkflowJournal(prefix),
        units = UserUnitPreferences(
            language = AppLanguage.INDONESIAN,
            temperatureUnit = TemperatureUnit.FAHRENHEIT,
            massUnit = MassUnit.OUNCES,
            volumeUnit = VolumeUnit.FLUID_OUNCES,
            elevationUnit = ElevationUnit.FEET
        )
    )

    private fun durableWorkflowBean(prefix: String, today: LocalDate): DetailedBeanRecord {
        val bag = BagInventoryItem(
            id = "$prefix-bag",
            beanId = "$prefix-bean",
            packageMassGrams = BigDecimal("250.00"),
            remainingMassGrams = BigDecimal("200.00"),
            status = BagStatus.ACTIVE
        )
        return DetailedBeanRecord(
            id = bag.beanId,
            roaster = "QA roaster",
            name = "QA recorded bean",
            originCountry = "Ethiopia",
            process = ProcessMethod.WASHED,
            roastDate = today.minusDays(10),
            roastLevel = RoastLevel.LIGHT,
            bags = listOf(bag)
        )
    }

    private fun durableWorkflowLot(prefix: String) = GreenCoffeeLot(
        id = "$prefix-lot",
        lotNumber = prefix,
        originCountry = "Ethiopia",
        producer = "QA producer",
        harvestYear = "2026",
        moisturePercent = BigDecimal("11.20"),
        bulkDensityGramsPerLiter = BigDecimal("700.00"),
        waterActivityAw = BigDecimal("0.58"),
        category1DefectCount = 0,
        category2DefectCount = 0,
        screenDistributionPercent = mapOf(18 to BigDecimal("60.00")),
        sampleStatus = SampleLotStatus.LANDED_SAMPLE
    )

    private fun durableWorkflowQc(prefix: String) = PhysicalQcRecord(
        id = "$prefix-qc",
        sampleId = "$prefix-lot",
        moisturePercentage = BigDecimal("11.20"),
        waterActivityAw = BigDecimal("0.58"),
        bulkDensityGPerL = BigDecimal("700.00"),
        totalPrimaryDefects = 0,
        totalSecondaryDefects = 0,
        calculatedGrade = "QA recorded",
        roastedAgtronWholeBean = BigDecimal("80.00"),
        roastedAgtronGround = BigDecimal("60.00"),
        roastDeltaAgtron = BigDecimal("-20.00")
    )

    private fun durableWorkflowRoastRest(prefix: String, today: LocalDate) = RoastRestObservation(
        id = "$prefix-rest",
        bagId = "$prefix-bag",
        roastDateEpoch = today.minusDays(10).toEpochDay() * MILLIS_PER_DAY,
        observationDateEpoch = today.toEpochDay() * MILLIS_PER_DAY,
        daysPostRoast = 10,
        cuppingScore = BigDecimal("8.20"),
        observedAroma = BigDecimal("7.00"),
        observedAcidity = BigDecimal("8.00"),
        observedSweetness = BigDecimal("8.50"),
        perceivedCarbonicAcid = false,
        qualitativeNotes = "QA human observation",
        observedPhase = DegassingPhase.PEAK_FLAVOR_WINDOW
    )

    private fun durableWorkflowGrinder(prefix: String) = GrinderRecord(
        id = "$prefix-grinder",
        brand = "QA brand",
        model = "QA grinder",
        calibrationUnit = CalibrationUnit.CLICKS,
        burrProfile = BurrProfile(BurrType.CONICAL, BigDecimal("38.00"), BurrMaterial.STAINLESS_STEEL)
    )

    private fun durableWorkflowEquipment(prefix: String) = EquipmentItem(
        id = "$prefix-equipment",
        category = EquipmentCategory.DRIPPER,
        brand = "QA brand",
        model = "QA dripper",
        parameters = mapOf("brewMethod" to "Pour Over (V60)"),
        photoUri = "content://media/external/images/qa-equipment-label.jpg"
    )

    private fun durableWorkflowWater(prefix: String) = WaterChemistryCalculator.formulateWater(
        name = "QA water",
        calciumPpm = BigDecimal("20.00"),
        magnesiumPpm = BigDecimal("10.00"),
        sodiumPpm = BigDecimal("5.00"),
        bicarbonatePpm = BigDecimal("40.00"),
        id = "$prefix-water"
    )

    private fun durableWorkflowIngredient(prefix: String) = FoodGradeIngredientItem(
        id = "$prefix-ingredient",
        name = "QA verified food ingredient",
        densityGramsPerMl = BigDecimal("1.00"),
        isFoodGradeVerified = true
    )

    private fun durableWorkflowRoastBatch(prefix: String) = RoastBatchRecord(
        id = "$prefix-batch",
        batchNumber = prefix,
        greenLotId = "$prefix-lot",
        chargeTemperatureCelsius = BigDecimal("185.00"),
        dropTemperatureCelsius = BigDecimal("204.00"),
        totalRoastDurationSeconds = 600,
        firstCrackTimeSeconds = 420,
        developmentTimeRatioPercent = RoastBatchRecord.calculateDtr(600, 420)
    )

    private fun durableWorkflowJournal(prefix: String) = BrewJournalEntry(
        id = "$prefix-journal",
        beanId = "$prefix-bean",
        beanName = "QA recorded bean",
        brewMethod = "Pour Over (V60)",
        doseGrams = 18.0,
        waterGrams = 300.0,
        brewDurationSeconds = 180,
        rating = 8.5,
        sensoryScores = mapOf("Roast" to 4, "Acidity" to 8, "Sweetness" to 9, "Bitterness" to 3, "Body" to 6)
    )

    private fun recordDurableWorkflow(model: CoffeeViewModel, records: DurableWorkflowRecords) {
        compose.runOnUiThread {
            model.addDetailedBean(records.bean)
            model.addGreenLot(records.lot)
            model.addGrinder(records.grinder)
            model.addEquipmentItem(records.equipment)
            model.addWaterProfile(records.water)
            model.addFoodGradeIngredient(records.ingredient)
            model.addRoastBatch(records.roastBatch)
            model.createCuppingSession(
                title = "${records.prefix} cupping",
                location = "Local test",
                templateType = CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
                maskingMode = MaskingMode.OPEN_LABEL,
                evaluatorNames = listOf("QA evaluator"),
                sampleBeanNames = listOf(records.bean.name)
            )
            model.savePhysicalQc(records.qc)
            model.recordRoastRestObservation(records.roastRest)
            model.saveCafeCup(CafeCupEntry("${records.prefix}-cafe", "QA cafe", drinkName = "QA cup", rating = 8.0))
            model.submitPalateQuiz(qaPalateQuizAnswers())
            model.saveJournalEntry(records.journal)
            model.updateWeatherParameters(123.0)
            model.setUnitPreferences(records.units)
        }
    }

    private fun qaPalateQuizAnswers() = PalateQuizAnswers(
        roast = RoastPreference.LIGHT,
        acidity = AcidityTolerance.HIGH_ENTHUSIAST,
        sweetness = SweetnessPriority.CORE_CRUCIAL,
        bitterness = BitternessSensitivity.AVERSE,
        body = BodyPreference.SILKY_MEDIUM
    )

    private fun exerciseDurableWorkflowRestore(
        app: CoffeeApplication,
        repository: com.coffeecreator.app.core.data.DataRepository,
        records: DurableWorkflowRecords
    ) {
        val (reopened, isolatedStore) = compose.runOnUiThread { createIsolatedViewModel(app) }
        try {
            awaitMain { reopened.state.value.ready && !reopened.state.value.busy }
            assertDurableWorkflowAvailable(reopened, records)
            reopened.generateInventoryRecommendations("Pour Over (V60)")
            assertEquals(records.bean.id, reopened.state.value.inventoryRecommendations.single().beanId)
            val durableBackup = runBlocking { repository.exportBackupJson() }
            assertTrue(durableWorkflowSettingKeys.all(BackupCodec.decode(durableBackup).payload.settings::containsKey))
            assertMalformedRestoreIsAtomic(reopened, repository, durableBackup, records)
            reopened.deleteDetailedBean(records.bean.id)
            awaitMain { repository.settings.value["detailed_beans"]?.contains(records.bean.id) == false }
            restoreRecords(reopened, durableBackup)
            assertTrue(reopened.state.value.detailedBeans.any { it.id == records.bean.id })
        } finally {
            compose.runOnUiThread { isolatedStore.clear() }
        }
    }

    private fun assertDurableWorkflowAvailable(model: CoffeeViewModel, records: DurableWorkflowRecords) {
        assertTrue(model.state.value.journalEntries.any { it.id == records.journal.id })
        assertTrue(model.state.value.cafeCupEntries.any { it.id == "${records.prefix}-cafe" })
        assertTrue(model.state.value.cuppingSessions.any { it.title == "${records.prefix} cupping" })
        assertTrue(model.state.value.palateProfile.isQuizCompleted)
        assertEquals(1, model.state.value.palateProfile.qualifiedSessionCount)
        assertEquals(123.0, model.state.value.weatherParams.elevationMeters ?: -1.0, 0.0)
        assertEquals(records.units, model.state.value.unitPreferences)
        assertTrue(model.state.value.physicalQcRecords.containsKey(records.lot.id))
        assertTrue(model.state.value.roastRestObservations.any { it.id == records.roastRest.id })
        assertTrue(model.state.value.detailedBeans.any { it.id == records.bean.id })
        assertTrue(model.state.value.grinders.any { it.id == records.grinder.id })
        assertTrue(model.state.value.equipmentCatalog.any { it == records.equipment })
        assertTrue(model.state.value.waterProfiles.any { it.id == records.water.id })
        assertTrue(model.state.value.foodGradeIngredients.any { it.id == records.ingredient.id })
        assertTrue(model.state.value.greenCoffeeLots.any { it.id == records.lot.id })
        assertTrue(model.state.value.roastBatches.any { it.id == records.roastBatch.id })
    }

    private fun assertMalformedRestoreIsAtomic(
        model: CoffeeViewModel,
        repository: com.coffeecreator.app.core.data.DataRepository,
        durableBackup: String,
        records: DurableWorkflowRecords
    ) {
        val durablePayload = BackupCodec.decode(durableBackup).payload
        val settingsBeforeRestore = durableWorkflowSettingKeys.associateWith(repository.settings.value::getValue)
        val malformedCupping = """
            [{"id":"bad-session","title":"bad","location":"local","template":{"type":"QUICK_BARISTA_DIAL_IN", "title":"bad", "description":"bad", "attributes":[{"key":"acidity"}]}, "maskingMode":"OPEN_LABEL", "status":"OPEN", "evaluators":[], "samples":[], "sheets":[], "auditTrail":[], "createdAt":1}]
        """.trimIndent()
        val malformedBackup = BackupCodec.encode(
            durablePayload.copy(settings = durablePayload.settings + ("cuppings" to malformedCupping))
        )
        val restore = compose.runOnUiThread { model.viewModelScope.async { model.restoreRecords(malformedBackup) } }
        awaitMain { restore.isCompleted }
        assertTrue(runCatching { runBlocking { restore.await() } }.isFailure)
        assertEquals(settingsBeforeRestore, durableWorkflowSettingKeys.associateWith(repository.settings.value::getValue))
        assertTrue(model.state.value.journalEntries.any { it.id == records.journal.id })
        assertTrue(model.state.value.cuppingSessions.any { it.title == "${records.prefix} cupping" })
        assertTrue(model.state.value.detailedBeans.any { it.id == records.bean.id })
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
    private fun awaitMain(condition: () -> Boolean) {
        compose.waitUntil(20_000) {
            shadowOf(Looper.getMainLooper()).idle()
            compose.mainClock.advanceTimeByFrame()
            condition()
        }
    }

    private fun restoreRecords(model: CoffeeViewModel, json: String) {
        val restore = compose.runOnUiThread { model.viewModelScope.async { model.restoreRecords(json) } }
        awaitMain { restore.isCompleted }
        runBlocking { restore.await() }
    }

    private fun createIsolatedViewModel(app: CoffeeApplication): Pair<CoffeeViewModel, ViewModelStore> {
        val store = ViewModelStore()
        val owner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = store
        }
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == CoffeeViewModel::class.java)
                return CoffeeViewModel(app.graph, SavedStateHandle()) as T
            }
        }
        return ViewModelProvider(owner, factory)[CoffeeViewModel::class.java] to store
    }

    @Test
    fun givenRecordRestoreInFlight_whenSendingChat_thenExistingChatCannotBeOverwritten() {
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }
        val before = model.state.value.messages
        val invalidFile = File(compose.activity.cacheDir, "owned-invalid-backup.json").also { it.writeText("{}") }
        compose.runOnUiThread {
            model.restoreBackup(Uri.fromFile(invalidFile), compose.activity.contentResolver)
            assertTrue(model.state.value.busy)
            model.sendChat("This must wait until the records operation finishes.")
        }
        awaitMain { !model.state.value.busy && !model.state.value.generating }
        assertEquals(before, model.state.value.messages)
        check(invalidFile.delete())
    }

    @Test
    fun givenVerifiedCatalogWithoutSafGrant_whenOpeningModels_thenDownloadsRemainDisabled() {
        val repository = (compose.activity.application as CoffeeApplication).graph.repository
        val catalogIds = BuiltInAssets.catalog.map { it.id }.toSet()
        compose.waitUntil(20_000) { repository.models.value.map { it.id }.containsAll(catalogIds) }
        compose.onNodeWithTag("nav:more").performClick()
        compose.onNodeWithTag("nav:Models").performClick()
        BuiltInAssets.catalog.forEach { asset ->
            compose.onNodeWithTag("page:Models").performScrollToNode(hasTestTag("models:download:${asset.id}"))
            compose.onNodeWithTag("models:download:${asset.id}").performScrollTo().assertIsNotEnabled()
        }
        assertEquals(null, repository.safDirectoryUri.value)
    }

    @Test
    fun givenManualPreference_whenUpdatingSliders_thenPreferenceAndRecommendationFlowAreAvailable() {
        val model = ViewModelProvider(compose.activity)[CoffeeViewModel::class.java]
        awaitMain { model.state.value.ready && !model.state.value.busy }

        model.updatePalateSliders(acidity = 0.8)

        assertTrue(model.state.value.palateProfile.hasManualPreference)
        assertEquals(0.8, model.state.value.palateProfile.vector.acidity, 0.0)
        model.generateInventoryRecommendations()
        assertFalse(model.state.value.notice.orEmpty().contains("Complete the palate quiz"))
    }

    @Test
    fun givenForgottenAsset_whenForgetCompletes_thenTransferProgressResetsWithoutDeletingSafUri() {
        val app = compose.activity.application as CoffeeApplication
        val repository = app.graph.repository
        compose.waitUntil(20_000) { repository.ready.value }
        val catalogAsset = BuiltInAssets.catalog.first()
        val downloading = catalogAsset.copy(
            status = ModelStatus.PAUSED,
            localUri = "content://coffee-models/${catalogAsset.id}",
            downloadedBytes = 12_345,
            progressPercent = 17
        )
        runBlocking { repository.upsertModel(downloading) }

        runBlocking { app.graph.assets.forget(downloading) }

        val forgotten = repository.models.value.single { it.id == catalogAsset.id }
        assertEquals(ModelStatus.CATALOGUED, forgotten.status)
        assertEquals(null, forgotten.localUri)
        assertEquals(0L, forgotten.downloadedBytes)
        assertEquals(0, forgotten.progressPercent)
    }

    private fun field(key: String, value: String) {
        compose.onNodeWithTag("page:Formulate").performScrollToNode(hasTestTag("form:$key"))
        compose.onNodeWithTag("form:$key").performScrollTo().performTextReplacement(value)
    }

    private fun capture(name: String) {
        val output = File(checkNotNull(System.getProperty("coffee.screenshotDir")), "$name.png")
        check(output.parentFile?.isDirectory == true || output.parentFile?.mkdirs() == true)
        compose.waitForIdle()
        compose.runOnUiThread {
            val view = compose.activity.window.decorView
            check(view.width > 0 && view.height > 0)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            output.outputStream().use { stream ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
            }
            bitmap.recycle()
        }
    }
}
