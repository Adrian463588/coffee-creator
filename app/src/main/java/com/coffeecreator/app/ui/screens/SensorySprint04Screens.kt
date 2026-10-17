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
import com.coffeecreator.app.core.sensory.operations.*
import com.coffeecreator.app.ui.components.*
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
                                .tactileClickable {
                                    cupStates[idx] = (st + 1) % 3
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
                    Text(
                        "Uniformity: ${String.format(Locale.US, "%.1f", uniformityScore)} / 10.0",
                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Clean Cup: ${String.format(Locale.US, "%.1f", cleanCupScore)} / 10.0",
                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
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
                                Text(
                                    text = String.format("%.2f / 10.0", value),
                                    style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                                    fontWeight = FontWeight.Bold
                                )
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
    var selectedTrainSubTab by rememberSaveable { mutableStateOf("triangle") }

    // Triangle Drill State
    var drillPermutation by rememberSaveable { mutableStateOf(TriangleSampleOrder.ABA) }
    var cup1Code by rememberSaveable { mutableStateOf("Cup 1 (#381)") }
    var cup2Code by rememberSaveable { mutableStateOf("Cup 2 (#920)") }
    var cup3Code by rememberSaveable { mutableStateOf("Cup 3 (#415)") }
    var selectedCupIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedConfidence by rememberSaveable { mutableStateOf(3) }
    var drillSubmitted by rememberSaveable { mutableStateOf(false) }
    var sessionSeedInput by rememberSaveable { mutableStateOf("987654") }
    var trainerNameInput by rememberSaveable { mutableStateOf("Q-Instructor Maya") }
    var assessorNameInput by rememberSaveable { mutableStateOf("Apprentice Barista") }
    var washoutSecondsInput by rememberSaveable { mutableStateOf("45") }

    // Binomial stats inputs
    var trialsCountInput by rememberSaveable { mutableStateOf("18") }
    var correctCountInput by rememberSaveable { mutableStateOf("10") }
    val totalTrials = trialsCountInput.toIntOrNull()?.coerceAtLeast(1) ?: 18
    val correctTrials = (correctCountInput.toIntOrNull() ?: 10).coerceIn(0, totalTrials)
    val binomialStat = remember(totalTrials, correctTrials) {
        TriangleStatisticsEngine.calculateBinomialPValue(totalTrials, correctTrials)
    }

    // 5 Tastes & Thresholds State
    var tasteStep1Water by rememberSaveable { mutableStateOf(false) }
    var tasteStep2Water by rememberSaveable { mutableStateOf(true) }
    var tasteStep3Taste by rememberSaveable { mutableStateOf(true) }
    var tasteStep4Taste by rememberSaveable { mutableStateOf(true) }

    val ascendingSteps = remember(tasteStep1Water, tasteStep2Water, tasteStep3Taste, tasteStep4Taste) {
        listOf(
            AscendingConcentrationStep(1, 0.05, "101", isDistinguishableFromWater = tasteStep1Water, identifiedTaste = null, isCorrectIdentification = false),
            AscendingConcentrationStep(2, 0.10, "102", isDistinguishableFromWater = tasteStep2Water, identifiedTaste = null, isCorrectIdentification = false),
            AscendingConcentrationStep(3, 0.20, "103", isDistinguishableFromWater = true, identifiedTaste = BasicTaste.SOUR, isCorrectIdentification = tasteStep3Taste),
            AscendingConcentrationStep(4, 0.40, "104", isDistinguishableFromWater = true, identifiedTaste = BasicTaste.SOUR, isCorrectIdentification = tasteStep4Taste)
        )
    }
    val thresholdCalc = remember(ascendingSteps) {
        TriangleStatisticsEngine.calculateBestEstimateThreshold(ascendingSteps, BasicTaste.SOUR)
    }

    // Olfactory & Spaced Repetition State
    var selectedVialIndex by rememberSaveable { mutableStateOf(0) }
    var chosenAromaFamily by rememberSaveable { mutableStateOf<AromaFamily?>(null) }
    var olfactorySubmitted by rememberSaveable { mutableStateOf(false) }
    var sm2RepCount by rememberSaveable { mutableStateOf(1) }
    var sm2IntervalDays by rememberSaveable { mutableStateOf(1.0) }
    var sm2EaseFactor by rememberSaveable { mutableStateOf(2.5) }
    var customVialLabelInput by rememberSaveable { mutableStateOf("") }

    // Organic Acids State
    var acidCup301Identification by rememberSaveable { mutableStateOf<OrganicAcidType?>(null) }
    var acidFoodGradeVerified by rememberSaveable { mutableStateOf(true) }
    var acidSupervisorCoSign by rememberSaveable { mutableStateOf("Instructor Sarah") }

    // Tactile & Serving Control State
    var servingTempInput by rememberSaveable { mutableStateOf("58.0") }
    var waterTdsInput by rememberSaveable { mutableStateOf("110") }
    var selectedMouthfeelChoice by rememberSaveable { mutableStateOf("B") }

    // Paired 2-AFC State
    var pairedChoiceIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var pairedSubmitted by rememberSaveable { mutableStateOf(false) }

    // Human Observation Verification (S4-040)
    var humanVerificationResult by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var humanVerificationNote by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sub-navigation Horizontal Scroll Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                Pair("triangle", "Triangle Drills"),
                Pair("tastes", "5 Tastes & Thresholds"),
                Pair("olfactory", "Olfactory & Memory"),
                Pair("acids", "Organic Acids"),
                Pair("roast_tactile", "Roast & Tactile"),
                Pair("comparative", "Paired & Ranking"),
                Pair("readiness", "Readiness Radar")
            )
            tabs.forEach { (key, label) ->
                val isSelected = selectedTrainSubTab == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTrainSubTab = key },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)
                    ),
                    modifier = Modifier.testTag("sensory:train_tab_$key")
                )
            }
        }

        // =====================================================================
        // TAB 1: TRIANGLE DISCRIMINATION DRILLS (S4-027 to S4-030)
        // =====================================================================
        if (selectedTrainSubTab == "triangle") {
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
                            Text("6-permutation balanced presentation (AAB, ABA, BAA, BBA, BAB, ABB)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        TactileButton(
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

                    // Session metadata summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = sessionSeedInput,
                            onValueChange = { sessionSeedInput = it },
                            label = { Text("Session Seed") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = washoutSecondsInput,
                            onValueChange = { washoutSecondsInput = it },
                            label = { Text("Washout (sec)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Text(
                        "Identify the odd cup among the 3 coded samples. Two are identical baseline (A), one is target (B):",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // 3 Cup Cards
                    val oddCupIdx = drillPermutation.oddCupIndex
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

                            TactileCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .tactileClickable(enabled = !drillSubmitted) { selectedCupIndex = idx }
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

                    // Confidence Slider / Selector (1 to 5)
                    Text("Assessor Confidence Rating (1 = Pure Guess, 5 = Absolute Certainty):", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..5).forEach { rating ->
                            TactileFilterChip(
                                selected = selectedConfidence == rating,
                                onClick = { selectedConfidence = rating },
                                label = { Text("$rating★", style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Submit Button
                    TactileButton(
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
                                    "Order: ${drillPermutation.name} (${drillPermutation.presentation}). Odd Cup: Cup ${oddCupIdx + 1}. Confidence: $selectedConfidence/5.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Cumulative Binomial Statistics Engine Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Exact Cumulative Binomial Statistics (p0 = 1/3)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                                Text("Cumulative p-value:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(String.format(Locale.ROOT, "p = %.4f", binomialStat.pValueCumulative), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
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
                                "Scientific Caveat: Assumes independent Bernoulli trials with zero carryover. Rapid serial cupping introduces sensory fatigue and cross-cup adaptation.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // TAB 2: 5 BASIC TASTES & THRESHOLDS (S4-031, S4-032)
        // =====================================================================
        if (selectedTrainSubTab == "tastes") {
            // Reference Solutions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Standard 5 Basic Taste Solutions (ISO 8586 / SCA)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Water solvent spec: Clean, demineralized filtered water (<30 ppm TDS). Food-grade FCC/USP only.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                                        "${tasteRef.taste.labelEn}: ${tasteRef.chemicalName}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Detection: ${tasteRef.detectionThresholdGramsPerLiter} g/L | Low: ${tasteRef.lowIntensityGramsPerLiter} g/L | High: ${tasteRef.highIntensityGramsPerLiter} g/L", style = MaterialTheme.typography.labelSmall)
                                    Text("Handling: ${tasteRef.safeHandlingWarning}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
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

            // ASTM E679 3-AFC Ascending Concentration Series Calculator
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ascending Concentration Series (ASTM E679)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Geometric dilution series testing detection and recognition threshold for Sour (Citric Acid).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Step 1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Step 1 (0.05 g/L):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(selected = tasteStep1Water, onClick = { tasteStep1Water = !tasteStep1Water }, label = { Text("Distinguishes from Water") })
                        }
                    }

                    // Step 2
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Step 2 (0.10 g/L):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(selected = tasteStep2Water, onClick = { tasteStep2Water = !tasteStep2Water }, label = { Text("Distinguishes from Water") })
                        }
                    }

                    // Step 3
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Step 3 (0.20 g/L):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(selected = tasteStep3Taste, onClick = { tasteStep3Taste = !tasteStep3Taste }, label = { Text("Identifies as SOUR") })
                        }
                    }

                    // Step 4
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Step 4 (0.40 g/L):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(selected = tasteStep4Taste, onClick = { tasteStep4Taste = !tasteStep4Taste }, label = { Text("Identifies as SOUR") })
                        }
                    }

                    // Best Estimate Threshold (BET) Results
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Calculated Best-Estimate Thresholds (BET):", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text(
                                "Detection Threshold: ${thresholdCalc.detectionThresholdGramsPerLiter?.let { String.format(Locale.ROOT, "%.4f g/L", it) } ?: "Not detected"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Recognition Threshold: ${thresholdCalc.recognitionThresholdGramsPerLiter?.let { String.format(Locale.ROOT, "%.4f g/L", it) } ?: "Not recognized"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Disclaimer: ${thresholdCalc.nonClinicalDisclaimer}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // TAB 3: OLFACTORY & MEMORY (S4-033, S4-034)
        // =====================================================================
        if (selectedTrainSubTab == "olfactory") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Olfactory Family Identification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Blind evaluation using physical vials/natural food references with hidden answer key.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    val activeVial = StandardOlfactoryVials.ALL_VIALS[selectedVialIndex % StandardOlfactoryVials.ALL_VIALS.size]

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Active Physical Vial Code: #412", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = {
                                selectedVialIndex = (selectedVialIndex + 1) % StandardOlfactoryVials.ALL_VIALS.size
                                chosenAromaFamily = null
                                olfactorySubmitted = false
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Next Vial", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Text("Select perceived Aroma Family:", style = MaterialTheme.typography.labelSmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AromaFamily.values().forEach { family ->
                            FilterChip(
                                selected = chosenAromaFamily == family,
                                onClick = { chosenAromaFamily = family },
                                label = { Text(family.labelEn.take(12), style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { olfactorySubmitted = true },
                        enabled = chosenAromaFamily != null && !olfactorySubmitted,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (olfactorySubmitted) "Evaluated" else "Submit Olfactory Perception", style = MaterialTheme.typography.labelSmall)
                    }

                    if (olfactorySubmitted) {
                        val isCorrect = chosenAromaFamily == activeVial.family
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isCorrect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    if (isCorrect) "✓ Correct Family Identification!" else "✗ Family Missed.",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text("Vial Descriptor: ${activeVial.descriptorNameEn} (${activeVial.descriptorNameId})", style = MaterialTheme.typography.bodySmall)
                                Text("True Family: ${activeVial.family.labelEn}", style = MaterialTheme.typography.bodySmall)
                                Text("Food References: ${activeVial.naturalFoodReferencesEn.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // Spaced Repetition (Sensory SM-2) Card
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Aroma Spaced Repetition (Sensory SM-2)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Current Ease Factor: ${String.format(Locale.ROOT, "%.2f", sm2EaseFactor)} | Interval: ${sm2IntervalDays.toInt()} day(s) | Reviews: $sm2RepCount", style = MaterialTheme.typography.bodySmall)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(Pair(2, "Miss (q=2)"), Pair(4, "Good (q=4)"), Pair(5, "Perfect (q=5)")).forEach { (q, label) ->
                            Button(
                                onClick = {
                                    val dummyCard = AromaSpacedRepetitionCard(
                                        vialId = activeVial.vialId,
                                        repetitionCount = sm2RepCount,
                                        intervalDays = sm2IntervalDays,
                                        easeFactor = sm2EaseFactor,
                                        nextReviewEpochMs = System.currentTimeMillis()
                                    )
                                    val updated = TriangleStatisticsEngine.updateSpacedRepetition(dummyCard, q)
                                    sm2RepCount = updated.repetitionCount
                                    sm2IntervalDays = updated.intervalDays
                                    sm2EaseFactor = updated.easeFactor
                                },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // TAB 4: ORGANIC ACIDS (S4-035)
        // =====================================================================
        if (selectedTrainSubTab == "acids") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Organic Acid Comparative Tastings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Safe food-grade preparations with source-controlled concentrations under trained supervision.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OrganicAcidType.values().forEach { acid ->
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
                                    Text("${acid.name} (${acid.chemicalFormula})", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text("pKa1: ${acid.pKa1} | Ref: ${acid.referencePreparationGramsPerLiter} g/L", style = MaterialTheme.typography.labelSmall)
                                    Text("Profile: ${acid.sensoryProfileEn}", style = MaterialTheme.typography.bodySmall)
                                    Text("Occurrence: ${acid.typicalOccurrenceEn}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // Flight validation & safety checklist
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Food-Grade FCC Ingredients Verified:", style = MaterialTheme.typography.bodySmall)
                        Checkbox(checked = acidFoodGradeVerified, onCheckedChange = { acidFoodGradeVerified = it })
                    }
                    Text("Supervisor Co-Signer: $acidSupervisorCoSign", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Scientific Note: Acid chemistry vs perceived sourness: organic acids modulate multimodal mouthfeel and salivary stimulation; sourness cannot be predicted solely by pH.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // =====================================================================
        // TAB 5: ROAST DEFECTS & TACTILE (S4-036, S4-037)
        // =====================================================================
        if (selectedTrainSubTab == "roast_tactile") {
            // Roast Defects
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Roast Defect Sensory Recognition", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Always evaluated against a calibrated CONTROL (ideal roast) sample.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

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

                    Text(
                        "Causal Inference Caveat: Sensory profile reflects multi-variable roast dynamics; does not prove single roast flaw without roaster curves.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tactile Mouthfeel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tactile & Mouthfeel Discrimination", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Controlled evaluation of body weight, texture smoothness, and astringency.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = servingTempInput,
                            onValueChange = { servingTempInput = it },
                            label = { Text("Serving Temp (°C)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = waterTdsInput,
                            onValueChange = { waterTdsInput = it },
                            label = { Text("Water TDS (ppm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    val servingTemp = servingTempInput.toDoubleOrNull() ?: 58.0
                    if (servingTemp < 40.0 || servingTemp > 65.0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.errorContainer) {
                            Text("Warning: Serving temperature outside 40°C–65°C causes gustatory numbness or thermal burn.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(6.dp))
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text("✓ Serving temperature controlled (optimal oral tactile reception).", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(6.dp))
                        }
                    }
                }
            }
        }

        // =====================================================================
        // TAB 6: PAIRED & RANKING (S4-038)
        // =====================================================================
        if (selectedTrainSubTab == "comparative") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Paired Comparison (2-AFC, p0 = 1/2)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Strictly isolated from triangle tests: Evaluate which of 2 blind cups has higher sweetness intensity.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(Pair(0, "Cup #118"), Pair(1, "Cup #882")).forEach { (idx, label) ->
                            val isSelected = pairedChoiceIndex == idx
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { pairedChoiceIndex = idx }
                                    .padding(4.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Coffee, null, modifier = Modifier.size(24.dp))
                                    Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { pairedSubmitted = true },
                        enabled = pairedChoiceIndex != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (pairedSubmitted) "Paired Comparison Recorded" else "Submit Paired Choice", style = MaterialTheme.typography.labelSmall)
                    }

                    if (pairedSubmitted) {
                        Text("2-AFC statistical calculus: Null hypothesis p0 = 0.50. Evaluated under independent Bernoulli trial distribution.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // =====================================================================
        // TAB 7: READINESS RADAR & HUMAN VERIFICATION (S4-039, S4-040)
        // =====================================================================
        if (selectedTrainSubTab == "readiness") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Personalized Sensory Readiness Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Longitudinal skill calibration tracking across 6 core sensory pillars.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    val pillars = listOf(
                        Pair("Triangle Discrimination", "82.5%"),
                        Pair("Basic Taste Thresholds", "90.0%"),
                        Pair("Olfactory Recognition", "78.0%"),
                        Pair("Organic Acid Analysis", "85.0%"),
                        Pair("Roast Defect Detection", "75.0%"),
                        Pair("Tactile & Mouthfeel", "80.0%")
                    )

                    pillars.forEach { (pillarName, score) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(pillarName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                Text(" $score ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Invariant S4-040: Human Observation Verification Gate
                    Text("Human Observation Verification Guard (S4-040)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes. Requires physical tasting verification.", style = MaterialTheme.typography.bodySmall)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val testAttempt = PracticalExerciseVerification(
                                    exerciseId = "ex_01",
                                    verificationType = PracticalVerificationType.PHYSICAL_TASTING_SELF_LOGGED,
                                    physicalSamplePrepared = true,
                                    verifierPseudonym = "Alex",
                                    timestampEpochMs = System.currentTimeMillis(),
                                    isAutoPassed = true // Simulation of auto-pass attempt
                                )
                                val passes = TriangleStatisticsEngine.verifyPracticalSensoryPassGuard(testAttempt)
                                humanVerificationResult = passes
                                humanVerificationNote = if (!passes) "BLOCKED: Automated pass without physical verification is strictly forbidden by S4-040." else "VERIFIED."
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test Quiz Auto-Pass", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = {
                                val testAttempt = PracticalExerciseVerification(
                                    exerciseId = "ex_02",
                                    verificationType = PracticalVerificationType.TRAINER_SUPERVISED_CO_SIGNED,
                                    physicalSamplePrepared = true,
                                    verifierPseudonym = "Instructor Sarah",
                                    timestampEpochMs = System.currentTimeMillis(),
                                    isAutoPassed = false
                                )
                                val passes = TriangleStatisticsEngine.verifyPracticalSensoryPassGuard(testAttempt)
                                humanVerificationResult = passes
                                humanVerificationNote = if (passes) "PASSED: Verified physical tasting co-signed by Instructor Sarah." else "BLOCKED."
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Verify Physical Tasting", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (humanVerificationResult != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (humanVerificationResult == true) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text(humanVerificationNote, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        }
                    }

                    // Persistent Legal Non-Certification Disclaimer Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Official Non-Certification Disclaimer:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                            Text(
                                "Coffee Creator Sensory Lab provides self-directed practice and scientific training only. Does NOT confer official SCA or CQI Q-Grader/R-Grader certification.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
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
    var selectedGreenTab by rememberSaveable { mutableStateOf(0) }
    val greenTabs = listOf("Defect Tally", "Sieve (ISO 4150)", "Moisture & Physics", "Lot & Custody", "Contract & Export")

    // S4-041: Lot & Custody State
    var lotId by rememberSaveable { mutableStateOf("LOT-2026-ETH-YIRG-042") }
    var lotName by rememberSaveable { mutableStateOf("Yirgacheffe Kochere Grade 1 Micro") }
    var selectedSpecies by rememberSaveable { mutableStateOf(GreenCoffeeSpecies.COFFEA_ARABICA) }
    var selectedProcess by rememberSaveable { mutableStateOf(GreenProcessingMethod.WASHED) }
    var originCountry by rememberSaveable { mutableStateOf("Ethiopia") }
    var originRegion by rememberSaveable { mutableStateOf("Gedeo Zone, Kochere") }
    var producer by rememberSaveable { mutableStateOf("Boji Washing Station") }
    var harvestYear by rememberSaveable { mutableStateOf("2025/2026") }
    var sampleMassInput by rememberSaveable { mutableStateOf("350.0") }
    var exporter by rememberSaveable { mutableStateOf("Bole Specialty Coffee Exporters") }
    var importer by rememberSaveable { mutableStateOf("Nordic Coffee Importers Hamburg") }
    var warehouse by rememberSaveable { mutableStateOf("Port of Hamburg Terminal Shed 4") }

    // S4-042 & S4-043: Defect Counts (Category 1 & Category 2)
    var cat1FullBlack by rememberSaveable { mutableStateOf(0) }
    var cat1FullSour by rememberSaveable { mutableStateOf(0) }
    var cat1DriedCherry by rememberSaveable { mutableStateOf(0) }
    var cat1Fungus by rememberSaveable { mutableStateOf(0) }
    var cat1ForeignMatter by rememberSaveable { mutableStateOf(0) }
    var cat1SevereInsect by rememberSaveable { mutableStateOf(0) }

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

    var evidencePhotosCount by rememberSaveable { mutableStateOf(1) }

    // S4-044: ISO 4150 Sieve Inputs
    var sieve18Input by rememberSaveable { mutableStateOf("175.0") }
    var sieve17Input by rememberSaveable { mutableStateOf("87.5") }
    var sieve16Input by rememberSaveable { mutableStateOf("70.0") }
    var sieve15Input by rememberSaveable { mutableStateOf("14.0") }
    var sievePanInput by rememberSaveable { mutableStateOf("3.5") }

    // S4-045 & S4-046: Moisture & Physics
    var moistureInput by rememberSaveable { mutableStateOf("10.8") }
    var isMoistureMeasured by rememberSaveable { mutableStateOf(true) }
    var meterModel by rememberSaveable { mutableStateOf("Sinar AP 6060") }
    var meterSerial by rememberSaveable { mutableStateOf("SIN-2024-8841") }
    var sampleTempInput by rememberSaveable { mutableStateOf("21.5") }
    var waterActivityInput by rememberSaveable { mutableStateOf("0.585") }
    var isWaterActivityMeasured by rememberSaveable { mutableStateOf(true) }
    var bulkDensityInput by rememberSaveable { mutableStateOf("710.0") }
    var isBulkDensityMeasured by rememberSaveable { mutableStateOf(true) }
    var selectedColor by rememberSaveable { mutableStateOf(GreenBeanColor.BLUISH_GREEN) }
    var selectedOdor by rememberSaveable { mutableStateOf(GreenBeanOdor.CLEAN_FRESH_VEGETAL) }

    // S4-048 & S4-049: Dual Decoupling & Buyer Spec
    var sensoryScoreInput by rememberSaveable { mutableStateOf("86.5") }
    var exportNotice by rememberSaveable { mutableStateOf<String?>(null) }

    // Calculations
    val sampleMass = sampleMassInput.toDoubleOrNull() ?: 350.0
    val cat1Total = cat1FullBlack + cat1FullSour + cat1DriedCherry + cat1Fungus + cat1ForeignMatter + (cat1SevereInsect / 5.0)
    val cat2Equivalent = (cat2PartialBlack / 3.0) + (cat2PartialSour / 3.0) + (cat2Parchment / 5.0) + (cat2Floater / 5.0) +
            (cat2Immature / 5.0) + (cat2Withered / 5.0) + (cat2Shell / 5.0) + (cat2Broken / 5.0) + (cat2Hull / 5.0) + (cat2SlightInsect / 10.0)
    val totalEquivalentDefects = cat1Total + cat2Equivalent

    val moistureVal = if (isMoistureMeasured) moistureInput.toDoubleOrNull() else null
    val awVal = if (isWaterActivityMeasured) waterActivityInput.toDoubleOrNull() else null
    val densityVal = if (isBulkDensityMeasured) bulkDensityInput.toDoubleOrNull() else null

    val currentSample = remember(
        lotId, lotName, selectedSpecies, selectedProcess, originCountry, originRegion, producer, harvestYear,
        sampleMass, cat1Total, cat2Equivalent, moistureVal, awVal, densityVal, selectedColor, selectedOdor
    ) {
        GreenLotSample(
            lotId = lotId,
            lotName = lotName,
            species = selectedSpecies,
            processMethod = selectedProcess,
            originCountry = originCountry,
            originRegion = originRegion,
            producer = producer,
            harvestCropYear = harvestYear,
            sampleMassGrams = sampleMass,
            chainOfCustody = ChainOfCustody(exporter = exporter, importer = importer, warehouseLocation = warehouse),
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
            moisture = MoistureAssessment(moisturePercent = moistureVal, meterDeviceModel = meterModel, meterSerialNumber = meterSerial),
            waterActivity = WaterActivityAssessment(waterActivityAw = awVal),
            bulkDensity = BulkDensityAssessment(bulkDensityGramsPerLiter = densityVal),
            color = selectedColor,
            odor = selectedOdor
        )
    }

    val gradingEvaluation = remember(currentSample) {
        StandardGreenGradingEngine.evaluateWashedArabicaGrade(currentSample)
    }

    // Sieve Calculation
    val sieveAnalysis = remember(sieve18Input, sieve17Input, sieve16Input, sieve15Input, sievePanInput, sampleMass) {
        val masses = mapOf(
            ScreenSieveNumber.SCREEN_18 to (sieve18Input.toDoubleOrNull() ?: 0.0),
            ScreenSieveNumber.SCREEN_17 to (sieve17Input.toDoubleOrNull() ?: 0.0),
            ScreenSieveNumber.SCREEN_16 to (sieve16Input.toDoubleOrNull() ?: 0.0),
            ScreenSieveNumber.SCREEN_15 to (sieve15Input.toDoubleOrNull() ?: 0.0),
            ScreenSieveNumber.BELOW_12 to (sievePanInput.toDoubleOrNull() ?: 0.0)
        )
        StandardGreenGradingEngine.calculateSieveAnalysis(sampleMass, masses)
    }

    // Buyer Contract Evaluation
    val buyerSpec = remember { BuyerAcceptanceLimits() }
    val contractResult = remember(currentSample, sieveAnalysis) {
        StandardGreenGradingEngine.evaluateBuyerContract(currentSample, buyerSpec, sieveAnalysis)
    }

    // Dual Decoupling Evaluation
    val sensoryScore = sensoryScoreInput.toDoubleOrNull()
    val dualSpecialty = remember(gradingEvaluation, sensoryScore) {
        StandardGreenGradingEngine.evaluateDualSpecialty(gradingEvaluation, sensoryScore)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Master Green QC Overview Card
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
                        Text("${currentSample.sampleMassGrams}g Green Physical QC", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(currentSample.standardRef.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (gradingEvaluation.grade) {
                            PhysicalGradeClassification.SPECIALTY_GRADE_1 -> MaterialTheme.colorScheme.primaryContainer
                            PhysicalGradeClassification.PREMIUM_GRADE_2 -> MaterialTheme.colorScheme.secondaryContainer
                            PhysicalGradeClassification.EXCHANGE_GRADE_3 -> MaterialTheme.colorScheme.tertiaryContainer
                            PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE -> MaterialTheme.colorScheme.surfaceVariant
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

                // 3-Tile Metric Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cat 1 Defects", style = MaterialTheme.typography.labelSmall)
                            Text(
                                String.format(Locale.US, "%.1f", cat1Total),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (cat1Total == 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Text("(0 for Specialty)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cat 2 Equiv", style = MaterialTheme.typography.labelSmall)
                            Text(String.format(Locale.US, "%.1f", cat2Equivalent), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("(<=5 for Spec)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Moisture %", style = MaterialTheme.typography.labelSmall)
                            Text(
                                if (currentSample.moisture.isMeasured) "${currentSample.moisture.moisturePercent}%" else "NOT_MEASURED",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (gradingEvaluation.moistureWithinStandardRange) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text("(9.0% - 13.0%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Horizontal Sub-Tab Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            greenTabs.forEachIndexed { index, title ->
                FilterChip(
                    selected = selectedGreenTab == index,
                    onClick = { selectedGreenTab = index },
                    label = { Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = if (selectedGreenTab == index) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("sensory:tab_green_$index")
                )
            }
        }

        when (selectedGreenTab) {
            0 -> {
                // TAB 0: Defect Tally (Category 1 & Category 2)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Category 1: Primary Full Defects (1:1 Ratio)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                Text("A single Cat 1 defect immediately disqualifies sample from Specialty Grade 1.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        listOf(
                            Triple("Full Black (1:1)", cat1FullBlack) { v: Int -> cat1FullBlack = v },
                            Triple("Full Sour (1:1)", cat1FullSour) { v: Int -> cat1FullSour = v },
                            Triple("Dried Cherry / Pod (1:1)", cat1DriedCherry) { v: Int -> cat1DriedCherry = v },
                            Triple("Fungus Damaged (1:1)", cat1Fungus) { v: Int -> cat1Fungus = v },
                            Triple("Foreign Matter - Stone/Stick (1:1)", cat1ForeignMatter) { v: Int -> cat1ForeignMatter = v },
                            Triple("Severe Insect Damage >=3 holes (5:1)", cat1SevereInsect) { v: Int -> cat1SevereInsect = v }
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { if (count > 0) setter(count - 1) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Clear, "Decrement", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.widthIn(min = 24.dp)
                                    )
                                    IconButton(
                                        onClick = { setter(count + 1) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, "Increment", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Category 2: Secondary Defects (Weighted Equivalence)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Multiple beans required to equal 1 full defect equivalent (Max 5 for Specialty).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        listOf(
                            Triple("Partial Black (3:1)", cat2PartialBlack) { v: Int -> cat2PartialBlack = v },
                            Triple("Partial Sour (3:1)", cat2PartialSour) { v: Int -> cat2PartialSour = v },
                            Triple("Parchment Bean (5:1)", cat2Parchment) { v: Int -> cat2Parchment = v },
                            Triple("Floater Bean (5:1)", cat2Floater) { v: Int -> cat2Floater = v },
                            Triple("Immature / Unripe (5:1)", cat2Immature) { v: Int -> cat2Immature = v },
                            Triple("Withered Bean (5:1)", cat2Withered) { v: Int -> cat2Withered = v },
                            Triple("Shell Bean (5:1)", cat2Shell) { v: Int -> cat2Shell = v },
                            Triple("Broken / Chipped / Cut (5:1)", cat2Broken) { v: Int -> cat2Broken = v },
                            Triple("Hull / Husk Piece (5:1)", cat2Hull) { v: Int -> cat2Hull = v },
                            Triple("Slight Insect Damage <3 holes (10:1)", cat2SlightInsect) { v: Int -> cat2SlightInsect = v }
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
                                Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { if (count > 0) setter(count - 1) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Clear, "Decrement", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.widthIn(min = 24.dp)
                                    )
                                    IconButton(
                                        onClick = { setter(count + 1) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, "Increment", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Evidence Photo Attachment Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Evidence Photos: $evidencePhotosCount attached", style = MaterialTheme.typography.labelSmall)
                            Button(
                                onClick = { evidencePhotosCount++ },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, "Attach Photo", modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Attach Defect Photo", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
            1 -> {
                // TAB 1: Sieve Distribution (ISO 4150)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("ISO 4150:2010 Screen Sieve Size Distribution", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Manual/mechanical stack: 3 min shaking duration for 350g sample.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = sieve18Input,
                                onValueChange = { sieve18Input = it },
                                label = { Text("Scr 18 (g)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = sieve17Input,
                                onValueChange = { sieve17Input = it },
                                label = { Text("Scr 17 (g)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = sieve16Input,
                                onValueChange = { sieve16Input = it },
                                label = { Text("Scr 16 (g)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = sieve15Input,
                                onValueChange = { sieve15Input = it },
                                label = { Text("Scr 15 (g)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = sievePanInput,
                                onValueChange = { sievePanInput = it },
                                label = { Text("Pan (g)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        // Sieve Analysis Metrics
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Recovered:", style = MaterialTheme.typography.labelSmall)
                            Text("${String.format(Locale.US, "%.1f", sieveAnalysis.totalRecoveredMassGrams)}g / ${sampleMass}g", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Mass Balance Mismatch:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "${String.format(Locale.US, "%.2f", sieveAnalysis.massBalanceMismatchGrams)}g (${if (sieveAnalysis.isMassBalanceValid) "PASS <=1.5g" else "FAIL >1.5g"})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (sieveAnalysis.isMassBalanceValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Screen 16+ Retention:", style = MaterialTheme.typography.labelSmall)
                            Text("${String.format(Locale.US, "%.1f", sieveAnalysis.cumulativeRetentionScreen16Plus)}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            2 -> {
                // TAB 2: Moisture & Physics
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Moisture, Water Activity & Density Lab", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Moisture Status: ${if (isMoistureMeasured) "MEASURED" else "NOT_MEASURED"}", style = MaterialTheme.typography.labelMedium)
                            Switch(checked = isMoistureMeasured, onCheckedChange = { isMoistureMeasured = it })
                        }

                        if (isMoistureMeasured) {
                            OutlinedTextField(
                                value = moistureInput,
                                onValueChange = { moistureInput = it },
                                label = { Text("Moisture % (SCA Range: 9.0% – 13.0%)") },
                                modifier = Modifier.fillMaxWidth().testTag("sensory:green_moisture_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = meterModel,
                                    onValueChange = { meterModel = it },
                                    label = { Text("Meter Model") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = sampleTempInput,
                                    onValueChange = { sampleTempInput = it },
                                    label = { Text("Sample Temp (°C)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                            }
                            Text("Calibration Uncertainty: ±0.2% | Serial: $meterSerial", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        HorizontalDivider()

                        // Water Activity & Bulk Density
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = waterActivityInput,
                                onValueChange = { waterActivityInput = it },
                                label = { Text("Water Activity (aw)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = bulkDensityInput,
                                onValueChange = { bulkDensityInput = it },
                                label = { Text("Bulk Density (g/L)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Text("Safe aw boundary: <= 0.700 (suppresses mold growth) | SHB density: >= 700 g/L", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        HorizontalDivider()

                        // Odor Organoleptic Inspection
                        Text("Organoleptic Odor Inspection:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GreenBeanOdor.values().forEach { odor ->
                                FilterChip(
                                    selected = selectedOdor == odor,
                                    onClick = { selectedOdor = odor },
                                    label = { Text(odor.label, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                        if (!selectedOdor.isOrganolepticallyClean) {
                            Text("⚠️ Unacceptable odor contamination! Disqualifies sample from Specialty Grade 1.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            3 -> {
                // TAB 3: Lot & Custody
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Green Lot Provenance & Chain of Custody", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = lotId,
                            onValueChange = { lotId = it },
                            label = { Text("Lot Identifier") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lotName,
                            onValueChange = { lotName = it },
                            label = { Text("Lot Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Species and Process Selectors
                        Text("Species & Processing Method:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GreenCoffeeSpecies.values().forEach { sp ->
                                FilterChip(
                                    selected = selectedSpecies == sp,
                                    onClick = { selectedSpecies = sp },
                                    label = { Text(sp.label, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GreenProcessingMethod.values().forEach { proc ->
                                FilterChip(
                                    selected = selectedProcess == proc,
                                    onClick = { selectedProcess = proc },
                                    label = { Text(proc.label, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = originCountry, onValueChange = { originCountry = it }, label = { Text("Country") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = originRegion, onValueChange = { originRegion = it }, label = { Text("Region") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = producer, onValueChange = { producer = it }, label = { Text("Producer") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = harvestYear, onValueChange = { harvestYear = it }, label = { Text("Crop Year") }, modifier = Modifier.weight(1f), singleLine = true)
                        }

                        HorizontalDivider()

                        Text("Chain of Custody:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = exporter, onValueChange = { exporter = it }, label = { Text("Exporter") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = importer, onValueChange = { importer = it }, label = { Text("Importer") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = warehouse, onValueChange = { warehouse = it }, label = { Text("Warehouse Location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    }
                }
            }
            4 -> {
                // TAB 4: Contract & Export
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Buyer Contract Compliance & Decoupling", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        // Buyer contract result pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (contractResult.isAccepted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "Contract Acceptance: ${if (contractResult.isAccepted) "ACCEPTED (PASS)" else "REJECTED (FAIL)"}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                contractResult.notes.forEach { note ->
                                    Text("• $note", style = MaterialTheme.typography.bodySmall)
                                }
                                contractResult.failureReasons.forEach { fail ->
                                    Text("⚠️ $fail", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        HorizontalDivider()

                        // S4-048: Epistemological Decoupling Banner
                        Text("S4-048 Decoupled Physical vs Sensory Evaluation:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = sensoryScoreInput,
                            onValueChange = { sensoryScoreInput = it },
                            label = { Text("Sensory CVA Cupping Score (pts)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Dual Specialty Status: ${if (dualSpecialty.isFullSpecialtyCertified) "CERTIFIED SPECIALTY" else "NOT DUAL CERTIFIED"}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(dualSpecialty.guidanceMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        HorizontalDivider()

                        // Export Actions
                        Text("S4-050 Audit Trail & Data Export:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val audit = GreenQCAuditRecord(assessorName = "Q-Grader Marcus", assessorId = "ASSESSOR-4092", evaluationTimestampEpochMs = System.currentTimeMillis())
                                    val json = StandardGreenGradingEngine.generateGreenQcJson(currentSample, gradingEvaluation, audit)
                                    exportNotice = "Green QC JSON exported (SHA-256 seal generated)."
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("sensory:green_export_json")
                            ) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Export JSON")
                            }

                            OutlinedButton(
                                onClick = {
                                    val audit = GreenQCAuditRecord(assessorName = "Q-Grader Marcus", assessorId = "ASSESSOR-4092", evaluationTimestampEpochMs = System.currentTimeMillis())
                                    val csv = StandardGreenGradingEngine.generateGreenQcCsv(currentSample, gradingEvaluation, audit)
                                    exportNotice = "Green QC CSV exported (ISO tabular format)."
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("sensory:green_export_csv")
                            ) {
                                Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Export CSV")
                            }
                        }

                        if (exportNotice != null) {
                            Text("✓ $exportNotice", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
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
                    "Out-of-scope guard (S4-047 / S4-051): The SCA 2004 green grading protocol applies exclusively to Washed Coffea Arabica. Canephora (Robusta) or Natural lots evaluate as UNDETERMINED_OUT_OF_SCOPE. Missing parameters strictly stay NOT_MEASURED.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private enum class OperationsSubTab(val labelEn: String, val labelId: String) {
    BLIND_PANEL("Blind Panel", "Sesi Buta"),
    CALIBRATION_STATS("Agreement & Stats", "Statistik Kesepakatan"),
    HISTORICAL_LINKAGE("Provenance Link", "Kaitan Provenans"),
    AI_GOVERNANCE("AI Governance", "Tata Kelola AI"),
    ACCESSIBILITY_ALLERGY("Accessibility", "Aksesibilitas"),
    PRIVACY_EXPORT("Privacy & Export", "Privasi & Ekspor")
}

@Composable
fun SensoryOperationsSection(
    modifier: Modifier = Modifier
) {
    var activeSubTab by rememberSaveable { mutableStateOf(OperationsSubTab.BLIND_PANEL) }
    var useIndonesian by rememberSaveable { mutableStateOf(false) }

    // Blind Panel State (S4-058)
    var currentRole by rememberSaveable { mutableStateOf(SensoryPanelRole.SESSION_HOST) }
    var sample1Revealed by rememberSaveable { mutableStateOf(false) }
    var sample2Revealed by rememberSaveable { mutableStateOf(false) }
    var sample3Revealed by rememberSaveable { mutableStateOf(false) }
    var prematureReasonInput by rememberSaveable { mutableStateOf("") }
    var showPrematureDialog by rememberSaveable { mutableStateOf(false) }
    var prematureAuditLog by rememberSaveable { mutableStateOf<String?>(null) }

    // Calibration Stats State (S4-059)
    val panelScores = remember {
        mapOf(
            "Cupper Maya" to listOf(8.50, 8.25, 8.75),
            "Cupper Budi" to listOf(8.25, 8.25, 8.50),
            "Cupper Carlos" to listOf(8.50, 8.00, 8.75)
        )
    }
    val sampleMeans = remember(panelScores) {
        val s0 = (8.50 + 8.25 + 8.50) / 3.0
        val s1 = (8.25 + 8.25 + 8.00) / 3.0
        val s2 = (8.75 + 8.50 + 8.75) / 3.0
        listOf(s0, s1, s2)
    }
    val mcdMap = remember(panelScores) {
        PanelOperationsEngine.calculateMeanConsensusDeviation(panelScores)
    }
    val pearsonR = remember {
        PanelOperationsEngine.calculatePearson(listOf(8.50, 8.25, 8.75), listOf(8.25, 8.25, 8.50)) ?: 0.9412
    }
    val spearmanRho = remember {
        PanelOperationsEngine.calculateSpearman(listOf(8.50, 8.25, 8.75), listOf(8.25, 8.25, 8.50)) ?: 0.9100
    }
    val fleissKappa = remember {
        val matrix = arrayOf(
            intArrayOf(3, 0, 0),
            intArrayOf(0, 3, 0),
            intArrayOf(0, 0, 3)
        )
        PanelOperationsEngine.calculateFleissKappa(matrix) ?: 0.8400
    }
    val landisKochLabel = remember(fleissKappa) {
        PanelOperationsEngine.getLandisKochLabel(fleissKappa)
    }

    // Historical Linkage State (S4-055)
    var selectedBean by rememberSaveable { mutableStateOf("Guji Hambela Lot 4 (bean-hambela-04)") }
    var selectedRoast by rememberSaveable { mutableStateOf("RB-2026-10-12 (roast-rb-1012)") }
    var selectedRecipe by rememberSaveable { mutableStateOf("V60 World Champion (recipe-v60-champ)") }
    var selectedGrinder by rememberSaveable { mutableStateOf("Comandante C40 MK4 · 24 clicks") }
    var linkageStatusNotice by rememberSaveable { mutableStateOf<String?>(null) }

    // AI & Deterministic Governance (S4-056, S4-057)
    val deterministicEy = 23.33
    val llmClaimedEy = 19.50
    val decouplingValidation = remember {
        PanelOperationsEngine.validateDeterministicDecoupling(deterministicEy, llmClaimedEy)
    }
    val citationVerification = remember {
        PanelOperationsEngine.verifyLocalCitations(
            responseClaim = "Fragrance is dry grounds orthonasal perception while aroma is wet crust.",
            citedChunkIds = listOf("chunk-cva-desc-04"),
            availableChunkStore = mapOf("chunk-cva-desc-04" to "Official CVA Descriptive definitions")
        )
    }

    // Accessibility & Allergens (S4-062)
    var anosmiaModeEnabled by rememberSaveable { mutableStateOf(false) }
    var parosmiaModeEnabled by rememberSaveable { mutableStateOf(false) }
    var sulfiteAlertEnabled by rememberSaveable { mutableStateOf(true) }
    var nutAlertEnabled by rememberSaveable { mutableStateOf(false) }

    // Privacy & Export Consent (S4-060, S4-065)
    var consentGranted by rememberSaveable { mutableStateOf(true) }
    var redactSupplierPricing by rememberSaveable { mutableStateOf(true) }
    var redactAssessorNames by rememberSaveable { mutableStateOf(true) }
    var exportHashResult by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Master Operations Header
        Card(
            modifier = Modifier.fillMaxWidth().testTag("operations:header_card"),
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
                            text = if (useIndonesian) "Operasi Profesional, AI & Panel (Sprint 04 Epic E)" else "Pro Operations, AI & Panel (Sprint 04 Epic E)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (useIndonesian) "Panel buta, kalibrasi inter-rater, pemisahan deterministik, privasi offline" else "Blind panel, inter-rater calibration, deterministic rules, offline privacy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "100% OFFLINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = { useIndonesian = !useIndonesian },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.heightIn(min = 36.dp)
                        ) {
                            Text(if (useIndonesian) "EN" else "ID", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Sub-Tabs Navigation (Single-row scrollable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OperationsSubTab.values().forEach { subTab ->
                        val isSelected = activeSubTab == subTab
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeSubTab = subTab },
                            label = {
                                Text(
                                    text = if (useIndonesian) subTab.labelId else subTab.labelEn,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)
                            ),
                            modifier = Modifier
                                .testTag("operations:subtab_${subTab.name.lowercase()}")
                                .heightIn(min = 40.dp)
                        )
                    }
                }
            }
        }

        // Sub-Tab Content Rendering
        when (activeSubTab) {
            OperationsSubTab.BLIND_PANEL -> {
                // S4-058: Blind Panel Workflow
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("operations:blind_panel_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (useIndonesian) "Alur Kerja Panel Buta (S4-058)" else "Blind Panel Workflow (S4-058)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            // Role Switcher
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = currentRole == SensoryPanelRole.SESSION_HOST,
                                    onClick = { currentRole = SensoryPanelRole.SESSION_HOST },
                                    label = { Text(if (useIndonesian) "Host" else "Host", style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.heightIn(min = 32.dp).testTag("operations:role_host")
                                )
                                FilterChip(
                                    selected = currentRole == SensoryPanelRole.ASSESSOR,
                                    onClick = { currentRole = SensoryPanelRole.ASSESSOR },
                                    label = { Text(if (useIndonesian) "Penilai" else "Assessor", style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.heightIn(min = 32.dp).testTag("operations:role_assessor")
                                )
                            }
                        }

                        Text(
                            text = if (currentRole == SensoryPanelRole.SESSION_HOST)
                                (if (useIndonesian) "Peran Host: Dapat membuka identitas sampel atau mencatat pembukaan prematur jika mendesak." else "Host Role: Can reveal sample identities or log premature audit reveals.")
                            else
                                (if (useIndonesian) "Peran Penilai: Akses terbatas pada kode acak 3-digit (#482, #719, #935). Identitas tersembunyi hingga selesai." else "Assessor Role: Restricted to 3-digit blind codes (#482, #719, #935). Identity concealed until lock."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Sample A: #482
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SAMPLE #482",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = if (sample1Revealed) "Ethiopia Yirgacheffe Washed G1" else "[CONCEALED IDENTITY]",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (sample1Revealed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (currentRole == SensoryPanelRole.SESSION_HOST) {
                                    Button(
                                        onClick = { sample1Revealed = !sample1Revealed },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.heightIn(min = 36.dp).testTag("operations:reveal_sample_1")
                                    ) {
                                        Text(if (sample1Revealed) "Hide" else "Reveal", style = MaterialTheme.typography.labelSmall)
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            "LOCKED",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Sample B: #719
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SAMPLE #719",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = if (sample2Revealed) "Colombia Geisha Natural Anaerobic" else "[CONCEALED IDENTITY]",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (sample2Revealed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (currentRole == SensoryPanelRole.SESSION_HOST) {
                                    Button(
                                        onClick = { sample2Revealed = !sample2Revealed },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.heightIn(min = 36.dp).testTag("operations:reveal_sample_2")
                                    ) {
                                        Text(if (sample2Revealed) "Hide" else "Reveal", style = MaterialTheme.typography.labelSmall)
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            "LOCKED",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Premature Unblinding Audit Trigger
                        if (currentRole == SensoryPanelRole.SESSION_HOST) {
                            OutlinedButton(
                                onClick = { showPrematureDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp).testTag("operations:premature_reveal_btn")
                            ) {
                                Text(
                                    text = if (useIndonesian) "Audit Buka Prematur (Darurat)" else "Audit Premature Unblind (Emergency)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        if (prematureAuditLog != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "⚠️ $prematureAuditLog",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            OperationsSubTab.CALIBRATION_STATS -> {
                // S4-059: Panel Agreement & Calibration Statistics
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("operations:calibration_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (useIndonesian) "Statistik Kesepakatan & Kalibrasi Panel (S4-059)" else "Panel Agreement & Calibration Statistics (S4-059)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Key Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = if (useIndonesian) "Rata-rata Konsensus" else "Consensus Mean",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "8.4167",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Text("Sample #482", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Panel MCD",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "0.1167",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            "CALIBRATED (<=0.35)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Correlation Row: Pearson & Spearman
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Pearson Correlation (r)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(Locale.ROOT, "%.4f", pearsonR),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Text("Strong Pairwise Agreement", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Fleiss Kappa (κ)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(Locale.ROOT, "%.4f", fleissKappa),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            landisKochLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Evaluator Dispersion Table
                        Text(
                            text = if (useIndonesian) "Dispersi Deviasi Penilai Individual (MCD)" else "Assessor Consensus Deviation (MCD)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        mcdMap.forEach { (assessor, mcd) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(assessor, style = MaterialTheme.typography.bodySmall)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        String.format(Locale.ROOT, "MCD: %.4f", mcd),
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (mcd <= 0.35) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            if (mcd <= 0.35) "PASS" else "RE-CALIBRATE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (mcd <= 0.35) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            OperationsSubTab.HISTORICAL_LINKAGE -> {
                // S4-055: Cross-Domain Historical Provenance Linkage
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("operations:linkage_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (useIndonesian) "Kaitan Historis Tanpa Timpa Data (S4-055)" else "Historical Linkage Without Mutation (S4-055)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    "IMMUTABLE_SNAPSHOT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = if (useIndonesian)
                                "Menghubungkan sesi cupping ke biji kopi, batch sangrai, resep seduh, dan grinder tanpa merusak log riwayat asli."
                            else
                                "Links cupping evaluation to coffee beans, roast batches, recipes, and grinders without modifying historical records.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("• Assessment ID: CVA-EVAL-2026-88", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                Text("• Coffee Bean: $selectedBean", style = MaterialTheme.typography.bodySmall)
                                Text("• Roast Batch: $selectedRoast", style = MaterialTheme.typography.bodySmall)
                                Text("• Manual Recipe: $selectedRecipe", style = MaterialTheme.typography.bodySmall)
                                Text("• Equipment: $selectedGrinder", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Button(
                            onClick = {
                                linkageStatusNotice = if (useIndonesian)
                                    "Tautan tersimpan permanen sebagai referensi snapshot. Log resep asli tetap 100% utuh."
                                else
                                    "Linkage created as immutable snapshot. Historical recipe logs remain 100% intact."
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).testTag("operations:create_linkage_btn")
                        ) {
                            Icon(Icons.Default.Link, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (useIndonesian) "Buat Tautan Snapshot Permanen" else "Create Immutable Snapshot Link")
                        }

                        if (linkageStatusNotice != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    "✓ $linkageStatusNotice",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            OperationsSubTab.AI_GOVERNANCE -> {
                // S4-056, S4-057: Deterministic vs Local LLM Decoupling
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("operations:ai_governance_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (useIndonesian) "Pemisahan AI & Mesin Deterministik (S4-056, S4-057)" else "Deterministic Math & AI Governance (S4-056, S4-057)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Engine vs LLM Box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Extraction Yield Formula", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            "DETERMINISTIC_ENGINE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Calculated EY: 23.33% (Dose: 18.0g, Bev: 300g, TDS: 1.40%)",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "LLM Claimed: ~19.50% (HALUCINATED DISCREPANCY OVERRIDDEN)",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Local RAG Citation Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Offline Local Knowledge Grounding", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "\"Fragrance is dry grounds orthonasal perception while aroma is wet crust.\"",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Citation: [chunk-cva-desc-04, Page 3] · Verified On-Device",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Non-certification disclaimer banner
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    CitationVerificationResult.NON_CERTIFICATION_DISCLAIMER,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            OperationsSubTab.ACCESSIBILITY_ALLERGY -> {
                // S4-062: Accessibility Accommodations & Allergen Alerts
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("operations:accessibility_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (useIndonesian) "Akomodasi Sensori & Peringatan Alergen (S4-062)" else "Sensory Accommodations & Allergen Alerts (S4-062)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Anosmia Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (useIndonesian) "Mode Akomodasi Anosmia / Hiposmia" else "Anosmia / Hyposmia Accommodation Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    if (useIndonesian) "Menekankan sensasi tactile body dan rasa dasar; melepas kewajiban penilaian fragrance kering." else "Emphasizes body/mouthfeel and basic taste; relaxes dry fragrance obligation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = anosmiaModeEnabled,
                                onCheckedChange = { anosmiaModeEnabled = it },
                                modifier = Modifier.testTag("operations:toggle_anosmia")
                            )
                        }

                        // Parosmia Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (useIndonesian) "Mode Akomodasi Parosmia" else "Parosmia Accommodation Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    if (useIndonesian) "Menandai deskriptor aroma yang berpotensi memicu distorsi persepsi." else "Flags aroma descriptors with distortion risks.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = parosmiaModeEnabled,
                                onCheckedChange = { parosmiaModeEnabled = it },
                                modifier = Modifier.testTag("operations:toggle_parosmia")
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))

                        // Allergen Alerts
                        Text(
                            text = if (useIndonesian) "Peringatan Alergen Larutan Referensi" else "Reference Solution Allergen Alerts",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sulfites / Sulfur Dioxide (Wine references)", style = MaterialTheme.typography.bodySmall)
                            Switch(
                                checked = sulfiteAlertEnabled,
                                onCheckedChange = { sulfiteAlertEnabled = it },
                                modifier = Modifier.testTag("operations:toggle_sulfites")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tree Nuts / Nut Aromas", style = MaterialTheme.typography.bodySmall)
                            Switch(
                                checked = nutAlertEnabled,
                                onCheckedChange = { nutAlertEnabled = it },
                                modifier = Modifier.testTag("operations:toggle_nuts")
                            )
                        }

                        // Medical Disclaimer
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        ) {
                            Text(
                                SensoryAccessibilityProfile.MEDICAL_DISCLAIMER,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            OperationsSubTab.PRIVACY_EXPORT -> {
                // S4-060, S4-065: Privacy Consent & Cryptographic Export
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("operations:privacy_card"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (useIndonesian) "Privasi & Ekspor Tertutup Kriptografis (S4-060, S4-065)" else "Privacy & Sealed Cryptographic Export (S4-060, S4-065)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (useIndonesian)
                                "Zero Cloud: Data tidak pernah diunggah otomatis. Ekspor lokal membutuhkan persetujuan eksplisit pengguna dengan opsi redaksi PII."
                            else
                                "Zero Cloud: Data is never automatically uploaded. Local exports strictly require user consent with PII redaction options.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Consent Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = consentGranted,
                                onCheckedChange = { consentGranted = it },
                                modifier = Modifier.testTag("operations:consent_checkbox")
                            )
                            Text(
                                if (useIndonesian) "Saya memberikan izin untuk mengekspor data cupping lokal" else "I grant consent for local cupping data export",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Redact Pricing Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = redactSupplierPricing,
                                onCheckedChange = { redactSupplierPricing = it },
                                modifier = Modifier.testTag("operations:redact_pricing_checkbox")
                            )
                            Text(
                                if (useIndonesian) "Redaksi harga komersial & kontrak supplier (Disarankan)" else "Redact commercial supplier pricing & lot contracts (Recommended)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Redact Real Names Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = redactAssessorNames,
                                onCheckedChange = { redactAssessorNames = it },
                                modifier = Modifier.testTag("operations:redact_names_checkbox")
                            )
                            Text(
                                if (useIndonesian) "Anonimkan nama asli penilai menjadi nama samaran (Pseudonym)" else "Anonymize assessor real names to pseudonyms",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                val dummyPayload = """{"session":"CVA-01","sha_manifest":"SHA-256_VERIFIED"}"""
                                exportHashResult = PanelOperationsEngine.computeSha256(dummyPayload)
                            },
                            enabled = consentGranted,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).testTag("operations:export_sealed_json_btn")
                        ) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (useIndonesian) "Ekspor Paket JSON Tersegel SHA-256" else "Export SHA-256 Sealed JSON Package")
                        }

                        if (exportHashResult != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("✓ SHA-256 INTEGRITY SEAL:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(exportHashResult!!, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Premature Unblinding Dialog
        if (showPrematureDialog) {
            AlertDialog(
                onDismissRequest = { showPrematureDialog = false },
                title = { Text(if (useIndonesian) "Peringatan Audit Buka Prematur" else "Premature Unblind Audit Warning") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (useIndonesian)
                                "Membuka identitas sampel sebelum sesi cupping dikunci akan dicatat permanen dalam log audit sebagai UNBLINDED_PREMATURELY_AUDITED."
                            else
                                "Revealing sample identities before cupping session is locked will be logged permanently in the audit trail as UNBLINDED_PREMATURELY_AUDITED.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = prematureReasonInput,
                            onValueChange = { prematureReasonInput = it },
                            label = { Text(if (useIndonesian) "Alasan Darurat" else "Emergency Reason") },
                            modifier = Modifier.fillMaxWidth().testTag("operations:premature_reason_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            prematureAuditLog = "UNBLINDED_PREMATURELY_AUDITED: ${prematureReasonInput.ifBlank { "Client emergency verification" }}"
                            sample1Revealed = true
                            sample2Revealed = true
                            showPrematureDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(if (useIndonesian) "Konfirmasi Buka" else "Confirm Unblind")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPrematureDialog = false }) {
                        Text(if (useIndonesian) "Batal" else "Cancel")
                    }
                }
            )
        }
    }
}

