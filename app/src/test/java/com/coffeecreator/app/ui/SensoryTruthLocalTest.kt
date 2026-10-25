package com.coffeecreator.app.ui

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coffeecreator.app.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SensoryTruthLocalTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun givenNewGreenQcDraft_whenOpened_thenMeasurementsStartUnknown() {
        openSensoryTab("sensory:tab_green_qc")
        compose.onNodeWithTag("sensory:green_unsaved_draft_notice").assertIsDisplayed()
        compose.onNodeWithTag("sensory:green_metric_cat1").assertTextEquals("—")
        compose.onNodeWithTag("sensory:green_metric_cat2").assertTextEquals("—")
        compose.onNodeWithTag("sensory:tab_green_2").performScrollTo().performClick()

        compose.onNodeWithText("Moisture Status: NOT_MEASURED").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("sensory:green_moisture_input").assertDoesNotExist()
        compose.onNodeWithTag("sensory:green_water_activity_input").assertDoesNotExist()
        compose.onNodeWithTag("sensory:green_bulk_density_input").assertDoesNotExist()
        compose.onNodeWithText("Contract & Export").performScrollTo().performClick()
        compose.onNodeWithTag("sensory:green_export_requirements").performScrollTo().assertIsDisplayed()
        val hashNotice = "After writing, the app reports the exact payload SHA-256. " +
            "CSV does not embed it; this digest is not a digital signature or a persisted audit log."
        compose.onNodeWithText(hashNotice).performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("sensory:green_export_json").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:green_export_csv").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:green_separate_assessment_notice")
            .performScrollTo()
            .assertIsDisplayed()

        val accidentalDefaults = compose.onAllNodes(hasSetTextAction()).fetchSemanticsNodes()
            .filter { it.config.contains(SemanticsProperties.EditableText) }
            .map { it.config[SemanticsProperties.EditableText].text }
            .filter { it in setOf("10.8", "0.585", "710.0") }
        assertEquals(emptyList<String>(), accidentalDefaults)
    }

    @Test
    fun givenPreEpochCalibrationDate_whenEntered_thenDraftRemainsRecoverableAndExportStaysBlocked() {
        openSensoryTab("sensory:tab_green_qc")
        compose.onNodeWithTag("sensory:tab_green_2").performScrollTo().performClick()
        compose.onNodeWithTag("sensory:green_measure_moisture").performScrollTo().performClick()
        compose.onNodeWithTag("sensory:green_calibration_date_input")
            .performScrollTo()
            .performTextInput("1969-12-31")
        compose.onNodeWithText("Enter a valid date from 1970 onward.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("sensory:tab_green_4").performScrollTo().performClick()

        compose.onNodeWithTag("sensory:green_export_requirements")
            .assertTextContains("valid calibration date (YYYY-MM-DD)", substring = true)
        compose.onNodeWithTag("sensory:green_export_json").assertIsNotEnabled()
    }

    @Test
    fun givenGreenQcColorObservation_whenSelected_thenChipExposesSelectedState() {
        openSensoryTab("sensory:tab_green_qc")
        compose.onNodeWithTag("sensory:tab_green_2").performScrollTo().performClick()
        compose.onNodeWithTag("sensory:green_measure_moisture").performScrollTo()
        compose.onNodeWithTag("sensory:green_color_GREEN").performClick()

        compose.onNodeWithTag("sensory:green_color_GREEN").assertIsSelected()
    }

    @Test
    fun givenFreshProfile_whenOpeningOperations_thenSharingConsentIsOff() {
        openSensoryTab("sensory:tab_operations")
        compose.onNodeWithTag("operations:panel_no_session_state").assertIsDisplayed()
        compose.onNodeWithTag("operations:reveal_unavailable").assertIsNotEnabled()
        compose.onNodeWithText("Ethiopia Yirgacheffe Washed G1").assertDoesNotExist()
        compose.onNodeWithText("Colombia Geisha Natural Anaerobic").assertDoesNotExist()
        compose.onNodeWithText("Privacy & Export").performScrollTo().performClick()

        val state = compose.onNodeWithTag("operations:consent_checkbox")
            .fetchSemanticsNode().config[SemanticsProperties.ToggleableState]
        assertEquals(ToggleableState.Off, state)
        compose.onNodeWithTag("operations:export_sealed_json_btn").assertIsNotEnabled()
    }

    @Test
    fun givenAccessibilityProfileHasNoConnectedRules_whenOpened_thenOptionsAreUnavailableAndDisclosed() {
        openSensoryTab("sensory:tab_operations")
        compose.onNodeWithText("Accessibility").performScrollTo().performClick()

        compose.onNodeWithTag("operations:accessibility_unavailable_notice").assertIsDisplayed()
        compose.onNodeWithTag("operations:toggle_anosmia").assertIsNotEnabled()
        compose.onNodeWithTag("operations:toggle_parosmia").assertIsNotEnabled()
        compose.onNodeWithTag("operations:toggle_sulfites").assertIsNotEnabled()
        compose.onNodeWithTag("operations:toggle_nuts").assertIsNotEnabled()
    }

    @Test
    fun givenNoSavedCvaSession_whenOpeningCupping_thenPersistenceActionsStayDisabled() {
        openSensoryTab("sensory:tab_cva")

        compose.onNodeWithTag("sensory:cva_unsaved_state").assertIsDisplayed()
        compose.onNodeWithTag("sensory:cva_btn_log_journal").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:cva_btn_compare").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:cva_btn_lock").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:cva_btn_export_json").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:cva_btn_export_csv").assertIsNotEnabled()
        compose.onNodeWithTag("sensory:cva_btn_log_journal").assertTextContains("Save to journal")
        compose.onNodeWithTag("sensory:cva_btn_compare").assertTextContains("Compare")
        compose.onNodeWithTag("sensory:cva_btn_lock").assertTextContains("Lock session")
        compose.onNodeWithTag("sensory:cva_btn_export_json").assertTextContains("Export JSON")
        compose.onNodeWithTag("sensory:cva_btn_export_csv").assertTextContains("Export CSV")
        compose.onNodeWithTag("sensory:cva_blind_toggle").assertIsNotEnabled()
        compose.onNodeWithText("Ethiopia Yirgacheffe Washed G1").assertDoesNotExist()
        compose.onNodeWithText("CVA 2024 STANDARD").assertDoesNotExist()
    }

    @Test
    fun givenThresholdStepHasNoResponse_whenSelectingNo_thenItIsExplicitlyRecorded() {
        openSensoryTab("sensory:tab_training")
        compose.onNodeWithTag("sensory:train_tab_tastes").performScrollTo().performClick()
        compose.onNodeWithTag("sensory:train_tab_tastes").assertIsSelected()
        compose.onNodeWithText("Ascending Concentration Series (ASTM E679)").performScrollTo()
        compose.onNodeWithText("Ascending Concentration Series (ASTM E679)").assertIsDisplayed()

        compose.onAllNodesWithText("Not answered").assertCountEquals(4)
        compose.onNodeWithTag("sensory:threshold_step1_no").performScrollTo().performClick().assertIsSelected()
        compose.onNodeWithTag("sensory:threshold_step1_status", useUnmergedTree = true).assertTextEquals("Response recorded")
        compose.onNodeWithTag("sensory:threshold_step1_yes").performClick().assertIsSelected()
        compose.onNodeWithTag("sensory:threshold_step1_no").assertIsNotSelected()
    }

    @Test
    fun givenLegacyScoreHasNoEntry_whenOpened_thenSliderAnnouncesNotRecorded() {
        openSensoryTab("sensory:tab_cva")
        compose.onNodeWithTag("sensory:cva_proto_sca_2004_legacy").performScrollTo().performClick()
        compose.onNodeWithText("SCA 2004 Historical Cupping Sheet").performScrollTo()

        val slider = compose.onNodeWithTag("sensory:cva_legacy_slider_0").performScrollTo().fetchSemanticsNode()
        assertEquals("Not recorded", slider.config[SemanticsProperties.StateDescription])
    }

    @Test
    fun givenNoPracticeHistory_whenOpeningTrainingReadiness_thenNoScoresOrVerifierAreInvented() {
        openSensoryTab("sensory:tab_training")
        compose.onNodeWithText("Readiness Radar").performScrollTo().performClick()

        compose.onNodeWithTag("training:no_practice_data").assertIsDisplayed()
        compose.onNodeWithTag("training:physical_verification_unavailable").assertIsNotEnabled()
        compose.onNodeWithText("Instructor Sarah").assertDoesNotExist()
        compose.onNodeWithText("82.5%").assertDoesNotExist()
    }

    @Test
    fun givenVirtualTrainingPrompts_whenAnsweringOlfactoryAndPairedExercises_thenNoPhysicalOrSavedResultIsClaimed() {
        openSensoryTab("sensory:tab_training")
        compose.onNodeWithTag("sensory:train_tab_olfactory").performScrollTo().performClick()
        compose.onNodeWithTag("training:virtual_olfactory_notice").assertIsDisplayed()
        compose.onNodeWithText("Active Physical Vial Code: #412").assertDoesNotExist()

        compose.onNodeWithTag("sensory:train_tab_comparative").performScrollTo().performClick()
        compose.onNodeWithTag("training:paired_unsaved_notice").assertIsDisplayed()
        compose.onNodeWithText("Sample A (virtual)").performScrollTo().performClick()
        compose.onNodeWithText("Submit paired choice").performScrollTo().performClick()
        compose.onNodeWithText("Paired Comparison Recorded").assertDoesNotExist()
    }

    private fun openSensoryTab(tabTag: String) {
        compose.onNodeWithTag("nav:more").performClick()
        compose.onNodeWithTag("nav:Sensory").performClick()
        compose.onNodeWithTag(tabTag).performScrollTo().performClick()
    }
}
