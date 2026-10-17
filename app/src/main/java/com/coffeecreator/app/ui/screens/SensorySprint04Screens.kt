package com.coffeecreator.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.sensory.academy.*
import com.coffeecreator.app.core.sensory.cva.*
import com.coffeecreator.app.core.sensory.greengrading.*
import com.coffeecreator.app.core.sensory.training.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SensoryAcademySection(
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<AcademyCurriculumCategory?>(null) }
    var useIndonesian by rememberSaveable { mutableStateOf(false) }
    var expandedLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    var showOrganicAcidAtlas by rememberSaveable { mutableStateOf(false) }
    var showAromaTaxonomy by rememberSaveable { mutableStateOf(false) }
    val quizAnswers = remember { mutableStateMapOf<String, Int>() }

    val allLessons = remember { AcademyLessonRegistry.allLessons }
    val filteredLessons = remember(searchQuery, selectedCategory, allLessons) {
        allLessons.filter { lesson ->
            val matchesCategory = selectedCategory == null || lesson.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    lesson.id.contains(searchQuery, ignoreCase = true) ||
                    lesson.titleEn.contains(searchQuery, ignoreCase = true) ||
                    lesson.titleId.contains(searchQuery, ignoreCase = true) ||
                    lesson.glossary.any { it.termEn.contains(searchQuery, ignoreCase = true) || it.termId.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Academy Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (useIndonesian) "Akademi Sensori Kopi (22 Modul)" else "Coffee Sensory Academy (22 Modules)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (useIndonesian) "Kurikulum ilmiah botani, biologi gustatori, CVA & grading" else "Scientific syllabus: botany, gustatory anatomy, CVA & grading",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Bilingual Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f), RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (!useIndonesian) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .clickable { useIndonesian = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("sensory:academy_lang_en")
                        ) {
                            Text(
                                "EN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (!useIndonesian) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (useIndonesian) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .clickable { useIndonesian = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("sensory:academy_lang_id")
                        ) {
                            Text(
                                "ID",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (useIndonesian) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(if (useIndonesian) "Cari modul / istilah glosarium..." else "Search lessons / glossary...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("sensory:academy_search"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Quick Tool Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showOrganicAcidAtlas = !showOrganicAcidAtlas },
                        modifier = Modifier.weight(1f).heightIn(min = 40.dp).testTag("sensory:btn_organic_acids"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showOrganicAcidAtlas) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = if (showOrganicAcidAtlas) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.Science, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text(if (useIndonesian) "Atlas Asam Organik" else "Organic Acid Atlas", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }

                    Button(
                        onClick = { showAromaTaxonomy = !showAromaTaxonomy },
                        modifier = Modifier.weight(1f).heightIn(min = 40.dp).testTag("sensory:btn_aroma_taxonomy"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showAromaTaxonomy) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = if (showAromaTaxonomy) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.Spa, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text(if (useIndonesian) "Taksonomi Aroma 60" else "Aroma Taxonomy 60", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }
        }

        // Drawer: Organic Acid Atlas
        AnimatedVisibility(visible = showOrganicAcidAtlas) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (useIndonesian) "Atlas 7 Asam Organik Kopi & Standar Kalibrasi" else "Coffee Organic Acid Atlas & Calibration Reference",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (useIndonesian) "Koreksi ilmiah: Asam bukanlah rasa dasar ke-6. Asam (sourness) adalah rasa dasar; 'acidity' adalah persepsi multimodal kompleks."
                        else "Scientific correction: Acid is not a 6th basic taste. Sourness is the basic taste; 'acidity' is a complex multimodal perception.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OrganicAcidAtlas.allAcids.forEach { acid ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (useIndonesian) acid.nameId else acid.nameEn} (${acid.chemicalFormula})",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("pKa1: ${acid.pKa1}", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Text("Sensory: ${if (useIndonesian) acid.typicalSensoryCuesId else acid.typicalSensoryCuesEn}", style = MaterialTheme.typography.bodySmall)
                                Text("In Coffee: ${if (useIndonesian) acid.occurrenceInCoffeeId else acid.occurrenceInCoffeeEn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Prep Reference: ${if (useIndonesian) acid.safeReferencePreparationNotesId else acid.safeReferencePreparationNotesEn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                Text("Standard Test: ${acid.standardTestingConcentrationGramsPerLiter} g/L | Daily Limit: ${acid.maxSafeDailyDosageThresholdGramsPerLiter} g/L", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Drawer: Aroma Taxonomy
        AnimatedVisibility(visible = showAromaTaxonomy) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (useIndonesian) "Taksonomi Aroma Independen (Kompatibel Le Nez du Café 60)" else "Generic Aroma Taxonomy (Le Nez du Café 60 Compatible)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = if (useIndonesian) "Taksonomi aroma generic independen untuk latihan vial kit fisik milik pengguna tanpa konten kit berlisensi."
                        else "Independent generic aroma taxonomy for user-owned physical vial kit practice without proprietary kit content.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    GenericAromaRegistry.ALL_GENERIC_AROMAS.forEach { aroma ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (useIndonesian) aroma.nameId else aroma.nameEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Family: ${aroma.familyGroup} · ${aroma.chemicalCompoundHint} (${if (useIndonesian) aroma.householdFoodReferenceId else aroma.householdFoodReferenceEn})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(aroma.userKitVialMappingId ?: aroma.id, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All (${allLessons.size})") },
                modifier = Modifier.testTag("sensory:academy_cat_all").heightIn(min = 40.dp)
            )
            AcademyCurriculumCategory.values().forEach { cat ->
                val count = allLessons.count { it.category == cat }
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                    label = { Text("${cat.name.replace('_', ' ').lowercase().capitalize()} ($count)") },
                    modifier = Modifier.testTag("sensory:academy_cat_${cat.name.lowercase()}").heightIn(min = 40.dp)
                )
            }
        }

        // Lessons List
        filteredLessons.forEach { lesson ->
            val isExpanded = expandedLessonId == lesson.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sensory:lesson_card_${lesson.id}")
                    .animateContentSize(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.50f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceContainer
                    else MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedLessonId = if (isExpanded) null else lesson.id },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(lesson.id, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    lesson.category.name.replace('_', ' '),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text("· ${lesson.estimatedDurationMinutes} min", style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (useIndonesian) lesson.titleId else lesson.titleEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { expandedLessonId = if (isExpanded) null else lesson.id }) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand"
                            )
                        }
                    }

                    // Expanded Content
                    if (isExpanded) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Learning Objectives
                        Text(
                            text = if (useIndonesian) "Tujuan Pembelajaran:" else "Learning Objectives:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val objectives = if (useIndonesian) lesson.objectivesId else lesson.objectivesEn
                        objectives.forEach { obj ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("•", color = MaterialTheme.colorScheme.primary)
                                Text(obj, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        // Lesson Content
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (useIndonesian) lesson.contentMarkdownId else lesson.contentMarkdownEn,
                            style = MaterialTheme.typography.bodySmall
                        )

                        // In-house ASCII / Text Diagram if present
                        if (lesson.asciiDiagram != null) {
                            Spacer(Modifier.height(6.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Text(
                                    text = lesson.asciiDiagram,
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace)
                                )
                            }
                        }

                        // Glossary Terms
                        if (lesson.glossary.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (useIndonesian) "Glosarium Terminologi:" else "Glossary of Terms:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            lesson.glossary.forEach { g ->
                                Column(modifier = Modifier.padding(start = 6.dp, top = 2.dp)) {
                                    Text(
                                        text = "${if (useIndonesian) g.termId else g.termEn}:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = if (useIndonesian) g.definitionId else g.definitionEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Practice Quiz
                        if (lesson.practiceQuestions.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (useIndonesian) "Kuis Praktik & Uji Pemahaman:" else "Practice Quiz & Comprehension:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            lesson.practiceQuestions.forEachIndexed { qIdx, q ->
                                val selectedOption = quizAnswers["${lesson.id}_$qIdx"]
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Q${qIdx + 1}: ${if (useIndonesian) q.promptId else q.promptEn}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        q.options.forEachIndexed { oIdx, opt ->
                                            val isSelected = selectedOption == oIdx
                                            val isCorrect = opt.isCorrect
                                            val showFeedback = selectedOption != null

                                            val bg = when {
                                                !showFeedback -> MaterialTheme.colorScheme.surfaceContainerLow
                                                isSelected && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                                                isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                                                isCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                                else -> MaterialTheme.colorScheme.surfaceContainerLow
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(bg)
                                                    .clickable { quizAnswers["${lesson.id}_$qIdx"] = oIdx }
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { quizAnswers["${lesson.id}_$qIdx"] = oIdx },
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = if (useIndonesian) opt.textId else opt.textEn,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }

                                        if (selectedOption != null) {
                                            val chosenOpt = q.options.getOrNull(selectedOption)
                                            val correct = chosenOpt?.isCorrect == true
                                            Text(
                                                text = if (correct) {
                                                    "✓ " + (if (useIndonesian) "Benar! " else "Correct! ") + (if (useIndonesian) q.explanationId else q.explanationEn)
                                                } else {
                                                    "✗ " + (if (useIndonesian) "Salah. " else "Incorrect. ") + (if (useIndonesian) q.explanationId else q.explanationEn)
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Citations & Accessibility notes
                        if (lesson.citations.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Citations: " + lesson.citations.joinToString("; ") { "${it.title} (${it.authorOrOrganization}, ${it.year})" },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SensoryCvaCuppingSection(
    modifier: Modifier = Modifier
) {
    var protocol by rememberSaveable { mutableStateOf(CuppingProtocol.SCA_CVA_CURRENT) }
    var currentPerspective by rememberSaveable { mutableStateOf(AssessmentPerspective.DESCRIPTIVE) }
    var selectedSampleCode by rememberSaveable { mutableStateOf("Sample #402") }
    var isBlindMode by rememberSaveable { mutableStateOf(true) }
    var isLocked by rememberSaveable { mutableStateOf(false) }
    var sessionSha256 by rememberSaveable { mutableStateOf("") }
    var exportReceiptNotice by rememberSaveable { mutableStateOf<String?>(null) }
    var showRevealPrompt by rememberSaveable { mutableStateOf(false) }
    var revealReasonInput by rememberSaveable { mutableStateOf("") }

    // Descriptive Scores (0.0 to 5.0)
    var dFragrance by rememberSaveable { mutableStateOf(3.8f) }
    var dAroma by rememberSaveable { mutableStateOf(4.0f) }
    var dFlavor by rememberSaveable { mutableStateOf(4.2f) }
    var dAftertaste by rememberSaveable { mutableStateOf(3.9f) }
    var dAcidity by rememberSaveable { mutableStateOf(4.1f) }
    var dSweetness by rememberSaveable { mutableStateOf(4.3f) }
    var dBitterness by rememberSaveable { mutableStateOf(1.8f) }
    var dBody by rememberSaveable { mutableStateOf(3.5f) }
    var dFinish by rememberSaveable { mutableStateOf(4.0f) }

    // Affective Scores (6.0 to 10.0)
    var aFragrance by rememberSaveable { mutableStateOf(8.25f) }
    var aAroma by rememberSaveable { mutableStateOf(8.50f) }
    var aFlavor by rememberSaveable { mutableStateOf(8.75f) }
    var aAftertaste by rememberSaveable { mutableStateOf(8.25f) }
    var aAcidity by rememberSaveable { mutableStateOf(8.50f) }
    var aSweetness by rememberSaveable { mutableStateOf(8.75f) }
    var aMouthfeel by rememberSaveable { mutableStateOf(8.25f) }
    var aBalance by rememberSaveable { mutableStateOf(8.50f) }
    var aOverall by rememberSaveable { mutableStateOf(8.50f) }
    var defectTaints by rememberSaveable { mutableStateOf(0) }
    var defectFaults by rememberSaveable { mutableStateOf(0) }

    // Physical QC
    var pRoastColor by rememberSaveable { mutableStateOf("Medium-Light (Agtron 65)") }
    var pDefects by rememberSaveable { mutableStateOf("0 quakers, 0 scorched") }
    var pRoastDays by rememberSaveable { mutableStateOf("7") }

    val totalAffective = (aFragrance + aAroma + aFlavor + aAftertaste + aAcidity + aSweetness + aMouthfeel + aBalance + aOverall) - (defectTaints * 2f + defectFaults * 4f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // CVA Header & Protocol Switcher
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Digital CVA Cupping Lab", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("SCA Coffee Value Assessment 4-perspective protocol", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isLocked) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isLocked) "LOCKED (SHA-256)" else "DRAFT / ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocked) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Protocol selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CuppingProtocol.values().forEach { proto ->
                        FilterChip(
                            selected = protocol == proto,
                            onClick = { protocol = proto },
                            label = { Text(proto.name.replace('_', ' ')) },
                            modifier = Modifier.heightIn(min = 40.dp).testTag("sensory:cva_proto_${proto.name.lowercase()}")
                        )
                    }
                }

                // Sample Selector & Blind Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Sample #402", "Sample #718", "Sample #891").forEach { code ->
                            FilterChip(
                                selected = selectedSampleCode == code,
                                onClick = { selectedSampleCode = code },
                                label = { Text(code) },
                                modifier = Modifier.heightIn(min = 40.dp)
                            )
                        }
                    }

                    FilterChip(
                        selected = isBlindMode,
                        onClick = {
                            if (isBlindMode) {
                                showRevealPrompt = true
                            } else {
                                isBlindMode = true
                            }
                        },
                        label = { Text(if (isBlindMode) "Blind Mode (ON)" else "Blind Mode (OFF)") },
                        leadingIcon = { Icon(if (isBlindMode) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.heightIn(min = 40.dp).testTag("sensory:cva_blind_toggle")
                    )
                }
            }
        }

        // Perspective Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AssessmentPerspective.values().forEach { persp ->
                FilterChip(
                    selected = currentPerspective == persp,
                    onClick = { currentPerspective = persp },
                    label = {
                        Text(
                            persp.name.capitalize(),
                            fontWeight = if (currentPerspective == persp) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.heightIn(min = 44.dp).testTag("sensory:cva_tab_${persp.name.lowercase()}")
                )
            }
        }

        // Perspective Body
        when (currentPerspective) {
            AssessmentPerspective.PHYSICAL -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Perspective 1: Physical Assessment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Evaluates green/roasted physical beans, roast color, and roast defects prior to cupping.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        OutlinedTextField(
                            value = pRoastColor,
                            onValueChange = { pRoastColor = it },
                            label = { Text("Roast Color (Agtron / RoastQuik)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pDefects,
                            onValueChange = { pDefects = it },
                            label = { Text("Roast Defects (Quakers / Scorched / Chipped)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pRoastDays,
                            onValueChange = { pRoastDays = it },
                            label = { Text("Days Post-Roast") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }

            AssessmentPerspective.DESCRIPTIVE -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Perspective 2: Descriptive Assessment (0.0 to 5.0 Intensity)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Non-hedonic intensity measurement for sensory descriptors and basic taste axes.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        listOf(
                            Triple("Fragrance", dFragrance) { v: Float -> dFragrance = v },
                            Triple("Aroma", dAroma) { v: Float -> dAroma = v },
                            Triple("Flavor", dFlavor) { v: Float -> dFlavor = v },
                            Triple("Aftertaste", dAftertaste) { v: Float -> dAftertaste = v },
                            Triple("Acidity Intensity", dAcidity) { v: Float -> dAcidity = v },
                            Triple("Sweetness Intensity", dSweetness) { v: Float -> dSweetness = v },
                            Triple("Bitterness Intensity", dBitterness) { v: Float -> dBitterness = v },
                            Triple("Body / Mouthfeel", dBody) { v: Float -> dBody = v },
                            Triple("Finish Duration", dFinish) { v: Float -> dFinish = v }
                        ).forEach { (label, value, setter) ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Text(String.format("%.1f / 5.0", value), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = value,
                                    onValueChange = setter,
                                    valueRange = 0.0f..5.0f,
                                    steps = 49,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            AssessmentPerspective.AFFECTIVE -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Perspective 3: Affective Assessment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Hedonic quality scores on standard 6.00 – 10.00 scale", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Text(
                                    String.format(" %.2f pts ", totalAffective),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        listOf(
                            Triple("Fragrance / Aroma", aFragrance) { v: Float -> aFragrance = v },
                            Triple("Flavor", aFlavor) { v: Float -> aFlavor = v },
                            Triple("Aftertaste", aAftertaste) { v: Float -> aAftertaste = v },
                            Triple("Acidity Quality", aAcidity) { v: Float -> aAcidity = v },
                            Triple("Sweetness Quality", aSweetness) { v: Float -> aSweetness = v },
                            Triple("Mouthfeel Quality", aMouthfeel) { v: Float -> aMouthfeel = v },
                            Triple("Balance", aBalance) { v: Float -> aBalance = v },
                            Triple("Overall Impression", aOverall) { v: Float -> aOverall = v }
                        ).forEach { (label, value, setter) ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Text(String.format("%.2f / 10.0", value), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = value,
                                    onValueChange = setter,
                                    valueRange = 6.0f..10.0f,
                                    steps = 39,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        HorizontalDivider()

                        // Defects penalty
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Defects Penalties", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { if (defectTaints > 0) defectTaints-- else defectTaints++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (defectTaints > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Taints: $defectTaints (-${defectTaints * 2})", style = MaterialTheme.typography.labelSmall)
                                }
                                Button(
                                    onClick = { if (defectFaults > 0) defectFaults-- else defectFaults++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (defectFaults > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Faults: $defectFaults (-${defectFaults * 4})", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            AssessmentPerspective.EXTRINSIC -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Perspective 4: Extrinsic Assessment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        if (isBlindMode) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("🔒 BLIND MASKING ACTIVE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    Text(
                                        "Extrinsic attributes (origin, farm, variety, altitude, roaster, price) are masked to prevent unconscious bias. Revealing requires an explicit audit log reason.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Button(
                                        onClick = { showRevealPrompt = true },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Reveal Extrinsic with Audit Reason", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Origin: Ethiopia Yirgacheffe", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Producer: Halo Beriti Washing Station", style = MaterialTheme.typography.bodySmall)
                                Text("Altitude: 2,100 – 2,250 MASL", style = MaterialTheme.typography.bodySmall)
                                Text("Variety: Kurume & Dega (Heirloom)", style = MaterialTheme.typography.bodySmall)
                                Text("Processing: Washed, 48h wet fermentation, raised bed drying", style = MaterialTheme.typography.bodySmall)
                                Text("Certifications: Organic, Traceable Lot", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Action Row: Lock session & Export buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isLocked = true
                    sessionSha256 = "cva_sha256_" + UUID.randomUUID().toString().replace("-", "").take(16)
                    exportReceiptNotice = "Session locked with SHA-256 fingerprint: $sessionSha256"
                },
                enabled = !isLocked,
                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("sensory:cva_btn_lock"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                Text(if (isLocked) "Session Locked" else "Lock & Sign SHA-256", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }

            Button(
                onClick = {
                    exportReceiptNotice = "Exported native CVA JSON: session_${selectedSampleCode.filter { it.isLetterOrDigit() }}.json"
                },
                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("sensory:cva_btn_export_json"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, contentColor = MaterialTheme.colorScheme.onSurface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
            ) {
                Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                Text("Export JSON", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }

            Button(
                onClick = {
                    exportReceiptNotice = "Exported native CVA CSV: session_${selectedSampleCode.filter { it.isLetterOrDigit() }}.csv"
                },
                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("sensory:cva_btn_export_csv"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, contentColor = MaterialTheme.colorScheme.onSurface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
            ) {
                Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                Text("Export CSV", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }

        // Receipt / Notice
        if (exportReceiptNotice != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(exportReceiptNotice!!, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                    IconButton(onClick = { exportReceiptNotice = null }) {
                        Icon(Icons.Default.Clear, contentDescription = "Dismiss", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Dialog: Reveal Extrinsic
        if (showRevealPrompt) {
            AlertDialog(
                onDismissRequest = { showRevealPrompt = false },
                title = { Text("Reveal Extrinsic Information") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "An immutable audit log will record that evaluator revealed extrinsic details before finalizing score.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = revealReasonInput,
                            onValueChange = { revealReasonInput = it },
                            label = { Text("Audit Reason") },
                            placeholder = { Text("e.g. Completed affective scoring, verifying lot altitude") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isBlindMode = false
                            showRevealPrompt = false
                        },
                        enabled = revealReasonInput.isNotBlank()
                    ) {
                        Text("Confirm & Reveal")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRevealPrompt = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SensoryTrainingLabSection(
    modifier: Modifier = Modifier
) {
    var drillPermutation by rememberSaveable { mutableStateOf(TriangleSampleOrder.ABA) }
    var cup1Code by rememberSaveable { mutableStateOf("Cup 1 (#381)") }
    var cup2Code by rememberSaveable { mutableStateOf("Cup 2 (#920)") }
    var cup3Code by rememberSaveable { mutableStateOf("Cup 3 (#415)") }
    var selectedCupIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var drillSubmitted by rememberSaveable { mutableStateOf(false) }

    // Binomial stats inputs
    var trialsCountInput by rememberSaveable { mutableStateOf("18") }
    var correctCountInput by rememberSaveable { mutableStateOf("10") }

    val totalTrials = trialsCountInput.toIntOrNull()?.coerceAtLeast(1) ?: 18
    val correctTrials = (correctCountInput.toIntOrNull() ?: 10).coerceIn(0, totalTrials)
    val binomialStat = remember(totalTrials, correctTrials) {
        TriangleStatisticsEngine.calculateBinomialPValue(totalTrials, correctTrials)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Triangle Test Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Triangle Discrimination Drill", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Balanced 6 permutations (AAB, ABA, BAA, BBA, BAB, ABB)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            val allPerms = TriangleSampleOrder.values()
                            val nextPerm = allPerms[(drillPermutation.ordinal + 1) % allPerms.size]
                            drillPermutation = nextPerm
                            cup1Code = "Cup 1 (#${(100..999).random()})"
                            cup2Code = "Cup 2 (#${(100..999).random()})"
                            cup3Code = "Cup 3 (#${(100..999).random()})"
                            selectedCupIndex = null
                            drillSubmitted = false
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                        modifier = Modifier.testTag("sensory:btn_new_triangle")
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("New Drill", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Text(
                    "Identify the odd cup among the 3 coded samples. Two are identical baseline (A), one is target (B):",
                    style = MaterialTheme.typography.bodySmall
                )

                // 3 Cup Cards
                val oddCupIdx = drillPermutation.oddCupIndex // 0, 1, or 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(Pair(0, cup1Code), Pair(1, cup2Code), Pair(2, cup3Code)).forEach { (idx, label) ->
                        val isSelected = selectedCupIndex == idx
                        val isOddCup = idx == oddCupIdx
                        val sampleLabel = if (isOddCup) "B (Target)" else "A (Baseline)"

                        val cardBg = when {
                            !drillSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                            !drillSubmitted -> MaterialTheme.colorScheme.surfaceContainer
                            drillSubmitted && isOddCup -> MaterialTheme.colorScheme.primaryContainer
                            drillSubmitted && isSelected && !isOddCup -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceContainer
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !drillSubmitted) { selectedCupIndex = idx }
                                .testTag("sensory:triangle_cup_$idx"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)
                            ),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Coffee, null, modifier = Modifier.size(24.dp))
                                Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                if (drillSubmitted) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isOddCup) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            if (isOddCup) "ODD ($sampleLabel)" else sampleLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isOddCup) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Submit Button
                Button(
                    onClick = { drillSubmitted = true },
                    enabled = selectedCupIndex != null && !drillSubmitted,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).testTag("sensory:btn_submit_triangle"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (drillSubmitted) "Result Evaluated" else "Submit Selection", style = MaterialTheme.typography.labelSmall)
                }

                if (drillSubmitted) {
                    val correct = selectedCupIndex == oddCupIdx
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = if (correct) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                if (correct) "✓ Correct Identification!" else "✗ Incorrect Identification.",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Text(
                                "Permutation was: ${drillPermutation.name} (${drillPermutation.presentation}). The odd cup was Cup ${oddCupIdx + 1}.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Binomial Statistics Engine Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Exact Cumulative Binomial Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Calculates exact P(X >= k) with p0 = 1/3 according to ISO 4120 sensory analysis standards.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = trialsCountInput,
                        onValueChange = { trialsCountInput = it },
                        label = { Text("Total Trials (n)") },
                        modifier = Modifier.weight(1f).testTag("sensory:stat_trials_n"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = correctCountInput,
                        onValueChange = { correctCountInput = it },
                        label = { Text("Correct (k)") },
                        modifier = Modifier.weight(1f).testTag("sensory:stat_correct_k"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Probability Output Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (binomialStat.isSignificantAlpha05) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Cumulative p-value:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                String.format("p = %.4f", binomialStat.pValueCumulative),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (binomialStat.isSignificantAlpha05) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                if (binomialStat.isSignificantAlpha05) "✓ STATISTICALLY SIGNIFICANT (p < 0.05)"
                                else "✗ NOT SIGNIFICANT (p >= 0.05)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            "Scientific Caveat: Assumes independent Bernoulli trials with zero carryover. Rapid serial cupping introduces sensory fatigue and cross-cup adaptation, which may violate independence assumptions.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 5 Basic Tastes References
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Standard 5 Basic Taste Solutions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Rigorous reference preparations for sensory calibration (ISO 8586 / SCA standard).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                StandardTasteReferences.ALL_REFERENCES.forEach { tasteRef ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${tasteRef.taste.labelEn}: ${tasteRef.chemicalName} (${tasteRef.chemicalFormula})",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Detection Threshold: ${tasteRef.detectionThresholdGramsPerLiter} g/L", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Safe Handling: ${tasteRef.safeHandlingWarning}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    " ${tasteRef.mediumIntensityGramsPerLiter} g/L ",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Roast Defects Atlas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Roast Defect Sensory Recognition", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Physical and gustatory markers of common roasting defects.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                StandardRoastDefects.ALL_DEFECTS.forEach { defect ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(defect.category.labelEn, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("Visual Cues: ${defect.visualAttributesEn}", style = MaterialTheme.typography.bodySmall)
                            Text("Root Cause: ${defect.roastingRootCauseEn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Cup Profile: ${defect.sensoryAttributesEn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SensoryGreenGradingSection(
    modifier: Modifier = Modifier
) {
    // Category 1 Primary Defects (1:1 full defect equivalent)
    var cat1FullBlack by rememberSaveable { mutableStateOf(0) }
    var cat1FullSour by rememberSaveable { mutableStateOf(0) }
    var cat1DriedCherry by rememberSaveable { mutableStateOf(0) }
    var cat1Fungus by rememberSaveable { mutableStateOf(0) }
    var cat1ForeignMatter by rememberSaveable { mutableStateOf(0) }
    var cat1SevereInsect by rememberSaveable { mutableStateOf(0) }

    // Category 2 Secondary Defects (weighted ratio)
    var cat2PartialBlack by rememberSaveable { mutableStateOf(0) } // 3:1
    var cat2PartialSour by rememberSaveable { mutableStateOf(0) }  // 3:1
    var cat2Parchment by rememberSaveable { mutableStateOf(0) }    // 5:1
    var cat2Floater by rememberSaveable { mutableStateOf(0) }      // 5:1
    var cat2Immature by rememberSaveable { mutableStateOf(0) }     // 5:1
    var cat2Withered by rememberSaveable { mutableStateOf(0) }     // 5:1
    var cat2Shell by rememberSaveable { mutableStateOf(0) }        // 5:1
    var cat2Broken by rememberSaveable { mutableStateOf(0) }       // 5:1
    var cat2Hull by rememberSaveable { mutableStateOf(0) }         // 5:1
    var cat2SlightInsect by rememberSaveable { mutableStateOf(0) } // 10:1

    // Moisture & Sieve
    var moistureInput by rememberSaveable { mutableStateOf("10.8") }
    var sieve18Input by rememberSaveable { mutableStateOf("120") }
    var sieve16Input by rememberSaveable { mutableStateOf("160") }
    var sieve14Input by rememberSaveable { mutableStateOf("55") }
    var sievePanInput by rememberSaveable { mutableStateOf("15") }

    val cat1Total = cat1FullBlack + cat1FullSour + cat1DriedCherry + cat1Fungus + cat1ForeignMatter + cat1SevereInsect
    val cat2Equivalent = (cat2PartialBlack / 3) + (cat2PartialSour / 3) + (cat2Parchment / 5) + (cat2Floater / 5) +
            (cat2Immature / 5) + (cat2Withered / 5) + (cat2Shell / 5) + (cat2Broken / 5) + (cat2Hull / 5) + (cat2SlightInsect / 10)
    val totalEquivalentDefects = cat1Total + cat2Equivalent

    val moistureVal = moistureInput.toDoubleOrNull() ?: 10.8
    val gradingEvaluation = remember(cat1Total, cat2Equivalent, moistureVal) {
        val sample = GreenLotSample(
            lotId = "sample_grading_001",
            lotName = "350g Washed Arabica Sample",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.FULL_BLACK, cat1FullBlack),
                DefectTallyRecord(GreenDefectType.FULL_SOUR, cat1FullSour),
                DefectTallyRecord(GreenDefectType.DRIED_CHERRY_POD, cat1DriedCherry),
                DefectTallyRecord(GreenDefectType.FUNGUS_DAMAGED, cat1Fungus),
                DefectTallyRecord(GreenDefectType.FOREIGN_MATTER, cat1ForeignMatter),
                DefectTallyRecord(GreenDefectType.SEVERE_INSECT_DAMAGE, cat1SevereInsect),
                DefectTallyRecord(GreenDefectType.PARTIAL_BLACK, cat2PartialBlack),
                DefectTallyRecord(GreenDefectType.PARTIAL_SOUR, cat2PartialSour),
                DefectTallyRecord(GreenDefectType.PARCHMENT, cat2Parchment),
                DefectTallyRecord(GreenDefectType.FLOATER, cat2Floater),
                DefectTallyRecord(GreenDefectType.IMMATURE_UNRIPE, cat2Immature),
                DefectTallyRecord(GreenDefectType.WITHERED, cat2Withered),
                DefectTallyRecord(GreenDefectType.SHELL, cat2Shell),
                DefectTallyRecord(GreenDefectType.BROKEN_CHIPPED_CUT, cat2Broken),
                DefectTallyRecord(GreenDefectType.HULL_HUSK, cat2Hull),
                DefectTallyRecord(GreenDefectType.SLIGHT_INSECT_DAMAGE, cat2SlightInsect)
            ),
            moisture = MoistureAssessment(moistureVal)
        )
        StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Green QC Overview & Classification Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("350g Washed Arabica Physical QC", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("SCA 2004 Green Coffee Defect Standard", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (gradingEvaluation.grade) {
                            PhysicalGradeClassification.SPECIALTY_GRADE_1 -> MaterialTheme.colorScheme.primaryContainer
                            PhysicalGradeClassification.PREMIUM_GRADE_2 -> MaterialTheme.colorScheme.secondaryContainer
                            PhysicalGradeClassification.EXCHANGE_GRADE_3 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = " " + gradingEvaluation.grade.label + " ",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                // Tallies summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cat 1 Defects", style = MaterialTheme.typography.labelSmall)
                            Text("$cat1Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (cat1Total == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            Text("(Max 0 for Specialty)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cat 2 Full Equiv", style = MaterialTheme.typography.labelSmall)
                            Text("$cat2Equivalent", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("(Max 5 for Specialty)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Equiv", style = MaterialTheme.typography.labelSmall)
                            Text("$totalEquivalentDefects", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("in 350g sample", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Category 1 Primary Defects Counter
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category 1: Primary Full Defects (1:1 Ratio)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Text("A single Category 1 defect immediately disqualifies sample from Specialty Grade.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                listOf(
                    Triple("Full Black", cat1FullBlack) { v: Int -> cat1FullBlack = v },
                    Triple("Full Sour", cat1FullSour) { v: Int -> cat1FullSour = v },
                    Triple("Dried Cherry / Pod", cat1DriedCherry) { v: Int -> cat1DriedCherry = v },
                    Triple("Fungus Damaged", cat1Fungus) { v: Int -> cat1Fungus = v },
                    Triple("Foreign Matter (Stone/Stick)", cat1ForeignMatter) { v: Int -> cat1ForeignMatter = v },
                    Triple("Severe Insect Damage", cat1SevereInsect) { v: Int -> cat1SevereInsect = v }
                ).forEach { (name, count, setter) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = { if (count > 0) setter(count - 1) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Decrement", modifier = Modifier.size(16.dp))
                            }
                            Text("$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { setter(count + 1) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Increment", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Category 2 Secondary Defects Counter
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category 2: Secondary Defects (Weighted Equivalence)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Multiple beans required to equal 1 full defect equivalent.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                listOf(
                    Pair("Partial Black (3:1)", cat2PartialBlack) to { v: Int -> cat2PartialBlack = v },
                    Pair("Partial Sour (3:1)", cat2PartialSour) to { v: Int -> cat2PartialSour = v },
                    Pair("Parchment (5:1)", cat2Parchment) to { v: Int -> cat2Parchment = v },
                    Pair("Floater Bean (5:1)", cat2Floater) to { v: Int -> cat2Floater = v },
                    Pair("Immature / Unripe (5:1)", cat2Immature) to { v: Int -> cat2Immature = v },
                    Pair("Withered (5:1)", cat2Withered) to { v: Int -> cat2Withered = v },
                    Pair("Shell Bean (5:1)", cat2Shell) to { v: Int -> cat2Shell = v },
                    Pair("Broken / Chipped (5:1)", cat2Broken) to { v: Int -> cat2Broken = v },
                    Pair("Hull / Husk (5:1)", cat2Hull) to { v: Int -> cat2Hull = v },
                    Pair("Slight Insect Damage (10:1)", cat2SlightInsect) to { v: Int -> cat2SlightInsect = v }
                ).forEach { (pair, setter) ->
                    val (name, count) = pair
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = { if (count > 0) setter(count - 1) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Decrement", modifier = Modifier.size(16.dp))
                            }
                            Text("$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { setter(count + 1) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Increment", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Moisture Content & Sieve Size Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Moisture & Sieve Distribution", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = moistureInput,
                    onValueChange = { moistureInput = it },
                    label = { Text("Moisture % (SCA Standard: 9.0% – 13.0%)") },
                    modifier = Modifier.fillMaxWidth().testTag("sensory:green_moisture_input"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Text(
                    "Measurement uncertainty: ±0.3% (calibrated dielectric or loss-on-drying meter).",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text("Screen Sieve Fractions (350g Sample):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = sieve18Input,
                        onValueChange = { sieve18Input = it },
                        label = { Text("Scr 18 (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = sieve16Input,
                        onValueChange = { sieve16Input = it },
                        label = { Text("Scr 16 (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = sieve14Input,
                        onValueChange = { sieve14Input = it },
                        label = { Text("Scr 14 (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = sievePanInput,
                        onValueChange = { sievePanInput = it },
                        label = { Text("Pan (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Out-of-scope Guard Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(
                    "Out-of-scope guard: The SCA 2004 green grading protocol applies exclusively to Washed Coffea Arabica. Natural (dry-processed) or Coffea canephora (Robusta) lots require separate specialized grading matrices.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
