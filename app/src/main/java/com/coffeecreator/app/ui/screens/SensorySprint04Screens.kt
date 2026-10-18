package com.coffeecreator.app.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.filesystem.SafTextDocumentWriter
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import com.coffeecreator.app.core.sensory.academy.AcademyCurriculumCategory
import com.coffeecreator.app.core.sensory.academy.AcademyLessonRegistry
import com.coffeecreator.app.core.sensory.academy.GenericAromaRegistry
import com.coffeecreator.app.core.sensory.academy.OrganicAcidAtlas
import com.coffeecreator.app.core.sensory.cva.AcidityType
import com.coffeecreator.app.core.sensory.cva.AssessmentPerspective
import com.coffeecreator.app.core.sensory.cva.BodyTextureType
import com.coffeecreator.app.core.sensory.cva.CuppingMilestone
import com.coffeecreator.app.core.sensory.cva.CuppingProtocol
import com.coffeecreator.app.core.sensory.cva.CuppingStepperEngine
import com.coffeecreator.app.core.sensory.greengrading.BulkDensityAssessment
import com.coffeecreator.app.core.sensory.greengrading.BuyerAcceptanceLimits
import com.coffeecreator.app.core.sensory.greengrading.ChainOfCustody
import com.coffeecreator.app.core.sensory.greengrading.DefectTallyRecord
import com.coffeecreator.app.core.sensory.greengrading.GreenBeanColor
import com.coffeecreator.app.core.sensory.greengrading.GreenBeanOdor
import com.coffeecreator.app.core.sensory.greengrading.GreenCoffeeSpecies
import com.coffeecreator.app.core.sensory.greengrading.GreenDefectType
import com.coffeecreator.app.core.sensory.greengrading.GreenLotSample
import com.coffeecreator.app.core.sensory.greengrading.GreenProcessingMethod
import com.coffeecreator.app.core.sensory.greengrading.GreenQCAuditRecord
import com.coffeecreator.app.core.sensory.greengrading.MoistureAssessment
import com.coffeecreator.app.core.sensory.greengrading.MoistureMeasurementMethod
import com.coffeecreator.app.core.sensory.greengrading.PhysicalGradeClassification
import com.coffeecreator.app.core.sensory.greengrading.ScreenSieveNumber
import com.coffeecreator.app.core.sensory.greengrading.StandardGreenGradingEngine
import com.coffeecreator.app.core.sensory.greengrading.WaterActivityAssessment
import com.coffeecreator.app.core.sensory.operations.CitationVerificationResult
import com.coffeecreator.app.core.sensory.operations.SensoryAccessibilityProfile
import com.coffeecreator.app.core.sensory.operations.SensoryPanelRole
import com.coffeecreator.app.core.sensory.training.AromaFamily
import com.coffeecreator.app.core.sensory.training.AromaSpacedRepetitionCard
import com.coffeecreator.app.core.sensory.training.AscendingConcentrationStep
import com.coffeecreator.app.core.sensory.training.BasicTaste
import com.coffeecreator.app.core.sensory.training.OrganicAcidType
import com.coffeecreator.app.core.sensory.training.StandardOlfactoryVials
import com.coffeecreator.app.core.sensory.training.StandardRoastDefects
import com.coffeecreator.app.core.sensory.training.StandardTasteReferences
import com.coffeecreator.app.core.sensory.training.TriangleSampleOrder
import com.coffeecreator.app.core.sensory.training.TriangleStatisticsEngine
import com.coffeecreator.app.ui.components.TactileButton
import com.coffeecreator.app.ui.components.TactileCard
import com.coffeecreator.app.ui.components.TactileFilterChip
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale
import kotlinx.coroutines.launch

private fun String.toTitleCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

@Composable
fun SensoryAcademySection(modifier: Modifier = Modifier) {
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
                lesson.glossary.any { it.termEn.contains(searchQuery, ignoreCase = true) || it.termId.contains(searchQuery,
                    ignoreCase = true) }
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
                            text = if (useIndonesian) "Kurikulum ilmiah botani, biologi gustatori, CVA & grading"
                                else "Scientific syllabus: botany, gustatory anatomy, CVA & grading",
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
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("sensory:btn_organic_acids"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showOrganicAcidAtlas) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = if (showOrganicAcidAtlas) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.Science, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text(
                            if (useIndonesian) "Atlas Asam Organik" else "Organic Acid Atlas",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = { showAromaTaxonomy = !showAromaTaxonomy },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("sensory:btn_aroma_taxonomy"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showAromaTaxonomy) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = if (showAromaTaxonomy) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.Spa, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text(
                            if (useIndonesian) "Taksonomi Aroma 60" else "Aroma Taxonomy 60",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
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
                        text = if (useIndonesian) "Atlas 7 Asam Organik Kopi & Standar Kalibrasi"
                            else "Coffee Organic Acid Atlas & Calibration Reference",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (useIndonesian) {
                            ("Koreksi ilmiah: Asam bukanlah rasa dasar ke-6. Asam (sourness) adalah rasa dasar; 'acidity' adalah " +
                                "persepsi multimodal kompleks.")
                        } else {
                            ("Scientific correction: Acid is not a 6th basic taste. Sourness is the basic taste; 'acidity' is a " +
                                "complex multimodal perception.")
                        },
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
                                Text(
                                    "Sensory: ${if (useIndonesian) acid.typicalSensoryCuesId else acid.typicalSensoryCuesEn}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "In Coffee: ${if (useIndonesian) acid.occurrenceInCoffeeId else acid.occurrenceInCoffeeEn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Prep Reference: " +
                                        (if (useIndonesian) {
                                            acid.safeReferencePreparationNotesId
                                        } else {
                                            acid.safeReferencePreparationNotesEn
                                        }),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    "Standard Test: ${acid.standardTestingConcentrationGramsPerLiter} g/L | " +
                                        "Daily Limit: ${acid.maxSafeDailyDosageThresholdGramsPerLiter} g/L",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
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
                        text = if (useIndonesian) "Taksonomi Aroma Independen (Kompatibel Le Nez du Café 60)"
                            else "Generic Aroma Taxonomy (Le Nez du Café 60 Compatible)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = if (useIndonesian) {
                            "Taksonomi aroma generic independen untuk latihan vial kit fisik milik pengguna tanpa konten kit berlisensi."
                        } else {
                            "Independent generic aroma taxonomy for user-owned physical vial kit practice without proprietary kit content."
                        },
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
                                    text = "Family: ${aroma.familyGroup} · ${aroma.chemicalCompoundHint} " +
                                        "(" +
                                        (if (useIndonesian) aroma.householdFoodReferenceId else aroma.householdFoodReferenceEn) +
                                        ")",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    aroma.userKitVialMappingId ?: aroma.id,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
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
                modifier = Modifier.testTag("sensory:academy_cat_all").heightIn(min = 48.dp)
            )
            AcademyCurriculumCategory.values().forEach { cat ->
                val count = allLessons.count { it.category == cat }
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                    label = { Text("${cat.name.replace('_', ' ').lowercase().toTitleCase()} ($count)") },
                    modifier = Modifier.testTag("sensory:academy_cat_${cat.name.lowercase()}").heightIn(min = 48.dp)
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
                    if (isExpanded) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.50f)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)
                    }
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) {
                        MaterialTheme.colorScheme.surfaceContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    }
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
                                                    "✓ " + (if (useIndonesian) "Benar! " else "Correct! ") +
                                                        (if (useIndonesian) q.explanationId else q.explanationEn)
                                                } else {
                                                    "✗ " + (if (useIndonesian) "Salah. " else "Incorrect. ") +
                                                        (if (useIndonesian) q.explanationId else q.explanationEn)
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
                                text = "Citations: " +
                                    lesson.citations.joinToString("; ") { "${it.title} (${it.authorOrOrganization}, ${it.year})" },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensoryCvaCuppingSection(modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    var protocol by rememberSaveable { mutableStateOf(CuppingProtocol.CUSTOM_RESEARCH) }
    var currentPerspective by rememberSaveable { mutableStateOf(AssessmentPerspective.DESCRIPTIVE) }
    var selectedSampleCode by rememberSaveable { mutableStateOf("") }
    val sampleCodes = remember { mutableStateListOf<String>() }
    var isBlindMode by rememberSaveable { mutableStateOf(true) }
    var showWheelPicker by rememberSaveable { mutableStateOf(false) }
    var wheelSearchQuery by rememberSaveable { mutableStateOf("") }
    var wheelCategoryFilter by rememberSaveable { mutableStateOf("All") }

    // Cupping Stepper & Timer State
    var timerSeconds by rememberSaveable { mutableStateOf(0) }
    var isTimerRunning by rememberSaveable { mutableStateOf(false) }
    var showStepperGuide by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            kotlinx.coroutines.delay(1000L)
            timerSeconds++
        }
    }

    // 5-Cup Multi-Cup Grid per sample (Cup 1..5: Clean, Taint -2, Fault -4)
    val cupStates = remember { mutableStateListOf(-1, -1, -1, -1, -1) } // -1 = not recorded
    val cupGridRecorded = cupStates.all { it >= 0 }
    val cleanCupCount = cupStates.count { it == 0 }
    val taintCupCount = cupStates.count { it == 1 }
    val faultCupCount = cupStates.count { it == 2 }
    val multiCupDefectPenalty = taintCupCount * 2f + faultCupCount * 4f
    val uniformityScore = if (cupGridRecorded) cleanCupCount * 2.0f else null
    val cleanCupScore = if (cupGridRecorded) cleanCupCount * 2.0f else null

    // 221-Descriptor Sensory Wheel Selection
    val selectedDescriptors = remember { mutableStateListOf<String>() }

    // Descriptive Scores (0.0 to 5.0)
    var dFragrance by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dAroma by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dFlavor by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dAftertaste by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dAcidity by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dSweetness by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dBitterness by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dBody by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var dFinish by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    val selectedAcidityTypes = remember { mutableStateListOf<AcidityType>() }
    var selectedBodyTexture by rememberSaveable { mutableStateOf<BodyTextureType?>(null) }

    // Affective Scores (6.0 to 10.0)
    var aFragrance by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var aFlavor by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var aAftertaste by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var aAcidity by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var aSweetness by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var aMouthfeel by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var aOverall by rememberSaveable { mutableFloatStateOf(Float.NaN) }

    val affectiveInputs = listOf(aFragrance, aFlavor, aAftertaste, aAcidity, aSweetness, aMouthfeel, aOverall)
    val totalAffective = if (
        protocol == CuppingProtocol.CUSTOM_RESEARCH && affectiveInputs.all { it.isFinite() } && cupGridRecorded
    ) {
        (affectiveInputs.sum() - multiCupDefectPenalty).coerceAtLeast(0f)
    } else {
        null
    }

    // Legacy 2004 100-Point Sheet
    var legFragrance by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var legFlavor by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var legAftertaste by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var legAcidity by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var legBody by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var legBalance by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    var legOverall by rememberSaveable { mutableFloatStateOf(Float.NaN) }
    val legUniformityBoxes = remember { mutableStateListOf<Boolean?>(null, null, null, null, null) }
    val legCleanCupBoxes = remember { mutableStateListOf<Boolean?>(null, null, null, null, null) }
    val legSweetnessBoxes = remember { mutableStateListOf<Boolean?>(null, null, null, null, null) }
    val legUniformity = legUniformityBoxes.count { it == true } * 2.0f
    val legCleanCup = legCleanCupBoxes.count { it == true } * 2.0f
    val legSweetness = legSweetnessBoxes.count { it == true } * 2.0f
    val legacyInputs = listOf(legFragrance, legFlavor, legAftertaste, legAcidity, legBody, legBalance, legOverall)
    val legacyCupEntriesRecorded = legUniformityBoxes.all {
        it != null
    } && legCleanCupBoxes.all { it != null } && legSweetnessBoxes.all { it != null }
    val legTotalScore = if (legacyInputs.all { it.isFinite() } && legacyCupEntriesRecorded && cupGridRecorded) {
        (legacyInputs.sum() + legUniformity + legCleanCup + legSweetness - multiCupDefectPenalty)
    } else {
        null
    }

    // Physical QC
    var pRoastColor by rememberSaveable { mutableStateOf("") }
    var pGroundColor by rememberSaveable { mutableStateOf("") }
    var pQuakers by rememberSaveable { mutableStateOf("") }
    var pMoisture by rememberSaveable { mutableStateOf("") }
    var pMoistureUncertainty by rememberSaveable { mutableStateOf("") }
    var pDefects by rememberSaveable { mutableStateOf("") }

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
                            text = when (protocol) {
                                CuppingProtocol.SCA_2004_LEGACY -> "Historical 100-point reference; mapping and rights unverified"
                                CuppingProtocol.SCA_CVA_CURRENT -> "Current CVA mapping and content rights have not been verified"
                                CuppingProtocol.CUSTOM_RESEARCH -> "Custom research form; not an official or standardized assessment"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (protocol == CuppingProtocol.SCA_2004_LEGACY) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (protocol) {
                                CuppingProtocol.SCA_2004_LEGACY -> "LEGACY 2004 — UNVERIFIED"
                                CuppingProtocol.SCA_CVA_CURRENT -> "CVA MAPPING UNVERIFIED"
                                CuppingProtocol.CUSTOM_RESEARCH -> "CUSTOM RESEARCH"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (protocol == CuppingProtocol.SCA_2004_LEGACY) MaterialTheme.colorScheme.onTertiaryContainer
                                else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Protocol selector chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                        CuppingProtocol.SCA_CVA_CURRENT -> "CVA"
                                        CuppingProtocol.SCA_2004_LEGACY -> "Legacy 2004"
                                        CuppingProtocol.CUSTOM_RESEARCH -> "Custom"
                                    },
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("sensory:cva_proto_${proto.name.lowercase()}")
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
                                ("HISTORICAL LEGACY FORM: Superseded by CVA standards. Scores must not be equated to modern CVA " +
                                    "evaluations."),
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
                                modifier = Modifier.heightIn(min = 48.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                val nextCode = "Draft ${sampleCodes.size + 1}"
                                sampleCodes.add(nextCode)
                                selectedSampleCode = nextCode
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, "Add Sample")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = isBlindMode,
                            onClick = {},
                            enabled = false,
                            label = { Text("Blind mode; no saved mapping") },
                            leadingIcon = { Icon(Icons.Default.VisibilityOff, null, modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("sensory:cva_blind_toggle")
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().testTag("sensory:cva_unsaved_state"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("UNSAVED LOCAL ASSESSMENT", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Entries are a temporary draft and are not saved as a cupping session. Current CVA mapping is unverified.",
                    style = MaterialTheme.typography.bodySmall
                )
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
                                String.format(Locale.ROOT, "%02d:%02d", mins, secs),
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
                            Text(
                                activeMilestone.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Target: ${activeMilestone.secondsTarget / 60}:00 min",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                        Text(
                            "5-Bowl Multi-Cup Grid (${selectedSampleCode.ifBlank { "No sample selected" }})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tap bowl to cycle: Clean (✓) → Taint (-2) → Fault (-4)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (!cupGridRecorded) MaterialTheme.colorScheme.surfaceVariant
                            else if (cleanCupCount == 5) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = if (!cupGridRecorded) "NOT_RECORDED" else if (cleanCupCount == 5) "5/5 Clean"
                                else "$cleanCupCount/5 Clean (-${multiCupDefectPenalty.toInt()} pts)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (!cupGridRecorded) MaterialTheme.colorScheme.onSurfaceVariant
                                else if (cleanCupCount == 5) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onErrorContainer,
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
                            -1 -> Triple(
                                "Bowl $cupNum\nNot rated",
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            0 -> Triple(
                                "Bowl $cupNum\nClean",
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            1 -> Triple(
                                "Bowl $cupNum\nTaint -2",
                                MaterialTheme.colorScheme.tertiaryContainer,
                                MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            else -> Triple(
                                "Bowl $cupNum\nFault -4",
                                MaterialTheme.colorScheme.errorContainer,
                                MaterialTheme.colorScheme.onErrorContainer
                            )
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
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Uniformity: ${uniformityScore?.let { String.format(Locale.US, "%.1f", it) } ?: "NOT_RECORDED"} / 10.0",
                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Clean Cup: ${cleanCupScore?.let { String.format(Locale.US, "%.1f", it) } ?: "NOT_RECORDED"} / 10.0",
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
                            Text(
                                "SCA 2004 Historical Cupping Sheet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "10 Quality Attributes (100-Point Scale)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (legTotalScore == null) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = legTotalScore?.let { String.format(Locale.ROOT, " %.2f pts ", it) } ?: " NOT_RECORDED ",
                                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Historical numeric bands are references, not grades or credentials.
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = legTotalScore?.let { score ->
                                when {
                                    score >= 90.0f -> "Historical reference band: 90+"
                                    score >= 85.0f -> "Historical reference band: 85–89.99"
                                    score >= 80.0f -> "Historical reference band: 80–84.99"
                                    else -> "Historical reference band: below 80"
                                }
                            } ?: "Historical reference band: NOT_RECORDED",
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
                    ).forEachIndexed { index, (label, value, setter) ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (value.isFinite()) String.format(Locale.ROOT, "%.2f / 10.0", value) else "NOT_RECORDED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = value.takeIf { it.isFinite() } ?: 6.0f,
                                onValueChange = setter,
                                valueRange = 6.0f..10.0f,
                                steps = 39,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        stateDescription = if (value.isFinite()) "Recorded score $value out of 10" else "Not recorded"
                                    }
                                    .testTag("sensory:cva_legacy_slider_$index")
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
                        modifier = Modifier.heightIn(min = 48.dp).testTag("sensory:cva_tab_${persp.name.lowercase()}")
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
                            Text(
                                "Perspective 1: Physical Assessment",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Green and roasted physical inspection (Roast Color Agtron, Quakers in 100g, Moisture % with uncertainty).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

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
                                    Text(
                                        "Perspective 2: Descriptive Assessment",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "0.0 to 5.0 intensity measurement & qualitative descriptors. No total score.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        "Non-Hedonic",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
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
                                        Text(
                                            "Sensory Wheel Descriptors (${selectedDescriptors.size})",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
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
                                        Text(
                                            "No descriptors selected. Tap '+ Add from Wheel' to pick descriptors.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
                                                        Text(
                                                            descName,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
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
                                Text(
                                    "Acidity Quality & Character",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
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
                                        Text(
                                            if (value.isFinite()) String.format(Locale.ROOT, "%.1f / 5.0", value) else "NOT_RECORDED",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Slider(
                                        value = value.takeIf { it.isFinite() } ?: 0.0f,
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Perspective 3: Affective Assessment",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "User-entered hedonic ratings; protocol mapping is unverified",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Text(
                                        totalAffective?.let {
                                            String.format(Locale.getDefault(),
                                                "Custom research subtotal %.2f; seven ratings minus cup defects", it)
                                        } ?: "No standardized total for this protocol",
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
                                        Text(
                                            if (value.isFinite()) String.format(Locale.ROOT, "%.2f / 10.0", value) else "NOT_RECORDED",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Slider(
                                        value = value.takeIf { it.isFinite() } ?: 6.0f,
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
                            Text(
                                "Perspective 4: Extrinsic Assessment",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            if (isBlindMode) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            "NO SAMPLE MAPPING",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            ("No sample identity or extrinsic metadata is attached. Reveal is unavailable until " +
                                                "mappings and an audit record can be saved."),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Button(
                                            onClick = {},
                                            enabled = false,
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Reveal unavailable", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    "No extrinsic coffee metadata is attached to this local form.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                Text(
                    "Save, compare, lock, and export need local session storage, which is not available yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("sensory:cva_actions_unavailable")
                )
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("sensory:cva_btn_log_journal"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Book, null, modifier = Modifier.size(16.dp).padding(end = 6.dp))
                    Text("Save to journal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("sensory:cva_btn_compare"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.CompareArrows, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("Compare", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }

                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("sensory:cva_btn_lock"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("Lock session", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("sensory:cva_btn_export_json"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("Export JSON", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }

                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("sensory:cva_btn_export_csv"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                    ) {
                        Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                        Text("Export CSV", style = MaterialTheme.typography.labelSmall, maxLines = 1)
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
                        SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.find { it.id == desc.categoryId }
                            ?.name.equals(wheelCategoryFilter, ignoreCase = true)
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

                        Text(
                            "Showing ${filteredDescriptors.size} descriptors:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Descriptors List
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredDescriptors, key = { it.id }) { desc ->
                                val isSelected = selectedDescriptors.contains(desc.name)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceContainer,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.30f
                                            )
                                        }
                                    ),
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
                                            Icon(
                                                Icons.Default.Check,
                                                null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThresholdResponseRow(
    step: Int,
    concentration: String,
    response: Boolean?,
    positiveLabel: String,
    negativeLabel: String,
    onResponse: (Boolean) -> Unit
) {
    val tag = "sensory:threshold_step$step"
    val haptic = LocalHapticFeedback.current
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Step $step ($concentration):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(
                if (response == null) "Not answered" else "Response recorded",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("${tag}_status")
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = response == true,
                onClick = {
                    if (response != true) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onResponse(true)
                },
                label = { Text(positiveLabel) },
                modifier = Modifier.heightIn(min = 48.dp).testTag("${tag}_yes")
            )
            FilterChip(
                selected = response == false,
                onClick = {
                    if (response != false) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onResponse(false)
                },
                label = { Text(negativeLabel) },
                modifier = Modifier.heightIn(min = 48.dp).testTag("${tag}_no")
            )
        }
    }
}

@Composable
fun SensoryTrainingLabSection(modifier: Modifier = Modifier) {
    var selectedTrainSubTab by rememberSaveable { mutableStateOf("triangle") }

    // Triangle Drill State
    var drillPermutation by rememberSaveable { mutableStateOf(TriangleSampleOrder.ABA) }
    var cup1Code by rememberSaveable { mutableStateOf("Cup 1") }
    var cup2Code by rememberSaveable { mutableStateOf("Cup 2") }
    var cup3Code by rememberSaveable { mutableStateOf("Cup 3") }
    var selectedCupIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedConfidence by rememberSaveable { mutableStateOf(3) }
    var drillSubmitted by rememberSaveable { mutableStateOf(false) }
    var sessionSeedInput by rememberSaveable { mutableStateOf("") }
    var washoutSecondsInput by rememberSaveable { mutableStateOf("45") }

    // Binomial stats inputs
    var trialsCountInput by rememberSaveable { mutableStateOf("") }
    var correctCountInput by rememberSaveable { mutableStateOf("") }
    val totalTrials = trialsCountInput.toIntOrNull()?.takeIf { it in 1..10_000 }
    val correctTrials = correctCountInput.toIntOrNull()?.takeIf { count -> totalTrials != null && count in 0..totalTrials }
    val binomialStat = remember(totalTrials, correctTrials) {
        if (totalTrials != null && correctTrials != null) {
            TriangleStatisticsEngine.calculateBinomialPValue(totalTrials, correctTrials)
        } else {
            null
        }
    }

    // 5 Tastes & Thresholds State
    var tasteStep1Water by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var tasteStep2Water by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var tasteStep3Taste by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var tasteStep4Taste by rememberSaveable { mutableStateOf<Boolean?>(null) }

    val ascendingSteps = remember(tasteStep1Water, tasteStep2Water, tasteStep3Taste, tasteStep4Taste) {
        listOf(
            AscendingConcentrationStep(
                1,
                0.05,
                "101",
                isDistinguishableFromWater = tasteStep1Water == true,
                identifiedTaste = null,
                isCorrectIdentification = false
            ),
            AscendingConcentrationStep(
                2,
                0.10,
                "102",
                isDistinguishableFromWater = tasteStep2Water == true,
                identifiedTaste = null,
                isCorrectIdentification = false
            ),
            AscendingConcentrationStep(
                3,
                0.20,
                "103",
                isDistinguishableFromWater = true,
                identifiedTaste = BasicTaste.SOUR,
                isCorrectIdentification = tasteStep3Taste == true
            ),
            AscendingConcentrationStep(
                4,
                0.40,
                "104",
                isDistinguishableFromWater = true,
                identifiedTaste = BasicTaste.SOUR,
                isCorrectIdentification = tasteStep4Taste == true
            )
        )
    }
    val thresholdCalc = remember(ascendingSteps, tasteStep1Water, tasteStep2Water, tasteStep3Taste, tasteStep4Taste) {
        if (tasteStep1Water != null && tasteStep2Water != null && tasteStep3Taste != null && tasteStep4Taste != null) {
            TriangleStatisticsEngine.calculateBestEstimateThreshold(ascendingSteps, BasicTaste.SOUR)
        } else {
            null
        }
    }

    // Olfactory & Spaced Repetition State
    var selectedVialIndex by rememberSaveable { mutableStateOf(0) }
    var chosenAromaFamily by rememberSaveable { mutableStateOf<AromaFamily?>(null) }
    var olfactorySubmitted by rememberSaveable { mutableStateOf(false) }
    var sm2RepCount by rememberSaveable { mutableStateOf(1) }
    var sm2IntervalDays by rememberSaveable { mutableStateOf(1.0) }
    var sm2EaseFactor by rememberSaveable { mutableStateOf(2.5) }

    // Organic Acids State
    var acidFoodGradeVerified by rememberSaveable { mutableStateOf(false) }

    // Tactile & Serving Control State
    var servingTempInput by rememberSaveable { mutableStateOf("") }
    var waterTdsInput by rememberSaveable { mutableStateOf("") }

    // Paired 2-AFC State
    var pairedChoiceIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var pairedSubmitted by rememberSaveable { mutableStateOf(false) }

    // Human verification cannot be recorded until a durable, attributable verifier flow exists.

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
                            Text(
                                "Triangle Discrimination Practice",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Virtual answer-key exercise only. It is not a physical sensory trial and is not saved.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        TactileButton(
                            onClick = {
                                val allPerms = TriangleSampleOrder.values()
                                val nextPerm = allPerms[(drillPermutation.ordinal + 1) % allPerms.size]
                                drillPermutation = nextPerm
                                cup1Code = "Cup 1"
                                cup2Code = "Cup 2"
                                cup3Code = "Cup 3"
                                selectedCupIndex = null
                                drillSubmitted = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
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
                            label = { Text("Reference note (not a random seed)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = washoutSecondsInput,
                            onValueChange = { washoutSecondsInput = it },
                            label = { Text("Washout plan (not timed)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Text(
                        "Practice identifying the odd option among three virtual cups. This exercise does not validate sensory skill:",
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
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant.copy(
                                            alpha = 0.40f
                                        )
                                    }
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
                                            color = if (isOddCup) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                if (isOddCup) "ODD ($sampleLabel)" else sampleLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isOddCup) MaterialTheme.colorScheme.onPrimary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Confidence Slider / Selector (1 to 5)
                    Text(
                        "Assessor Confidence Rating (1 = Pure Guess, 5 = Absolute Certainty):",
                        style = MaterialTheme.typography.labelSmall
                    )
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
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("sensory:btn_submit_triangle"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (drillSubmitted) "Exercise evaluated (not saved)" else "Submit practice choice",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (drillSubmitted) {
                        val correct = selectedCupIndex == oddCupIdx
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (correct) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    if (correct) "Answer-key match" else "Answer-key mismatch",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    ("Practice order: ${drillPermutation.name}. Answer key: Cup ${oddCupIdx + 1}. This feedback " +
                                        "is not persisted. Confidence: $selectedConfidence/5."),
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
                    Text(
                        "Exact Cumulative Binomial Statistics (p0 = 1/3)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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

                    if (binomialStat == null) {
                        Text(
                            "NO_TRIAL_DATA — enter valid total and correct counts (1–10,000 trials) to calculate.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("training:no_binomial_data")
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (binomialStat.isSignificantAlpha05) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Cumulative p-value:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        String.format(Locale.ROOT, "p = %.4f", binomialStat.pValueCumulative),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (binomialStat.isSignificantAlpha05) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        if (binomialStat.isSignificantAlpha05) {
                                            "Entered counts meet the configured p < 0.05 threshold"
                                        } else {
                                            "Entered counts do not meet the configured p < 0.05 threshold"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (binomialStat.isSignificantAlpha05) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    ("Scientific Caveat: Assumes independent Bernoulli trials with zero carryover. Rapid serial " +
                                        "cupping introduces sensory fatigue and cross-cup adaptation."),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                    Text(
                        "Standard 5 Basic Taste Solutions (ISO 8586 / SCA)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Water solvent spec: Clean, demineralized filtered water (<30 ppm TDS). Food-grade FCC/USP only.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                                    Text(
                                        ("Detection: ${tasteRef.detectionThresholdGramsPerLiter} g/L | Low: " +
                                            "${tasteRef.lowIntensityGramsPerLiter} g/L | High: " +
                                            "${tasteRef.highIntensityGramsPerLiter} g/L"),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        "Handling: ${tasteRef.safeHandlingWarning}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
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
                    Text(
                        "Ascending Concentration Series (ASTM E679)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Geometric dilution series testing detection and recognition threshold for Sour (Citric Acid).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ThresholdResponseRow(
                        step = 1,
                        concentration = "0.05 g/L",
                        response = tasteStep1Water,
                        positiveLabel = "Different from water",
                        negativeLabel = "Same as water",
                        onResponse = { tasteStep1Water = it }
                    )
                    ThresholdResponseRow(
                        step = 2,
                        concentration = "0.10 g/L",
                        response = tasteStep2Water,
                        positiveLabel = "Different from water",
                        negativeLabel = "Same as water",
                        onResponse = { tasteStep2Water = it }
                    )
                    ThresholdResponseRow(
                        step = 3,
                        concentration = "0.20 g/L",
                        response = tasteStep3Taste,
                        positiveLabel = "Identified as sour",
                        negativeLabel = "Not identified as sour",
                        onResponse = { tasteStep3Taste = it }
                    )
                    ThresholdResponseRow(
                        step = 4,
                        concentration = "0.40 g/L",
                        response = tasteStep4Taste,
                        positiveLabel = "Identified as sour",
                        negativeLabel = "Not identified as sour",
                        onResponse = { tasteStep4Taste = it }
                    )

                    // Best Estimate Threshold (BET) Results
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Calculated Best-Estimate Thresholds (BET):",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Detection Threshold: " +
                                    (if (thresholdCalc == null) {
                                        "NOT_RECORDED"
                                    } else {
                                        thresholdCalc.detectionThresholdGramsPerLiter?.let {
                                            String.format(Locale.ROOT, "%.4f g/L", it)
                                        } ?: "Not detected"
                                    }),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Recognition Threshold: " +
                                    (if (thresholdCalc == null) {
                                        "NOT_RECORDED"
                                    } else {
                                        thresholdCalc.recognitionThresholdGramsPerLiter?.let {
                                            String.format(Locale.ROOT, "%.4f g/L", it)
                                        } ?: "Not recognized"
                                    }),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Disclaimer: " +
                                    (thresholdCalc?.nonClinicalDisclaimer
                                        ?: "No complete response sequence has been entered; no threshold is calculated."),
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
                    Text(
                        "Virtual descriptor exercise only. No physical vial is provided and responses are not saved.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("training:virtual_olfactory_notice")
                    )

                    val activeVial = StandardOlfactoryVials.ALL_VIALS[selectedVialIndex % StandardOlfactoryVials.ALL_VIALS.size]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Virtual prompt ${selectedVialIndex + 1}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = {
                                selectedVialIndex = (selectedVialIndex + 1) % StandardOlfactoryVials.ALL_VIALS.size
                                chosenAromaFamily = null
                                olfactorySubmitted = false
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Next prompt", style = MaterialTheme.typography.labelSmall)
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
                        Text(
                            if (olfactorySubmitted) "Checked on this screen (not saved)" else "Check virtual response",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (olfactorySubmitted) {
                        val isCorrect = chosenAromaFamily == activeVial.family
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    if (isCorrect) "✓ Virtual answer matched" else "✗ Virtual answer differs",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    "Descriptor reference: ${activeVial.descriptorNameEn} (${activeVial.descriptorNameId})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text("True Family: ${activeVial.family.labelEn}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Food References: ${activeVial.naturalFoodReferencesEn.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Spaced Repetition (Sensory SM-2) Card
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        "Aroma Spaced Repetition (Sensory SM-2)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Current Ease Factor: ${String.format(
                            Locale.ROOT,
                            "%.2f",
                            sm2EaseFactor
                        )} | Interval: ${sm2IntervalDays.toInt()} day(s) | Reviews: $sm2RepCount",
                        style = MaterialTheme.typography.bodySmall
                    )

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
                    Text(
                        "Safe food-grade preparations with source-controlled concentrations under trained supervision.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                                    Text(
                                        "${acid.name} (${acid.chemicalFormula})",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "pKa1: ${acid.pKa1} | Ref: ${acid.referencePreparationGramsPerLiter} g/L",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text("Profile: ${acid.sensoryProfileEn}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        "Occurrence: ${acid.typicalOccurrenceEn}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Flight validation & safety checklist
                    Text(
                        if (acidFoodGradeVerified) "Supplier food-grade statement recorded by user; not independently verified."
                            else "Food-grade status: NOT_VERIFIED — no supplier evidence attached.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { acidFoodGradeVerified = !acidFoodGradeVerified }) {
                        Text(if (acidFoodGradeVerified) "Clear self-reported status" else "Mark supplier statement as self-reported")
                    }
                    Text(
                        ("Scientific Note: Acid chemistry vs perceived sourness: organic acids modulate multimodal mouthfeel and " +
                            "salivary stimulation; sourness cannot be predicted solely by pH."),
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
                    Text(
                        "Always evaluated against a calibrated CONTROL (ideal roast) sample.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    StandardRoastDefects.ALL_DEFECTS.forEach { defect ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(defect.category.labelEn, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text("Visual Cues: ${defect.visualAttributesEn}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Root Cause: ${defect.roastingRootCauseEn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Cup Profile: ${defect.sensoryAttributesEn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Text(
                        ("Causal Inference Caveat: Sensory profile reflects multi-variable roast dynamics; does not prove single " +
                            "roast flaw without roaster curves."),
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
                    Text(
                        "Controlled evaluation of body weight, texture smoothness, and astringency.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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

                    val servingTemp = servingTempInput.toDoubleOrNull()?.takeIf { it.isFinite() }
                    if (servingTemp == null) {
                        Text(
                            "Temperature status: NOT_MEASURED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (servingTemp < 40.0 || servingTemp > 65.0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.errorContainer) {
                            Text(
                                ("Entered temperature is outside this exercise's 40°C–65°C reference range; this is not a safety " +
                                    "determination."),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                "Entered temperature is within the exercise reference range; no sensory outcome is inferred.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(6.dp)
                            )
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
                    Text(
                        "Virtual 2-AFC prompt only. No physical samples are provided and no result is saved.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("training:paired_unsaved_notice")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(Pair(0, "Sample A (virtual)"), Pair(1, "Sample B (virtual)")).forEach { (idx, label) ->
                            val isSelected = pairedChoiceIndex == idx
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { pairedChoiceIndex = idx }
                                    .padding(4.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant.copy(
                                            alpha = 0.40f
                                        )
                                    }
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceContainer
                                )
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
                        Text(
                            if (pairedSubmitted) "Choice checked on this screen (not saved)" else "Submit paired choice",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (pairedSubmitted) {
                        Text(
                            "Practice response only; no statistical result is calculated or saved.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    Text(
                        "Personalized Sensory Readiness Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "NO_PRACTICE_DATA — this screen has no persisted physical-trial history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("training:no_practice_data")
                    )
                    Text(
                        "Lesson completion and virtual exercises do not establish sensory-trial mastery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Invariant S4-040: Human Observation Verification Gate
                    Text(
                        "Human Observation Verification Guard (S4-040)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        ("Never auto-mark a practical sensory exercise PASS based solely on reading or virtual quizzes. Requires " +
                            "physical tasting verification."),
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("training:physical_verification_unavailable")
                    ) {
                        Text("Physical verification unavailable — no durable assessor record")
                    }

                    // Persistent Legal Non-Certification Disclaimer Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Official Non-Certification Disclaimer:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                ("Coffee Creator Sensory Lab provides self-directed practice and scientific training only. Does " +
                                    "NOT confer official SCA or CQI Q-Grader/R-Grader certification."),
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
fun SensoryGreenGradingSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exportScope = rememberCoroutineScope()
    var exportPayload by remember { mutableStateOf<String?>(null) }
    var exportNotice by rememberSaveable { mutableStateOf<String?>(null) }
    var evidencePhotoUris by rememberSaveable { mutableStateOf(emptyList<String>()) }
    val evidencePhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            exportNotice = "Evidence photo selection cancelled."
        } else {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                evidencePhotoUris = (evidencePhotoUris + uri.toString()).distinct()
                exportNotice = "Evidence photo reference added. The image remains in its SAF provider."
            } catch (failure: SecurityException) {
                exportNotice = "Photo read grant rejected (${failure::class.simpleName}); photo was not added."
            } catch (failure: IllegalArgumentException) {
                exportNotice = "Photo provider rejected a persistent grant (${failure::class.simpleName}); photo was not added."
            }
        }
    }
    val jsonExportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val payload = exportPayload
        if (uri == null || payload == null) {
            exportNotice = "Export cancelled. No file was written."
        } else {
            exportScope.launch {
                exportNotice = try {
                    val digest = SafTextDocumentWriter(context.contentResolver).write(uri, payload)
                    "JSON saved to the selected SAF location. Payload SHA-256: $digest"
                } catch (failure: IOException) {
                    "Export failed; the file could not be confirmed: ${failure.message ?: "storage unavailable"}"
                } catch (failure: SecurityException) {
                    "Export failed; the file could not be confirmed: ${failure.message ?: "storage unavailable"}"
                }
            }
        }
    }
    val csvExportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        val payload = exportPayload
        if (uri == null || payload == null) {
            exportNotice = "Export cancelled. No file was written."
        } else {
            exportScope.launch {
                exportNotice = try {
                    val digest = SafTextDocumentWriter(context.contentResolver).write(uri, payload)
                    "CSV saved to the selected SAF location. Payload SHA-256: $digest"
                } catch (failure: IOException) {
                    "Export failed; the file could not be confirmed: ${failure.message ?: "storage unavailable"}"
                } catch (failure: SecurityException) {
                    "Export failed; the file could not be confirmed: ${failure.message ?: "storage unavailable"}"
                }
            }
        }
    }
    var selectedGreenTab by rememberSaveable { mutableStateOf(0) }
    val greenTabs = listOf("Defect Tally", "Sieve (ISO 4150)", "Moisture & Physics", "Lot & Custody", "Contract & Export")

    // S4-041: Lot & Custody State
    var lotId by rememberSaveable { mutableStateOf("") }
    var lotName by rememberSaveable { mutableStateOf("") }
    var selectedSpecies by rememberSaveable { mutableStateOf<GreenCoffeeSpecies?>(null) }
    var selectedProcess by rememberSaveable { mutableStateOf<GreenProcessingMethod?>(null) }
    var originCountry by rememberSaveable { mutableStateOf("") }
    var originRegion by rememberSaveable { mutableStateOf("") }
    var producer by rememberSaveable { mutableStateOf("") }
    var harvestYear by rememberSaveable { mutableStateOf("") }
    var sampleMassInput by rememberSaveable { mutableStateOf("") }
    var contractNameInput by rememberSaveable { mutableStateOf("") }
    var contractSourceInput by rememberSaveable { mutableStateOf("") }
    var contractCat1Input by rememberSaveable { mutableStateOf("") }
    var contractTotalDefectsInput by rememberSaveable { mutableStateOf("") }
    var contractMinMoistureInput by rememberSaveable { mutableStateOf("") }
    var contractMaxMoistureInput by rememberSaveable { mutableStateOf("") }
    var contractMaxAwInput by rememberSaveable { mutableStateOf("") }
    var contractMinScreen16Input by rememberSaveable { mutableStateOf("") }
    var contractMinDensityInput by rememberSaveable { mutableStateOf("") }
    var exporter by rememberSaveable { mutableStateOf("") }
    var importer by rememberSaveable { mutableStateOf("") }
    var warehouse by rememberSaveable { mutableStateOf("") }

    // S4-042 & S4-043: Defect Counts (Category 1 & Category 2)
    var cat1FullBlack by rememberSaveable { mutableStateOf(0) }
    var cat1FullSour by rememberSaveable { mutableStateOf(0) }
    var cat1DriedCherry by rememberSaveable { mutableStateOf(0) }
    var cat1Fungus by rememberSaveable { mutableStateOf(0) }
    var cat1ForeignMatter by rememberSaveable { mutableStateOf(0) }
    var cat1SevereInsect by rememberSaveable { mutableStateOf(0) }

    var cat2PartialBlack by rememberSaveable { mutableStateOf(0) } // 3:1
    var cat2PartialSour by rememberSaveable { mutableStateOf(0) } // 3:1
    var cat2Parchment by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2Floater by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2Immature by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2Withered by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2Shell by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2Broken by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2Hull by rememberSaveable { mutableStateOf(0) } // 5:1
    var cat2SlightInsect by rememberSaveable { mutableStateOf(0) } // 10:1

    var defectInspectionComplete by rememberSaveable { mutableStateOf(false) }

    // S4-044: ISO 4150 Sieve Inputs
    var sieve18Input by rememberSaveable { mutableStateOf("") }
    var sieve17Input by rememberSaveable { mutableStateOf("") }
    var sieve16Input by rememberSaveable { mutableStateOf("") }
    var sieve15Input by rememberSaveable { mutableStateOf("") }
    var sievePanInput by rememberSaveable { mutableStateOf("") }

    // S4-045 & S4-046: Moisture & Physics
    var moistureInput by rememberSaveable { mutableStateOf("") }
    var isMoistureMeasured by rememberSaveable { mutableStateOf(false) }
    var meterModel by rememberSaveable { mutableStateOf("") }
    var meterSerial by rememberSaveable { mutableStateOf("") }
    var moistureUncertaintyInput by rememberSaveable { mutableStateOf("") }
    var calibrationDateInput by rememberSaveable { mutableStateOf("") }
    var selectedMoistureMethod by rememberSaveable { mutableStateOf<MoistureMeasurementMethod?>(null) }
    var sampleTempInput by rememberSaveable { mutableStateOf("") }
    var waterActivityInput by rememberSaveable { mutableStateOf("") }
    var isWaterActivityMeasured by rememberSaveable { mutableStateOf(false) }
    var bulkDensityInput by rememberSaveable { mutableStateOf("") }
    var isBulkDensityMeasured by rememberSaveable { mutableStateOf(false) }
    var assessorNameInput by rememberSaveable { mutableStateOf("") }
    var assessorIdInput by rememberSaveable { mutableStateOf("") }
    var equipmentCalibrationSummaryInput by rememberSaveable { mutableStateOf("") }
    var selectedColor by rememberSaveable { mutableStateOf<GreenBeanColor?>(null) }
    var selectedOdor by rememberSaveable { mutableStateOf<GreenBeanOdor?>(null) }

    // S4-049: Buyer limits must be entered from a real, user-held specification.

    // Calculations
    val sampleMass = sampleMassInput.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 } ?: 0.0
    val cat1Total = cat1FullBlack + cat1FullSour + cat1DriedCherry + cat1Fungus + cat1ForeignMatter + (cat1SevereInsect / 5.0)
    val cat2Equivalent = (cat2PartialBlack / 3.0) + (cat2PartialSour / 3.0) + (cat2Parchment / 5.0) + (cat2Floater / 5.0) +
        (cat2Immature / 5.0) + (cat2Withered / 5.0) + (cat2Shell / 5.0) + (cat2Broken / 5.0) + (cat2Hull / 5.0) + (cat2SlightInsect / 10.0)

    val moistureVal = if (isMoistureMeasured) moistureInput.toDoubleOrNull()?.takeIf { it.isFinite() && it in 0.0..100.0 } else null
    val moistureUncertainty = moistureUncertaintyInput.toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
    val calibrationDateEpochMs = runCatching {
        LocalDate.parse(calibrationDateInput).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }.getOrNull()?.takeIf { it >= 0L }
    val moistureTemperature = sampleTempInput.toDoubleOrNull()?.takeIf { it.isFinite() }
    val awVal = if (isWaterActivityMeasured) waterActivityInput.toDoubleOrNull()?.takeIf { it.isFinite() && it in 0.0..1.0 } else null
    val densityVal = if (isBulkDensityMeasured) bulkDensityInput.toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 } else null

    val currentSample = remember(
        lotId, lotName, selectedSpecies, selectedProcess, originCountry, originRegion, producer, harvestYear,
        exporter, importer, warehouse, sampleMass, defectInspectionComplete, cat1Total, cat2Equivalent,
        moistureVal, moistureUncertainty, moistureTemperature, meterModel, meterSerial, calibrationDateEpochMs,
        selectedMoistureMethod, awVal, densityVal, selectedColor, selectedOdor
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
            defectInspectionComplete = defectInspectionComplete,
            chainOfCustody = if (exporter.isNotBlank() && importer.isNotBlank() && warehouse.isNotBlank()) {
                ChainOfCustody(exporter = exporter, importer = importer, warehouseLocation = warehouse)
            } else {
                null
            },
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
            moisture = MoistureAssessment(
                moisturePercent = moistureVal,
                uncertaintyPercent = moistureUncertainty,
                meterDeviceModel = meterModel.ifBlank { null },
                meterSerialNumber = meterSerial.ifBlank { null },
                calibrationDateEpochMs = calibrationDateEpochMs,
                sampleTemperatureCelsius = moistureTemperature,
                measurementMethod = selectedMoistureMethod,
                isMeasured = moistureVal != null
            ),
            waterActivity = WaterActivityAssessment(waterActivityAw = awVal),
            bulkDensity = BulkDensityAssessment(bulkDensityGramsPerLiter = densityVal),
            color = selectedColor,
            odor = selectedOdor
        )
    }

    val measuredGrading = remember(currentSample) {
        StandardGreenGradingEngine.evaluateWashedArabicaGrade(currentSample)
    }
    val gradingInputsComplete = sampleMass == 350.0 && defectInspectionComplete && moistureVal != null && selectedOdor != null &&
        selectedSpecies != null && selectedProcess != null
    val gradingEvaluation = remember(measuredGrading, gradingInputsComplete) {
        if (gradingInputsComplete) {
            measuredGrading
        } else {
            measuredGrading.copy(
                grade = PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE,
                isSpecialtyPhysicalEligible = false,
                evaluationNotes = measuredGrading.evaluationNotes +
                    "Classification pending: enter sample mass, complete defect inspection, record moisture, and assess odor."
            )
        }
    }

    // Sieve Calculation
    val sieveAnalysis = remember(sieve18Input, sieve17Input, sieve16Input, sieve15Input, sievePanInput, sampleMass) {
        val masses = listOf(
            sieve18Input.toDoubleOrNull(),
            sieve17Input.toDoubleOrNull(),
            sieve16Input.toDoubleOrNull(),
            sieve15Input.toDoubleOrNull(),
            sievePanInput.toDoubleOrNull()
        )
        if (sampleMass <= 0.0 || masses.any { it == null || !it.isFinite() || it < 0.0 }) {
            null
        } else {
            val values = masses.map { requireNotNull(it) }
            StandardGreenGradingEngine.calculateSieveAnalysis(
                sampleMass,
                mapOf(
                    ScreenSieveNumber.SCREEN_18 to values[0],
                    ScreenSieveNumber.SCREEN_17 to values[1],
                    ScreenSieveNumber.SCREEN_16 to values[2],
                    ScreenSieveNumber.SCREEN_15 to values[3],
                    ScreenSieveNumber.BELOW_12 to values[4]
                )
            )
        }
    }

    val exportMissingRequirements = buildList {
        if (lotId.isBlank()) add("lot identifier")
        if (!gradingInputsComplete) add("complete 350 g defect, moisture, species, process, and odor inspection")
        if (sieveAnalysis?.isMassBalanceValid != true) add("valid sieve mass balance")
        if (moistureUncertainty == null) add("moisture uncertainty")
        if (selectedMoistureMethod == null) add("moisture measurement method")
        if (meterModel.isBlank() || meterSerial.isBlank()) add("moisture meter model and serial")
        if (calibrationDateEpochMs == null) add("valid calibration date (YYYY-MM-DD)")
        if (assessorNameInput.isBlank() || assessorIdInput.isBlank()) add("assessor name and ID")
        if (equipmentCalibrationSummaryInput.isBlank()) add("equipment calibration record")
        if (evidencePhotoUris.isEmpty()) add("at least one SAF evidence photo")
    }
    val exportReady = exportMissingRequirements.isEmpty()

    // Buyer Contract Evaluation
    val contractCat1Limit = contractCat1Input.toIntOrNull()?.takeIf { it >= 0 }
    val parsedContract = listOf(
        contractTotalDefectsInput.toDoubleOrNull(),
        contractMinMoistureInput.toDoubleOrNull(),
        contractMaxMoistureInput.toDoubleOrNull(),
        contractMaxAwInput.toDoubleOrNull(),
        contractMinScreen16Input.toDoubleOrNull(),
        contractMinDensityInput.toDoubleOrNull()
    )
    val buyerSpec = if (contractNameInput.isNotBlank() && contractSourceInput.isNotBlank() && contractCat1Limit != null &&
        parsedContract.all {
            it != null && it.isFinite()
        }
    ) {
        runCatching {
            BuyerAcceptanceLimits(
                contractName = contractNameInput.trim(),
                sourceReference = contractSourceInput.trim(),
                maxCategory1FullDefects = contractCat1Limit,
                maxTotalEquivalentDefects = requireNotNull(parsedContract[0]),
                minMoisturePercent = requireNotNull(parsedContract[1]),
                maxMoisturePercent = requireNotNull(parsedContract[2]),
                maxWaterActivityAw = requireNotNull(parsedContract[3]),
                minScreen16PlusPercent = requireNotNull(parsedContract[4]),
                minBulkDensityGramsPerLiter = requireNotNull(parsedContract[5])
            )
        }.getOrNull()
    } else {
        null
    }
    val contractReady = buyerSpec != null && gradingInputsComplete && awVal != null && densityVal != null && sieveAnalysis
        ?.isMassBalanceValid == true
    val contractResult = remember(currentSample, sieveAnalysis, buyerSpec, contractReady) {
        if (contractReady) {
            buyerSpec?.let {
                StandardGreenGradingEngine.evaluateBuyerContract(currentSample, it, sieveAnalysis)
            }
        } else {
            null
        }
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
                        Text(
                            if (sampleMass > 0.0) "${sampleMass}g Green Physical QC" else "Green Physical QC",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            ("${currentSample.standardRef.name}. Educational reference only; source rights and current " +
                                "applicability are unverified."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                                if (defectInspectionComplete) String.format(Locale.US, "%.1f", cat1Total) else "—",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    !defectInspectionComplete -> MaterialTheme.colorScheme.onSurfaceVariant
                                    cat1Total == 0.0 -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.testTag("sensory:green_metric_cat1")
                            )
                            Text(
                                if (defectInspectionComplete) "recorded" else "NOT_MEASURED",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cat 2 Equiv", style = MaterialTheme.typography.labelSmall)
                            Text(
                                if (defectInspectionComplete) String.format(Locale.US, "%.1f", cat2Equivalent) else "—",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("sensory:green_metric_cat2")
                            )
                            Text(
                                if (defectInspectionComplete) "recorded" else "NOT_MEASURED",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                                color = if (gradingEvaluation.moistureWithinStandardRange) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "(9.0% - 13.0%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().testTag("sensory:green_unsaved_draft_notice"),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Text(
                ("Unsaved screen draft. Leaving this screen may discard entries; export creates a file but does not save the " +
                    "draft in the app."),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(12.dp)
            )
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
                    label = {
                        Text(
                            title,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (selectedGreenTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Category 1: Primary Full Defects (1:1 Ratio)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    "A Cat 1 defect changes the historical reference result; this is not an official grade.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = defectInspectionComplete,
                                onCheckedChange = { defectInspectionComplete = it },
                                modifier = Modifier.testTag("sensory:green_defect_inspection_complete")
                            )
                            Text("Inspection complete; zero means none observed.", style = MaterialTheme.typography.bodySmall)
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
                                Text(
                                    name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
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
                        Text(
                            "Category 2: Secondary Defects (Weighted Equivalence)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Multiple beans required to equal 1 full defect equivalent (Max 5 for Specialty).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

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

                        // User-initiated SAF photo selection; only persistable read grants are retained.
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (evidencePhotoUris.isEmpty()) "No lot evidence photo attached; required for export."
                                        else "${evidencePhotoUris.size} lot evidence photo(s) attached.",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = { evidencePhotoPicker.launch(arrayOf("image/*")) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.heightIn(min = 48.dp).testTag("sensory:green_attach_evidence")
                                ) {
                                    Icon(Icons.Default.CameraAlt, "Add evidence photo", modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Add evidence photo", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            if (evidencePhotoUris.isNotEmpty()) {
                                TextButton(
                                    onClick = { evidencePhotoUris = evidencePhotoUris.dropLast(1) },
                                    modifier = Modifier.align(Alignment.End).heightIn(min = 48.dp)
                                ) {
                                    Text("Remove most recent photo reference")
                                }
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
                        Text(
                            "ISO 4150:2010 Screen Sieve Size Distribution",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Enter the measured sample mass and each recovered fraction.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = sampleMassInput,
                            onValueChange = { sampleMassInput = it },
                            label = { Text("Measured sample mass (g)") },
                            modifier = Modifier.fillMaxWidth().testTag("sensory:green_sample_mass_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

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

                        HorizontalDivider()
                        if (sieveAnalysis == null) {
                            Text(
                                "NOT_MEASURED — complete sample mass and all sieve fractions to calculate.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Recovered:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${String.format(Locale.US, "%.1f", sieveAnalysis.totalRecoveredMassGrams)}g / ${sampleMass}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mass Balance Mismatch:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${String.format(
                                        Locale.US,
                                        "%.2f",
                                        sieveAnalysis.massBalanceMismatchGrams
                                    )}g (${if (sieveAnalysis.isMassBalanceValid) "PASS <=1.5g" else "FAIL >1.5g"})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sieveAnalysis.isMassBalanceValid) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Screen 16+ Retention:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${String.format(Locale.US, "%.1f", sieveAnalysis.cumulativeRetentionScreen16Plus)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // TAB 2: Moisture & Physics
                Card(
                    modifier = Modifier.animateContentSize().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Moisture, Water Activity & Density Lab",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text("Bean color (visual observation, optional)", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GreenBeanColor.values().forEach { option ->
                                FilterChip(
                                    selected = selectedColor == option,
                                    onClick = { selectedColor = if (selectedColor == option) null else option },
                                    label = { Text(option.label, maxLines = 1) },
                                    modifier = Modifier.testTag("sensory:green_color_${option.name}")
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Moisture Status: ${if (moistureVal != null) "MEASURED" else "NOT_MEASURED"}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Switch(
                                checked = isMoistureMeasured,
                                onCheckedChange = { isMoistureMeasured = it },
                                modifier = Modifier.testTag("sensory:green_measure_moisture")
                            )
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
                                    label = { Text("Moisture meter model") },
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
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = meterSerial,
                                    onValueChange = { meterSerial = it },
                                    label = { Text("Meter serial") },
                                    modifier = Modifier.weight(1f).testTag("sensory:green_meter_serial_input"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = moistureUncertaintyInput,
                                    onValueChange = { moistureUncertaintyInput = it },
                                    label = { Text("Uncertainty (% points)") },
                                    modifier = Modifier.weight(1f).testTag("sensory:green_moisture_uncertainty_input"),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                            }
                            OutlinedTextField(
                                value = calibrationDateInput,
                                onValueChange = { calibrationDateInput = it },
                                label = { Text("Calibration date (YYYY-MM-DD)") },
                                modifier = Modifier.fillMaxWidth().testTag("sensory:green_calibration_date_input"),
                                singleLine = true,
                                isError = calibrationDateInput.isNotBlank() && calibrationDateEpochMs == null,
                                supportingText = {
                                    if (calibrationDateInput.isNotBlank() && calibrationDateEpochMs == null) {
                                        Text("Enter a valid date from 1970 onward.")
                                    }
                                }
                            )
                            Text("Moisture measurement method", style = MaterialTheme.typography.labelMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                MoistureMeasurementMethod.values().forEach { method ->
                                    FilterChip(
                                        selected = selectedMoistureMethod == method,
                                        onClick = { selectedMoistureMethod = method },
                                        label = { Text(method.label, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.testTag("sensory:green_method_${method.name.lowercase(Locale.ROOT)}")
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Water Activity & Bulk Density
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Water activity: ${if (isWaterActivityMeasured && awVal != null) "MEASURED" else "NOT_MEASURED"}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Switch(
                                checked = isWaterActivityMeasured,
                                onCheckedChange = { isWaterActivityMeasured = it },
                                modifier = Modifier.testTag("sensory:green_measure_water_activity")
                            )
                        }
                        if (isWaterActivityMeasured) {
                            OutlinedTextField(
                                value = waterActivityInput,
                                onValueChange = { waterActivityInput = it },
                                label = { Text("Water Activity (aw)") },
                                modifier = Modifier.fillMaxWidth().testTag("sensory:green_water_activity_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Bulk density: ${if (isBulkDensityMeasured && densityVal != null) "MEASURED" else "NOT_MEASURED"}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Switch(
                                checked = isBulkDensityMeasured,
                                onCheckedChange = { isBulkDensityMeasured = it },
                                modifier = Modifier.testTag("sensory:green_measure_bulk_density")
                            )
                        }
                        if (isBulkDensityMeasured) {
                            OutlinedTextField(
                                value = bulkDensityInput,
                                onValueChange = { bulkDensityInput = it },
                                label = { Text("Bulk Density (g/L)") },
                                modifier = Modifier.fillMaxWidth().testTag("sensory:green_bulk_density_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Text(
                            ("Record actual readings and the method/instrument used. Storage and density limits depend on the " +
                                "selected contract."),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider()

                        // Odor Organoleptic Inspection
                        Text("Organoleptic Odor Inspection:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GreenBeanOdor.values().forEach { odor ->
                                FilterChip(
                                    selected = selectedOdor == odor,
                                    onClick = { selectedOdor = odor },
                                    label = { Text(odor.label, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                        selectedOdor?.takeIf { !it.isOrganolepticallyClean }?.let {
                            Text(
                                "Unacceptable odor observation; historical Grade 1 reference threshold is not met.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
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
                        Text(
                            "Green Lot Provenance & Chain of Custody",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

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
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GreenCoffeeSpecies.values().forEach { sp ->
                                FilterChip(
                                    selected = selectedSpecies == sp,
                                    onClick = { selectedSpecies = sp },
                                    label = { Text(sp.label, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
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
                            OutlinedTextField(value = originCountry, onValueChange = {
                                originCountry = it
                            }, label = { Text("Country") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = originRegion, onValueChange = {
                                originRegion = it
                            }, label = { Text("Region") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = producer, onValueChange = {
                                producer = it
                            }, label = { Text("Producer") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = harvestYear, onValueChange = {
                                harvestYear = it
                            }, label = { Text("Crop Year") }, modifier = Modifier.weight(1f), singleLine = true)
                        }

                        HorizontalDivider()

                        Text("Chain of Custody:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = exporter, onValueChange = {
                            exporter = it
                        }, label = { Text("Exporter") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = importer, onValueChange = {
                            importer = it
                        }, label = { Text("Importer") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = warehouse, onValueChange = {
                            warehouse = it
                        }, label = { Text("Warehouse Location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
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
                        Text("Buyer Contract Checks", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            "Enter limits from a real buyer specification. No contract is assumed by default.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = contractNameInput,
                            onValueChange = { contractNameInput = it },
                            label = { Text("Contract name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = contractSourceInput,
                            onValueChange = { contractSourceInput = it },
                            label = { Text("Source / document reference") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                contractCat1Input,
                                {
                                    contractCat1Input = it
                                },
                                label = {
                                    Text("Max Cat 1 eq")
                                },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                contractTotalDefectsInput,
                                {
                                    contractTotalDefectsInput = it
                                },
                                label = {
                                    Text("Max total defects")
                                },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                contractMinMoistureInput,
                                {
                                    contractMinMoistureInput = it
                                },
                                label = {
                                    Text("Min moisture %")
                                },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                contractMaxMoistureInput,
                                {
                                    contractMaxMoistureInput = it
                                },
                                label = {
                                    Text("Max moisture %")
                                },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                contractMaxAwInput,
                                {
                                    contractMaxAwInput = it
                                },
                                label = {
                                    Text("Max water activity")
                                },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                contractMinScreen16Input,
                                {
                                    contractMinScreen16Input = it
                                },
                                label = {
                                    Text("Min screen 16+ %")
                                },
                                modifier = Modifier.weight(
                                    1f
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                        OutlinedTextField(
                            contractMinDensityInput,
                            { contractMinDensityInput = it },
                            label = { Text("Min bulk density (g/L)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        // Buyer contract result pill
                        if (contractResult == null) {
                            Text(
                                ("PENDING_INPUT — provide a sourced contract, valid measurements, a 350 g reference sample, and a " +
                                    "completed defect inspection."),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (contractResult.isAccepted) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        "Entered-limit check: " +
                                            if (contractResult.isAccepted) {
                                                "all entered limits met"
                                            } else {
                                                "one or more limits unmet"
                                            },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    contractResult.notes.forEach { note ->
                                        Text("• $note", style = MaterialTheme.typography.bodySmall)
                                    }
                                    buyerSpec?.let {
                                        Text(
                                            "User-entered source reference (not independently verified): ${it.sourceReference}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    contractResult.failureReasons.forEach { fail ->
                                        Text(
                                            "⚠️ $fail",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        Text("Measurement export provenance", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = assessorNameInput,
                            onValueChange = { assessorNameInput = it },
                            label = { Text("Assessor name") },
                            modifier = Modifier.fillMaxWidth().testTag("sensory:green_assessor_name_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = assessorIdInput,
                            onValueChange = { assessorIdInput = it },
                            label = { Text("Assessor ID / local identifier") },
                            modifier = Modifier.fillMaxWidth().testTag("sensory:green_assessor_id_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = equipmentCalibrationSummaryInput,
                            onValueChange = { equipmentCalibrationSummaryInput = it },
                            label = { Text("Equipment calibration record reference") },
                            modifier = Modifier.fillMaxWidth().testTag("sensory:green_equipment_calibration_input"),
                            singleLine = true
                        )

                        // S4-048: No unverified CVA score mapping is combined with the physical reference.
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.fillMaxWidth().testTag("sensory:green_separate_assessment_notice")
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Physical and sensory assessments remain separate",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    ("No combined grade is available: the CVA score mapping and content rights have not been " +
                                        "verified. This screen does not issue an official grade or certification."),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider()

                        // Export Actions
                        Text("S4-050 Complete QC Report Export", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(
                            ("After writing, the app reports the exact payload SHA-256. CSV does not embed it; this digest is not " +
                                "a digital signature or a persisted audit log."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!exportReady) {
                            Text(
                                "Complete report requirements: ${exportMissingRequirements.joinToString()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("sensory:green_export_requirements")
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val audit = GreenQCAuditRecord(
                                        assessorName = assessorNameInput.trim(),
                                        assessorId = assessorIdInput.trim(),
                                        evaluationTimestampEpochMs = System.currentTimeMillis(),
                                        evidencePhotoUris = evidencePhotoUris,
                                        standardVersion =
                                            "${currentSample.standardRef.id}:${currentSample.standardRef.version} (unverified)",
                                        equipmentCalibrationSummary = equipmentCalibrationSummaryInput.trim()
                                    )
                                    exportPayload = StandardGreenGradingEngine.generateGreenQcJson(currentSample,
                                        gradingEvaluation, audit, sieveAnalysis)
                                    jsonExportPicker.launch("coffee-creator-green-qc.json")
                                },
                                enabled = exportReady,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("sensory:green_export_json")
                            ) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Export JSON")
                            }

                            OutlinedButton(
                                onClick = {
                                    val audit = GreenQCAuditRecord(
                                        assessorName = assessorNameInput.trim(),
                                        assessorId = assessorIdInput.trim(),
                                        evaluationTimestampEpochMs = System.currentTimeMillis(),
                                        evidencePhotoUris = evidencePhotoUris,
                                        standardVersion =
                                            "${currentSample.standardRef.id}:${currentSample.standardRef.version} (unverified)",
                                        equipmentCalibrationSummary = equipmentCalibrationSummaryInput.trim()
                                    )
                                    exportPayload = StandardGreenGradingEngine.generateGreenQcCsv(currentSample, gradingEvaluation,
                                        audit, sieveAnalysis)
                                    csvExportPicker.launch("coffee-creator-green-qc.csv")
                                },
                                enabled = exportReady,
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
                    ("Out-of-scope guard (S4-047 / S4-051): the historical 2004 reference in this app is unverified and limited " +
                        "to explicitly selected Washed Coffea Arabica. Other species/processes remain UNDETERMINED_OUT_OF_SCOPE. " +
                        "Missing measurements remain NOT_MEASURED."),
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
fun SensoryOperationsSection(modifier: Modifier = Modifier) {
    var activeSubTab by rememberSaveable { mutableStateOf(OperationsSubTab.BLIND_PANEL) }
    var useIndonesian by rememberSaveable { mutableStateOf(false) }

    // Blind Panel State (S4-058)
    var currentRole by rememberSaveable { mutableStateOf(SensoryPanelRole.SESSION_HOST) }

    // Calibration Stats State (S4-059)
    val mcdMap = emptyMap<String, Double>()
    val pearsonR: Double? = null
    val fleissKappa: Double? = null

    // Historical Linkage State (S4-055)
    // Accessibility & Allergens (S4-062)
    // Privacy & Export Consent (S4-060, S4-065)
    var consentGranted by rememberSaveable { mutableStateOf(false) }
    var redactSupplierPricing by rememberSaveable { mutableStateOf(true) }
    var redactAssessorNames by rememberSaveable { mutableStateOf(true) }

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
                            text = if (useIndonesian) "Operasi Profesional, AI & Panel (Sprint 04 Epic E)"
                                else "Pro Operations, AI & Panel (Sprint 04 Epic E)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (useIndonesian) "Panel buta, kalibrasi inter-rater, pemisahan deterministik, privasi offline"
                                else "Blind panel, inter-rater calibration, deterministic rules, offline privacy",
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
                                .heightIn(min = 48.dp)
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
                                    label = {
                                        Text(
                                            if (useIndonesian) "Penilai" else "Assessor",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.heightIn(min = 32.dp).testTag("operations:role_assessor")
                                )
                            }
                        }

                        Text(
                            text = if (currentRole == SensoryPanelRole.SESSION_HOST) {
                                (if (useIndonesian) ("Peran Host: pembukaan identitas tersedia setelah sesi panel tersimpan " +
                                    "dengan pemetaan sampel.")
                                        else "Host Role: identity reveal requires a saved panel session with sample mappings.")
                            } else {
                                (if (useIndonesian) ("Peran Penilai: kode buta hanya tersedia setelah sesi tersimpan; tidak ada " +
                                    "identitas contoh yang ditampilkan.")
                                        else ("Assessor Role: blind codes are available only from a saved session; no sample " +
                                            "identities are supplied here."))
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // No hardcoded sample codes or identities are shown as panel data.
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("operations:panel_no_session_state"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("NO_SAVED_PANEL_SESSION", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    ("Sample identities and reveal actions are unavailable until panel mappings and a durable " +
                                        "audit record are stored."),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedButton(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("operations:reveal_unavailable")
                                ) {
                                    Text("Reveal unavailable — no saved session")
                                }
                            }
                        }

                        Text(
                            if (useIndonesian) "Pembukaan darurat dinonaktifkan sampai audit log sesi dapat disimpan."
                                else "Emergency reveal is disabled until session audit logs can be persisted.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                            text = if (useIndonesian) "Statistik Kesepakatan & Kalibrasi Panel (S4-059)"
                                else "Panel Agreement & Calibration Statistics (S4-059)",
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
                                        text = "NO_SESSION_DATA",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Text("No saved panel session", style = MaterialTheme.typography.labelSmall)
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
                                        text = "NO_SESSION_DATA",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            "NOT_CALCULATED",
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
                                        text = pearsonR?.let { String.format(Locale.ROOT, "%.4f", it) } ?: "NOT_CALCULATED",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Text("No saved assessor pairs", style = MaterialTheme.typography.labelSmall)
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
                                        text = fleissKappa?.let { String.format(Locale.ROOT, "%.4f", it) } ?: "NOT_CALCULATED",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum")
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            "NO_SESSION_DATA",
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
                                        color = if (mcd <= 0.35) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            if (mcd <= 0.35) "PASS" else "RE-CALIBRATE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (mcd <= 0.35) MaterialTheme.colorScheme.onPrimaryContainer
                                                else MaterialTheme.colorScheme.onErrorContainer,
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
                                text = if (useIndonesian) "Kaitan Historis Tanpa Timpa Data (S4-055)"
                                    else "Historical Linkage Without Mutation (S4-055)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "NO_SESSION_DATA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = if (useIndonesian) {
                                ("Menghubungkan sesi cupping ke biji kopi, batch sangrai, resep seduh, dan grinder tanpa merusak " +
                                    "log riwayat asli.")
                            } else {
                                ("Links cupping evaluation to coffee beans, roast batches, recipes, and grinders without " +
                                    "modifying historical records.")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = if (useIndonesian) {
                                ("Belum ada sesi cupping tersimpan untuk ditautkan. Tautan tidak dibuat sampai rekaman dan ID " +
                                    "bean, roast, resep, serta alat tersedia.")
                            } else {
                                ("No saved cupping session is available to link. Link creation is unavailable until the session " +
                                    "and bean, roast, recipe, and equipment IDs are persisted.")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("operations:create_linkage_btn")
                        ) {
                            Icon(Icons.Default.Link, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (useIndonesian) "Tautkan rekaman tersimpan" else "Link saved records")
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
                            text = if (useIndonesian) "Pemisahan AI & Mesin Deterministik (S4-056, S4-057)"
                                else "Deterministic Math & AI Governance (S4-056, S4-057)",
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
                                    Text(
                                        "Extraction Yield Formula",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
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
                                    text = "EY = beverage mass × TDS ÷ dose. No measured inputs are loaded, so no result is calculated.",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "No local model response is selected for comparison.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                Text(
                                    "Offline Local Knowledge Grounding",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "No local response or cited source is loaded. Citation verification: NOT_RUN.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "When source evidence is unavailable, the assistant must abstain.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            text = if (useIndonesian) "Akomodasi Sensori & Peringatan Alergen (S4-062)"
                                else "Sensory Accommodations & Allergen Alerts (S4-062)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (useIndonesian) {
                                ("Pengaturan ini belum terhubung ke penilaian latihan atau pemeriksaan keamanan. Tidak ada " +
                                    "akomodasi atau penyaringan alergen yang diterapkan.")
                            } else {
                                ("These settings are not connected to practice scoring or reference-solution safety checks in " +
                                    "this build. No accommodation or allergen filtering is active.")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("operations:accessibility_unavailable_notice")
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
                                    if (useIndonesian) ("Menekankan sensasi tactile body dan rasa dasar; melepas kewajiban " +
                                        "penilaian fragrance kering.")
                                            else "Emphasizes body/mouthfeel and basic taste; relaxes dry fragrance obligation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = false,
                                onCheckedChange = {},
                                enabled = false,
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
                                    if (useIndonesian) "Menandai deskriptor aroma yang berpotensi memicu distorsi persepsi."
                                        else "Flags aroma descriptors with distortion risks.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = false,
                                onCheckedChange = {},
                                enabled = false,
                                modifier = Modifier.testTag("operations:toggle_parosmia")
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))

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
                                checked = false,
                                onCheckedChange = {},
                                enabled = false,
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
                                checked = false,
                                onCheckedChange = {},
                                enabled = false,
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
                            text = if (useIndonesian) "Privasi & Ekspor Tertutup Kriptografis (S4-060, S4-065)"
                                else "Privacy & Sealed Cryptographic Export (S4-060, S4-065)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (useIndonesian) {
                                ("Zero Cloud: Data tidak pernah diunggah otomatis. Ekspor lokal membutuhkan persetujuan eksplisit " +
                                    "pengguna dengan opsi redaksi PII.")
                            } else {
                                ("Zero Cloud: Data is never automatically uploaded. Local exports strictly require user consent " +
                                    "with PII redaction options.")
                            },
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
                                if (useIndonesian) "Saya memberikan izin untuk mengekspor data cupping lokal"
                                    else "I grant consent for local cupping data export",
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
                                if (useIndonesian) "Redaksi harga komersial & kontrak supplier (Disarankan)"
                                    else "Redact commercial supplier pricing & lot contracts (Recommended)",
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
                                if (useIndonesian) "Anonimkan nama asli penilai menjadi nama samaran (Pseudonym)"
                                    else "Anonymize assessor real names to pseudonyms",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("operations:export_sealed_json_btn")
                        ) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (useIndonesian) "Belum ada sesi cupping tersimpan" else "No saved cupping session to export")
                        }
                        Text(
                            if (useIndonesian) ("Ekspor akan aktif setelah sesi disimpan secara lokal dan pengguna memberi " +
                                "persetujuan.") else ("Export is " +
                                "unavailable until a cupping session is saved locally and the user grants consent."),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
