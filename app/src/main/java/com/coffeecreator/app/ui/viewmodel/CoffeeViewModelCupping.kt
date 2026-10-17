package com.coffeecreator.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import com.coffeecreator.app.core.ai.personalization.BaristaPersonaPreset
import com.coffeecreator.app.core.ai.personalization.BeanInventoryCandidate
import com.coffeecreator.app.core.ai.personalization.ElevationUnit
import com.coffeecreator.app.core.ai.personalization.FeedbackLoopEngine
import com.coffeecreator.app.core.ai.personalization.IngredientSafetyValidator
import com.coffeecreator.app.core.ai.personalization.PalateBayesianEngine
import com.coffeecreator.app.core.ai.personalization.PalateQuizAnswers
import com.coffeecreator.app.core.ai.personalization.PalateQuizMapper
import com.coffeecreator.app.core.ai.personalization.PalateVector
import com.coffeecreator.app.core.ai.personalization.PersonaSanitizer
import com.coffeecreator.app.core.ai.personalization.PersonalSensoryEvidence
import com.coffeecreator.app.core.ai.personalization.RecommendationEngine
import com.coffeecreator.app.core.ai.personalization.SensoryFeedbackDiagnosis
import com.coffeecreator.app.core.ai.personalization.WeatherAltitudeCalculator
import com.coffeecreator.app.core.ai.personalization.WeatherParameters
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.RoastLevel
import com.coffeecreator.app.core.model.AssistantPersona
import com.coffeecreator.app.core.reliability.AppLanguage
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.ModuleStatus
import com.coffeecreator.app.core.reliability.UserUnitPreferences
import com.coffeecreator.app.core.sensory.AuditEntry
import com.coffeecreator.app.core.sensory.CuppingAnalyticsEngine
import com.coffeecreator.app.core.sensory.CuppingPhaseObservation
import com.coffeecreator.app.core.sensory.CuppingSessionFactory
import com.coffeecreator.app.core.sensory.CuppingTemplateType
import com.coffeecreator.app.core.sensory.DefectRecord
import com.coffeecreator.app.core.sensory.EpistemologicalOrigin
import com.coffeecreator.app.core.sensory.EvaluationSheet
import com.coffeecreator.app.core.sensory.MaskingMode
import com.coffeecreator.app.core.sensory.PhysicalQcRecord
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.coffeecreator.app.core.sensory.SessionStatus
import com.coffeecreator.app.core.sensory.toBrewJournalEntry
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private val SUPPORTED_ROAST_LEVELS = listOf(
    RoastLevel.CINNAMON,
    RoastLevel.LIGHT,
    RoastLevel.MEDIUM_LIGHT,
    RoastLevel.MEDIUM,
    RoastLevel.MEDIUM_DARK,
    RoastLevel.DARK,
    RoastLevel.VERY_DARK
)
private val REQUIRED_RECOMMENDATION_SENSORY_KEYS = listOf("acidity", "sweetness", "bitterness", "body")

fun CoffeeViewModel.selectCuppingSession(sessionId: String?) {
    mutableState.update {
        it.copy(
            activeCuppingSessionId = sessionId,
            selectedCuppingSampleId = it.cuppingSessions.find { s -> s.id == sessionId }?.samples?.firstOrNull()?.sampleId
        )
    }
}

fun CoffeeViewModel.selectCuppingSample(sampleId: String?) {
    mutableState.update { it.copy(selectedCuppingSampleId = sampleId) }
}

fun CoffeeViewModel.createCuppingSession(
    title: String,
    location: String,
    templateType: CuppingTemplateType,
    maskingMode: MaskingMode,
    evaluatorNames: List<String>,
    sampleBeanNames: List<String>
) {
    val session = runCatching {
        CuppingSessionFactory.create(
            title = title,
            location = location,
            templateType = templateType,
            maskingMode = maskingMode,
            evaluatorNames = evaluatorNames,
            sampleBeanNames = sampleBeanNames,
            createdAtEpochMs = System.currentTimeMillis()
        )
    }.getOrElse { error ->
        mutableState.update { it.copy(notice = error.message ?: "Complete the cupping session details first.") }
        return
    }
    val sessionId = session.id

    mutableState.update {
        it.copy(
            cuppingSessions = listOf(session) + it.cuppingSessions,
            activeCuppingSessionId = sessionId,
            selectedCuppingSampleId = session.samples.first().sampleId,
            notice = "Created cupping session '${session.title}'."
        )
    }
}

fun CoffeeViewModel.revealCuppingSample(sessionId: String, sampleId: String) {
    mutableState.update { current ->
        val updated = current.cuppingSessions.map { s ->
            if (s.id == sessionId) {
                val updatedSamples = s.samples.map { smp ->
                    if (smp.sampleId == sampleId) smp.copy(isRevealed = true) else smp
                }
                val audit = s.auditTrail + AuditEntry(
                    timestamp = System.currentTimeMillis(),
                    editorId = s.evaluators.firstOrNull()?.id ?: "lead",
                    action = "REVEAL_SAMPLE",
                    previousStateSummary = "Masked",
                    newStateSummary = "Revealed",
                    reason = "Controlled blind reveal for sample $sampleId"
                )
                s.copy(samples = updatedSamples, auditTrail = audit)
            } else {
                s
            }
        }
        current.copy(cuppingSessions = updated, notice = "Revealed true identity for sample.")
    }
}

fun CoffeeViewModel.submitCuppingSheet(
    sessionId: String,
    sampleId: String,
    evaluatorId: String,
    attributeScores: Map<String, BigDecimal>,
    descriptors: List<String>,
    taintCups: Int = 0,
    faultCups: Int = 0,
    defectDesc: String = "",
    phaseObservations: List<CuppingPhaseObservation> = emptyList(),
    notes: String = ""
) {
    val session = mutableState.value.cuppingSessions.find { it.id == sessionId }
    if (session == null || session.samples.none { it.sampleId == sampleId }) {
        mutableState.update { it.copy(notice = "Choose a sample in an existing cupping session.") }
        return
    }
    if (session.status != SessionStatus.OPEN && session.status != SessionStatus.REOPENED) {
        mutableState.update { it.copy(notice = "This cupping session is ${session.status.name.lowercase()}.") }
        return
    }
    if (session.evaluators.none { it.id == evaluatorId }) {
        mutableState.update { it.copy(notice = "Choose an evaluator before submitting scores.") }
        return
    }
    if (taintCups < 0 || faultCups < 0) {
        mutableState.update { it.copy(notice = "Defect counts cannot be negative.") }
        return
    }
    runCatching { CuppingAnalyticsEngine.validateAttributeScores(session.template, attributeScores) }
        .onFailure { error ->
            mutableState.update { it.copy(notice = error.message ?: "Enter all protocol scores.") }
            return
        }
    val defects = DefectRecord(taintCups, faultCups, defectDesc)
    val totalScore = CuppingAnalyticsEngine.calculateTotalCuppingScore(
        session.template.type,
        attributeScores,
        defects
    )
    val sheet = EvaluationSheet(
        id = UUID.randomUUID().toString(),
        sessionId = sessionId,
        sampleId = sampleId,
        evaluatorId = evaluatorId,
        origin = EpistemologicalOrigin.HUMAN_OBSERVATION,
        attributeScores = attributeScores,
        descriptors = descriptors,
        defects = defects,
        totalScore = totalScore,
        phaseObservations = phaseObservations,
        notes = notes,
        submittedAt = System.currentTimeMillis()
    )

    mutableState.update { current ->
        val updated = current.cuppingSessions.map { s ->
            if (s.id == sessionId) {
                val newSheets = s.sheets.filter { it.sampleId != sampleId || it.evaluatorId != evaluatorId } + sheet
                s.copy(sheets = newSheets)
            } else {
                s
            }
        }
        current.copy(cuppingSessions = updated, notice = "Submitted score: $totalScore.")
    }
}

fun CoffeeViewModel.logCuppingSampleToJournal(sessionId: String, sampleId: String, customNotes: String = "") {
    val current = mutableState.value
    val session = current.cuppingSessions.find { it.id == sessionId }
    val sample = session?.samples?.find { it.sampleId == sampleId }
    if (session == null || sample == null) {
        mutableState.update { it.copy(notice = "Choose a valid cupping sample to log.") }
        return
    }

    val sheet = session.sheets.firstOrNull { it.sampleId == sampleId }
    val journalEntry = sample.toBrewJournalEntry(
        session = session,
        sheet = sheet,
        customNotes = customNotes
    )

    mutableState.update { state ->
        state.copy(
            journalEntries = listOf(journalEntry) + state.journalEntries,
            notice = "Logged cupping sample '${sample.blindCode}' to Journal."
        )
    }
}

fun CoffeeViewModel.closeCuppingSession(sessionId: String) {
    mutableState.update { current ->
        val updated = current.cuppingSessions.map { s ->
            if (s.id == sessionId) {
                val audit = s.auditTrail + AuditEntry(
                    timestamp = System.currentTimeMillis(),
                    editorId = s.evaluators.firstOrNull()?.id ?: "lead",
                    action = "CLOSE_SESSION",
                    previousStateSummary = s.status.name,
                    newStateSummary = SessionStatus.CLOSED.name,
                    reason = "Formal evaluation closed; scores locked for consensus"
                )
                s.copy(status = SessionStatus.CLOSED, closedAt = System.currentTimeMillis(), auditTrail = audit)
            } else {
                s
            }
        }
        current.copy(cuppingSessions = updated, notice = "Cupping session closed and locked.")
    }
}

fun CoffeeViewModel.reopenCuppingSession(sessionId: String, editorId: String, reason: String) {
    mutableState.update { current ->
        val updated = current.cuppingSessions.map { s ->
            if (s.id == sessionId) {
                val audit = s.auditTrail + AuditEntry(
                    timestamp = System.currentTimeMillis(),
                    editorId = editorId,
                    action = "REOPEN_SESSION",
                    previousStateSummary = s.status.name,
                    newStateSummary = SessionStatus.REOPENED.name,
                    reason = reason.ifBlank { "Administrative score correction requested" }
                )
                s.copy(status = SessionStatus.REOPENED, auditTrail = audit)
            } else {
                s
            }
        }
        current.copy(cuppingSessions = updated, notice = "Cupping session reopened with immutable audit trail.")
    }
}

fun CoffeeViewModel.savePhysicalQc(record: PhysicalQcRecord) {
    mutableState.update {
        val map = it.physicalQcRecords + (record.sampleId to record)
        it.copy(physicalQcRecords = map, notice = "Saved Physical QC record: ${record.calculatedGrade}.")
    }
}

fun CoffeeViewModel.recordRoastRestObservation(observation: RoastRestObservation) {
    mutableState.update {
        val list = listOf(observation) + it.roastRestObservations.filter { o -> o.id != observation.id }
        it.copy(roastRestObservations = list, notice = "Logged roast-rest observation: ${observation.observedPhase.label}.")
    }
}

fun CoffeeViewModel.exportSessionQcReport(sessionId: String) {
    val session = mutableState.value.cuppingSessions.find { it.id == sessionId } ?: return
    val receipt = CuppingAnalyticsEngine.generateOfflineReport(session, mutableState.value.physicalQcRecords)
    mutableState.update {
        it.copy(
            generatedQcReceipt = receipt,
            notice = "Generated QC Report (SHA-256: ${receipt.sha256Fingerprint.take(12)}...)"
        )
    }
}

fun CoffeeViewModel.clearQcReceipt() {
    mutableState.update { it.copy(generatedQcReceipt = null) }
}

// ========================================================================
// Epic S2-E06: Personalization, Local AI & RAG Integration
// ========================================================================

fun CoffeeViewModel.submitPalateQuiz(answers: PalateQuizAnswers) {
    val profile = PalateQuizMapper.createFromQuiz(answers)
    mutableState.update {
        it.copy(
            palateProfile = profile,
            notice = "Calibrated personal palate profile! Light/Acidity/Sweetness calibrated."
        )
    }
}

fun CoffeeViewModel.updatePalateSliders(
    roast: Double? = null,
    acidity: Double? = null,
    sweetness: Double? = null,
    bitterness: Double? = null,
    body: Double? = null
) {
    val profile = mutableState.value.palateProfile
    val updated = PalateQuizMapper.updateManualSliders(profile, roast, acidity, sweetness, bitterness, body)
    mutableState.update { it.copy(palateProfile = updated, notice = "Updated palate preference sliders.") }
}

fun CoffeeViewModel.recordSensoryEvidence(evidence: PersonalSensoryEvidence) {
    val profile = mutableState.value.palateProfile
    if (!profile.isQuizCompleted && profile.qualifiedSessionCount == 0) {
        mutableState.update { it.copy(notice = "Complete the palate quiz before recording sensory evidence.") }
        return
    }
    val updated = PalateBayesianEngine.updateProfile(profile, evidence)
    mutableState.update {
        it.copy(
            palateProfile = updated,
            notice = "Bayesian palate vector updated from qualified session (${evidence.rating}/10)."
        )
    }
}

fun CoffeeViewModel.updateWeatherParameters(elevationMeters: Double) {
    val tBoil = WeatherAltitudeCalculator.calculateBoilingPointCelsius(elevationMeters, ElevationUnit.METERS)
    val params = WeatherParameters(
        elevationMeters = elevationMeters,
        calculatedBoilingPointCelsius = tBoil
    )
    mutableState.update {
        it.copy(
            weatherParams = params,
            notice = "Elevation entered as ${elevationMeters.toInt()}m. Approximate boiling-point estimate: $tBoil°C."
        )
    }
}

fun CoffeeViewModel.generateInventoryRecommendations(brewMethod: String = "Pour Over (V60)") {
    val current = mutableState.value
    val hasRecordedPreference = current.palateProfile.isQuizCompleted || current.palateProfile.hasManualPreference ||
        current.palateProfile.qualifiedSessionCount > 0
    if (!hasRecordedPreference) {
        mutableState.update {
            it.copy(
                inventoryRecommendations = emptyList(),
                notice = "Set a palate preference, complete the quiz, or record a qualified sensory session " +
                    "before requesting recommendations."
            )
        }
        return
    }

    val compatibleGear = current.equipmentCatalog
        .flatMap(::declaredBrewMethods)
        .filterTo(mutableSetOf()) { it.equals(brewMethod, ignoreCase = true) }
    if (compatibleGear.isEmpty()) {
        mutableState.update {
            it.copy(
                inventoryRecommendations = emptyList(),
                notice = "No recommendation for $brewMethod: add equipment with an explicit matching brew method."
            )
        }
        return
    }

    val candidates = current.detailedBeans.mapNotNull { bean ->
        bean.toInventoryCandidate(current.journalEntries)
    }
    if (candidates.isEmpty()) {
        mutableState.update {
            it.copy(
                inventoryRecommendations = emptyList(),
                notice = "No recommendation: inventory needs a roast date, positive remaining bag mass, " +
                    "and linked qualified sensory scores."
            )
        }
        return
    }

    val recommendations = RecommendationEngine.rankInventory(
        palate = current.palateProfile,
        candidates = candidates,
        availableGearMethods = compatibleGear,
        brewMethod = brewMethod
    )
    mutableState.update {
        it.copy(
            inventoryRecommendations = recommendations,
            notice = if (recommendations.isEmpty()) {
                "No recommendation is available for the selected brew method and recorded inventory."
            } else {
                "Ranked ${recommendations.size} inventory option(s) from your recorded palate, stock, roast dates, and equipment."
            }
        )
    }
}

private fun declaredBrewMethods(item: EquipmentItem): Set<String> = buildSet {
    addAll(item.tags.map(String::trim).filter(String::isNotEmpty))
    item.parameters.forEach { (key, value) ->
        if (key.equals("brewMethod", ignoreCase = true) || key.equals("brewMethods", ignoreCase = true)) {
            addAll(value.split(',', ';').map(String::trim).filter(String::isNotEmpty))
        }
    }
}

private fun DetailedBeanRecord.toInventoryCandidate(
    journalEntries: List<com.coffeecreator.app.core.journal.BrewJournalEntry>
): BeanInventoryCandidate? {
    val roastDate = roastDate ?: return null
    val roastIndex = SUPPORTED_ROAST_LEVELS.indexOf(roastLevel)
    if (roastIndex < 0) return null
    val availableMass = bags.asSequence()
        .filter { it.status != BagStatus.FINISHED }
        .sumOf { it.remainingMassGrams.toDouble() }
    if (!availableMass.isFinite() || availableMass <= 0.0) return null

    val latestQualifiedScores = journalEntries.asSequence()
        .filter { it.beanId == id && it.rating?.let { rating -> rating in 7.5..10.0 } == true }
        .map { entry ->
            entry to entry.sensoryScores.entries.associate { (key, value) -> key.trim().lowercase() to value }
        }
        .filter { (_, scores) -> REQUIRED_RECOMMENDATION_SENSORY_KEYS.all { scores[it] in 1..10 } }
        .maxByOrNull { (entry, _) -> entry.createdAt }
        ?.second
        ?: return null
    return runCatching {
        PalateVector(
            // The roast axis encodes the explicitly selected inventory roast category.
            roast = roastIndex.toDouble() / (SUPPORTED_ROAST_LEVELS.size - 1),
            acidity = latestQualifiedScores.getValue("acidity") / 10.0,
            sweetness = latestQualifiedScores.getValue("sweetness") / 10.0,
            bitterness = latestQualifiedScores.getValue("bitterness") / 10.0,
            body = latestQualifiedScores.getValue("body") / 10.0
        )
    }.getOrNull()?.let { profile ->
        BeanInventoryCandidate(id, name, profile, roastDate, availableMass)
    }
}

fun CoffeeViewModel.proposeSingleVariableAdjustment(diagnosis: SensoryFeedbackDiagnosis, actualSec: Int, targetSec: Int) {
    val current = mutableState.value
    val recipe = try {
        current.draft.build(current.ingredients)
    } catch (invalid: IllegalArgumentException) {
        mutableState.update {
            it.copy(
                ovatAdjustmentProposal = null,
                notice = invalid.message ?: "Save valid recipe values before requesting an adjustment."
            )
        }
        return
    }
    val grinderSetting = recipe.grinderSetting ?: recipe.grindSize
    val clicks = Regex("(\\d+)\\s*clicks?", RegexOption.IGNORE_CASE)
        .find(grinderSetting)
        ?.groupValues
        ?.get(1)
        ?.toIntOrNull()
    val recipeInputs = buildMap<String, Any> {
        put("doseGrams", recipe.doseGrams)
        put("waterGrams", recipe.waterGrams)
        put("waterTempCelsius", recipe.temperatureCelsius)
        clicks?.let { put("grindClicks", it) }
    }
    val proposal = try {
        FeedbackLoopEngine.proposeAdjustment(
            previousRecipe = recipeInputs,
            actualDurationSeconds = actualSec,
            targetDurationSeconds = targetSec,
            diagnosis = diagnosis,
            boilingPointCelsius = current.weatherParams.calculatedBoilingPointCelsius
        )
    } catch (invalid: IllegalArgumentException) {
        mutableState.update {
            it.copy(ovatAdjustmentProposal = null, notice = invalid.message ?: "Required recipe evidence is missing.")
        }
        return
    }
    mutableState.update {
        it.copy(
            ovatAdjustmentProposal = proposal,
            notice = "Proposed adjustment from the current recipe: ${proposal.variableAdjusted} -> ${proposal.proposedValue}"
        )
    }
}

fun CoffeeViewModel.applyOvatAdjustmentToDraft() {
    val proposal = mutableState.value.ovatAdjustmentProposal ?: return
    val currentDraft = mutableState.value.draft
    val newFields = currentDraft.fields.toMutableMap()
    when (proposal.variableAdjusted) {
        "Grind Size" -> {
            val newClicks = proposal.modifiedRecipePayload["grindClicks"] as? Int ?: return
            newFields["grind"] = "$newClicks clicks (${proposal.proposedValue})"
        }
        "Water Temperature" -> {
            val newTemp = proposal.modifiedRecipePayload["waterTempCelsius"] as? Double ?: return
            newFields["temperature"] = newTemp.toString()
        }
        "Coffee Dose" -> {
            val newDose = proposal.modifiedRecipePayload["doseGrams"] as? Double ?: return
            newFields["dose"] = newDose.toString()
        }
        "Water Volume" -> {
            val newWater = proposal.modifiedRecipePayload["waterGrams"] as? Double ?: return
            newFields["water"] = newWater.toString()
        }
    }
    val updatedDraft = currentDraft.copy(fields = newFields)
    mutableState.update {
        it.copy(
            draft = updatedDraft,
            ovatAdjustmentProposal = null,
            notice = "Applied OVAT adjustment (${proposal.variableAdjusted}) to current recipe draft."
        )
    }
}

fun CoffeeViewModel.dismissOvatAdjustment() {
    mutableState.update { it.copy(ovatAdjustmentProposal = null) }
}

fun CoffeeViewModel.selectBaristaPersonaPreset(preset: BaristaPersonaPreset) {
    val persona = AssistantPersona(
        id = preset.id,
        title = preset.title,
        description = preset.baseSystemPrompt,
        systemPrompt = preset.baseSystemPrompt
    )
    mutableState.update {
        val list = if (it.personas.none { p -> p.id == preset.id }) it.personas + persona else it.personas
        it.copy(personas = list, selectedPersona = persona, notice = "Switched persona to '${preset.title}'.")
    }
}

fun CoffeeViewModel.updateCustomPersonaPrompt(customPrompt: String) {
    val sanitized = PersonaSanitizer.sanitizeCustomPrompt(customPrompt)
    val customPersona = AssistantPersona(
        id = "custom_persona",
        title = "Custom Persona",
        description = sanitized,
        systemPrompt = sanitized
    )
    mutableState.update {
        val list = it.personas.filter { p -> p.id != "custom_persona" } + customPersona
        it.copy(personas = list, selectedPersona = customPersona, notice = "Custom persona prompt sanitized and saved.")
    }
}

fun CoffeeViewModel.auditIngredientSafety(name: String, massFractionPercent: Double) {
    val matches = mutableState.value.ingredients.filter { it.name.contains(name.trim(), ignoreCase = true) }
    val audit = when (matches.size) {
        1 -> matches.single().let { ingredient ->
            IngredientSafetyValidator.auditIngredient(
                ingredient.name,
                ingredient.foodGrade,
                ingredient.allergens,
                massFractionPercent,
                ingredient.supplierMaxPercent
            )
        }
        else -> IngredientSafetyValidator.auditIngredient(name, null, emptyList(), massFractionPercent, null)
    }
    mutableState.update {
        it.copy(
            safetyAuditResult = audit,
            notice = if (audit.permitsBeverageConsumption) {
                "Ingredient audit passed using local catalog evidence."
            } else {
                "Ingredient not approved: verified local food-grade evidence is missing or unsafe."
            }
        )
    }
}

fun CoffeeViewModel.clearNotice() {
    mutableState.update { it.copy(notice = null, error = null) }
}
fun CoffeeViewModel.flushDraft() {
    if (mutableState.value.ready && !replacingDraft) graph.persistDraft(gson.toJson(mutableState.value.draft))
}
fun CoffeeViewModel.setLanguage(lang: AppLanguage) = task("Updating language") {
    graph.repository.saveSetting("user_language", lang.code)
    mutableState.update {
        val updated = it.unitPreferences.copy(language = lang)
        it.copy(unitPreferences = updated, notice = "Language updated to ${lang.displayName}.")
    }
    diagnosticBuffer.log(LogLevel.INFO, "Settings", "Language changed to ${lang.displayName}")
}

fun CoffeeViewModel.setUnitPreferences(prefs: UserUnitPreferences) = task("Saving unit preferences") {
    graph.repository.saveSetting("user_units", gson.toJson(prefs))
    mutableState.update { it.copy(unitPreferences = prefs, notice = "Unit preferences updated.") }
    diagnosticBuffer.log(
        LogLevel.INFO,
        "Settings",
        "Unit preferences updated: ${prefs.temperatureUnit.symbol}, ${prefs.massUnit.symbol}"
    )
}

fun CoffeeViewModel.selectHelpTopic(topicId: String?) {
    mutableState.update { it.copy(selectedHelpTopicId = topicId) }
}

fun CoffeeViewModel.refreshDiagnostics() {
    mutableState.update { it.copy(diagnosticLogSnapshot = diagnosticBuffer.snapshot()) }
}

fun CoffeeViewModel.clearDiagnostics() {
    diagnosticBuffer.clear()
    mutableState.update { it.copy(diagnosticLogSnapshot = emptyList(), notice = "Diagnostic logs cleared.") }
}

fun CoffeeViewModel.exportDiagnosticLogs(context: Context) {
    val formatted = diagnosticBuffer.exportFormattedString()
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, formatted)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Export Diagnostic Logs")
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
    diagnosticBuffer.log(LogLevel.INFO, "Diagnostics", "User exported diagnostic logs via Sharesheet")
}

fun CoffeeViewModel.updateDegradationState(llm: ModuleStatus? = null, ocr: ModuleStatus? = null, ble: ModuleStatus? = null) {
    mutableState.update {
        val current = it.degradationState
        val newState = current.copy(
            llmStatus = llm ?: current.llmStatus,
            ocrStatus = ocr ?: current.ocrStatus,
            bleStatus = ble ?: current.bleStatus
        )
        it.copy(degradationState = newState)
    }
}

// ========================================================================
// Epic S2-E01: Coffee Bean, Ingredient & Equipment Foundation
// ========================================================================
