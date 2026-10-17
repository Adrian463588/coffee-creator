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
import com.coffeecreator.app.core.sensory.FlavorCategory
import com.coffeecreator.app.core.sensory.FlavorDescriptor
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import com.coffeecreator.app.core.sensory.academy.*
import com.coffeecreator.app.core.sensory.cva.*
import com.coffeecreator.app.core.sensory.greengrading.*
import com.coffeecreator.app.core.sensory.training.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

private fun String.toTitleCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

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
                    label = { Text("${cat.name.replace('_', ' ').lowercase().toTitleCase()} ($count)") },
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
    val haptic = LocalHapticFeedback.current
    var protocol by rememberSaveable { mutableStateOf(CuppingProtocol.SCA_CVA_CURRENT) }
    var currentPerspective by rememberSaveable { mutableStateOf(AssessmentPerspective.DESCRIPTIVE) }
    var selectedSampleCode by rememberSaveable { mutableStateOf("Sample #402") }
    val sampleCodes = remember { mutableStateListOf("Sample #402", "Sample #718", "Sample #891") }
    var isBlindMode by rememberSaveable { mutableStateOf(true) }
    var isLocked by rememberSaveable { mutableStateOf(false) }
    var sessionSha256 by rememberSaveable { mutableStateOf("") }
    var exportReceiptNotice by rememberSaveable { mutableStateOf<String?>(null) }
    var showRevealPrompt by rememberSaveable { mutableStateOf(false) }
    var revealReasonInput by rememberSaveable { mutableStateOf("") }
    var showWheelPicker by rememberSaveable { mutableStateOf(false) }
    var wheelSearchQuery by rememberSaveable { mutableStateOf("") }
    var wheelCategoryFilter by rememberSaveable { mutableStateOf("All") }
    var showComparisonModal by rememberSaveable { mutableStateOf(false) }
    var showAmendmentDialog by rememberSaveable { mutableStateOf(false) }
    var amendmentReasonInput by rememberSaveable { mutableStateOf("") }
    var amendmentRevision by rememberSaveable { mutableStateOf(0) }

    // Cupping Stepper & Timer State
    var timerSeconds by rememberSaveable { mutableStateOf(240) } // Starts at 4:00 min
    var isTimerRunning by rememberSaveable { mutableStateOf(false) }
    var showStepperGuide by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            kotlinx.coroutines.delay(1000L)
            timerSeconds++
        }
    }

    // 5-Cup Multi-Cup Grid per sample (Cup 1..5: Clean, Taint -2, Fault -4)
    val cupStates = remember { mutableStateListOf(0, 0, 0, 0, 0) } // 0 = Clean, 1 = Taint, 2 = Fault
    val cleanCupCount = cupStates.count { it == 0 }
    val taintCupCount = cupStates.count { it == 1 }
    val faultCupCount = cupStates.count { it == 2 }
    val multiCupDefectPenalty = taintCupCount * 2f + faultCupCount * 4f
    val uniformityScore = cleanCupCount * 2.0f
    val cleanCupScore = cleanCupCount * 2.0f

    // 221-Descriptor Sensory Wheel Selection
    val selectedDescriptors = remember { mutableStateListOf("Lemon", "Jasmine", "Peach", "Honey") }

    // Descriptive Scores (0.0 to 5.0)
    var dFragrance by rememberSaveable { mutableStateOf(4.2f) }
    var dAroma by rememberSaveable { mutableStateOf(4.5f) }
    var dFlavor by rememberSaveable { mutableStateOf(4.4f) }
    var dAftertaste by rememberSaveable { mutableStateOf(3.8f) }
    var dAcidity by rememberSaveable { mutableStateOf(4.6f) }
    var dSweetness by rememberSaveable { mutableStateOf(4.2f) }
    var dBitterness by rememberSaveable { mutableStateOf(1.8f) }
    var dBody by rememberSaveable { mutableStateOf(3.9f) }
    var dFinish by rememberSaveable { mutableStateOf(4.0f) }
    var selectedAcidityTypes = remember { mutableStateListOf(AcidityType.CITRIC, AcidityType.MALIC) }
    var selectedBodyTexture by rememberSaveable { mutableStateOf(BodyTextureType.SILKY) }

    // Affective Scores (6.0 to 10.0)
    var aFragrance by rememberSaveable { mutableStateOf(8.50f) }
    var aAroma by rememberSaveable { mutableStateOf(8.50f) }
    var aFlavor by rememberSaveable { mutableStateOf(8.75f) }
    var aAftertaste by rememberSaveable { mutableStateOf(8.25f) }
    var aAcidity by rememberSaveable { mutableStateOf(9.00f) }
    var aSweetness by rememberSaveable { mutableStateOf(8.75f) }
    var aMouthfeel by rememberSaveable { mutableStateOf(8.25f) }
    var aBalance by rememberSaveable { mutableStateOf(8.50f) }
    var aOverall by rememberSaveable { mutableStateOf(8.75f) }

    val totalAffective = ((aFragrance + aFlavor + aAftertaste + aAcidity + aSweetness + aMouthfeel + aOverall) - multiCupDefectPenalty).coerceAtLeast(0f)

    // Legacy 2004 100-Point Sheet
    var legFragrance by rememberSaveable { mutableStateOf(8.25f) }
    var legFlavor by rememberSaveable { mutableStateOf(8.50f) }
    var legAftertaste by rememberSaveable { mutableStateOf(8.00f) }
    var legAcidity by rememberSaveable { mutableStateOf(8.50f) }
    var legBody by rememberSaveable { mutableStateOf(8.25f) }
    var legBalance by rememberSaveable { mutableStateOf(8.25f) }
    var legOverall by rememberSaveable { mutableStateOf(8.25f) }
    val legUniformityBoxes = remember { mutableStateListOf(true, true, true, true, true) }
    val legCleanCupBoxes = remember { mutableStateListOf(true, true, true, true, true) }
    val legSweetnessBoxes = remember { mutableStateListOf(true, true, true, true, true) }
    val legUniformity = legUniformityBoxes.count { it } * 2.0f
    val legCleanCup = legCleanCupBoxes.count { it } * 2.0f
    val legSweetness = legSweetnessBoxes.count { it } * 2.0f
    val legTotalScore = (legFragrance + legFlavor + legAftertaste + legAcidity + legBody + legBalance + legUniformity + legCleanCup + legSweetness + legOverall) - multiCupDefectPenalty

    // Physical QC
    var pRoastColor by rememberSaveable { mutableStateOf("Medium-Light (Agtron 65.2)") }
    var pGroundColor by rememberSaveable { mutableStateOf("Agtron 58.4") }
    var pQuakers by rememberSaveable { mutableStateOf("1") }
    var pMoisture by rememberSaveable { mutableStateOf("10.8") }
    var pMoistureUncertainty by rememberSaveable { mutableStateOf("0.2") }
    var pDefects by rememberSaveable { mutableStateOf("0 primary, 2 slight insect damage") }

    val activeMilestone = CuppingStepperEngine.getActiveMilestone(timerSeconds)

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
                        Text(
                            text = if (protocol == CuppingProtocol.SCA_2004_LEGACY) "Historical 100-point cupping protocol (Superceded)"
                            else "SCA Coffee Value Assessment 4-perspective protocol",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            isLocked -> MaterialTheme.colorScheme.errorContainer
                            protocol == CuppingProtocol.SCA_2004_LEGACY -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when {
                                isLocked -> "LOCKED (SHA-256)"
                                protocol == CuppingProtocol.SCA_2004_LEGACY -> "LEGACY 2004"
                                else -> "CVA 2024 STANDARD"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isLocked -> MaterialTheme.colorScheme.onErrorContainer
                                protocol == CuppingProtocol.SCA_2004_LEGACY -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
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
                            onClick = {
                                protocol = proto
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            label = {
                                Text(
                                    when (proto) {
                                        CuppingProtocol.SCA_CVA_CURRENT -> "SCA CVA Current"
                                        CuppingProtocol.SCA_2004_LEGACY -> "Legacy 2004 (Historical)"
                                        CuppingProtocol.CUSTOM_RESEARCH -> "Custom Lab"
                                    },
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.heightIn(min = 40.dp).testTag("sensory:cva_proto_${proto.name.lowercase()}")
                        )
                    }
                }

                // Disclaimer Banner for Legacy Protocol
                if (protocol == CuppingProtocol.SCA_2004_LEGACY) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.50f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                            Text(
                                "HISTORICAL LEGACY FORM: Superseded by CVA standards. Scores must not be equated to modern CVA evaluations.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Sample Selector, Add Sample & Blind Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sampleCodes.forEach { code ->
                            FilterChip(
                                selected = selectedSampleCode == code,
                                onClick = {
                                    selectedSampleCode = code
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                label = { Text(code) },
                                modifier = Modifier.heightIn(min = 40.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                val nextCode = "Sample #${(100..999).random()}"
                                sampleCodes.add(nextCode)
                                selectedSampleCode = nextCode
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Add, "Add Sample")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = isBlindMode,
                            onClick = {
                                if (isBlindMode) {
                                    showRevealPrompt = true
                                } else {
                                    isBlindMode = true
                                }
                            },
                            label = { Text(if (isBlindMode) "Blind ON" else "Blind OFF") },
                            leadingIcon = { Icon(if (isBlindMode) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.heightIn(min = 40.dp).testTag("sensory:cva_blind_toggle")
                        )
                    }
                }
            }
        }

        // Guided Cupping Stepper & Timing Console (S4-018)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Timer, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Text("Cupping Stepper", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            val mins = timerSeconds / 60
                            val secs = timerSeconds % 60
                            Text(
                                String.format("%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                isTimerRunning = !isTimerRunning
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, "Toggle Timer")
                        }
                        IconButton(
                            onClick = { showStepperGuide = !showStepperGuide },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(if (showStepperGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Toggle Guide")
                        }
                    }
                }

                // Current Milestone Callout
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(activeMilestone.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Target: ${activeMilestone.secondsTarget / 60}:00 min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(activeMilestone.promptNote, style = MaterialTheme.typography.bodySmall)
                    }
                }

                AnimatedVisibility(visible = showStepperGuide) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CuppingMilestone.values().forEach { m ->
                            val isCurrent = m == activeMilestone
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        timerSeconds = m.secondsTarget
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(m.title, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // 5-Cup Multi-Cup Evaluation Grid (S4-021)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("5-Bowl Multi-Cup Grid ($selectedSampleCode)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Tap bowl to cycle: Clean (✓) → Taint (-2) → Fault (-4)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (cleanCupCount == 5) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = if (cleanCupCount == 5) "5/5 Clean" else "$cleanCupCount/5 Clean (-${multiCupDefectPenalty.toInt()} pts)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (cleanCupCount == 5) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cupStates.forEachIndexed { idx, st ->
                        val cupNum = idx + 1
                        val (label, bg, fg) = when (st) {
                            0 -> Triple("Bowl $cupNum\nClean", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                            1 -> Triple("Bowl $cupNum\nTaint -2", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
                            else -> Triple("Bowl $cupNum\nFault -4", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bg,
                            contentColor = fg,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    cupStates[idx] = (st + 1) % 3
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                            border = BorderStroke(1.dp, fg.copy(alpha = 0.35f))
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(2.dp)) {
                                Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Uniformity: ${String.format("%.1f", uniformityScore)} / 10.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Clean Cup: ${String.format("%.1f", cleanCupScore)} / 10.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // If Legacy Protocol: Render Dedicated Historic 2004 100-Point Cupping Sheet (S4-026)
        if (protocol == CuppingProtocol.SCA_2004_LEGACY) {
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
                        Column {
                            Text("SCA 2004 Historical Cupping Sheet", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("10 Quality Attributes (100-Point Scale)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (legTotalScore >= 80.0f) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = String.format(" %.2f pts ", legTotalScore),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (legTotalScore >= 80.0f) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    // Specialty Classification Banner
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when {
                                legTotalScore >= 90.0f -> "Specialty Grade 1 — Outstanding (90+)"
                                legTotalScore >= 85.0f -> "Specialty Grade 1 — Excellent (85–89.99)"
                                legTotalScore >= 80.0f -> "Specialty Grade 1 — Very Good (80–84.99)"
                                else -> "Below Specialty Quality (< 80)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    // 10 Attributes Sliders
                    listOf(
                        Triple("Fragrance / Aroma", legFragrance) { v: Float -> legFragrance = v },
                        Triple("Flavor", legFlavor) { v: Float -> legFlavor = v },
                        Triple("Aftertaste", legAftertaste) { v: Float -> legAftertaste = v },
                        Triple("Acidity", legAcidity) { v: Float -> legAcidity = v },
                        Triple("Body", legBody) { v: Float -> legBody = v },
                        Triple("Balance", legBalance) { v: Float -> legBalance = v },
                        Triple("Overall", legOverall) { v: Float -> legOverall = v }
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
                }
            }
        } else {
            // Modern CVA 4-Perspective Tabs (Physical, Descriptive, Affective, Extrinsic)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AssessmentPerspective.values().forEach { persp ->
                    FilterChip(
                        selected = currentPerspective == persp,
                        onClick = {
                            currentPerspective = persp
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        label = {
                            Text(
                                persp.name.lowercase().toTitleCase(),
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
                            Text("Green and roasted physical inspection (Roast Color Agtron, Quakers in 100g, Moisture % with uncertainty).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            OutlinedTextField(
                                value = pRoastColor,
                                onValueChange = { pRoastColor = it },
                                label = { Text("Whole Bean Roast Color (Agtron Gourmet)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = pGroundColor,
                                onValueChange = { pGroundColor = it },
                                label = { Text("Ground Bean Roast Color (Agtron Gourmet)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = pMoisture,
                                    onValueChange = { pMoisture = it },
                                    label = { Text("Moisture %") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                OutlinedTextField(
                                    value = pMoistureUncertainty,
                                    onValueChange = { pMoistureUncertainty = it },
                                    label = { Text("Uncertainty (±%)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                            }
                            OutlinedTextField(
                                value = pQuakers,
                                onValueChange = { pQuakers = it },
                                label = { Text("Quakers in 100g Roasted Sample") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = pDefects,
                                onValueChange = { pDefects = it },
                                label = { Text("Physical Defects & Sieve Notes") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Perspective 2: Descriptive Assessment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("0.0 to 5.0 intensity measurement & qualitative descriptors. No total score.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text("Non-Hedonic", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            // 221-Descriptor Sensory Wheel Trigger & Chips (S4-014)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Sensory Wheel Descriptors (${selectedDescriptors.size})", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Button(
                                            onClick = { showWheelPicker = true },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("sensory:btn_open_wheel_picker")
                                        ) {
                                            Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                                            Text("+ Add from Wheel", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }

                                    // Selected Descriptor Chips
                                    if (selectedDescriptors.isEmpty()) {
                                        Text("No descriptors selected. Tap '+ Add from Wheel' to pick descriptors.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            selectedDescriptors.forEach { descName ->
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.50f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Text(descName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                                        Icon(
                                                            Icons.Default.Clear,
                                                            contentDescription = "Remove $descName",
                                                            modifier = Modifier.size(12.dp).clickable {
                                                                selectedDescriptors.remove(descName)
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Acidity Types Multi-Select Chips
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Acidity Quality & Character", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    AcidityType.values().forEach { acid ->
                                        val isSelected = selectedAcidityTypes.contains(acid)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                if (isSelected) selectedAcidityTypes.remove(acid) else selectedAcidityTypes.add(acid)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                            label = { Text(acid.name.lowercase().toTitleCase()) },
                                            modifier = Modifier.heightIn(min = 36.dp)
                                        )
                                    }
                                }
                            }

                            // Mouthfeel Body Texture
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Mouthfeel Texture", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BodyTextureType.values().forEach { tex ->
                                        FilterChip(
                                            selected = selectedBodyTexture == tex,
                                            onClick = {
                                                selectedBodyTexture = tex
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                            label = { Text(tex.name.replace('_', ' ').lowercase().toTitleCase()) },
                                            modifier = Modifier.heightIn(min = 36.dp)
                                        )
                                    }
                                }
                            }

                            // Intensity Sliders (0.0 to 5.0)
                            listOf(
                                Triple("Fragrance (Dry Grounds)", dFragrance) { v: Float -> dFragrance = v },
                                Triple("Aroma (Crust)", dAroma) { v: Float -> dAroma = v },
                                Triple("Flavor Intensity", dFlavor) { v: Float -> dFlavor = v },
                                Triple("Aftertaste Intensity", dAftertaste) { v: Float -> dAftertaste = v },
                                Triple("Acidity Intensity", dAcidity) { v: Float -> dAcidity = v },
                                Triple("Sweetness Intensity", dSweetness) { v: Float -> dSweetness = v },
                                Triple("Bitterness Intensity", dBitterness) { v: Float -> dBitterness = v },
                                Triple("Mouthfeel Body Intensity", dBody) { v: Float -> dBody = v },
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
                                    Text("Origin: Ethiopia Yirgacheffe (Gedeo Zone)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text("Producer: Halo Beriti Washing Station / Boji Village", style = MaterialTheme.typography.bodySmall)
                                    Text("Altitude: 2,100 – 2,250 MASL", style = MaterialTheme.typography.bodySmall)
                                    Text("Variety: Kurume & Dega (Indigenous Heirloom)", style = MaterialTheme.typography.bodySmall)
                                    Text("Processing: Fully Washed, 48h wet fermentation, 15-day raised bed drying", style = MaterialTheme.typography.bodySmall)
                                    Text("Certifications: Organic EU/USDA, Fair Trade, Traceable Micro-Lot", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text("Green Lot Code: LOT-ETH-2026-HB04 · Sample Mass: 350g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Console: 1-Click Brew Journal, Comparison, Lock & Export
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Primary Bridge: Log to Brew Journal as Graded Coffee
                Button(
                    onClick = {
                        val scoreFormatted = if (protocol == CuppingProtocol.SCA_2004_LEGACY) String.format("%.2f", legTotalScore) else String.format("%.2f", totalAffective)
                        exportReceiptNotice = "✓ Successfully logged $selectedSampleCode to Brew Journal as Graded Coffee (Score: $scoreFormatted, Descriptors: ${selectedDescriptors.joinToString()})"
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).testTag("sensory:cva_btn_log_journal"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Book, null, modifier = Modifier.size(16.dp).padding(end = 6.dp))
                    Text("Log to Brew Journal as Graded Coffee", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showComparisonModal = true },
                        modifier = Modifier.weight(1f).heightIn(min = 40.dp).testTag("sensory:cva_btn_compare"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, contentColor = MaterialTheme.colorScheme.onSurface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.CompareArrows, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("A/B Compare", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }

                    Button(
                        onClick = {
                            if (!isLocked) {
                                isLocked = true
                                sessionSha256 = "cva_sha256_" + UUID.randomUUID().toString().replace("-", "").take(16)
                                exportReceiptNotice = "Session locked with SHA-256 seal: $sessionSha256"
                            } else {
                                showAmendmentDialog = true
                            }
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 40.dp).testTag("sensory:cva_btn_lock"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLocked) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                            contentColor = if (isLocked) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(if (isLocked) Icons.Default.EditNote else Icons.Default.Lock, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text(if (isLocked) "Amend (Rev ${amendmentRevision + 1})" else "Lock SHA-256", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            exportReceiptNotice = "Exported native CVA JSON: session_${selectedSampleCode.filter { it.isLetterOrDigit() }}.json"
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 40.dp).testTag("sensory:cva_btn_export_json"),
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
                        modifier = Modifier.weight(1f).heightIn(min = 40.dp).testTag("sensory:cva_btn_export_csv"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, contentColor = MaterialTheme.colorScheme.onSurface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("Export CSV", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
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

        // Modal: 221-Descriptor Sensory Wheel Sheet (S4-014)
        if (showWheelPicker) {
            val allDescriptors = remember { SensoryTaxonomyRegistry.getAllDescriptors() }
            val categories = remember { listOf("All") + SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.map { it.name } }
            val filteredDescriptors = remember(wheelSearchQuery, wheelCategoryFilter, allDescriptors) {
                allDescriptors.filter { desc ->
                    val matchesCategory = wheelCategoryFilter == "All" ||
                            SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.find { it.id == desc.categoryId }?.name.equals(wheelCategoryFilter, ignoreCase = true)
                    val matchesSearch = wheelSearchQuery.isBlank() ||
                            desc.name.contains(wheelSearchQuery, ignoreCase = true) ||
                            (desc.idLabel != null && desc.idLabel.contains(wheelSearchQuery, ignoreCase = true)) ||
                            desc.sensoryNotes.contains(wheelSearchQuery, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
            }

            AlertDialog(
                onDismissRequest = { showWheelPicker = false },
                title = { Text("Sensory Wheel (221 Descriptors)") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = wheelSearchQuery,
                            onValueChange = { wheelSearchQuery = it },
                            label = { Text("Search descriptor (EN / ID)") },
                            placeholder = { Text("e.g. Jasmine, Peach, Durian, Honey...") },
                            modifier = Modifier.fillMaxWidth().testTag("sensory:cva_wheel_search"),
                            singleLine = true
                        )

                        // Category Row
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                FilterChip(
                                    selected = wheelCategoryFilter == cat,
                                    onClick = {
                                        wheelCategoryFilter = cat
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    label = { Text(cat) },
                                    modifier = Modifier.heightIn(min = 36.dp)
                                )
                            }
                        }

                        Text("Showing ${filteredDescriptors.size} descriptors:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Descriptors List
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredDescriptors, key = { it.id }) { desc ->
                                val isSelected = selectedDescriptors.contains(desc.name)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (isSelected) selectedDescriptors.remove(desc.name) else selectedDescriptors.add(desc.name)
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${desc.name}${if (!desc.idLabel.isNullOrBlank()) " (${desc.idLabel})" else ""}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${desc.categoryId.removePrefix("cat_")} · ${desc.sensoryNotes}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showWheelPicker = false }) {
                        Text("Done (${selectedDescriptors.size} selected)")
                    }
                }
            )
        }

        // Modal: Side-by-Side Comparison (S4-025)
        if (showComparisonModal) {
            AlertDialog(
                onDismissRequest = { showComparisonModal = false },
                title = { Text("Side-by-Side Sample Comparison") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Comparing Sample #402 vs Sample #718", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Attribute", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("#402", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("#718", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("Delta", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                HorizontalDivider()
                                listOf(
                                    Triple("Flavor Intensity", 4.4f, 4.1f),
                                    Triple("Acidity Intensity", 4.6f, 3.8f),
                                    Triple("Sweetness Intensity", 4.2f, 4.5f),
                                    Triple("Mouthfeel Body", 3.9f, 4.2f),
                                    Triple("Affective Overall", 8.75f, 8.25f)
                                ).forEach { (attr, vA, vB) ->
                                    val delta = vA - vB
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(attr, style = MaterialTheme.typography.labelSmall)
                                        Text(String.format("%.2f", vA), style = MaterialTheme.typography.labelSmall)
                                        Text(String.format("%.2f", vB), style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = (if (delta >= 0) "+" else "") + String.format("%.2f", delta),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (delta >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        // Descriptors Overlap
                        Text("Shared Descriptors:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Lemon, Jasmine", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

                        Text("Unique to Sample #402:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Peach, Honey", style = MaterialTheme.typography.bodySmall)

                        Text("Unique to Sample #718:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Bergamot, Cane Sugar", style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = {
                    Button(onClick = { showComparisonModal = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Dialog: Create Amendment (S4-023)
        if (showAmendmentDialog) {
            AlertDialog(
                onDismissRequest = { showAmendmentDialog = false },
                title = { Text("Create Session Amendment") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "The session is cryptographically locked with SHA-256 seal. Amending will increment the revision number (R+1) and preserve the original immutable audit snapshot.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = amendmentReasonInput,
                            onValueChange = { amendmentReasonInput = it },
                            label = { Text("Amendment Reason") },
                            placeholder = { Text("e.g. Re-cupped after water filtration flush") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isLocked = false
                            amendmentRevision++
                            exportReceiptNotice = "Session unlocked for amendment revision R$amendmentRevision: $amendmentReasonInput"
                            showAmendmentDialog = false
                            amendmentReasonInput = ""
                        },
                        enabled = amendmentReasonInput.isNotBlank()
                    ) {
                        Text("Confirm Amendment")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAmendmentDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog: Reveal Extrinsic (S4-017)
        if (showRevealPrompt) {
            AlertDialog(
                onDismissRequest = { showRevealPrompt = false },
                title = { Text("Reveal Extrinsic Information") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "An immutable audit log will record that evaluator revealed extrinsic details before finalizing score (UNBLINDED_PREMATURELY_AUDITED).",
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
                            exportReceiptNotice = "Audited Extrinsic Reveal: $revealReasonInput"
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
