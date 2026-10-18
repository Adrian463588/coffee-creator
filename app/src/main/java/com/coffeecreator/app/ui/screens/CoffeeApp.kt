@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.coffeecreator.app.ui.screens

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.coffeecreator.app.core.ai.personalization.AcidityTolerance
import com.coffeecreator.app.core.ai.personalization.BaristaPersonaPreset
import com.coffeecreator.app.core.ai.personalization.BitternessSensitivity
import com.coffeecreator.app.core.ai.personalization.BodyPreference
import com.coffeecreator.app.core.ai.personalization.ElevationUnit
import com.coffeecreator.app.core.ai.personalization.IngredientSafetyLevel
import com.coffeecreator.app.core.ai.personalization.PalateQuizAnswers
import com.coffeecreator.app.core.ai.personalization.RoastPreference
import com.coffeecreator.app.core.ai.personalization.SensoryFeedbackDiagnosis
import com.coffeecreator.app.core.ai.personalization.SweetnessPriority
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.core.brew.ProximityState
import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.CatalogPhotoTarget
import com.coffeecreator.app.core.catalog.ConflictResolutionStrategy
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.DripperIntelligenceCatalog
import com.coffeecreator.app.core.catalog.EquipmentCategory
import com.coffeecreator.app.core.catalog.EquipmentItem
import com.coffeecreator.app.core.catalog.EvidenceStatus
import com.coffeecreator.app.core.catalog.GrinderIntelligenceCatalog
import com.coffeecreator.app.core.catalog.GrinderType
import com.coffeecreator.app.core.catalog.ProcessMethod
import com.coffeecreator.app.core.catalog.RoastLevel
import com.coffeecreator.app.core.catalog.RoastRestCalculator
import com.coffeecreator.app.core.catalog.ValveState
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.filesystem.CatalogPhotoCapture
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.recipes.champion.ChampionRecipe
import com.coffeecreator.app.core.recipes.champion.RecipeCategory
import com.coffeecreator.app.core.recipes.champion.RecipeVerificationStatus
import com.coffeecreator.app.core.recipes.champion.WorldChampionRecipeCatalog
import com.coffeecreator.app.core.reliability.AppLanguage
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.MassUnit
import com.coffeecreator.app.core.reliability.ModuleStatus
import com.coffeecreator.app.core.reliability.OfflineHelpCatalog
import com.coffeecreator.app.core.reliability.TemperatureUnit
import com.coffeecreator.app.core.reliability.VolumeUnit
import com.coffeecreator.app.core.sensory.CuppingAnalyticsEngine
import com.coffeecreator.app.core.sensory.CuppingTemplateType
import com.coffeecreator.app.core.sensory.DegassingPhase
import com.coffeecreator.app.core.sensory.GreenCoffeeDefectEntry
import com.coffeecreator.app.core.sensory.GreenDefectType
import com.coffeecreator.app.core.sensory.MaskingMode
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import com.coffeecreator.app.core.sensory.SessionStatus
import com.coffeecreator.app.ui.components.MicroBadgePill
import com.coffeecreator.app.ui.components.TactileButton
import com.coffeecreator.app.ui.components.TactileCard
import com.coffeecreator.app.ui.components.TactileFilledTonalButton
import com.coffeecreator.app.ui.components.TactileFilterChip
import com.coffeecreator.app.ui.components.TactileOutlinedButton
import com.coffeecreator.app.ui.components.TactileTextButton
import com.coffeecreator.app.ui.components.TelemetryStatusPill
import com.coffeecreator.app.ui.components.tactilePress
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme
import com.coffeecreator.app.ui.viewmodel.CoffeeUiState
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.RecipeDraft
import com.coffeecreator.app.ui.viewmodel.addBagToBean
import com.coffeecreator.app.ui.viewmodel.addDetailedBean
import com.coffeecreator.app.ui.viewmodel.addDraftStep
import com.coffeecreator.app.ui.viewmodel.addLine
import com.coffeecreator.app.ui.viewmodel.addUserGrinderProfile
import com.coffeecreator.app.ui.viewmodel.applyOvatAdjustmentToDraft
import com.coffeecreator.app.ui.viewmodel.auditIngredientSafety
import com.coffeecreator.app.ui.viewmodel.brewChampionRecipe
import com.coffeecreator.app.ui.viewmodel.cancelCatalogPhotoOcr
import com.coffeecreator.app.ui.viewmodel.cancelDeletion
import com.coffeecreator.app.ui.viewmodel.cancelGeneration
import com.coffeecreator.app.ui.viewmodel.clearComparison
import com.coffeecreator.app.ui.viewmodel.clearConflictPreview
import com.coffeecreator.app.ui.viewmodel.clearDiagnostics
import com.coffeecreator.app.ui.viewmodel.clearNotice
import com.coffeecreator.app.ui.viewmodel.clearQcReceipt
import com.coffeecreator.app.ui.viewmodel.closeCuppingSession
import com.coffeecreator.app.ui.viewmodel.confirmAiRecipe
import com.coffeecreator.app.ui.viewmodel.confirmDeletion
import com.coffeecreator.app.ui.viewmodel.confirmEquipmentOcrDraft
import com.coffeecreator.app.ui.viewmodel.confirmOcrDraft
import com.coffeecreator.app.ui.viewmodel.confirmToolInputs
import com.coffeecreator.app.ui.viewmodel.connectMockScale
import com.coffeecreator.app.ui.viewmodel.convertDraftBrix
import com.coffeecreator.app.ui.viewmodel.createCuppingSession
import com.coffeecreator.app.ui.viewmodel.deductDoseFromBag
import com.coffeecreator.app.ui.viewmodel.deleteCafeCup
import com.coffeecreator.app.ui.viewmodel.deleteDetailedBean
import com.coffeecreator.app.ui.viewmodel.deleteEquipmentItem
import com.coffeecreator.app.ui.viewmodel.deleteGrinder
import com.coffeecreator.app.ui.viewmodel.deleteJournalEntry
import com.coffeecreator.app.ui.viewmodel.deleteUserGrinderProfile
import com.coffeecreator.app.ui.viewmodel.disconnectScale
import com.coffeecreator.app.ui.viewmodel.dismissAiRecipe
import com.coffeecreator.app.ui.viewmodel.dismissEquipmentOcrDraft
import com.coffeecreator.app.ui.viewmodel.dismissOcrDraft
import com.coffeecreator.app.ui.viewmodel.dismissOvatAdjustment
import com.coffeecreator.app.ui.viewmodel.dismissToolInputs
import com.coffeecreator.app.ui.viewmodel.editRecipe
import com.coffeecreator.app.ui.viewmodel.estimateCaffeineForDose
import com.coffeecreator.app.ui.viewmodel.evaluateOvatForEntry
import com.coffeecreator.app.ui.viewmodel.executeCatalogImport
import com.coffeecreator.app.ui.viewmodel.exportBeansCsv
import com.coffeecreator.app.ui.viewmodel.exportCatalogJson
import com.coffeecreator.app.ui.viewmodel.exportDiagnosticLogs
import com.coffeecreator.app.ui.viewmodel.exportJournalCsv
import com.coffeecreator.app.ui.viewmodel.exportSessionQcReport
import com.coffeecreator.app.ui.viewmodel.feedSimulatedWeight
import com.coffeecreator.app.ui.viewmodel.finishBrewSession
import com.coffeecreator.app.ui.viewmodel.generateInventoryRecommendations
import com.coffeecreator.app.ui.viewmodel.importBeanconquerorJson
import com.coffeecreator.app.ui.viewmodel.isSupportedByNativeRuntime
import com.coffeecreator.app.ui.viewmodel.logChampionRecipeToJournal
import com.coffeecreator.app.ui.viewmodel.logCuppingSampleToJournal
import com.coffeecreator.app.ui.viewmodel.newRecipe
import com.coffeecreator.app.ui.viewmodel.parseBagOcr
import com.coffeecreator.app.ui.viewmodel.parseEquipmentOcr
import com.coffeecreator.app.ui.viewmodel.pauseBrewSession
import com.coffeecreator.app.ui.viewmodel.previewBackup
import com.coffeecreator.app.ui.viewmodel.previewCatalogImport
import com.coffeecreator.app.ui.viewmodel.proposeSingleVariableAdjustment
import com.coffeecreator.app.ui.viewmodel.recordRoastRestObservation
import com.coffeecreator.app.ui.viewmodel.refreshDiagnostics
import com.coffeecreator.app.ui.viewmodel.refreshStreakAndStats
import com.coffeecreator.app.ui.viewmodel.removeDraftStep
import com.coffeecreator.app.ui.viewmodel.removeLine
import com.coffeecreator.app.ui.viewmodel.reopenCuppingSession
import com.coffeecreator.app.ui.viewmodel.repeatLastBrew
import com.coffeecreator.app.ui.viewmodel.reportCatalogPhotoError
import com.coffeecreator.app.ui.viewmodel.requestDeletion
import com.coffeecreator.app.ui.viewmodel.resetBrewSession
import com.coffeecreator.app.ui.viewmodel.resetTimer
import com.coffeecreator.app.ui.viewmodel.restartBrewStep
import com.coffeecreator.app.ui.viewmodel.revealCuppingSample
import com.coffeecreator.app.ui.viewmodel.runManualCalculation
import com.coffeecreator.app.ui.viewmodel.saveBean
import com.coffeecreator.app.ui.viewmodel.saveCafeCup
import com.coffeecreator.app.ui.viewmodel.saveChampionRecipeAsTemplate
import com.coffeecreator.app.ui.viewmodel.saveEntryAsTemplate
import com.coffeecreator.app.ui.viewmodel.saveIngredient
import com.coffeecreator.app.ui.viewmodel.saveRecipe
import com.coffeecreator.app.ui.viewmodel.scaleDraft
import com.coffeecreator.app.ui.viewmodel.scanCatalogPhoto
import com.coffeecreator.app.ui.viewmodel.selectBaristaPersonaPreset
import com.coffeecreator.app.ui.viewmodel.selectComparisonSlot
import com.coffeecreator.app.ui.viewmodel.selectCuppingSample
import com.coffeecreator.app.ui.viewmodel.selectCuppingSession
import com.coffeecreator.app.ui.viewmodel.selectDripperModel
import com.coffeecreator.app.ui.viewmodel.selectGrinderModel
import com.coffeecreator.app.ui.viewmodel.sendChat
import com.coffeecreator.app.ui.viewmodel.setLanguage
import com.coffeecreator.app.ui.viewmodel.setUnitPreferences
import com.coffeecreator.app.ui.viewmodel.setValveState
import com.coffeecreator.app.ui.viewmodel.shareRecipe
import com.coffeecreator.app.ui.viewmodel.skipBrewStep
import com.coffeecreator.app.ui.viewmodel.startBrewSession
import com.coffeecreator.app.ui.viewmodel.submitCuppingSheet
import com.coffeecreator.app.ui.viewmodel.submitPalateQuiz
import com.coffeecreator.app.ui.viewmodel.tareScale
import com.coffeecreator.app.ui.viewmodel.toggleBeanFavorite
import com.coffeecreator.app.ui.viewmodel.toggleEquipmentFavorite
import com.coffeecreator.app.ui.viewmodel.toggleFreezeBag
import com.coffeecreator.app.ui.viewmodel.toggleGrinderFavorite
import com.coffeecreator.app.ui.viewmodel.toggleHaptics
import com.coffeecreator.app.ui.viewmodel.toggleKeepScreenOn
import com.coffeecreator.app.ui.viewmodel.toggleSound
import com.coffeecreator.app.ui.viewmodel.toggleTimer
import com.coffeecreator.app.ui.viewmodel.undoDraftChange
import com.coffeecreator.app.ui.viewmodel.updateCustomPersonaPrompt
import com.coffeecreator.app.ui.viewmodel.updateDraftStep
import com.coffeecreator.app.ui.viewmodel.updateField
import com.coffeecreator.app.ui.viewmodel.updateJournalEntryTasteNotes
import com.coffeecreator.app.ui.viewmodel.updatePalateSliders
import com.coffeecreator.app.ui.viewmodel.updateWeatherParameters
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class Destination(val route: String, val label: String, val icon: ImageVector)
private const val DECIMAL_GIGABYTE_BYTES = 1_000_000_000.0
private const val LICENSE_SUMMARY_CHARS = 120
private const val PULSE_ANIMATION_DURATION_MILLIS = 1200
private const val PULSE_INNER_RADIUS_DIVISOR = 3.2f
private const val LABEL_INPUT_HEIGHT_DP = 120
private const val HALF_RECIPE_SCALE = 0.5
private const val ONE_AND_HALF_RECIPE_SCALE = 1.5
private const val CAFFEINE_BLEND_ARABICA_FRACTION = 0.5
private const val SAFE_BREW_TEMPERATURE_MARGIN_CELSIUS = 0.5
private const val MAX_CUSTOM_PERSONA_CHARS = 300
private const val TRANSITION_FADE_IN_MILLIS = 150
private const val TRANSITION_FADE_OUT_MILLIS = 100
private const val WIDE_INPUT_WEIGHT = 1.5f
private const val TIMER_RESTART_ACTION_WEIGHT = 1.1f
private const val TIMER_FINISH_ACTION_WEIGHT = 1.4f
private const val PRIMARY_ACTION_WEIGHT = 1.2f
private const val SECONDARY_ACTION_WEIGHT = 1.1f
private const val POUR_INCREMENT_SMALL_GRAMS = 10.0
private const val POUR_INCREMENT_MEDIUM_GRAMS = 25.0
private const val POUR_INCREMENT_LARGE_GRAMS = 50.0
private const val POUR_INCREMENT_FULL_GRAMS = 100.0
private val handGrinderBadgeContainerColor = Color(0xFF8D6E63)
private val electricGrinderBadgeContainerColor = Color(0xFF3F51B5)
private val handGrinderBadgeContentColor = Color(0xFFD7CCC8)
private val electricGrinderBadgeContentColor = Color(0xFFC5CAE9)
private val verifiedEquipmentColor = Color(0xFF2E7D32)
private val pendingEquipmentColor = Color(0xFFEF6C00)
private val verifiedEquipmentContentColor = Color(0xFF81C784)
private val pendingEquipmentContentColor = Color(0xFFFFB74D)
private val filterOnlyBadgeContainerColor = Color(0xFFC62828)
private val filterOnlyBadgeContentColor = Color(0xFFEF9A9A)
private val valveBadgeContainerColor = Color(0xFF7B1FA2)
private val valveBadgeContentColor = Color(0xFFCE93D8)
private val SIMULATED_POUR_INCREMENT_GRAMS = listOf(
    POUR_INCREMENT_SMALL_GRAMS,
    POUR_INCREMENT_MEDIUM_GRAMS,
    POUR_INCREMENT_LARGE_GRAMS,
    POUR_INCREMENT_FULL_GRAMS
)
private val destinations = listOf(
    Destination("Home", "Home", Icons.Default.Home),
    Destination("Formulate", "Brew", Icons.Default.Coffee),
    Destination("AI Chat", "Chat", Icons.AutoMirrored.Filled.Chat),
    Destination("Library", "Library", Icons.AutoMirrored.Filled.LibraryBooks),
    Destination("Journal", "Journal", Icons.Default.Book)
)

@Composable
fun CoffeeApp(model: CoffeeViewModel, state: CoffeeUiState) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: "Home"
    var brewResetKey by rememberSaveable { mutableStateOf(0) }
    val navigate: (String) -> Unit = { target ->
        navController.navigate(target) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    var menu by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = route != "Home") { navigate("Home") }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val expanded = maxWidth >= 600.dp
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Column {
                        Text("Coffee Creator")
                        Text(
                            destinations.find { it.route == route }?.label ?: route,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }, actions = {
                    IconButton(onClick = { menu = true }, modifier = Modifier.testTag("nav:more")) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Open Knowledge, Models, Sensory and Settings")
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        listOf("Knowledge", "Models", "Sensory", "Settings").forEach { target ->
                            DropdownMenuItem(text = { Text(target) }, onClick = {
                                navigate(target)
                                menu = false
                            }, modifier = Modifier.testTag("nav:$target"))
                        }
                    }
                })
            },
            bottomBar = {
                if (!expanded) {
                    val haptic = LocalHapticFeedback.current
                    Column {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        )
                        NavigationBar {
                            destinations.forEach { destination ->
                                val isSelected = route == destination.route
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        navigate(destination.route)
                                    },
                                    icon = { Icon(destination.icon, contentDescription = null) },
                                    label = {
                                        Text(
                                            destination.label,
                                            maxLines = 1,
                                            softWrap = false,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("nav:${destination.route}")
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                if (expanded) {
                    val haptic = LocalHapticFeedback.current
                    NavigationRail(Modifier.fillMaxHeight()) {
                        destinations.forEach { destination ->
                            val isSelected = route == destination.route
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    navigate(destination.route)
                                },
                                icon = { Icon(destination.icon, null) },
                                label = {
                                    Text(
                                        destination.label,
                                        maxLines = 1,
                                        softWrap = false,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("nav:${destination.route}")
                            )
                        }
                    }
                }
                Column(Modifier.weight(1f)) {
                    if (state.busy) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text(state.progress, Modifier.padding(horizontal = 16.dp).semantics { liveRegion = LiveRegionMode.Polite })
                    }
                    AnimatedVisibility(
                        visible = state.error != null || state.notice != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.error != null) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    if (state.error != null) Icons.Default.Info else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (state.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    state.error ?: state.notice.orEmpty(),
                                    Modifier.weight(1f).semantics {
                                        liveRegion = LiveRegionMode.Polite
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                TactileTextButton(onClick = model::clearNotice) { Text("Dismiss", maxLines = 1, softWrap = false) }
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = route == "Formulate" && state.canUndoDraft,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        TactileTextButton(
                            onClick = model::undoDraftChange,
                            enabled = !state.busy,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) { Text("Undo draft change", maxLines = 1, softWrap = false) }
                    }
                    NavHost(navController, startDestination = "Home", modifier = Modifier.weight(1f)) {
                        composable("Home") {
                            Home(state, model) { target ->
                                if (target == "Formulate") brewResetKey++
                                navigate(target)
                            }
                        }
                        composable("Formulate") { Formulate(state, model, brewResetKey) }
                        composable("Library") { Library(state, model) }
                        composable("Journal") {
                            Journal(state, model) { recipe ->
                                model.editRecipe(recipe)
                                brewResetKey++
                                navigate("Formulate")
                            }
                        }
                        composable("Sensory") { Sensory(state, model) }
                        composable("Knowledge") { Knowledge(state, model) }
                        composable("Models") { Models(state, model) }
                        composable("AI Chat") { Chat(state, model) }
                        composable("Settings") { Settings(state, model) }
                    }
                }
            }
        }
    }
    state.deletion?.let { deletion ->
        AlertDialog(
            onDismissRequest = model::cancelDeletion,
            title = { Text("Delete ${deletion.first}?") },
            text = {
                Text(
                    if (deletion.first ==
                        "all application records"
                    ) {
                        "Delete catalog, recipes, observations, chat and imported source records from this app? " +
                            "Model files and exported backups remain in your directories. Export current records first " +
                            "if needed."
                    } else {
                        "Delete this record and associated local data? Export a backup first if you need recovery."
                    }
                )
            },
            confirmButton = {
                TactileTextButton(onClick = model::confirmDeletion, enabled = !state.busy) {
                    Text("Delete", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = model::cancelDeletion) {
                    Text("Keep record", maxLines = 1, softWrap = false)
                }
            }
        )
    }
    state.inspectedChunk?.let { chunk ->
        val context = LocalContext.current
        AlertDialog(onDismissRequest = { model.inspectChunk(null) }, title = { Text(chunk.citationText) }, text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Imported source excerpt; document instructions are untrusted data.", style = MaterialTheme.typography.labelMedium)
                Text(chunk.content)
                state.sourceOpenError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }, confirmButton = {
            TactileTextButton(onClick = { model.openOriginal(context) }) { Text("Open original", maxLines = 1, softWrap = false) }
        }, dismissButton = {
            TactileTextButton(onClick = { model.inspectChunk(null) }) { Text("Close", maxLines = 1, softWrap = false) }
        })
    }
}

@Composable
private fun Page(tag: String, modifier: Modifier = Modifier, content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("page:$tag"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        content()
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun Heading(title: String, detail: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InfoCard(title: String, detail: String, content: @Composable () -> Unit = {}) {
    TactileCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, Modifier.semantics { heading() }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (detail.isNotBlank()) {
                Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            content()
        }
    }
}

@Composable
private fun LivePulseIndicator(isActive: Boolean, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    if (isActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(PULSE_ANIMATION_DURATION_MILLIS, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulseScale"
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(PULSE_ANIMATION_DURATION_MILLIS, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulseAlpha"
        )
        Box(
            modifier = modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = color.copy(alpha = pulseAlpha),
                    radius = (size.minDimension / 2f) * pulseScale
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension / PULSE_INNER_RADIUS_DIVISOR
                )
            }
        }
    } else {
        Box(
            modifier = modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = color.copy(alpha = 0.35f),
                    radius = size.minDimension / PULSE_INNER_RADIUS_DIVISOR
                )
            }
        }
    }
}

@Composable
private fun GrindRangeTile(
    label: String,
    range: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false,
    isVerified: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isWarning) {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = BorderStroke(
            1.dp,
            if (isWarning) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.30f)
            } else if (isVerified) {
                verifiedEquipmentColor.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            }
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                if (isVerified) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = verifiedEquipmentColor
                    )
                }
            }
            Text(
                text = range,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum"
                ),
                color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InteractiveValveSwitch(isOpen: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isOpen) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                border = if (isOpen) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)) else null,
                modifier = Modifier
                    .weight(1f)
                    .tactilePress(onClick = { if (!isOpen) onToggle(true) })
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OPEN (Percolation)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isOpen) FontWeight.Bold else FontWeight.Normal,
                        color = if (isOpen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (!isOpen) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                border = if (!isOpen) BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.40f)) else null,
                modifier = Modifier
                    .weight(1f)
                    .tactilePress(onClick = { if (isOpen) onToggle(false) })
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLOSED (Immersion)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (!isOpen) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isOpen) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit, tag: String, numeric: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        shape = RoundedCornerShape(8.dp),
        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
            fontFeatureSettings = if (numeric) "tnum" else ""
        ),
        modifier = Modifier.fillMaxWidth().testTag(tag),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text)
    )
}

@Composable
private fun Select(label: String, selected: String, choices: List<Pair<String, String>>, onSelect: (String) -> Unit, tag: String) {
    val haptic = LocalHapticFeedback.current
    var open by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (open) 180f else 0f,
        animationSpec = tween(150),
        label = "dropdownArrow"
    )
    Column {
        TactileOutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(tag),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$label: ${choices.find { it.first == selected }?.second ?: selected.ifBlank { "Choose" }}",
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            choices.forEach { (id, text) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (id == selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (id == selected) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    } else {
                        null
                    },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSelect(id)
                        open = false
                    }
                )
            }
        }
    }
}

@Composable
private fun Home(state: CoffeeUiState, model: CoffeeViewModel, navigate: (String) -> Unit) = Page("Home") {
    item {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Coffee, contentDescription = null)
                    Text("YOUR COFFEE WORKBENCH", style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    "Make every brew repeatable",
                    Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Plan a recipe, measure your brew, then keep what you learned.", style = MaterialTheme.typography.bodyLarge)
                TactileButton(
                    onClick = {
                        model.newRecipe()
                        navigate("Formulate")
                    },
                    enabled = state.ready && !state.busy,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("home:new")
                ) { Text("New recipe", maxLines = 1, softWrap = false) }
            }
        }
    }
    if (!state.onboarded) {
        item {
            InfoCard(
                "Offline by design",
                "Manual tools work without an AI model. Documents and chat stay on this device. Only a " +
                    "download you request uses the network."
            ) {
                TactileButton(onClick = model::finishOnboarding, enabled = !state.busy) {
                    Text("Continue offline", maxLines = 1, softWrap = false)
                }
            }
        }
    }
    item {
        InfoCard(
            "Ready for your next brew",
            "${state.recipes.size} saved revisions · ${state.documents.size} imported sources"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(if (state.ready) "Local records ready" else "Opening local records", style = MaterialTheme.typography.bodyMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        state.loadedModel?.let { "Local AI loaded: $it" } ?: if (state.ready) {
                            "Manual mode ready · AI model optional"
                        } else {
                            "AI model optional · opening local records"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "WORKBENCH MODULES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            val navItems = listOf(
                Triple("AI Chat", "Offline coffee intelligence", Icons.AutoMirrored.Filled.Chat),
                Triple("Knowledge", "Imported guides & RAG", Icons.AutoMirrored.Filled.LibraryBooks),
                Triple("Models", "Local GGUF / ONNX assets", Icons.Default.Memory),
                Triple("Sensory", "Tasting radar & observations", Icons.Default.Psychology),
                Triple("Settings", "Storage, export & reset", Icons.Default.Settings)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                navItems.forEach { (target, subtitle, icon) ->
                    Surface(
                        onClick = { navigate(target) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .tactilePress(onClick = { navigate(target) })
                            .testTag("home:nav:$target")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(Modifier.weight(1f)) {
                                Text(
                                    target,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (state.recipes.isEmpty()) {
        item {
            InfoCard("No brews recorded", "Create a recipe and enter measurements only when you have measured them.")
        }
    }
    items(state.recipes.take(3), key = { it.id }) { recipe ->
        InfoCard(
            "${recipe.name} · v${recipe.version}",
            "${recipe.doseGrams} g coffee / ${recipe.waterGrams} g water · ${recipe.brewMethod}"
        ) {
            TactileTextButton(onClick = {
                model.editRecipe(recipe)
                navigate("Formulate")
            }) { Text("Repeat / edit", maxLines = 1, softWrap = false) }
        }
    }
}

@Composable
private fun Formulate(state: CoffeeUiState, model: CoffeeViewModel, resetKey: Int) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val fields = state.draft.fields
    var selectedSection by rememberSaveable(resetKey) { mutableStateOf(BrewSection.Recipe.name) }
    val section = BrewSection.valueOf(selectedSection)
    var ingredientId by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf("g") }
    var density by rememberSaveable { mutableStateOf("") }
    var science by rememberSaveable { mutableStateOf("Dilution") }
    var scienceInputs by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compactHeight = 480.dp
        val largeFontScale = 1.5f
        val scrollSections = maxHeight < compactHeight || LocalDensity.current.fontScale >= largeFontScale
        Column(Modifier.fillMaxSize()) {
            if (!scrollSections) {
                BrewSectionTabs(section, Modifier.padding(horizontal = 16.dp)) { selectedSection = it.name }
            }
            Box(Modifier.weight(1f).testTag("brew:content:${section.name}")) {
                androidx.compose.runtime.key(section) {
                    Page("Formulate") {
                        if (scrollSections) {
                            item(key = "brew:sections") { BrewSectionTabs(section) { selectedSection = it.name } }
                        }
                        when (section) {
                            BrewSection.Recipe -> {
                                item {
                                    BrewCard("Build your recipe", "Planned inputs. Each save keeps an immutable revision.") {
                                        Field("Recipe name", fields.getValue("name"), { model.updateField("name", it) }, "form:name")
                                        Select(
                                            "Coffee bean",
                                            fields.getValue("bean"),
                                            listOf("" to "Unspecified") + state.beans.map { it.id to it.name },
                                            { model.updateField("bean", it) },
                                            "form:bean"
                                        )
                                        var customBeanText by rememberSaveable { mutableStateOf("") }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = customBeanText,
                                                onValueChange = { customBeanText = it },
                                                label = { Text("Custom coffee bean") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f).testTag("form:custom_bean")
                                            )
                                            TactileButton(
                                                onClick = {
                                                    val name = customBeanText.trim()
                                                    if (name.isNotBlank()) {
                                                        val existing = state.beans.find { it.name.equals(name, ignoreCase = true) }
                                                        if (existing != null) {
                                                            model.updateField("bean", existing.id)
                                                        } else {
                                                            val id = java.util.UUID.randomUUID().toString()
                                                            model.saveBean(
                                                                id,
                                                                mapOf(
                                                                    "name" to name,
                                                                    "variety" to "Custom",
                                                                    "provenance" to "Custom user lot"
                                                                )
                                                            )
                                                            model.updateField("bean", id)
                                                        }
                                                    }
                                                },
                                                enabled = customBeanText.isNotBlank() && !state.busy,
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("form:add_custom_bean")
                                            ) {
                                                Text("Apply bean", maxLines = 1, softWrap = false)
                                            }
                                        }
                                        Select(
                                            "Brew method preset",
                                            fields.getValue("method"),
                                            RecipeDraft.standardMethods().map { it to it },
                                            { model.updateField("method", it) },
                                            "form:method"
                                        )
                                        if (fields.getValue("method") == "Custom") {
                                            Field(
                                                "Custom brew apparatus name",
                                                fields.getValue("customMethod"),
                                                { model.updateField("customMethod", it) },
                                                "form:custom_method"
                                            )
                                        } else {
                                            Field(
                                                "Brew method (or custom apparatus)",
                                                fields.getValue("method"),
                                                { model.updateField("method", it) },
                                                "form:custom_method"
                                            )
                                        }
                                        Field("Dose (g)", fields.getValue("dose"), { model.updateField("dose", it) }, "form:dose", true)
                                        Field("Brew water (g)", fields.getValue("water"), {
                                            model.updateField("water", it)
                                        }, "form:water", true)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Scale:", style = MaterialTheme.typography.labelMedium)
                                            TactileOutlinedButton(
                                                onClick = { model.scaleDraft(HALF_RECIPE_SCALE) },
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("form:scale:0.5")
                                            ) { Text("0.5x", maxLines = 1, softWrap = false) }
                                            TactileOutlinedButton(
                                                onClick = { model.scaleDraft(ONE_AND_HALF_RECIPE_SCALE) },
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("form:scale:1.5")
                                            ) { Text("1.5x", maxLines = 1, softWrap = false) }
                                            TactileOutlinedButton(
                                                onClick = { model.scaleDraft(2.0) },
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("form:scale:2.0")
                                            ) { Text("2.0x", maxLines = 1, softWrap = false) }
                                        }
                                    }
                                }
                                item {
                                    BrewCard("Brew timer", "%02d:%02d".format(state.timerSeconds / 60, state.timerSeconds % 60)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            TactileButton(
                                                onClick = model::toggleTimer,
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("recipe:toggle_timer")
                                            ) {
                                                Text(
                                                    text = if (state.timerRunning) "Pause timer" else "Start timer",
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                            TactileOutlinedButton(
                                                onClick = model::resetTimer,
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("recipe:reset_timer")
                                            ) {
                                                Text(
                                                    text = "Reset timer",
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    BrewCard("Equipment & notes", "Record the setup so another brew can repeat it.") {
                                        Select(
                                            "Grinder Model (44 Catalog)",
                                            state.selectedGrinderModelId ?: "",
                                            listOf("" to "Manual / Custom Grinder") + state.grinderCatalog.map { it.id to it.modelName },
                                            { selectedId ->
                                                if (selectedId.isNotBlank()) {
                                                    model.selectGrinderModel(selectedId, fields.getValue("method"))
                                                    val grinder = state.grinderCatalog.find { it.id == selectedId }
                                                    if (grinder != null) {
                                                        val guidance = GrinderIntelligenceCatalog.getStartingRange(
                                                            grinder.id,
                                                            fields.getValue("method")
                                                        )
                                                        model.updateField("equipment", grinder.modelName)
                                                        model.updateField(
                                                            "grind",
                                                            "${guidance.recommendedRange} (${grinder.adjustmentMechanism})"
                                                        )
                                                    }
                                                }
                                            },
                                            "form:select_grinder_model"
                                        )
                                        Select(
                                            "Dripper / Brewer (26 Catalog)",
                                            state.selectedDripperModelId ?: "",
                                            listOf("" to "Manual / Custom Brewer") + state.dripperCatalog.map { it.id to it.name },
                                            { selectedId ->
                                                if (selectedId.isNotBlank()) {
                                                    model.selectDripperModel(selectedId)
                                                    val dripper = state.dripperCatalog.find { it.id == selectedId }
                                                    if (dripper != null) {
                                                        val existing = fields.getValue("equipment")
                                                        val newEquip = if (existing.isNotBlank() && !existing.contains(dripper.name)) {
                                                            "$existing + ${dripper.name}"
                                                        } else {
                                                            dripper.name
                                                        }
                                                        model.updateField("equipment", newEquip)
                                                    }
                                                }
                                            },
                                            "form:select_dripper_model"
                                        )
                                        state.currentDripperFlowAdvice?.let { advice ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        MicroBadgePill(
                                                            text = advice.flowClass.name,
                                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                            contentColor = MaterialTheme.colorScheme.primary,
                                                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
                                                        )
                                                        MicroBadgePill(
                                                            text = "Drain: ${advice.qualitativeDrainSpeed}",
                                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f),
                                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Text(
                                                        text = advice.relativeGrindAdjustmentNote,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    advice.valveNotice?.let { vn ->
                                                        Text(
                                                            text = "💡 $vn",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.tertiary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Field("Equipment", fields.getValue("equipment"), {
                                            model.updateField("equipment", it)
                                        }, "form:equipment")
                                        Field("Grind description", fields.getValue("grind"), {
                                            model.updateField("grind", it)
                                        }, "form:grind")
                                        Field("Recipe notes", fields.getValue("notes"), { model.updateField("notes", it) }, "form:notes")
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            TactileOutlinedButton(
                                                onClick = {
                                                    val r = runCatching { state.draft.build(state.ingredients) }.getOrNull()
                                                    if (r != null) model.shareRecipe(context, r, formatJson = false)
                                                },
                                                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:share_card")
                                            ) { Text("Share Card", maxLines = 1, softWrap = false) }
                                            TactileOutlinedButton(
                                                onClick = {
                                                    val r = runCatching { state.draft.build(state.ingredients) }.getOrNull()
                                                    if (r != null) model.shareRecipe(context, r, formatJson = true)
                                                },
                                                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:share_json")
                                            ) { Text("Share JSON", maxLines = 1, softWrap = false) }
                                        }
                                    }
                                }
                                item {
                                    TactileTextButton(
                                        onClick = model::newRecipe,
                                        modifier = Modifier.heightIn(min = 48.dp).testTag("form:new")
                                    ) {
                                        Text("Start a separate recipe", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                            BrewSection.Measurements -> {
                                item {
                                    BrewCard(
                                        "Measured beverage",
                                        "Optional. Leave blank until an instrument or scale supplies the value."
                                    ) {
                                        Field("Measured beverage mass (g)", fields.getValue("beverage"), {
                                            model.updateField("beverage", it)
                                        }, "form:beverage", true)
                                        Field("Measured TDS (% w/w)", fields.getValue("tds"), {
                                            model.updateField("tds", it)
                                        }, "form:tds", true)
                                    }
                                }
                                item {
                                    BrewCard(
                                        "Optical refractometer lab (Brix → TDS)",
                                        "Convert optical refractive index (°Bx) using documented instrument calibration profiles."
                                    ) {
                                        Field(
                                            "Measured Brix (%)",
                                            fields.getValue("brix"),
                                            { model.updateField("brix", it) },
                                            "form:brix",
                                            true
                                        )
                                        Select(
                                            "Calibration profile",
                                            fields.getValue("brixProfile"),
                                            listOf(
                                                "Standard Coffee (0.85)",
                                                "Atago PAL-COFFEE (0.84)",
                                                "VST LAB Coffee III (0.857)",
                                                "Generic Sucrose Equivalent (0.85)"
                                            ).map { it to it },
                                            { model.updateField("brixProfile", it) },
                                            "form:brix_profile"
                                        )
                                        TactileButton(
                                            onClick = model::convertDraftBrix,
                                            enabled = fields.getValue("brix").isNotBlank() && !state.busy,
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("form:convert_brix")
                                        ) {
                                            Text("Convert Brix → TDS & Apply", maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                                item { InfoCard("Calculated science · not a measurement", state.calculation) }
                                item {
                                    BrewCard(
                                        "Process inputs",
                                        "Describe the brew. Estimated extraction retains method-specific assumptions."
                                    ) {
                                        Field("Brew temperature (°C)", fields.getValue("temperature"), {
                                            model.updateField("temperature", it)
                                        }, "form:temperature", true)
                                        Field("Pressure (bar), optional", fields.getValue("pressure"), {
                                            model.updateField("pressure", it)
                                        }, "form:pressure", true)
                                        Field("Brew time (seconds)", fields.getValue("seconds"), {
                                            model.updateField("seconds", it)
                                        }, "form:seconds", true)
                                        Field("Bypass water (g)", fields.getValue("bypass"), {
                                            model.updateField("bypass", it)
                                        }, "form:bypass", true)
                                        Field("Agitation / pour routine", fields.getValue("agitation"), {
                                            model.updateField("agitation", it)
                                        }, "form:agitation")
                                    }
                                }
                            }
                            BrewSection.Ingredients -> {
                                item {
                                    BrewCard(
                                        "Ingredient additions",
                                        "Only confirmed food-grade ingredients. Known density is required for mL."
                                    ) {
                                        Select("Ingredient", ingredientId, state.ingredients.map { it.id to it.name }, {
                                            ingredientId = it
                                        }, "form:ingredient")
                                        Field("Ingredient quantity", quantity, { quantity = it }, "form:quantity", true)
                                        Select("Unit", unit, listOf("g", "mg", "mL").map { it to it }, { unit = it }, "form:unit")
                                        Field("Density (g/mL), optional override", density, { density = it }, "form:density", true)
                                        TactileOutlinedButton(
                                            onClick = { model.addLine(ingredientId, quantity, unit, density) },
                                            enabled = ingredientId.isNotBlank() && !state.busy,
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        ) { Text("Add ingredient", maxLines = 1, softWrap = false) }
                                    }
                                }
                                if (state.draft.lines.isEmpty()) {
                                    item {
                                        InfoCard(
                                            "No additions",
                                            "Coffee and brew water are recorded in Recipe. Ingredient additions are optional."
                                        )
                                    }
                                }
                                items(state.draft.lines.withIndex().toList(), key = { it.index }) { indexed ->
                                    val line = indexed.value
                                    InfoCard(
                                        state.ingredients.find { it.id == line.ingredientId }?.name ?: "Unknown ingredient",
                                        "${line.quantity} ${line.unit}; food-grade and supplier limits checked on save"
                                    ) {
                                        TactileTextButton(
                                            onClick = { model.removeLine(indexed.index) },
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        ) {
                                            Text("Remove line", maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                                if (state.draft.lines.isNotEmpty()) {
                                    item {
                                        val allergens = state.draft.lines.flatMap { line ->
                                            state.ingredients.find { it.id == line.ingredientId }?.allergens.orEmpty()
                                        }.distinct().joinToString().ifBlank { "none declared; verify supplier" }
                                        TactileFilterChip(
                                            selected = fields["allergens"] == "yes",
                                            onClick = { model.updateField("allergens", if (fields["allergens"] == "yes") "" else "yes") },
                                            label = { Text("Acknowledge listed ingredient allergens: $allergens") },
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        )
                                    }
                                }
                            }
                            BrewSection.Tools -> {
                                item {
                                    BrewCard(
                                        "Coffee science tools",
                                        "Calculated estimates from explicit inputs. No measured values are filled in."
                                    ) {
                                        Select(
                                            "Calculation",
                                            science,
                                            listOf("Dilution", "Concentration", "Volume to mass").map { it to it },
                                            {
                                                science = it
                                                scienceInputs = emptyMap()
                                            },
                                            "science:kind"
                                        )
                                        val scienceFields = when (science) {
                                            "Dilution" -> listOf(
                                                "initialMassGrams" to "Initial beverage mass (g)",
                                                "initialTdsPercent" to "Measured initial TDS (% w/w)",
                                                "addedWaterGrams" to "Added water (g)"
                                            )
                                            "Concentration" -> listOf(
                                                "soluteMassGrams" to "Solute mass (g)",
                                                "solutionMassGrams" to "Total solution mass, including solute (g)"
                                            )
                                            else -> listOf("volumeMl" to "Volume (mL)", "densityGramsPerMl" to "Known density (g/mL)")
                                        }
                                        scienceFields.forEach { (key, label) ->
                                            Field(label, scienceInputs[key].orEmpty(), {
                                                scienceInputs = scienceInputs + (key to it)
                                            }, "science:$key", true)
                                        }
                                        TactileButton(onClick = {
                                            val tool = when (science) {
                                                "Dilution" -> "calculate_dilution"
                                                "Concentration" -> "calculate_concentration_ppm"
                                                else -> "convert_units"
                                            }
                                            model.runManualCalculation(
                                                tool,
                                                scienceFields.associate { it.first to scienceInputs[it.first].orEmpty() }
                                            )
                                        }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp)) {
                                            Text("Calculate ${science.lowercase()}", maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                                state.manualToolResult?.let { result -> item { InfoCard("Exact calculation", result) } }
                            }
                            BrewSection.Steps -> {
                                item {
                                    BrewCard("Ordered pour steps", "Action, timing, pour water, temperature and valve state.") {
                                        TactileButton(
                                            onClick = model::addDraftStep,
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("steps:add")
                                        ) {
                                            Text("+ Add pour step", maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                                if (state.draft.steps.isEmpty()) {
                                    item {
                                        InfoCard(
                                            "No custom steps defined",
                                            "Tap '+ Add pour step' to build a structured multi-pour routine or open a saved recipe."
                                        )
                                    }
                                }
                                items(state.draft.steps.withIndex().toList(), key = { it.value.stepNumber }) { (index, step) ->
                                    InfoCard(
                                        "Step ${step.stepNumber}: ${step.action} (+${step.targetWaterGrams} g)",
                                        "Cumulative: ${step.cumulativeMassGrams} g · ${step.durationSeconds} s" +
                                            (if (step.temperatureCelsius != null) " · ${step.temperatureCelsius} °C" else "") +
                                            (if (!step.valveStatus.isNullOrBlank()) " · Valve: ${step.valveStatus}" else "")
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Select(
                                                "Action",
                                                step.action,
                                                listOf("Bloom", "Pour", "Wait", "Stir", "Press", "Drawdown", "Valve").map { it to it },
                                                { model.updateDraftStep(index, step.copy(action = it)) },
                                                "step:action:$index"
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedTextField(
                                                    value = step.targetWaterGrams.toString(),
                                                    onValueChange = {
                                                        val w = it.toDoubleOrNull()
                                                        if (w != null && w >= 0) {
                                                            model.updateDraftStep(
                                                                index,
                                                                step.copy(targetWaterGrams = w)
                                                            )
                                                        }
                                                    },
                                                    label = { Text("Water (g)") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                    modifier = Modifier.weight(1f).testTag("step:water:$index")
                                                )
                                                OutlinedTextField(
                                                    value = step.durationSeconds.toString(),
                                                    onValueChange = {
                                                        val s = it.toIntOrNull()
                                                        if (s != null && s > 0) model.updateDraftStep(index, step.copy(durationSeconds = s))
                                                    },
                                                    label = { Text("Sec") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier.weight(1f).testTag("step:duration:$index")
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Select(
                                                    "Valve",
                                                    step.valveStatus.orEmpty(),
                                                    listOf("" to "N/A", "Open" to "Open", "Closed" to "Closed", "Half" to "Half"),
                                                    { model.updateDraftStep(index, step.copy(valveStatus = it.ifBlank { null })) },
                                                    "step:valve:$index"
                                                )
                                            }
                                            OutlinedTextField(
                                                value = step.description,
                                                onValueChange = { model.updateDraftStep(index, step.copy(description = it)) },
                                                label = { Text("Description") },
                                                modifier = Modifier.fillMaxWidth().testTag("step:desc:$index")
                                            )
                                            TactileTextButton(
                                                onClick = { model.removeDraftStep(index) },
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("step:remove:$index")
                                            ) {
                                                Text("Remove step", maxLines = 1, softWrap = false)
                                            }
                                        }
                                    }
                                }
                            }
                            BrewSection.Timer -> {
                                item {
                                    val session = state.brewSession
                                    val elapsedSec = state.timerSeconds
                                    val elapsedFormatted = "%02d:%02d".format(elapsedSec / 60, elapsedSec % 60)
                                    val currentProgress = session?.stepProgress

                                    BrewCard(
                                        "Active brew chronometer & scale",
                                        "Real-time countdown, step-by-step guidance, BLE scale streaming, and live flow rate."
                                    ) {
                                        // Unified Precision Laboratory Console
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("timer:display"),
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            shape = RoundedCornerShape(16.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                // Header Bar: Live Pulse + State + Scale Connection Status
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        LivePulseIndicator(isActive = state.timerRunning)
                                                        Text(
                                                            text = if (state.timerRunning) {
                                                                "ACTIVE BREWING"
                                                            } else if (session?.status == BrewExecutionStatus.PAUSED) {
                                                                "PAUSED"
                                                            } else if (session?.status == BrewExecutionStatus.COMPLETED) {
                                                                "COMPLETED"
                                                            } else {
                                                                "READY / ARMED"
                                                            },
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (state.timerRunning) {
                                                                MaterialTheme.colorScheme.primary
                                                            } else {
                                                                MaterialTheme.colorScheme.onSurfaceVariant
                                                            }
                                                        )
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (session?.scaleConnectionState == ScaleConnectionState.READY) {
                                                            MaterialTheme.colorScheme.primaryContainer
                                                        } else {
                                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                                        }
                                                    ) {
                                                        Text(
                                                            text = session?.scaleDevice?.name
                                                                ?: session?.scaleConnectionState?.name
                                                                ?: "DISCONNECTED",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Medium,
                                                            color = if (session?.scaleConnectionState == ScaleConnectionState.READY) {
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            } else {
                                                                MaterialTheme.colorScheme.outline
                                                            },
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }

                                                // Primary Telemetry Cluster: Elapsed Time & Live Mass/Flow
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = elapsedFormatted,
                                                        style = MaterialTheme.typography.displayMedium.copy(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFeatureSettings = "tnum"
                                                        ),
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.semantics {
                                                            liveRegion = LiveRegionMode.Polite
                                                        }
                                                    )
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Text(
                                                            "CURRENT MASS",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                        Text(
                                                            text = "${"%.1f".format(
                                                                java.util.Locale.US,
                                                                session?.currentWeightGrams ?: 0.0
                                                            )} g",
                                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.Bold,
                                                                fontFeatureSettings = "tnum"
                                                            ),
                                                            maxLines = 1,
                                                            softWrap = false
                                                        )
                                                        Text(
                                                            text = "${"%.2f".format(
                                                                java.util.Locale.US,
                                                                session?.currentFlowRateGps ?: 0.0
                                                            )} g/s",
                                                            style = MaterialTheme.typography.titleMedium.copy(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontFeatureSettings = "tnum"
                                                            ),
                                                            maxLines = 1,
                                                            softWrap = false,
                                                            color = MaterialTheme.colorScheme.secondary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Step Progression Card
                                        if (currentProgress != null) {
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().testTag("timer:step_card"),
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "Step ${currentProgress.stepIndex + 1} of " +
                                                                "${currentProgress.totalSteps}: ${currentProgress.currentStep.action}",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        Text(
                                                            text = "${currentProgress.stepRemainingSeconds}s remaining",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    if (currentProgress.currentStep.description.isNotBlank()) {
                                                        Text(
                                                            text = currentProgress.currentStep.description,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("Step Target: +${currentProgress.stepTargetWaterGrams}g")
                                                        Text("Cumulative: ${currentProgress.cumulativeTargetWaterGrams}g")
                                                    }
                                                    if (currentProgress.nextStep != null) {
                                                        Text(
                                                            text = "Next: ${currentProgress.nextStep.action} " +
                                                                "(+${currentProgress.nextStep.targetWaterGrams}g in " +
                                                                "${currentProgress.nextStep.durationSeconds}s)",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    }
                                                }
                                            }

                                            val prox = currentProgress.proximityState
                                            val remainingGrams = "%.1f".format(
                                                java.util.Locale.US,
                                                currentProgress.remainingToCumulativeTargetGrams
                                            )
                                            val targetGrams = "%.1f".format(
                                                java.util.Locale.US,
                                                currentProgress.cumulativeTargetWaterGrams
                                            )
                                            val overshootGrams = "%.1f".format(
                                                java.util.Locale.US,
                                                (session?.currentWeightGrams ?: 0.0) -
                                                    currentProgress.cumulativeTargetWaterGrams
                                            )
                                            val (proxText, proxColor) = when (prox) {
                                                ProximityState.NORMAL ->
                                                    "Pouring: ${remainingGrams}g remaining" to
                                                        MaterialTheme.colorScheme.onSurface
                                                ProximityState.APPROACHING_10G ->
                                                    "Within 10g (${remainingGrams}g left)" to
                                                        MaterialTheme.colorScheme.tertiary
                                                ProximityState.IMMINENT_2G -> "Within 2g! Prepare cutoff" to MaterialTheme.colorScheme.error
                                                ProximityState.TARGET_MATCHED ->
                                                    "Target Matched (${targetGrams}g)" to
                                                        MaterialTheme.colorScheme.primary
                                                ProximityState.OVERSHOOT ->
                                                    "Target Overshoot (+${overshootGrams}g)" to
                                                        MaterialTheme.colorScheme.error
                                            }
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().testTag("timer:proximity_badge"),
                                                color = proxColor.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, proxColor)
                                            ) {
                                                Text(
                                                    text = proxText,
                                                    modifier = Modifier.padding(8.dp),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = proxColor
                                                )
                                            }
                                        }

                                        // BLE Scale Card
                                        Surface(
                                            modifier = Modifier.fillMaxWidth().testTag("timer:scale_card"),
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    if (session?.scaleDevice == null) {
                                                        TactileFilledTonalButton(
                                                            onClick = model::connectMockScale,
                                                            modifier = Modifier.weight(
                                                                1f
                                                            ).heightIn(min = 48.dp).testTag("timer:connect_scale")
                                                        ) {
                                                            Text("Connect Scale", maxLines = 1, softWrap = false)
                                                        }
                                                    } else {
                                                        TactileOutlinedButton(
                                                            onClick = model::disconnectScale,
                                                            modifier = Modifier.weight(
                                                                1f
                                                            ).heightIn(min = 48.dp).testTag("timer:disconnect_scale")
                                                        ) {
                                                            Text("Disconnect", maxLines = 1, softWrap = false)
                                                        }
                                                    }
                                                    TactileButton(
                                                        onClick = model::tareScale,
                                                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("timer:tare_scale")
                                                    ) {
                                                        Text("Tare (0.0g)", maxLines = 1, softWrap = false)
                                                    }
                                                }

                                                if (session?.scaleDevice != null) {
                                                    Text("Simulate Pouring Water:", style = MaterialTheme.typography.labelSmall)
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        SIMULATED_POUR_INCREMENT_GRAMS.forEach { delta ->
                                                            TactileOutlinedButton(
                                                                onClick = {
                                                                    model.feedSimulatedWeight(
                                                                        (session.currentWeightGrams + delta)
                                                                    )
                                                                },
                                                                modifier = Modifier.weight(
                                                                    1f
                                                                ).heightIn(min = 48.dp).testTag("timer:sim_pour_${delta.toInt()}")
                                                            ) {
                                                                Text("+${delta.toInt()}g", maxLines = 1, softWrap = false)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Ergonomic Primary CTA (Start / Pause / Resume)
                                        TactileButton(
                                            onClick = {
                                                if (state.timerRunning) model.pauseBrewSession() else model.startBrewSession()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(54.dp)
                                                .testTag("timer:toggle"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (state.timerRunning) {
                                                    MaterialTheme.colorScheme.tertiary
                                                } else {
                                                    MaterialTheme.colorScheme.primary
                                                }
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (state.timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = if (state.timerRunning) {
                                                    "Pause Session"
                                                } else if (session?.status == BrewExecutionStatus.PAUSED) {
                                                    "Resume Session"
                                                } else {
                                                    "Start Session"
                                                },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }

                                        // Secondary Segmented Control Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            TactileOutlinedButton(
                                                onClick = model::skipBrewStep,
                                                enabled = session != null && session.status != BrewExecutionStatus.COMPLETED,
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("timer:skip_step")
                                            ) {
                                                Text("Skip", style = MaterialTheme.typography.labelSmall, maxLines = 1, softWrap = false)
                                            }
                                            TactileOutlinedButton(
                                                onClick = model::restartBrewStep,
                                                enabled = session != null,
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier
                                                    .weight(TIMER_RESTART_ACTION_WEIGHT)
                                                    .heightIn(min = 48.dp)
                                                    .testTag("timer:restart_step")
                                            ) {
                                                Text("Restart", style = MaterialTheme.typography.labelSmall, maxLines = 1, softWrap = false)
                                            }
                                            TactileOutlinedButton(
                                                onClick = model::resetBrewSession,
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("timer:reset")
                                            ) {
                                                Text("Reset", style = MaterialTheme.typography.labelSmall, maxLines = 1, softWrap = false)
                                            }
                                            TactileFilledTonalButton(
                                                onClick = model::finishBrewSession,
                                                enabled = session != null && (state.timerSeconds > 0 || session.currentWeightGrams > 0.0),
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier
                                                    .weight(TIMER_FINISH_ACTION_WEIGHT)
                                                    .heightIn(min = 48.dp)
                                                    .testTag("timer:finish_and_log")
                                            ) {
                                                Text(
                                                    "Finish & Log",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TactileFilterChip(
                                                selected = session?.keepScreenOn ?: true,
                                                onClick = model::toggleKeepScreenOn,
                                                label = { Text("Screen Awake", maxLines = 1, softWrap = false) },
                                                modifier = Modifier.testTag("timer:toggle_awake")
                                            )
                                            TactileFilterChip(
                                                selected = session?.audioEnabled ?: true,
                                                onClick = model::toggleSound,
                                                label = { Text("Audio", maxLines = 1, softWrap = false) },
                                                modifier = Modifier.testTag("timer:toggle_sound")
                                            )
                                            TactileFilterChip(
                                                selected = session?.hapticsEnabled ?: true,
                                                onClick = model::toggleHaptics,
                                                label = { Text("Haptics", maxLines = 1, softWrap = false) },
                                                modifier = Modifier.testTag("timer:toggle_haptics")
                                            )
                                        }

                                        if (state.brewCurveRmse != null) {
                                            Text(
                                                text = "Extraction Curve RMSE: ${"%.2f".format(
                                                    java.util.Locale.US,
                                                    state.brewCurveRmse
                                                )}g deviation from planned profile",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                            BrewSection.Expert -> {
                                item {
                                    BrewCard(
                                        "Dial-in parameters & grinder setup",
                                        "Capture grinder model, click/RPM settings, first drop time and water mineral composition."
                                    ) {
                                        Field(
                                            "Grinder model",
                                            fields.getValue("grinderModel"),
                                            { model.updateField("grinderModel", it) },
                                            "form:grinder_model"
                                        )
                                        Field(
                                            "Grinder setting / clicks",
                                            fields.getValue("grinderSetting"),
                                            { model.updateField("grinderSetting", it) },
                                            "form:grinder_setting"
                                        )
                                        Field(
                                            "First drop time (seconds)",
                                            fields.getValue("firstDrop"),
                                            { model.updateField("firstDrop", it) },
                                            "form:first_drop",
                                            true
                                        )
                                        Field("Water general hardness (ppm GH)", fields.getValue("waterHardness"), {
                                            model.updateField("waterHardness", it)
                                        }, "form:water_hardness", true)
                                        Field("Water total alkalinity (ppm KH)", fields.getValue("waterAlkalinity"), {
                                            model.updateField("waterAlkalinity", it)
                                        }, "form:water_alkalinity", true)
                                        Field(
                                            "Basket & puck prep notes",
                                            fields.getValue("puckPrep"),
                                            { model.updateField("puckPrep", it) },
                                            "form:puck_prep"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val calculationSummary = state.calculation.lineSequence().firstOrNull().orEmpty()
                    val ratioLabel = if (calculationSummary.startsWith("Calculated ratio ")) {
                        "Ratio ${calculationSummary.removePrefix("Calculated ratio ")}"
                    } else {
                        "Review calculation"
                    }
                    TactileTextButton(
                        onClick = { selectedSection = BrewSection.Measurements.name },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:ratio")
                    ) {
                        AnimatedContent(
                            targetState = ratioLabel,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(TRANSITION_FADE_IN_MILLIS)) togetherWith
                                    fadeOut(animationSpec = tween(TRANSITION_FADE_OUT_MILLIS))
                            },
                            label = "ratioAnimation"
                        ) { targetText ->
                            Text(
                                targetText,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    TactileButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            model.saveRecipe()
                        },
                        enabled = state.ready && !state.busy,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:save")
                    ) {
                        AnimatedContent(
                            targetState = (state.draft.id == null),
                            transitionSpec = {
                                fadeIn(animationSpec = tween(TRANSITION_FADE_IN_MILLIS)) togetherWith
                                    fadeOut(animationSpec = tween(TRANSITION_FADE_OUT_MILLIS))
                            },
                            label = "saveLabelAnimation"
                        ) { isNew ->
                            Text(
                                if (isNew) "Save recipe" else "Save new revision",
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrewCard(title: String, detail: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    InfoCard(title, detail) { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) }
}

@Composable
private fun Library(state: CoffeeUiState, model: CoffeeViewModel) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var tab by rememberSaveable { mutableStateOf("Beans & Bags") }
    var formKind by rememberSaveable { mutableStateOf<String?>(null) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var form by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }
    var processFilter by rememberSaveable { mutableStateOf("All") }
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }
    var gearFilter by rememberSaveable { mutableStateOf("All") }
    var ocrDialogVisible by rememberSaveable { mutableStateOf(false) }
    var ocrSampleText by rememberSaveable { mutableStateOf("") }
    var ocrTargetName by rememberSaveable { mutableStateOf(CatalogPhotoTarget.BEAN.name) }
    var lastPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var lastPhotoTargetName by rememberSaveable { mutableStateOf(CatalogPhotoTarget.BEAN.name) }
    var lastPhotoIsCameraOwned by rememberSaveable { mutableStateOf(false) }
    var cameraOutputUri by rememberSaveable { mutableStateOf<String?>(null) }
    val photoScope = rememberCoroutineScope()
    var importJsonText by rememberSaveable { mutableStateOf("") }
    var selectedStrategy by rememberSaveable { mutableStateOf(ConflictResolutionStrategy.KEEP_BOTH_RENAME) }
    var addingBagBeanId by rememberSaveable { mutableStateOf<String?>(null) }
    var bagPackageMassText by rememberSaveable { mutableStateOf("") }
    var bagRemainingMassText by rememberSaveable { mutableStateOf("") }
    var bagPriceText by rememberSaveable { mutableStateOf("") }
    var deductingBagId by rememberSaveable { mutableStateOf<String?>(null) }
    var doseDeductionText by rememberSaveable { mutableStateOf("") }
    var gearSubTab by rememberSaveable { mutableStateOf("44 Grinders Catalog") }
    var grinderTypeFilter by rememberSaveable { mutableStateOf("All") }
    var selectedZeroGrinderId by rememberSaveable { mutableStateOf<String?>(null) }
    var dripperValveStateToggle by rememberSaveable { mutableStateOf(ValveState.CLOSED) }
    var testFilterDripperId by rememberSaveable { mutableStateOf<String?>(null) }
    var testFilterId by rememberSaveable { mutableStateOf("filter-v60-02") }
    var exportProfileDialog by rememberSaveable { mutableStateOf(false) }
    var exportProfileJson by rememberSaveable { mutableStateOf("") }
    var importProfileDialog by rememberSaveable { mutableStateOf(false) }
    var importProfileJson by rememberSaveable { mutableStateOf("") }

    fun openSourcePhoto(photoUri: String) {
        runCatching {
            val uri = Uri.parse(photoUri)
            require(uri.scheme == "content") { "Saved photo URI is invalid." }
            context.startActivity(
                Intent(Intent.ACTION_VIEW).setDataAndType(uri, "image/*")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            )
        }.onFailure {
            model.reportCatalogPhotoError("Saved photo is unavailable. Select it again to attach a readable copy.")
        }
    }

    suspend fun discardCameraOutput(uri: Uri) {
        withContext(Dispatchers.IO) {
            runCatching { CatalogPhotoCapture.discard(context.contentResolver, uri) }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val target = CatalogPhotoTarget.valueOf(ocrTargetName)
            lastPhotoUri = uri.toString()
            lastPhotoTargetName = target.name
            lastPhotoIsCameraOwned = false
            ocrDialogVisible = false
            model.scanCatalogPhoto(uri, target)
        }
    }
    val cameraCapture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        val uri = cameraOutputUri?.let(Uri::parse)
        cameraOutputUri = null
        if (uri != null) {
            photoScope.launch {
                if (saved) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        discardCameraOutput(uri)
                        model.reportCatalogPhotoError(
                            "System camera capture requires Android 10 or later. Choose a photo instead."
                        )
                        return@launch
                    }
                    val publishResult = runCatching {
                        withContext(Dispatchers.IO) { CatalogPhotoCapture.publish(context.contentResolver, uri) }
                    }
                    if (publishResult.isSuccess) {
                        val target = CatalogPhotoTarget.valueOf(ocrTargetName)
                        lastPhotoUri = uri.toString()
                        lastPhotoTargetName = target.name
                        lastPhotoIsCameraOwned = true
                        ocrDialogVisible = false
                        model.scanCatalogPhoto(uri, target, capturedByCamera = true)
                    } else {
                        discardCameraOutput(uri)
                        model.reportCatalogPhotoError(
                            publishResult.exceptionOrNull()?.message ?: "Camera photo could not be saved. Choose an existing photo."
                        )
                    }
                } else {
                    discardCameraOutput(uri)
                }
            }
        }
    }

    fun startCameraCapture() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            model.reportCatalogPhotoError("System camera capture requires Android 10 or later. Choose a photo instead.")
            return
        }
        photoScope.launch {
            runCatching { withContext(Dispatchers.IO) { CatalogPhotoCapture.createPendingImage(context.contentResolver) } }
                .onSuccess { uri ->
                    cameraOutputUri = uri.toString()
                    runCatching { cameraCapture.launch(uri) }.onFailure { failure ->
                        cameraOutputUri = null
                        photoScope.launch {
                            discardCameraOutput(uri)
                        }
                        model.reportCatalogPhotoError(
                            failure.message ?: "No camera app could open. Choose an existing photo instead."
                        )
                    }
                }
                .onFailure { failure ->
                    model.reportCatalogPhotoError(failure.message ?: "Camera output could not be prepared. Choose a photo instead.")
                }
        }
    }

    val libraryTabs = listOf("Beans & Bags", "Grinders & Gear", "Water Chemistry", "Ingredients", "Green & Roast QC", "Import / Export")

    Page("Library") {
        item {
            Heading(
                "Beans, Gear & Formulation Catalog",
                "Verified agricultural origins, bag inventory with rest aging heuristics, precision grinder geometry, " +
                    "water mineral chemistry and conflict-resolving import/export."
            )
        }
        item {
            Field("Search catalog (origin, roaster, gear, varietal)", query, { query = it }, "library:query")
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                libraryTabs.forEach { label ->
                    val isSelected = tab == label || (tab == "Coffee" && label == "Beans & Bags")
                    val icon = when (label) {
                        "Beans & Bags" -> Icons.Default.Coffee
                        "Grinders & Gear" -> Icons.Default.Settings
                        "Water Chemistry" -> Icons.Default.WaterDrop
                        "Ingredients" -> Icons.Default.Science
                        "Green & Roast QC" -> Icons.Default.Spa
                        "Import / Export" -> Icons.Default.ImportExport
                        else -> Icons.Default.Coffee
                    }
                    TactileFilterChip(
                        selected = isSelected,
                        onClick = { tab = label },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }
        }

        // ====================================================================
        // TAB 1: Beans & Bags Inventory (S2-001, S2-002, S2-003, S2-008, S2-010)
        // ====================================================================
        if (tab == "Beans & Bags" || tab == "Coffee") {
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactileButton(
                        onClick = {
                            formKind = "Coffee"
                            editingId = null
                            form = emptyMap()
                        },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("library:add")
                    ) {
                        Text("+ Add Bean Record", maxLines = 1, softWrap = false)
                    }
                    TactileOutlinedButton(
                        onClick = {
                            ocrDialogVisible = true
                        },
                        modifier = Modifier.heightIn(min = 48.dp).testTag("library:photo_scan")
                    ) {
                        Text("Scan a label", maxLines = 1, softWrap = false)
                    }
                    TactileFilterChip(
                        selected = favoritesOnly,
                        onClick = { favoritesOnly = !favoritesOnly },
                        label = { Text(if (favoritesOnly) "★ Favorites Only" else "☆ All Beans") },
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }
            if (state.busy && state.progress == "Recognizing label locally") {
                item {
                    Column(
                        Modifier.fillMaxWidth().semantics {
                            liveRegion = LiveRegionMode.Polite
                            stateDescription = "Recognizing the selected label on this device"
                        },
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text("Recognizing the label on this device. Your photo stays local.")
                        TactileTextButton(
                            onClick = model::cancelCatalogPhotoOcr,
                            modifier = Modifier.heightIn(min = 48.dp).testTag("library:cancel_photo_scan")
                        ) { Text("Cancel scan", maxLines = 1, softWrap = false) }
                    }
                }
            }
            state.photoOcrNotice?.let { notice ->
                item { InfoCard("Label scan", notice) }
            }
            state.photoOcrError?.let { error ->
                item {
                    InfoCard("Photo scan needs attention", error) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (lastPhotoUri != null) {
                                TactileTextButton(
                                    onClick = {
                                        lastPhotoUri?.let { photoUri ->
                                            model.scanCatalogPhoto(
                                                Uri.parse(photoUri),
                                                CatalogPhotoTarget.valueOf(lastPhotoTargetName),
                                                capturedByCamera = lastPhotoIsCameraOwned
                                            )
                                        }
                                    },
                                    modifier = Modifier.heightIn(min = 48.dp).testTag("library:retry_photo_scan")
                                ) { Text("Retry", maxLines = 1, softWrap = false) }
                            }
                            TactileTextButton(
                                onClick = { ocrDialogVisible = true },
                                modifier = Modifier.heightIn(min = 48.dp).testTag("library:reselect_photo")
                            ) { Text("Choose another photo", maxLines = 1, softWrap = false) }
                        }
                    }
                }
            }
            item {
                val processes = listOf("All", "WASHED", "NATURAL", "HONEY", "ANAEROBIC_NATURAL", "WET_HULLED")
                Select("Process filter", processFilter, processes.map { it to it }, { processFilter = it }, "library:process")
            }

            // Detailed Beans
            val filteredDetailed = state.detailedBeans.filter { bean ->
                (processFilter == "All" || bean.process.name.equals(processFilter, ignoreCase = true)) &&
                    (!favoritesOnly || bean.isFavorite) &&
                    listOf(bean.name, bean.roaster, bean.originCountry, bean.varietal, bean.process.name).any { it.contains(query, true) }
            }

            items(filteredDetailed, key = { "detailed-${it.id}" }) { bean ->
                val analysis = bean.roastDate?.let { roastDate ->
                    RoastRestCalculator.calculateFreshness(
                        roastDate = roastDate,
                        roastLevel = bean.roastLevel,
                        process = bean.process,
                        freezeHistory = bean.bags.flatMap { it.freezeHistory }
                    )
                }

                InfoCard(
                    "${bean.roaster} — ${bean.name}",
                    listOf(
                        "Origin: ${bean.originCountry} · ${bean.region} (Farm: ${bean.farm}, Producer: ${bean.producer})",
                        "Variety: ${bean.varietal} · Process: ${bean.process.name} · Altitude: ${bean.altitudeMeters ?: "Unknown"}m",
                        analysis?.let {
                            "Roast: ${bean.roastLevel.name} on ${bean.roastDate} " +
                                "(${it.effectiveRestDays} effective rest days)"
                        }
                            ?: "Roast date: Unknown · freshness is not estimated",
                        analysis?.let { "Freshness: ${it.state.name} (Peak: Days ${it.peakStartDay}–${it.peakEndDay})" }.orEmpty(),
                        analysis?.let { "Degas Rationale: ${it.heuristicRationale}" }.orEmpty(),
                        if (bean.tastingNotes.isNotEmpty()) "Tasting Notes: ${bean.tastingNotes.joinToString(", ")}" else "",
                        if (bean.photoUri != null) "Source label photo attached" else ""
                    ).filter { it.isNotBlank() }.joinToString("\n")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (bean.photoUri != null) {
                                TactileTextButton(
                                    onClick = { openSourcePhoto(bean.photoUri) },
                                    modifier = Modifier.heightIn(min = 48.dp).testTag("library:open_bean_photo")
                                ) { Text("View label photo", maxLines = 1, softWrap = false) }
                            }
                            TactileTextButton(onClick = { model.toggleBeanFavorite(bean.id) }, modifier = Modifier.heightIn(min = 48.dp)) {
                                Text(if (bean.isFavorite) "★ Favorited" else "☆ Add Favorite", maxLines = 1, softWrap = false)
                            }
                            TactileTextButton(onClick = {
                                addingBagBeanId = bean.id
                                bagPackageMassText = ""
                                bagRemainingMassText = ""
                                bagPriceText = ""
                            }, modifier = Modifier.heightIn(min = 48.dp)) {
                                Text("+ Add bag", maxLines = 1, softWrap = false)
                            }
                            TactileTextButton(onClick = { model.deleteDetailedBean(bean.id) }, modifier = Modifier.heightIn(min = 48.dp)) {
                                Text("Delete", color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                            }
                        }

                        // Bags Inventory List
                        if (bean.bags.isNotEmpty()) {
                            Text("Bags in Inventory (${bean.bags.size}):", style = MaterialTheme.typography.labelMedium)
                            bean.bags.forEach { bag ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                "${bag.remainingMassGrams}g / ${bag.packageMassGrams}g",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFeatureSettings = "tnum",
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (bag.status == BagStatus.ACTIVE) {
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.secondaryContainer
                                                    }
                                                ) {
                                                    Text(
                                                        bag.status.name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (bag.status == BagStatus.ACTIVE) {
                                                            MaterialTheme.colorScheme.onPrimaryContainer
                                                        } else {
                                                            MaterialTheme.colorScheme.onSecondaryContainer
                                                        },
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                                if (bag.freezeHistory.any { it.isOngoing }) {
                                                    Text(
                                                        "❄ Frozen",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.tertiary,
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TactileOutlinedButton(
                                                onClick = { model.toggleFreezeBag(bag.id) },
                                                enabled = bag.status != BagStatus.FINISHED,
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            ) {
                                                Text(
                                                    if (bag.status == BagStatus.FROZEN) "Thaw" else "Freeze",
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                            TactileButton(
                                                onClick = {
                                                    deductingBagId = bag.id
                                                    doseDeductionText = ""
                                                },
                                                enabled = bag.status != BagStatus.FROZEN &&
                                                    bag.status != BagStatus.FINISHED &&
                                                    bag.remainingMassGrams > BigDecimal.ZERO,
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            ) {
                                                Text(
                                                    "Record dose",
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum")
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Legacy / Curated Seed Beans
            items(
                state.beans.filter { bean ->
                    (processFilter == "All" || bean.process.equals(processFilter, ignoreCase = true)) &&
                        listOf(bean.name, bean.variety, bean.country, bean.region).any { it.contains(query, true) }
                },
                key = { "legacy-${it.id}" }
            ) { bean ->
                InfoCard(
                    bean.name,
                    if (bean.isCuratedSeed) {
                        "Reference variety · ${bean.variety}\n" +
                            "Supplier lot, origin, process, roast and sensory measurements are not supplied.\n" +
                            "Source: ${bean.provenance}"
                    } else {
                        listOf(
                            "${bean.country} · ${bean.region}",
                            "Variety ${bean.variety} · ${bean.process} · roast ${bean.roastLevel}",
                            "Producer ${bean.producer} · altitude ${bean.altitudeMeters ?: "Unknown"} m",
                            "Source: ${bean.provenance}"
                        ).joinToString("\n")
                    }
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileTextButton(onClick = {
                            formKind = "Coffee"
                            editingId = bean.id
                            form = beanForm(bean)
                        }) { Text("Edit coffee", maxLines = 1, softWrap = false) }
                        if (!bean.isCuratedSeed) {
                            TactileTextButton(onClick = { model.requestDeletion("bean", bean.id) }) {
                                Text("Delete coffee", maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 2: Grinders & Gear Catalog (S2-004, S2-005)
        // ====================================================================
        if (tab == "Grinders & Gear") {
            val gearSubTabs = listOf("44 Grinders Catalog", "26 Drippers Catalog", "My Gear & Inventory", "Grinder Profiles & Calibration")
            val effectiveGearSubTab = if (gearSubTab in gearSubTabs) gearSubTab else "44 Grinders Catalog"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gearSubTabs.forEach { subTab ->
                        TactileFilterChip(
                            selected = effectiveGearSubTab == subTab,
                            onClick = { gearSubTab = subTab },
                            label = { Text(subTab, maxLines = 1, softWrap = false) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }
            }

            // Sub-Tab 1: 44 Grinders Catalog (GR-01 .. GR-12)
            if (effectiveGearSubTab == "44 Grinders Catalog") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "HAND", "ELECTRIC").forEach { t ->
                            TactileFilterChip(
                                selected = grinderTypeFilter == t,
                                onClick = { grinderTypeFilter = t },
                                label = { Text(if (t == "All") "All (44)" else "$t Grinders", maxLines = 1, softWrap = false) },
                                modifier = Modifier.heightIn(min = 48.dp)
                            )
                        }
                    }
                }
                val filteredGrinders = state.grinderCatalog.filter { g ->
                    (grinderTypeFilter == "All" || g.type.name == grinderTypeFilter) &&
                        (query.isBlank() || listOf(g.brand, g.modelName, g.adjustmentMechanism).any { it.contains(query, true) })
                }
                items(filteredGrinders, key = { "grinder-cat-${it.id}" }) { grinder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${grinder.brand} — ${grinder.modelName}",
                                modifier = Modifier.semantics { heading() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MicroBadgePill(
                                    text = grinder.type.name,
                                    containerColor = if (grinder.type == GrinderType.HAND) {
                                        handGrinderBadgeContainerColor.copy(alpha = 0.20f)
                                    } else {
                                        electricGrinderBadgeContainerColor.copy(alpha = 0.20f)
                                    },
                                    contentColor = if (grinder.type == GrinderType.HAND) {
                                        handGrinderBadgeContentColor
                                    } else {
                                        electricGrinderBadgeContentColor
                                    }
                                )
                                MicroBadgePill(text = grinder.adjustmentUnit.name.replace("_", " "))
                                MicroBadgePill(
                                    text = grinder.evidenceStatus.name.replace("_", " "),
                                    containerColor = if (grinder.evidenceStatus == EvidenceStatus.MANUFACTURER_VERIFIED) {
                                        verifiedEquipmentColor.copy(alpha = 0.20f)
                                    } else {
                                        pendingEquipmentColor.copy(alpha = 0.20f)
                                    },
                                    contentColor = if (grinder.evidenceStatus == EvidenceStatus.MANUFACTURER_VERIFIED) {
                                        verifiedEquipmentContentColor
                                    } else {
                                        pendingEquipmentContentColor
                                    }
                                )
                                if (!grinder.isEspressoCapable) {
                                    MicroBadgePill(
                                        text = "Filter Only",
                                        containerColor = filterOnlyBadgeContainerColor.copy(alpha = 0.20f),
                                        contentColor = filterOnlyBadgeContentColor
                                    )
                                }
                            }
                            Text(
                                text = "Mechanism: ${grinder.adjustmentMechanism}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                GrindRangeTile(
                                    label = "Coarse (Tubruk)",
                                    range = grinder.rawCoarseRange,
                                    modifier = Modifier.weight(1f)
                                )
                                GrindRangeTile(
                                    label = "Medium (V60)",
                                    range = grinder.rawMediumRange,
                                    isVerified = grinder.verifiedRangeMedium != null,
                                    modifier = Modifier.weight(1f)
                                )
                                GrindRangeTile(
                                    label = "Fine (Espresso)",
                                    range = if (grinder.isEspressoCapable) grinder.rawFineRange else "Filter Only",
                                    isWarning = !grinder.isEspressoCapable,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (grinder.verifiedRangeMedium != null) {
                                Text(
                                    text = "Verified Range: ${grinder.verifiedRangeMedium}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = verifiedEquipmentContentColor
                                )
                            }
                            if (grinder.variants.isNotEmpty()) {
                                Text(
                                    text = "Variants: " + grinder.variants.joinToString { it.name },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (grinder.caveats.isNotEmpty()) {
                                Text(
                                    text = "Notes: " + grinder.caveats.joinToString("; "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selectedZeroGrinderId == grinder.id && grinder.safeZeroInstructions.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)),
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Zero Calibration Procedure",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            MicroBadgePill(
                                                text = "Manufacturer Guide",
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                                contentColor = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        grinder.safeZeroInstructions.forEachIndexed { idx, step ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.20f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "${idx + 1}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Text(
                                                    step,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (grinder.safeZeroInstructions.isNotEmpty()) {
                                    TactileOutlinedButton(
                                        onClick = {
                                            selectedZeroGrinderId = if (selectedZeroGrinderId == grinder.id) null else grinder.id
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            if (selectedZeroGrinderId == grinder.id) "Hide Zero Guide" else "Zero Calibration Guide",
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                                TactileFilledTonalButton(
                                    onClick = {
                                        model.addUserGrinderProfile(grinder.id, grinder.modelName)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+ Add to Owned", maxLines = 1, softWrap = false)
                                }
                                TactileButton(
                                    onClick = {
                                        model.selectGrinderModel(grinder.id)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Select for Brew", maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }

            // Sub-Tab 2: 26 Drippers Catalog (BR-01 .. BR-18)
            if (effectiveGearSubTab == "26 Drippers Catalog") {
                val filteredDrippers = state.dripperCatalog.filter { d ->
                    query.isBlank() || listOf(d.name, d.flowBehaviorDescription, d.geometry.name).any { it.contains(query, true) }
                }
                items(filteredDrippers, key = { "dripper-cat-${it.id}" }) { dripper ->
                    val isValve = dripper.isValveEquipped
                    val flowAdvice = DripperIntelligenceCatalog.getDripperFlowAdvice(
                        dripper.id,
                        if (isValve) dripperValveStateToggle else ValveState.NOT_APPLICABLE
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = dripper.name,
                                modifier = Modifier.semantics { heading() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MicroBadgePill(text = dripper.geometry.name.replace("_", " "))
                                MicroBadgePill(text = "Flow: ${dripper.flowClass.name.replace("_", " ")}")
                                MicroBadgePill(text = dripper.flowMode.name.replace("_", " "))
                                MicroBadgePill(
                                    text = dripper.evidenceStatus.name.replace("_", " "),
                                    containerColor = if (dripper.evidenceStatus == EvidenceStatus.MANUFACTURER_VERIFIED) {
                                        verifiedEquipmentColor.copy(alpha = 0.20f)
                                    } else {
                                        pendingEquipmentColor.copy(alpha = 0.20f)
                                    },
                                    contentColor = if (dripper.evidenceStatus == EvidenceStatus.MANUFACTURER_VERIFIED) {
                                        verifiedEquipmentContentColor
                                    } else {
                                        pendingEquipmentContentColor
                                    }
                                )
                                if (isValve) {
                                    MicroBadgePill(
                                        text = "Valve Equipped",
                                        containerColor = valveBadgeContainerColor.copy(alpha = 0.20f),
                                        contentColor = valveBadgeContentColor
                                    )
                                }
                            }
                            Text(
                                text = "Flow behavior: ${dripper.flowBehaviorDescription}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            MicroBadgePill(
                                text = "Drain: ${flowAdvice.qualitativeDrainSpeed}",
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f),
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Grind rel. to V60: ${dripper.grindBaselineRelativeToV60}",
                                style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Starting suggestion: ${dripper.startingGrindText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (dripper.supportedBottomVariants.isNotEmpty()) {
                                Text(
                                    text = "Interchangeable bottoms: " + dripper.supportedBottomVariants.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isValve) {
                                InteractiveValveSwitch(
                                    isOpen = dripperValveStateToggle == ValveState.OPEN,
                                    onToggle = { open ->
                                        dripperValveStateToggle = if (open) ValveState.OPEN else ValveState.CLOSED
                                        model.setValveState(dripperValveStateToggle)
                                    }
                                )
                                flowAdvice.valveNotice?.let { vn ->
                                    Text(
                                        text = "💡 $vn",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            if (testFilterDripperId == dripper.id) {
                                val c = DripperIntelligenceCatalog.checkFilterCompatibility(dripper.id, testFilterId)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        MicroBadgePill(
                                            text = c.statusText,
                                            containerColor = if (c.statusText == "RECOMMENDED_FIT") {
                                                verifiedEquipmentColor.copy(alpha = 0.20f)
                                            } else {
                                                pendingEquipmentColor.copy(alpha = 0.20f)
                                            },
                                            contentColor = if (c.statusText == "RECOMMENDED_FIT") {
                                                verifiedEquipmentContentColor
                                            } else {
                                                pendingEquipmentContentColor
                                            }
                                        )
                                        Text(
                                            text = "Filter ($testFilterId): ${c.recommendationNote}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TactileOutlinedButton(
                                    onClick = {
                                        testFilterDripperId = if (testFilterDripperId == dripper.id) null else dripper.id
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        if (testFilterDripperId == dripper.id) "Hide Filter Check" else "Check Filter Fit",
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                                TactileButton(
                                    onClick = {
                                        model.selectDripperModel(
                                            dripper.id,
                                            if (isValve) dripperValveStateToggle else ValveState.NOT_APPLICABLE
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Select for Brew", maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }

            // Sub-Tab 3: Grinder Profiles & Calibration (GR-06, GR-12)
            if (effectiveGearSubTab == "Grinder Profiles & Calibration") {
                item {
                    Text("Owned Grinder Profiles & Zero Offset Calibration (GR-06, GR-12)", style = MaterialTheme.typography.titleMedium)
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TactileOutlinedButton(
                            onClick = {
                                exportProfileJson = state.userGrinderProfiles.joinToString("\n---\n") {
                                    GrinderIntelligenceCatalog.exportProfileToJson(it)
                                }.ifBlank { "No profiles saved yet." }
                                exportProfileDialog = true
                            },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) { Text("Export Profiles", maxLines = 1, softWrap = false) }
                        TactileOutlinedButton(
                            onClick = {
                                importProfileJson = ""
                                importProfileDialog = true
                            },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) { Text("Import Profile", maxLines = 1, softWrap = false) }
                    }
                }
                if (state.userGrinderProfiles.isEmpty()) {
                    item {
                        InfoCard(
                            "No Owned Grinders Registered",
                            "Select any grinder from the '44 Grinders Catalog' tab and tap '+ Add to Owned' " +
                                "to track custom zero point calibration and burr wear."
                        )
                    }
                }
                items(state.userGrinderProfiles, key = { "profile-${it.id}" }) { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = profile.customName.ifBlank { "Grinder Profile" },
                                modifier = Modifier.semantics { heading() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MicroBadgePill(text = "Model: ${profile.grinderModelId}")
                                MicroBadgePill(text = "Burr: ${profile.burrCondition}")
                                MicroBadgePill(
                                    text = "Offset: ${profile.zeroCalibrationOffset}",
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f),
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (profile.calibrationNotes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${profile.calibrationNotes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TactileOutlinedButton(
                                    onClick = {
                                        val json = GrinderIntelligenceCatalog.exportProfileToJson(profile)
                                        exportProfileJson = json
                                        exportProfileDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Export JSON", maxLines = 1, softWrap = false)
                                }
                                TactileOutlinedButton(
                                    onClick = {
                                        model.deleteUserGrinderProfile(profile.id)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }

            // Sub-Tab 4: My Gear & Inventory (Original S2-004 & S2-005)
            if (effectiveGearSubTab == "My Gear & Inventory") {
                item {
                    TactileOutlinedButton(
                        onClick = {
                            ocrTargetName = CatalogPhotoTarget.EQUIPMENT.name
                            ocrDialogVisible = true
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("library:scan_equipment_photo")
                    ) { Text("Add equipment from label photo", maxLines = 1, softWrap = false) }
                }
                item {
                    val gearCategories =
                        listOf("All", "GRINDER", "DRIPPER", "KETTLE", "SCALE", "ESPRESSO_MACHINE", "PORTAFILTER_BASKET", "PUCK_PREP_TOOL")
                    Select("Category filter", gearFilter, gearCategories.map { it to it }, { gearFilter = it }, "library:gear_filter")
                }

                if (gearFilter == "All" || gearFilter == "GRINDER") {
                    item {
                        Text("Precision Grinders & Burr Geometry (S2-004)", style = MaterialTheme.typography.titleMedium)
                    }
                    items(
                        state.grinders.filter { listOf(it.brand, it.model).any { s -> s.contains(query, true) } },
                        key = { "grinder-${it.id}" }
                    ) { grinder ->
                        InfoCard(
                            "${grinder.brand} — ${grinder.model}",
                            listOf(
                                "Calibration Unit: ${grinder.calibrationUnit.name}",
                                "Burrs: ${grinder.burrProfile.diameterMm}mm " +
                                    "${grinder.burrProfile.burrType.name} (${grinder.burrProfile.material.name})",
                                "Burr Model: ${grinder.burrProfile.burrModelName}",
                                "Zero Point Offset: ${grinder.zeroPointOffset} ${grinder.calibrationUnit.name.lowercase()}",
                                if (grinder.clicksPerRotation != null) "Clicks/Rotation: ${grinder.clicksPerRotation}" else "",
                                if (grinder.typicalRpm != null) "Motor: ${grinder.typicalRpm} RPM" else "",
                                if (grinder.notes.isNotBlank()) "Notes: ${grinder.notes}" else ""
                            ).filter { it.isNotBlank() }.joinToString("\n")
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TactileTextButton(onClick = { model.toggleGrinderFavorite(grinder.id) }) {
                                    Text(if (grinder.isFavorite) "★ Favorited" else "☆ Add Favorite", maxLines = 1, softWrap = false)
                                }
                                TactileTextButton(onClick = { model.deleteGrinder(grinder.id) }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }

                if (gearFilter != "GRINDER") {
                    item {
                        Text("Brewing Gear & Equipment Catalog (S2-005)", style = MaterialTheme.typography.titleMedium)
                    }
                    val filteredEquip = state.equipmentCatalog.filter { eq ->
                        (gearFilter == "All" || eq.category.name == gearFilter) &&
                            listOf(eq.brand, eq.model, eq.category.name).any { it.contains(query, true) }
                    }
                    items(filteredEquip, key = { "equip-${it.id}" }) { item ->
                        InfoCard(
                            "[${item.category.name}] ${item.brand} ${item.model}",
                            listOf(
                                item.parameters.entries.joinToString("\n") { "${it.key}: ${it.value}" }.ifBlank { "No special parameters" },
                                if (item.photoUri != null) "Source label photo attached" else ""
                            ).filter(String::isNotBlank).joinToString("\n")
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (item.photoUri != null) {
                                    TactileTextButton(
                                        onClick = { openSourcePhoto(item.photoUri) },
                                        modifier = Modifier.heightIn(min = 48.dp).testTag("library:open_equipment_photo")
                                    ) { Text("View label photo", maxLines = 1, softWrap = false) }
                                }
                                TactileTextButton(onClick = { model.toggleEquipmentFavorite(item.id) }) {
                                    Text(if (item.isFavorite) "★ Favorited" else "☆ Add Favorite", maxLines = 1, softWrap = false)
                                }
                                TactileTextButton(onClick = { model.deleteEquipmentItem(item.id) }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 3: Water Mineral Profile & Chemistry (S2-006)
        // ====================================================================
        if (tab == "Water Chemistry") {
            item {
                Text("Water Chemistry & Mineral Presets (S2-006)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.waterProfiles, key = { "water-${it.id}" }) { water ->
                InfoCard(
                    water.name,
                    listOf(
                        "Calcium (Ca2+): ${water.calciumPpm} ppm · Magnesium (Mg2+): ${water.magnesiumPpm} ppm",
                        "Sodium (Na+): ${water.sodiumPpm} ppm · Bicarbonate (HCO3-): ${water.bicarbonatePpm} ppm",
                        "Total Hardness (GH): ${water.totalHardnessPpmCaCO3} ppm CaCO3",
                        "Total Alkalinity (KH): ${water.totalAlkalinityPpmCaCO3} ppm CaCO3",
                        "Calculated TDS: ${water.calculatedTdsPpm} ppm · pH: ${water.ph ?: "7.0"}",
                        "Preset Type: ${water.preset.name}"
                    ).joinToString("\n")
                ) {
                    TactileTextButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, water.name)
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Water Profile: ${water.name}\n" +
                                    "Hardness: ${water.totalHardnessPpmCaCO3} ppm CaCO3\n" +
                                    "Alkalinity: ${water.totalAlkalinityPpmCaCO3} ppm CaCO3\n" +
                                    "TDS: ${water.calculatedTdsPpm} ppm"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Water Profile"))
                    }) {
                        Text("Share Profile", maxLines = 1, softWrap = false)
                    }
                }
            }
        }

        // ====================================================================
        // TAB 4: Ingredients & Food-Grade Catalog (S2-007)
        // ====================================================================
        if (tab == "Ingredients") {
            item {
                Text("Food-Grade Verified Solutions & Additives (S2-007)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.foodGradeIngredients, key = { "food-ing-${it.id}" }) { ing ->
                InfoCard(
                    ing.name,
                    listOf(
                        "Density: ${ing.densityGramsPerMl} g/mL (Required for volume-mass conversion)",
                        "Food Grade Verified: ${if (ing.isFoodGradeVerified) "Yes (Food-Safe)" else "NO - FORBIDDEN"}",
                        "Supplier Max Limit: ${ing.supplierMaxPercent ?: "None"}% w/w",
                        "Allergens: ${ing.allergens.joinToString().ifBlank { "None declared" }}",
                        if (ing.casNumber != null) "CAS Registry: ${ing.casNumber}" else "",
                        "Provenance: ${ing.provenance}"
                    ).filter { it.isNotBlank() }.joinToString("\n")
                )
            }
            items(state.ingredients, key = { "legacy-ing-${it.id}" }) { ingredient ->
                InfoCard(
                    ingredient.name,
                    listOf(
                        "Food grade: ${ingredient.foodGrade?.toString() ?: "Unknown"}",
                        "Density: ${ingredient.densityGramsPerMl ?: "Unknown"} g/mL",
                        "Supplier maximum: ${ingredient.supplierMaxPercent ?: "Unknown"}% w/w",
                        "Allergens: ${ingredient.allergens.joinToString().ifBlank { "not declared; verify supplier" }}",
                        "Source: ${ingredient.provenance}"
                    ).joinToString("\n")
                )
            }
        }

        // ====================================================================
        // TAB 5: Green Coffee QC & Roast Batches (S2-011, S2-012)
        // ====================================================================
        if (tab == "Green & Roast QC") {
            item {
                Text("Green Coffee Physical QC (S2-011)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.greenCoffeeLots, key = { "green-${it.id}" }) { lot ->
                InfoCard(
                    "Lot: ${lot.lotNumber} (${lot.originCountry} - ${lot.producer})",
                    listOf(
                        "Moisture: ${lot.moisturePercent}% (Target: 10.0-12.0%) · Water Activity: ${lot.waterActivityAw} aw",
                        "Bulk Density: ${lot.bulkDensityGramsPerLiter} g/L",
                        "Defects: Cat 1 = ${lot.category1DefectCount}, Cat 2 = ${lot.category2DefectCount} " +
                            "(Total Points = ${lot.totalDefectPoints})",
                        "Grade Status: ${if (lot.isSpecialtyGradeEligible) "★ Specialty Grade 1 Eligible" else "Commercial / Off-Grade"}",
                        "Status: ${lot.sampleStatus.name} (Harvest: ${lot.harvestYear})"
                    ).joinToString("\n")
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Roast Batches, Curves & Blends (S2-012)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.roastBatches, key = { "roast-${it.id}" }) { batch ->
                InfoCard(
                    "Batch #${batch.batchNumber}",
                    listOf(
                        "Charge Temp: ${batch.chargeTemperatureCelsius}°C · Drop Temp: ${batch.dropTemperatureCelsius}°C",
                        "Duration: ${batch.totalRoastDurationSeconds}s · First Crack: ${batch.firstCrackTimeSeconds}s",
                        "Development Time Ratio (DTR): ${batch.developmentTimeRatioPercent}%",
                        "Curve Data Points: ${batch.curvePoints.size} samples (Rate of Rise RoR available)",
                        "Blend Formulations: ${batch.blendComponents.joinToString { "${it.beanId}: ${it.componentPercentage}%" }}"
                    ).joinToString("\n")
                )
            }
        }

        // ====================================================================
        // TAB 6: Bulk Import/Export & Conflicts (S2-009)
        // ====================================================================
        if (tab == "Import / Export") {
            item {
                Heading(
                    "Catalog Data Portability & Conflict Resolution (S2-009)",
                    "Export canonical JSON/CSV or import backup data with deterministic duplicate resolution strategies."
                )
            }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactileButton(onClick = {
                        val json = model.exportCatalogJson()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_SUBJECT, "coffee_creator_catalog.json")
                            putExtra(Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Export Catalog JSON"))
                    }) {
                        Text("Export Catalog (JSON)", maxLines = 1, softWrap = false)
                    }
                    TactileOutlinedButton(onClick = {
                        val csv = model.exportBeansCsv()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, "beans_export.csv")
                            putExtra(Intent.EXTRA_TEXT, csv)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Export Beans (CSV)"))
                    }) {
                        Text("Export Beans (CSV)", maxLines = 1, softWrap = false)
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Text("Import Catalog JSON Payload:", style = MaterialTheme.typography.titleMedium)
            }
            item {
                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = {
                        importJsonText = it
                        if (it.isNotBlank()) model.previewCatalogImport(it)
                    },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    label = { Text("Paste Catalog JSON here") }
                )
            }

            state.catalogImportPreview?.let { preview ->
                item {
                    InfoCard(
                        "Import Dry-Run Preview",
                        "Total records: ${preview.totalRecords}\n" +
                            "New records: ${preview.newRecordsCount}\n" +
                            "Conflicting records: ${preview.conflictingRecordsCount}\n" +
                            preview.conflicts.joinToString("\n")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Conflict Resolution Strategy:", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ConflictResolutionStrategy.values().forEach { strategy ->
                                    TactileFilterChip(
                                        selected = selectedStrategy == strategy,
                                        onClick = { selectedStrategy = strategy },
                                        label = { Text(strategy.name, maxLines = 1, softWrap = false) }
                                    )
                                }
                            }
                            TactileButton(
                                onClick = {
                                    model.executeCatalogImport(importJsonText, selectedStrategy)
                                    importJsonText = ""
                                },
                                enabled = preview.totalRecords > 0
                            ) {
                                Text("Execute Import (${selectedStrategy.name})", maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }
        }
    }

    addingBagBeanId?.let { beanId ->
        val packageMass = bagPackageMassText.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        val remainingMass = bagRemainingMassText.toBigDecimalOrNull()
            ?.takeIf { value -> value >= BigDecimal.ZERO && packageMass != null && value <= packageMass }
        AlertDialog(
            onDismissRequest = { addingBagBeanId = null },
            title = { Text("Add bag inventory") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter values from the package and the mass currently remaining. No bag size or balance is assumed.")
                    Field("Package mass (g)", bagPackageMassText, { bagPackageMassText = it }, "library:bag_package_mass", numeric = true)
                    Field(
                        "Current remaining mass (g)",
                        bagRemainingMassText,
                        { bagRemainingMassText = it },
                        "library:bag_remaining_mass",
                        numeric = true
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        model.addBagToBean(beanId, requireNotNull(packageMass), requireNotNull(remainingMass))
                        addingBagBeanId = null
                    },
                    enabled = packageMass != null && remainingMass != null,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("library:add_bag_confirm")
                ) { Text("Save inventory", maxLines = 1, softWrap = false) }
            },
            dismissButton = { TactileTextButton(onClick = { addingBagBeanId = null }) { Text("Cancel", maxLines = 1, softWrap = false) } }
        )
    }

    deductingBagId?.let { bagId ->
        val doseMass = doseDeductionText.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        AlertDialog(
            onDismissRequest = { deductingBagId = null },
            title = { Text("Record used coffee") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the measured dose to deduct from this bag.")
                    Field("Used dose (g)", doseDeductionText, { doseDeductionText = it }, "library:bag_dose_mass", numeric = true)
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        if (model.deductDoseFromBag(bagId, requireNotNull(doseMass))) deductingBagId = null
                    },
                    enabled = doseMass != null,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("library:bag_dose_confirm")
                ) { Text("Record dose", maxLines = 1, softWrap = false) }
            },
            dismissButton = { TactileTextButton(onClick = { deductingBagId = null }) { Text("Cancel", maxLines = 1, softWrap = false) } }
        )
    }

    if (exportProfileDialog) {
        AlertDialog(
            onDismissRequest = { exportProfileDialog = false },
            title = { Text("Export Grinder Profile (JSON)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Lossless JSON representation with schema version 1 and exact raw units:")
                    OutlinedTextField(
                        value = exportProfileJson,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                }
            },
            confirmButton = {
                TactileButton(onClick = { exportProfileDialog = false }) {
                    Text("Close", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    if (importProfileDialog) {
        AlertDialog(
            onDismissRequest = { importProfileDialog = false },
            title = { Text("Import Grinder Profile (JSON)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste a valid grinder profile JSON with schema version and raw units:")
                    OutlinedTextField(
                        value = importProfileJson,
                        onValueChange = { importProfileJson = it },
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        label = { Text("Profile JSON") }
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        val imported = runCatching {
                            com.coffeecreator.app.core.catalog.GrinderIntelligenceCatalog.importProfileFromJson(importProfileJson)
                        }.getOrNull()
                        if (imported != null) {
                            model.addUserGrinderProfile(
                                modelId = imported.grinderModelId,
                                customName = imported.customName,
                                variantId = imported.selectedVariantId,
                                offset = imported.zeroCalibrationOffset,
                                burrCondition = imported.burrCondition,
                                notes = imported.calibrationNotes
                            )
                            importProfileDialog = false
                        }
                    },
                    enabled = importProfileJson.isNotBlank()
                ) {
                    Text("Import", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { importProfileDialog = false }) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Offline label source: local system camera, SAF picker, or manual text.
    if (ocrDialogVisible) {
        AlertDialog(
            onDismissRequest = { ocrDialogVisible = false },
            title = { Text("Add from a label") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Choose what the label belongs to. Bundled English OCR runs on this device; " +
                            "verify every draft. Missing facts stay unknown.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileFilterChip(
                            selected = ocrTargetName == CatalogPhotoTarget.BEAN.name,
                            onClick = { ocrTargetName = CatalogPhotoTarget.BEAN.name },
                            label = { Text("Coffee bag", maxLines = 1, softWrap = false) },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("ocr:target_bean")
                        )
                        TactileFilterChip(
                            selected = ocrTargetName == CatalogPhotoTarget.EQUIPMENT.name,
                            onClick = { ocrTargetName = CatalogPhotoTarget.EQUIPMENT.name },
                            label = { Text("Equipment", maxLines = 1, softWrap = false) },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("ocr:target_equipment")
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileButton(
                            onClick = ::startCameraCapture,
                            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !state.busy,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("ocr:take_photo")
                        ) { Text("Take photo", maxLines = 1, softWrap = false) }
                        TactileOutlinedButton(
                            onClick = { imagePicker.launch(arrayOf("image/jpeg", "image/png", "image/webp")) },
                            enabled = !state.busy,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("ocr:choose_photo")
                        ) { Text("Choose photo", maxLines = 1, softWrap = false) }
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        Text("System camera capture is available on Android 10 and later. Photo selection works on this device.")
                    }
                    HorizontalDivider()
                    Text("Or enter the label text manually.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = ocrSampleText,
                        onValueChange = { ocrSampleText = it },
                        label = { Text("Label text") },
                        modifier = Modifier.fillMaxWidth().height(LABEL_INPUT_HEIGHT_DP.dp).testTag("ocr:manual_text")
                    )
                    TactileButton(
                        onClick = {
                            when (CatalogPhotoTarget.valueOf(ocrTargetName)) {
                                CatalogPhotoTarget.BEAN -> model.parseBagOcr(null, ocrSampleText)
                                CatalogPhotoTarget.EQUIPMENT -> model.parseEquipmentOcr(null, ocrSampleText)
                            }
                            ocrDialogVisible = false
                        },
                        enabled = ocrSampleText.isNotBlank(),
                        modifier = Modifier.heightIn(min = 48.dp).testTag("ocr:review_manual_text")
                    ) {
                        Text("Review entered text", maxLines = 1, softWrap = false)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TactileTextButton(onClick = { ocrDialogVisible = false }) { Text("Cancel", maxLines = 1, softWrap = false) }
            }
        )
    }

    // OCR Draft Confirmation Gate (User MUST confirm before saving)
    state.pendingOcrDraft?.let { draft ->
        var editRoaster by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.roaster.value ?: "") }
        var editName by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.beanName.value ?: "") }
        var editOrigin by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.origin.value ?: "") }
        var editVarietal by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.varietal.value ?: "") }
        var editAltitude by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.altitudeMeters.value?.toString().orEmpty())
        }
        var editRoastDate by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.roastDate.value?.toString().orEmpty())
        }
        var editProcess by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.process.value?.name ?: ProcessMethod.UNKNOWN.name)
        }
        var editPackageMass by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.packageMassGrams.value?.toPlainString().orEmpty())
        }
        var editRemainingMass by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf("") }
        val packageMass = editPackageMass.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        val remainingMass = editRemainingMass.toBigDecimalOrNull()
            ?.takeIf { value -> value >= BigDecimal.ZERO && packageMass != null && value <= packageMass }
        val validInventory = (editPackageMass.isBlank() && editRemainingMass.isBlank()) ||
            (packageMass != null && remainingMass != null)
        val altitude = editAltitude.toIntOrNull()?.takeIf { it > 0 }
        val validAltitude = editAltitude.isBlank() || altitude != null
        val roastDate = runCatching { LocalDate.parse(editRoastDate) }.getOrNull()
        val validRoastDate = editRoastDate.isBlank() || roastDate != null

        AlertDialog(
            onDismissRequest = { model.dismissOcrDraft() },
            title = { Text("Confirm OCR Extracted Bean (Gate)") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Verify extracted fields before saving. Blank facts remain unknown; OCR does not provide a confidence score.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        if (draft.photoUri == null) {
                            "No photo is attached to this draft."
                        } else {
                            "The selected label photo will be attached to the saved bean."
                        }
                    )
                    Field("Roaster", editRoaster, { editRoaster = it }, "ocr:roaster")
                    Field("Bean Name", editName, { editName = it }, "ocr:name")
                    Field("Origin Country", editOrigin, { editOrigin = it }, "ocr:origin")
                    Field("Variety", editVarietal, { editVarietal = it }, "ocr:varietal")
                    Select(
                        "Process",
                        editProcess,
                        ProcessMethod.values().map { it.name to it.name.replace('_', ' ') },
                        { editProcess = it },
                        "ocr:process"
                    )
                    Field("Altitude (m)", editAltitude, { editAltitude = it }, "ocr:altitude")
                    Field("Roast Date (YYYY-MM-DD)", editRoastDate, { editRoastDate = it }, "ocr:roastDate")
                    Field("Package mass from label (g)", editPackageMass, { editPackageMass = it }, "ocr:package_mass", numeric = true)
                    Field("Current remaining mass (g)", editRemainingMass, { editRemainingMass = it }, "ocr:remaining_mass", numeric = true)
                    Text(
                        "A bag is added to inventory only when both package mass and current remaining mass are confirmed.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        val beanId = UUID.randomUUID().toString()
                        val bags = if (packageMass != null && remainingMass != null) {
                            listOf(
                                BagInventoryItem(
                                    id = UUID.randomUUID().toString(),
                                    beanId = beanId,
                                    packageMassGrams = packageMass,
                                    remainingMassGrams = remainingMass,
                                    status = when {
                                        remainingMass == BigDecimal.ZERO -> BagStatus.FINISHED
                                        remainingMass < packageMass -> BagStatus.ACTIVE
                                        else -> BagStatus.PURCHASED
                                    }
                                )
                            )
                        } else {
                            emptyList()
                        }
                        val confirmed = DetailedBeanRecord(
                            id = beanId,
                            roaster = editRoaster.ifBlank { "Unknown" },
                            name = editName.trim(),
                            originCountry = editOrigin.ifBlank { "Unknown" },
                            varietal = editVarietal.ifBlank { "Unknown" },
                            altitudeMeters = altitude,
                            roastDate = roastDate,
                            process = runCatching { ProcessMethod.valueOf(editProcess) }.getOrDefault(ProcessMethod.UNKNOWN),
                            bags = bags,
                            photoUri = draft.photoUri
                        )
                        model.confirmOcrDraft(confirmed)
                    },
                    enabled = editName.isNotBlank() && validAltitude && validRoastDate && validInventory && !state.busy
                ) {
                    Text("Confirm & Save Bean", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { model.dismissOcrDraft() }) { Text("Discard Draft", maxLines = 1, softWrap = false) }
            }
        )
    }

    state.pendingEquipmentOcrDraft?.let { draft ->
        var editBrand by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.brand.value.orEmpty()) }
        var editModel by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.model.value.orEmpty()) }
        var selectedCategory by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf("") }
        val categoryOptions = listOf("" to "Choose category") + EquipmentCategory.values().map {
            it.name to it.name.replace('_', ' ')
        }
        AlertDialog(
            onDismissRequest = model::dismissEquipmentOcrDraft,
            title = { Text("Review equipment label") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Only explicitly labeled brand and model are drafted. Select the category yourself; " +
                            "unobserved properties remain unknown."
                    )
                    Text(
                        if (draft.photoUri == null) {
                            "No photo is attached to this draft."
                        } else {
                            "The selected label photo will be attached to this equipment record."
                        }
                    )
                    Field("Brand (leave blank if unknown)", editBrand, { editBrand = it }, "ocr:equipment_brand")
                    Field("Model (leave blank if unknown)", editModel, { editModel = it }, "ocr:equipment_model")
                    Select(
                        "Equipment category",
                        selectedCategory,
                        categoryOptions,
                        { selectedCategory = it },
                        "ocr:equipment_category"
                    )
                    Text(
                        "Capacity, material, calibration, and food-safety details are not inferred from this photo.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        model.confirmEquipmentOcrDraft(
                            EquipmentItem(
                                id = UUID.randomUUID().toString(),
                                category = EquipmentCategory.valueOf(selectedCategory),
                                brand = editBrand.trim().ifBlank { "Unknown" },
                                model = editModel.trim().ifBlank { "Unknown" },
                                photoUri = draft.photoUri
                            )
                        )
                    },
                    enabled = selectedCategory.isNotBlank() && !state.busy,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("ocr:equipment_confirm")
                ) { Text("Confirm & Save Equipment", maxLines = 1, softWrap = false) }
            },
            dismissButton = {
                TactileTextButton(onClick = model::dismissEquipmentOcrDraft, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Discard Draft", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Existing Bean / Ingredient CRUD Form Dialog
    formKind?.let { kind ->
        val validCoffeeMetadata = kind != "Coffee" || (
            (form["altitude"].orEmpty().isBlank() || form["altitude"].orEmpty().toIntOrNull()?.let { it > 0 } == true) &&
                (form["roastDate"].orEmpty().isBlank() || runCatching { LocalDate.parse(form["roastDate"].orEmpty()) }.isSuccess) &&
                (
                    form["process"].orEmpty().isBlank() || runCatching {
                        ProcessMethod.valueOf(form["process"].orEmpty().uppercase())
                    }.isSuccess
                    ) &&
                (
                    form["roast"].orEmpty().isBlank() || runCatching {
                        RoastLevel.valueOf(form["roast"].orEmpty().uppercase())
                    }.isSuccess
                    )
            )
        val keys = if (kind == "Coffee") {
            listOf(
                "name" to "Name", "country" to "Country", "region" to "Region", "producer" to "Producer",
                "variety" to "Variety", "process" to "Process", "roast" to "Roast", "altitude" to "Altitude (m)",
                "roastDate" to "Roast date YYYY-MM-DD", "degasDays" to "Degas days", "density" to "Known density (g/mL)",
                "provenance" to "Source / provenance"
            )
        } else {
            listOf(
                "name" to "Name",
                "density" to "Known density (g/mL)",
                "allergens" to "Allergens, comma separated",
                "limit" to "Supplier maximum (% w/w)",
                "provenance" to "Supplier / provenance"
            )
        }
        AlertDialog(
            onDismissRequest = { formKind = null },
            title = { Text("${if (editingId == null) "Add" else "Edit"} ${if (kind == "Coffee") "coffee" else "ingredient"}") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    keys.forEach { (key, label) -> Field(label, form[key].orEmpty(), { form = form + (key to it) }, "catalog:$key") }
                    if (kind != "Coffee") {
                        Select(
                            "Food-grade evidence",
                            form["foodGrade"].orEmpty(),
                            listOf("Unknown", "Confirmed food grade", "Not food grade").map { it to it },
                            { form = form + ("foodGrade" to it) },
                            "catalog:foodGrade"
                        )
                    }
                    Text("Leave unknown facts blank. Unknown food-grade ingredients cannot be used in a drink.")
                }
            },
            confirmButton = {
                TactileTextButton(
                    onClick = {
                        val values = form + ("name" to form["name"].orEmpty())
                        if (kind == "Coffee") {
                            model.saveBean(editingId, values)
                            val detailed = DetailedBeanRecord(
                                id = editingId ?: UUID.randomUUID().toString(),
                                roaster = form["producer"].orEmpty().ifBlank { "Unknown" },
                                name = form["name"].orEmpty(),
                                originCountry = form["country"].orEmpty().ifBlank { "Unknown" },
                                region = form["region"].orEmpty(),
                                varietal = form["variety"].orEmpty(),
                                process = runCatching {
                                    ProcessMethod.valueOf(
                                        form["process"].orEmpty().uppercase()
                                    )
                                }.getOrDefault(ProcessMethod.UNKNOWN),
                                altitudeMeters = form["altitude"]?.toIntOrNull(),
                                roastDate = runCatching { LocalDate.parse(form["roastDate"].orEmpty()) }.getOrNull(),
                                roastLevel = runCatching { RoastLevel.valueOf(form["roast"].orEmpty().uppercase()) }
                                    .getOrDefault(RoastLevel.UNKNOWN)
                            )
                            model.addDetailedBean(detailed)
                        } else {
                            model.saveIngredient(editingId, values)
                        }
                        formKind = null
                    },
                    enabled = form["name"].orEmpty().isNotBlank() && validCoffeeMetadata && !state.busy,
                    modifier = Modifier.testTag("catalog:save")
                ) {
                    Text("Save entry", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = { TactileTextButton(onClick = { formKind = null }) { Text("Cancel", maxLines = 1, softWrap = false) } }
        )
    }
}

private fun beanForm(bean: CoffeeBean) = mapOf(
    "name" to bean.name, "country" to bean.country, "region" to bean.region,
    "producer" to bean.producer, "variety" to bean.variety, "process" to bean.process, "roast" to bean.roastLevel,
    "altitude" to bean.altitudeMeters?.toString().orEmpty(), "roastDate" to bean.roastDate.orEmpty(),
    "degasDays" to bean.degasDays?.toString().orEmpty(),
    "density" to bean.densityGramsPerMl?.toString().orEmpty(), "provenance" to bean.provenance
)
private enum class JournalTab {
    BREWS,
    CHAMPION_RECIPES,
    DIAL_IN,
    COMPARISON,
    ANALYTICS,
    CAFE_CUPS,
    TOOLS
}

@Composable
private fun ChampionRecipeCard(
    recipe: ChampionRecipe,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onLogToJournal: () -> Unit,
    onBrewOnTimer: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (catLabel, catBg, catFg) = when (recipe.category) {
                        RecipeCategory.COMPETITION -> Triple(
                            "🏆 Competition",
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        RecipeCategory.SIGNATURE -> Triple(
                            "✍️ Signature",
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        RecipeCategory.STANDARD -> Triple(
                            "☕ Traditional",
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = catBg) {
                        Text(
                            text = catLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = catFg,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    val (verLabel, verBg, verFg) = when (recipe.verificationStatus) {
                        RecipeVerificationStatus.OFFICIAL -> Triple(
                            "OFFICIAL",
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.primary
                        )
                        RecipeVerificationStatus.DOCUMENTED_RECREATION -> Triple(
                            "RECREATION",
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        RecipeVerificationStatus.SECONDARY_ADAPTATION -> Triple(
                            "ADAPTED",
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        RecipeVerificationStatus.NEEDS_VERIFICATION -> Triple(
                            "STANDARD",
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.outline
                        )
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = verBg) {
                        Text(
                            text = verLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = verFg,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                recipe.podium?.let { podium ->
                    Text(
                        text = "${podium.competitionName} '${podium.year.toString().takeLast(2)} · #${podium.rank}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "By ${recipe.baristaOrCreator} · ${recipe.equipment} · ${recipe.method}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DOSE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            "${recipe.doseGrams}g",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WATER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            "${recipe.waterGrams}g",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("RATIO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            recipe.ratio,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TEMP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            "${recipe.waterTemperatureCelsius.toInt()}°C",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        val m = recipe.totalTimeSeconds / 60
                        val s = recipe.totalTimeSeconds % 60
                        Text(
                            if (m > 0) "${m}m ${s}s" else "${s}s",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Text(
                text = "Grind: ${recipe.grindSize} · ${recipe.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (recipe.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    recipe.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (recipe.steps.isNotEmpty()) {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "chevron_rotation"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleExpand
                        )
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pour Routine (${recipe.steps.size} steps)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse steps" else "Expand steps",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeIn(),
                    exit = shrinkVertically(spring(dampingRatio = Spring.DampingRatioLowBouncy)) + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        recipe.steps.forEach { step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    val m = step.startOffsetSeconds / 60
                                    val s = step.startOffsetSeconds % 60
                                    val timeStr = "%02d:%02d".format(java.util.Locale.US, m, s)
                                    Text(
                                        text = timeStr,
                                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${step.action} (${step.waterGrams}g -> ${step.cumulativeWaterGrams}g accum)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (step.description.isNotBlank()) {
                                        Text(
                                            text = step.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TactileButton(
                    onClick = onLogToJournal,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(PRIMARY_ACTION_WEIGHT).height(36.dp).testTag("champion:log:${recipe.id}")
                ) {
                    Icon(Icons.Default.Book, null, Modifier.size(16.dp).padding(end = 4.dp))
                    Text("Log Brew", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                }

                TactileFilledTonalButton(
                    onClick = onBrewOnTimer,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(SECONDARY_ACTION_WEIGHT).height(36.dp).testTag("champion:brew:${recipe.id}")
                ) {
                    Icon(Icons.Default.PlayArrow, null, Modifier.size(16.dp).padding(end = 4.dp))
                    Text("Brew Timer", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                }

                TactileOutlinedButton(
                    onClick = onSaveTemplate,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp).testTag("champion:save:${recipe.id}")
                ) {
                    Icon(Icons.Default.Star, null, Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Journal(state: CoffeeUiState, model: CoffeeViewModel, edit: (Recipe) -> Unit) {
    val context = LocalContext.current
    var activeTab by rememberSaveable { mutableStateOf(JournalTab.BREWS) }
    var query by rememberSaveable { mutableStateOf("") }
    var championQuery by rememberSaveable { mutableStateOf("") }
    var selectedChampionCategory by rememberSaveable { mutableStateOf<RecipeCategory?>(null) }
    var selectedChampionMethod by rememberSaveable { mutableStateOf("All") }
    var expandedChampionRecipeId by rememberSaveable { mutableStateOf<String?>(null) }
    var showArchived by rememberSaveable { mutableStateOf(false) }
    var selectedMethodFilter by rememberSaveable { mutableStateOf("All") }
    var comparing by rememberSaveable { mutableStateOf<String?>(null) }
    var newCafeName by rememberSaveable { mutableStateOf("") }
    var newDrinkName by rememberSaveable { mutableStateOf("") }
    var newCafeRoaster by rememberSaveable { mutableStateOf("") }
    var newCafePrice by rememberSaveable { mutableStateOf("") }
    var newCafeRating by rememberSaveable { mutableStateOf("") }
    var newCafeNotes by rememberSaveable { mutableStateOf("") }
    var showAddCafeDialog by rememberSaveable { mutableStateOf(false) }
    var beanconquerorJsonInput by rememberSaveable { mutableStateOf("") }
    var backupJsonInput by rememberSaveable { mutableStateOf("") }
    var caffeineDoseInput by rememberSaveable { mutableStateOf(18.0) }
    var caffeineArabicaFraction by rememberSaveable { mutableStateOf(1.0) }
    var editingJournalNotesEntry by rememberSaveable { mutableStateOf<String?>(null) }

    Page("Journal") {
        item {
            Heading(
                "Brew journal & analytics",
                "Deterministic brew history, espresso dial-in progression, single-variable guidance, and local streaks."
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileFilterChip(
                    selected = activeTab == JournalTab.BREWS,
                    onClick = { activeTab = JournalTab.BREWS },
                    label = {
                        Text(
                            "Brews & Logs",
                            fontWeight = if (activeTab == JournalTab.BREWS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Book, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_brews").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.CHAMPION_RECIPES,
                    onClick = { activeTab = JournalTab.CHAMPION_RECIPES },
                    label = {
                        Text(
                            "World Recipes (80)",
                            fontWeight = if (activeTab == JournalTab.CHAMPION_RECIPES) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Star, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_champion_recipes").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.DIAL_IN,
                    onClick = { activeTab = JournalTab.DIAL_IN },
                    label = {
                        Text(
                            "Espresso Dial-In",
                            fontWeight = if (activeTab == JournalTab.DIAL_IN) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_dialin").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.COMPARISON,
                    onClick = { activeTab = JournalTab.COMPARISON },
                    label = {
                        Text(
                            "Compare (${if (state.activeComparison != null) "Active" else "Select"})",
                            fontWeight = if (activeTab == JournalTab.COMPARISON) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.CompareArrows, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_compare").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.ANALYTICS,
                    onClick = {
                        activeTab = JournalTab.ANALYTICS
                        model.refreshStreakAndStats()
                        model.estimateCaffeineForDose(caffeineDoseInput, caffeineArabicaFraction)
                    },
                    label = {
                        Text(
                            "Analytics & Streak",
                            fontWeight = if (activeTab == JournalTab.ANALYTICS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.BarChart, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_analytics").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.CAFE_CUPS,
                    onClick = { activeTab = JournalTab.CAFE_CUPS },
                    label = {
                        Text(
                            "Cafe Cups (${state.cafeCupEntries.size})",
                            fontWeight = if (activeTab == JournalTab.CAFE_CUPS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.LocalCafe, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_cafecups").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.TOOLS,
                    onClick = { activeTab = JournalTab.TOOLS },
                    label = {
                        Text(
                            "Export & Tools",
                            fontWeight = if (activeTab == JournalTab.TOOLS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.FileDownload, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_tools").heightIn(min = 48.dp)
                )
            }
        }

        when (activeTab) {
            JournalTab.BREWS -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            Field("Search brews / beans / notes", query, { query = it }, "journal:query")
                        }
                        TactileFilterChip(
                            selected = showArchived,
                            onClick = { showArchived = !showArchived },
                            label = { Text("Archived") },
                            modifier = Modifier.heightIn(min = 44.dp).testTag("journal:show_archived")
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "V60", "Espresso", "AeroPress", "Chemex", "High Rated").forEach { filter ->
                            TactileFilterChip(
                                selected = selectedMethodFilter == filter,
                                onClick = { selectedMethodFilter = filter },
                                label = {
                                    Text(
                                        filter,
                                        fontWeight = if (selectedMethodFilter == filter) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TactileButton(
                            onClick = { model.repeatLastBrew() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.weight(PRIMARY_ACTION_WEIGHT).heightIn(min = 44.dp).testTag("journal:repeat_last_brew")
                        ) {
                            Text("⚡ Repeat Last", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                        TactileFilledTonalButton(
                            onClick = { activeTab = JournalTab.CHAMPION_RECIPES },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(SECONDARY_ACTION_WEIGHT)
                                .heightIn(min = 44.dp)
                                .testTag("journal:nav_champion_recipes")
                        ) {
                            Icon(Icons.Default.Star, null, Modifier.size(16.dp).padding(end = 4.dp))
                            Text("World (80)", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                        TactileOutlinedButton(
                            onClick = { model.exportJournalCsv(context) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.heightIn(min = 44.dp).testTag("journal:export_csv_quick")
                        ) {
                            Text("Export CSV", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                val visibleEntries = state.journalEntries.filter { entry ->
                    val matchesQuery = query.isBlank() ||
                        entry.beanName.contains(query, ignoreCase = true) ||
                        entry.brewMethod.contains(query, ignoreCase = true) ||
                        entry.generalNotes.contains(query, ignoreCase = true) ||
                        entry.tags.any { it.contains(query, ignoreCase = true) }

                    val matchesMethod = when (selectedMethodFilter) {
                        "All" -> true
                        "High Rated" -> (entry.rating ?: 0.0) >= 8.5
                        else -> entry.brewMethod.contains(selectedMethodFilter, ignoreCase = true)
                    }
                    matchesQuery && matchesMethod
                }

                if (visibleEntries.isEmpty()) {
                    item {
                        InfoCard(
                            "Your brew history is empty",
                            "Complete a timer session or add a brew log with the values you observed.\n" +
                                "Or explore 80 World Champion and Signature recipes to log your first brew!"
                        ) {
                            TactileButton(
                                onClick = { activeTab = JournalTab.CHAMPION_RECIPES },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("journal:explore_champion_recipes_cta")
                            ) {
                                Icon(Icons.Default.Star, null, Modifier.padding(end = 6.dp))
                                Text("Explore 80 World Champion Recipes")
                            }
                        }
                    }
                }

                items(visibleEntries, key = { it.id }) { entry ->
                    InfoCard(
                        "${entry.beanName} · ${entry.brewMethod}",
                        listOf(
                            "${entry.doseGrams}g dose / " +
                                (if (entry.timerOnly) "recipe target " else "") +
                                "${entry.waterGrams}g water (1:${entry.ratioDenominator}) · " +
                                "${entry.brewDurationSeconds}s · ${entry.waterTemperatureCelsius}°C",
                            "Grind: ${entry.grinderModel.ifBlank { "Burr Grinder" }} [${entry.grindSetting}] · " +
                                "TDS: ${entry.measuredTdsPercent?.let { "$it%" } ?: "—"} · " +
                                "Approx EY: ${entry.extractionYieldPercent?.let { "$it%" } ?: "—"}",
                            "Rating: ⭐ ${entry.rating ?: "—"}/10 · Tags: ${entry.tags.joinToString(", ")}",
                            if (entry.tasteNotes.isNotEmpty()) "Notes: ${entry.tasteNotes.joinToString(", ")}" else "",
                            if (entry.generalNotes.isNotBlank()) "\"${entry.generalNotes}\"" else ""
                        ).filter { it.isNotBlank() }.joinToString("\n")
                    ) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val recipe = state.recipes.find { it.id == entry.recipeId }
                            if (recipe != null) {
                                TactileTextButton(
                                    onClick = { edit(recipe) },
                                    modifier = Modifier.heightIn(min = 44.dp).testTag("journal:edit:${recipe.id}")
                                ) {
                                    Text("Repeat / edit", maxLines = 1, softWrap = false)
                                }
                            }
                            TactileTextButton(onClick = {
                                model.selectComparisonSlot(entry, "A")
                                activeTab = JournalTab.COMPARISON
                            }, modifier = Modifier.heightIn(min = 44.dp)) { Text("Set Slot A", maxLines = 1, softWrap = false) }
                            TactileTextButton(onClick = {
                                model.selectComparisonSlot(entry, "B")
                                activeTab = JournalTab.COMPARISON
                            }, modifier = Modifier.heightIn(min = 44.dp)) { Text("Set Slot B", maxLines = 1, softWrap = false) }
                            TactileTextButton(onClick = {
                                model.evaluateOvatForEntry(entry)
                                activeTab = JournalTab.DIAL_IN
                            }, modifier = Modifier.heightIn(min = 44.dp)) { Text("OVAT Coach", maxLines = 1, softWrap = false) }
                            TactileTextButton(onClick = {
                                model.saveEntryAsTemplate(entry, "${entry.beanName} ${entry.brewMethod}")
                            }, modifier = Modifier.heightIn(min = 44.dp)) { Text("Save Template", maxLines = 1, softWrap = false) }
                            TactileTextButton(onClick = {
                                editingJournalNotesEntry = entry.id
                            }, modifier = Modifier.heightIn(min = 44.dp)) { Text("Wheel Notes", maxLines = 1, softWrap = false) }
                            TactileTextButton(
                                onClick = {
                                    model.deleteJournalEntry(entry.id)
                                },
                                modifier = Modifier.heightIn(
                                    min = 44.dp
                                )
                            ) { Text("Delete", color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false) }
                        }
                    }
                }

                if (state.recipes.isNotEmpty()) {
                    item {
                        Heading("Formulated Recipes & Revisions", "Stored recipes with exact measurements and revision history.")
                    }
                    items(state.recipes, key = { "recipe_${it.id}" }) { recipe ->
                        val ratioStr = if (recipe.doseGrams > 0.0) {
                            String.format(
                                java.util.Locale.US,
                                "1:%.1f",
                                recipe.waterGrams / recipe.doseGrams
                            )
                        } else {
                            "—"
                        }
                        InfoCard(
                            "${recipe.name} · v${recipe.version}",
                            listOf(
                                "${recipe.doseGrams}g dose / ${recipe.waterGrams}g water ($ratioStr) · ${recipe.temperatureCelsius}°C",
                                "TDS: ${recipe.measuredTdsPercent?.let { "$it%" } ?: "not measured"} · " +
                                    "Beverage: ${recipe.beverageMassGrams?.let { "${it}g" } ?: "not measured"}",
                                "Method: ${recipe.brewMethod} · Time: ${recipe.brewTimeSeconds}s"
                            ).joinToString("\n")
                        ) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TactileButton(
                                    onClick = { edit(recipe) },
                                    modifier = Modifier.heightIn(min = 44.dp).testTag("journal:edit:${recipe.id}")
                                ) {
                                    Text("Edit / Revision", maxLines = 1, softWrap = false)
                                }
                                if (state.recipes.count { it.revisionGroupId == recipe.revisionGroupId } > 1) {
                                    TactileOutlinedButton(
                                        onClick = { comparing = recipe.revisionGroupId },
                                        modifier = Modifier.heightIn(min = 44.dp)
                                    ) {
                                        Text("Compare versions", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            JournalTab.CHAMPION_RECIPES -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            Field(
                                "Search 80 recipes (e.g. Kasuya, V60, Hoffmann)",
                                championQuery,
                                { championQuery = it },
                                "champion:search"
                            )
                        }
                        if (championQuery.isNotBlank() || selectedChampionCategory != null || selectedChampionMethod != "All") {
                            TactileTextButton(
                                onClick = {
                                    championQuery = ""
                                    selectedChampionCategory = null
                                    selectedChampionMethod = "All"
                                },
                                modifier = Modifier.heightIn(min = 44.dp).testTag("champion:reset_filters")
                            ) {
                                Text("Reset", maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TactileFilterChip(
                            selected = selectedChampionCategory == null,
                            onClick = { selectedChampionCategory = null },
                            label = {
                                Text(
                                    "All (80)",
                                    fontWeight = if (selectedChampionCategory == null) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.heightIn(min = 44.dp).testTag("champion:filter_cat_all")
                        )
                        TactileFilterChip(
                            selected = selectedChampionCategory == RecipeCategory.COMPETITION,
                            onClick = { selectedChampionCategory = RecipeCategory.COMPETITION },
                            label = {
                                Text(
                                    "🏆 Competition (21)",
                                    fontWeight = if (selectedChampionCategory == RecipeCategory.COMPETITION) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            },
                            modifier = Modifier.heightIn(min = 44.dp).testTag("champion:filter_cat_competition")
                        )
                        TactileFilterChip(
                            selected = selectedChampionCategory == RecipeCategory.SIGNATURE,
                            onClick = { selectedChampionCategory = RecipeCategory.SIGNATURE },
                            label = {
                                Text(
                                    "✍️ Signature (14)",
                                    fontWeight = if (selectedChampionCategory == RecipeCategory.SIGNATURE) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            },
                            modifier = Modifier.heightIn(min = 44.dp).testTag("champion:filter_cat_signature")
                        )
                        TactileFilterChip(
                            selected = selectedChampionCategory == RecipeCategory.STANDARD,
                            onClick = { selectedChampionCategory = RecipeCategory.STANDARD },
                            label = {
                                Text(
                                    "☕ Traditional (45)",
                                    fontWeight = if (selectedChampionCategory == RecipeCategory.STANDARD) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            },
                            modifier = Modifier.heightIn(min = 44.dp).testTag("champion:filter_cat_traditional")
                        )
                    }
                }

                item {
                    val methodsList = listOf(
                        "All", "V60", "AeroPress", "Switch", "Origami / Flat Bed",
                        "Espresso", "Cold Brew / Drip", "Immersion / Press", "Heritage / Pot"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        methodsList.forEach { method ->
                            TactileFilterChip(
                                selected = selectedChampionMethod == method,
                                onClick = { selectedChampionMethod = method },
                                label = {
                                    Text(
                                        method,
                                        fontWeight = if (selectedChampionMethod == method) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.heightIn(min = 44.dp).testTag("champion:filter_method_$method")
                            )
                        }
                    }
                }

                val filteredChampionRecipes = WorldChampionRecipeCatalog.entries.filter { recipe ->
                    val matchesCategory = selectedChampionCategory == null || recipe.category == selectedChampionCategory
                    val matchesMethod = when (selectedChampionMethod) {
                        "All" -> true
                        "V60" -> recipe.equipment.contains("V60", ignoreCase = true) ||
                            recipe.method.contains("V60", ignoreCase = true)
                        "AeroPress" -> recipe.equipment.contains("AeroPress", ignoreCase = true) ||
                            recipe.method.contains("AeroPress", ignoreCase = true)
                        "Switch" -> recipe.equipment.contains("Switch", ignoreCase = true)
                        "Origami / Flat Bed" -> recipe.equipment.contains("Origami", ignoreCase = true) ||
                            recipe.equipment.contains("Orea", ignoreCase = true) ||
                            recipe.equipment.contains("Kalita", ignoreCase = true) ||
                            recipe.equipment.contains("Wave", ignoreCase = true)
                        "Espresso" -> recipe.equipment.contains("Espresso", ignoreCase = true) ||
                            recipe.method.contains("Espresso", ignoreCase = true)
                        "Cold Brew / Drip" -> recipe.method.contains("Cold", ignoreCase = true)
                        "Immersion / Press" -> recipe.equipment.contains("French Press", ignoreCase = true) ||
                            recipe.equipment.contains("Clever", ignoreCase = true) ||
                            recipe.method.contains("Immersion", ignoreCase = true)
                        "Heritage / Pot" -> recipe.equipment.contains("Phin", ignoreCase = true) ||
                            recipe.equipment.contains("Moka", ignoreCase = true) ||
                            recipe.equipment.contains("Tubruk", ignoreCase = true) ||
                            recipe.equipment.contains("Ibrik", ignoreCase = true) ||
                            recipe.equipment.contains("Cezve", ignoreCase = true) ||
                            recipe.equipment.contains("Jebena", ignoreCase = true)
                        else -> recipe.method.contains(selectedChampionMethod, ignoreCase = true) ||
                            recipe.equipment.contains(selectedChampionMethod, ignoreCase = true)
                    }
                    val matchesQuery = championQuery.isBlank() ||
                        recipe.name.contains(championQuery, ignoreCase = true) ||
                        recipe.baristaOrCreator.contains(championQuery, ignoreCase = true) ||
                        recipe.method.contains(championQuery, ignoreCase = true) ||
                        recipe.equipment.contains(championQuery, ignoreCase = true) ||
                        recipe.tags.any { it.contains(championQuery, ignoreCase = true) }

                    matchesCategory && matchesMethod && matchesQuery
                }

                item {
                    Text(
                        "Showing ${filteredChampionRecipes.size} of 80 recipes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                if (filteredChampionRecipes.isEmpty()) {
                    item {
                        InfoCard("No recipes match criteria", "Try adjusting your query or category filters.")
                    }
                }

                items(filteredChampionRecipes, key = { it.id }) { recipe ->
                    ChampionRecipeCard(
                        recipe = recipe,
                        isExpanded = expandedChampionRecipeId == recipe.id,
                        onToggleExpand = {
                            expandedChampionRecipeId = if (expandedChampionRecipeId == recipe.id) null else recipe.id
                        },
                        onLogToJournal = {
                            model.logChampionRecipeToJournal(recipe)
                        },
                        onBrewOnTimer = {
                            model.brewChampionRecipe(recipe)
                            edit(recipe.toRecipe())
                        },
                        onSaveTemplate = {
                            model.saveChampionRecipeAsTemplate(recipe)
                        },
                        modifier = Modifier.testTag("champion:card:${recipe.id}")
                    )
                }
            }

            JournalTab.DIAL_IN -> {
                item {
                    InfoCard(
                        "Espresso Dial-In Progression",
                        "Evaluate target vs actual extraction parameters, track flow rate dynamics, " +
                            "and receive single-variable adjustment advice."
                    )
                }

                state.activeOvatRecommendation?.let { rec ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            )
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "🎯 OVAT Recommendation",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    MicroBadgePill(
                                        text = "${rec.targetVariable.name} → ${rec.direction.name}",
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    "Adjustment: ${rec.suggestedMagnitude}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontFeatureSettings = "tnum"
                                    )
                                )
                                Text("Physical Mechanism: ${rec.physicalReasoning}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Expected Impact: ${rec.expectedImpact}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                val espressoEntries = state.journalEntries.filter { it.isEspresso || it.brewMethod.contains("Espresso", true) }
                if (espressoEntries.isEmpty()) {
                    item { InfoCard("No espresso shots logged", "Complete an espresso timer or add a shot using values you observed.") }
                }

                items(espressoEntries, key = { it.id }) { shot ->
                    val esp = shot.espressoDetails
                    val doseTarget = esp?.let {
                        val delta = (shot.doseGrams - it.targetDoseGrams)
                            .let { value -> if (value >= 0) "+$value" else "$value" }
                        " (Target: ${it.targetDoseGrams}g, Δ: ${delta}g)"
                    }.orEmpty()
                    val liquidYield = shot.beverageMassGrams ?: shot.waterGrams
                    val yieldTarget = esp?.let {
                        val delta = (liquidYield - it.targetYieldGrams)
                            .let { value -> if (value >= 0) "+$value" else "$value" }
                        " (Target: ${it.targetYieldGrams}g, Δ: ${delta}g)"
                    }.orEmpty()
                    val timeTarget = esp?.let {
                        val delta = shot.brewDurationSeconds - it.targetDurationSeconds
                        " (Target: ${it.targetDurationSeconds}s, Δ: ${delta}s)"
                    }.orEmpty()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Espresso Shot: ${shot.beanName}",
                                modifier = Modifier.semantics { heading() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (shot.grinderModel.isNotBlank()) MicroBadgePill(text = "Grinder: ${shot.grinderModel}")
                                if (shot.grindSetting.isNotBlank()) MicroBadgePill(text = "Setting: ${shot.grindSetting}")
                                if (esp?.meanFlowRateGps != null) MicroBadgePill(text = "Flow: ${esp.meanFlowRateGps} g/s")
                                if (shot.measuredTdsPercent != null) MicroBadgePill(text = "TDS: ${shot.measuredTdsPercent}%")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                GrindRangeTile(
                                    label = "Dose",
                                    range = "${shot.doseGrams}g",
                                    modifier = Modifier.weight(1f)
                                )
                                GrindRangeTile(
                                    label = "Yield",
                                    range = "${liquidYield}g",
                                    modifier = Modifier.weight(1f)
                                )
                                GrindRangeTile(
                                    label = "Time",
                                    range = "${shot.brewDurationSeconds}s",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (doseTarget.isNotBlank() || yieldTarget.isNotBlank() || timeTarget.isNotBlank()) {
                                Text(
                                    text = "Targets & Deltas: $doseTarget $yieldTarget $timeTarget".trim(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TactileButton(onClick = { model.evaluateOvatForEntry(shot) }) {
                                Text("Diagnose with OVAT Coach", maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }

            JournalTab.COMPARISON -> {
                item {
                    InfoCard(
                        "Side-by-Side Session Comparison",
                        "Compare two brewing sessions with deterministic numeric deltas across dose, yield, ratio, time, TDS, and EY."
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(Modifier.weight(1f)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Slot A (Baseline)", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    state.selectedComparisonA?.let {
                                        "${it.beanName}\n${it.brewMethod} " +
                                            "(${it.doseGrams}g/${it.waterGrams}g)"
                                    } ?: "No brew selected",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Card(Modifier.weight(1f)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Slot B (Target)", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    state.selectedComparisonB?.let {
                                        "${it.beanName}\n${it.brewMethod} " +
                                            "(${it.doseGrams}g/${it.waterGrams}g)"
                                    } ?: "No brew selected",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileOutlinedButton(onClick = { model.clearComparison() }) {
                            Text("Clear Comparison", maxLines = 1, softWrap = false)
                        }
                    }
                }

                state.activeComparison?.let { comp ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("📊 Deterministic Numeric Deltas (B minus A)", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "• Coffee Dose Δ: " +
                                        "${if (comp.doseDeltaGrams >= 0) "+${comp.doseDeltaGrams}" else comp.doseDeltaGrams} g",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Water Mass Δ: " +
                                        "${if (comp.waterDeltaGrams >= 0) "+${comp.waterDeltaGrams}" else comp.waterDeltaGrams} g",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Brew Ratio Δ: ${if (comp.ratioDelta >= 0) "+${comp.ratioDelta}" else "${comp.ratioDelta}"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Duration Δ: " +
                                        (
                                            if (comp.durationDeltaSeconds >= 0) {
                                                "+${comp.durationDeltaSeconds}"
                                            } else {
                                                "${comp.durationDeltaSeconds}"
                                            }
                                            ) + " s",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Measured TDS Δ: ${comp.tdsDeltaPercent?.let { "${if (it >= 0) "+$it" else "$it"}%" } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Extraction Yield Δ: ${comp.eyDeltaPercent?.let { "${if (it >= 0) "+$it" else "$it"}%" } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Rating Δ: ${comp.ratingDelta?.let { "${if (it >= 0) "+$it" else "$it"}" } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Water Temp Δ: " +
                                        (
                                            if (comp.temperatureDeltaCelsius >= 0) {
                                                "+${comp.temperatureDeltaCelsius}"
                                            } else {
                                                "${comp.temperatureDeltaCelsius}"
                                            }
                                            ) + " °C",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Select a brew below to assign to Slot A or Slot B:", style = MaterialTheme.typography.titleSmall)
                }

                items(state.journalEntries.take(6), key = { it.id }) { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${entry.beanName} (${entry.brewMethod})", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        TactileTextButton(onClick = { model.selectComparisonSlot(entry, "A") }) {
                            Text("Slot A", maxLines = 1, softWrap = false)
                        }
                        TactileTextButton(onClick = { model.selectComparisonSlot(entry, "B") }) {
                            Text("Slot B", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }

            JournalTab.ANALYTICS -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🔥 Local Brewing Streak", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Current Streak: ${state.brewStreak?.currentStreakDays ?: 0} Days Active",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Longest Recorded Streak: ${state.brewStreak?.longestStreakDays ?: 0} Days",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Total Days Brewed: ${state.brewStreak?.activeDaysCount ?: 0} Days",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val stats = state.monthlyStats
                            val monthLabel = stats?.let { "${it.year}-${it.month.toString().padStart(2, '0')}" } ?: "No month recorded"
                            Text(
                                "📅 Monthly Consumption Stats ($monthLabel)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("• Total Brew Sessions: ${stats?.totalBrewsCount ?: 0}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "• Total Dry Coffee Consumed: ${stats?.totalDoseGrams ?: 0.0} g",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "• Preferred Brewing Method: ${stats?.topBrewMethod ?: "None"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "• Average Rating: ${stats?.averageRating?.let { "⭐ $it / 10" } ?: "Not rated"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "• Average Extraction Yield: ${stats?.averageExtractionYield?.let { "$it%" } ?: "Not measured"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("☕ Caffeine Intake Estimator (S2-050)", style = MaterialTheme.typography.titleMedium)
                            Text("Adjust dose and species blend to estimate dissolved caffeine mass:")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Dose: ${"%.1f".format(java.util.Locale.US, caffeineDoseInput)} g", Modifier.weight(1f))
                                Slider(
                                    value = caffeineDoseInput.toFloat(),
                                    onValueChange = {
                                        caffeineDoseInput = it.toDouble()
                                        model.estimateCaffeineForDose(caffeineDoseInput, caffeineArabicaFraction)
                                    },
                                    valueRange = 10f..35f,
                                    steps = 24,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TactileFilterChip(
                                    selected = caffeineArabicaFraction >= 0.99,
                                    onClick = {
                                        caffeineArabicaFraction = 1.0
                                        model.estimateCaffeineForDose(caffeineDoseInput, 1.0)
                                    },
                                    label = { Text("100% Arabica", maxLines = 1, softWrap = false) }
                                )
                                TactileFilterChip(
                                    selected = caffeineArabicaFraction in 0.4..0.6,
                                    onClick = {
                                        caffeineArabicaFraction = CAFFEINE_BLEND_ARABICA_FRACTION
                                        model.estimateCaffeineForDose(caffeineDoseInput, CAFFEINE_BLEND_ARABICA_FRACTION)
                                    },
                                    label = { Text("50/50 Blend", maxLines = 1, softWrap = false) }
                                )
                                TactileFilterChip(
                                    selected = caffeineArabicaFraction <= 0.01,
                                    onClick = {
                                        caffeineArabicaFraction = 0.0
                                        model.estimateCaffeineForDose(caffeineDoseInput, 0.0)
                                    },
                                    label = { Text("100% Robusta", maxLines = 1, softWrap = false) }
                                )
                            }
                            state.activeCaffeineEstimate?.let { est ->
                                Text("Estimated Caffeine: ~${est.estimatedCaffeineMg} mg", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Uncertainty Range: [${est.uncertaintyRangeMg.first} mg — ${est.uncertaintyRangeMg.second} mg]",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text("Species Basis: ${est.speciesRatioDescription}", style = MaterialTheme.typography.bodySmall)
                                Text(est.disclaimer, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            JournalTab.CAFE_CUPS -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Cafe Cup Logbook", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Manual entry · Zero cloud maps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TactileButton(
                            onClick = { showAddCafeDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Cup", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (showAddCafeDialog) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Log Specialty Cafe Visit", style = MaterialTheme.typography.titleMedium)
                                Field("Cafe Name", newCafeName, { newCafeName = it }, "cafecup:name")
                                Field("Roaster", newCafeRoaster, { newCafeRoaster = it }, "cafecup:roaster")
                                Field("Drink Name", newDrinkName, { newDrinkName = it }, "cafecup:drink")
                                Field("Price Paid", newCafePrice, { newCafePrice = it }, "cafecup:price")
                                Field("Rating (1-10)", newCafeRating, { newCafeRating = it }, "cafecup:rating")
                                Field("Tasting Notes", newCafeNotes, { newCafeNotes = it }, "cafecup:notes")
                                val cafeRating = newCafeRating.toDoubleOrNull()?.takeIf { it.isFinite() && it in 1.0..10.0 }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TactileButton(onClick = {
                                        if (newCafeName.isNotBlank() && newDrinkName.isNotBlank() && cafeRating != null) {
                                            model.saveCafeCup(
                                                CafeCupEntry(
                                                    id = UUID.randomUUID().toString(),
                                                    cafeName = newCafeName,
                                                    roaster = newCafeRoaster,
                                                    drinkName = newDrinkName,
                                                    pricePaid = newCafePrice,
                                                    rating = cafeRating,
                                                    notes = newCafeNotes
                                                )
                                            )
                                            newCafeName = ""
                                            newDrinkName = ""
                                            newCafeRoaster = ""
                                            newCafePrice = ""
                                            newCafeRating = ""
                                            newCafeNotes = ""
                                            showAddCafeDialog = false
                                        }
                                    }, enabled = newCafeName.isNotBlank() && newDrinkName.isNotBlank() && cafeRating != null) {
                                        Text("Save Cafe Cup", maxLines = 1, softWrap = false)
                                    }
                                    TactileTextButton(onClick = { showAddCafeDialog = false }) {
                                        Text("Cancel", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }
                }

                if (state.cafeCupEntries.isEmpty()) {
                    item { InfoCard("No cafe visits logged yet", "Tap '+ Add Cafe Cup' to record an artisan coffee experience.") }
                }

                items(state.cafeCupEntries, key = { it.id }) { cup ->
                    InfoCard(
                        "${cup.drinkName} @ ${cup.cafeName}",
                        listOf(
                            "Roaster: ${cup.roaster.ifBlank { "Not recorded" }} · Rating: ⭐ ${cup.rating}/10",
                            "Price: ${cup.pricePaid.ifBlank { "—" }}",
                            if (cup.notes.isNotBlank()) "\"${cup.notes}\"" else ""
                        ).filter { it.isNotBlank() }.joinToString("\n")
                    ) {
                        TactileTextButton(onClick = { model.deleteCafeCup(cup.id) }) {
                            Text("Delete", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }

            JournalTab.TOOLS -> {
                item {
                    InfoCard(
                        "Data Portability & Migration (S2-047, S2-048)",
                        "Export complete journal history to standard CSV or import JSON backups from Beanconqueror."
                    ) {
                        TactileButton(onClick = { model.exportJournalCsv(context) }) {
                            Text("Export Journal to CSV", maxLines = 1, softWrap = false)
                        }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📥 Beanconqueror JSON Importer (S2-048)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Paste Beanconqueror export JSON below to migrate beans and brew logs into Coffee Creator:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = beanconquerorJsonInput,
                                onValueChange = { beanconquerorJsonInput = it },
                                label = { Text("Beanconqueror JSON payload") },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
                            )
                            TactileButton(
                                onClick = {
                                    if (beanconquerorJsonInput.isNotBlank()) {
                                        model.importBeanconquerorJson(beanconquerorJsonInput)
                                        beanconquerorJsonInput = ""
                                    }
                                },
                                enabled = beanconquerorJsonInput.isNotBlank()
                            ) {
                                Text("Import Beanconqueror Records", maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔍 Backup Import Conflict Preview (S2-047)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Paste Coffee Creator JSON backup to detect 3-way conflicts before writing:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = backupJsonInput,
                                onValueChange = { backupJsonInput = it },
                                label = { Text("Backup JSON envelope") },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TactileButton(
                                    onClick = { model.previewBackup(backupJsonInput) },
                                    enabled = backupJsonInput.isNotBlank()
                                ) {
                                    Text("Preview Conflicts", maxLines = 1, softWrap = false)
                                }
                                if (state.importConflictPreview != null) {
                                    TactileTextButton(onClick = { model.clearConflictPreview() }) {
                                        Text("Clear Preview", maxLines = 1, softWrap = false)
                                    }
                                }
                            }

                            state.importConflictPreview?.let { preview ->
                                Text(
                                    "New Records: ${preview.newRecordsCount} · " +
                                        "Identical: ${preview.identicalCount} · Conflicting: ${preview.conflictCount}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                preview.items.take(5).forEach { item ->
                                    Text("• [${item.status}] ID: ${item.recordId}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    comparing?.let { group ->
        AlertDialog(onDismissRequest = { comparing = null }, title = { Text("Revision comparison") }, text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.recipes.filter { it.revisionGroupId == group }.sortedBy { it.version }.forEach { recipe ->
                    Text(
                        "v${recipe.version}: ${recipe.doseGrams} g / ${recipe.waterGrams} g · ${recipe.temperatureCelsius} °C\n" +
                            "TDS ${recipe.measuredTdsPercent ?: "not measured"} · beverage ${recipe.beverageMassGrams ?: "not measured"} g"
                    )
                    state.observations.filter {
                        it.recipeId == recipe.id
                    }.forEach { observation ->
                        Text(
                            "Observed by ${observation.assessor.ifBlank {
                                "unspecified assessor"
                            }}: ${observation.tastingDescriptors.joinToString()}\n${observation.notes}"
                        )
                    }
                    state.estimates.filter { it.recipeId == recipe.id }.forEach { estimated ->
                        Text("ESTIMATED: ${estimated.profile.basis}\n${estimated.profile.uncertaintyDisclaimer}")
                    }
                    HorizontalDivider()
                }
            }
        }, confirmButton = { TextButton(onClick = { comparing = null }) { Text("Close") } })
    }

    if (editingJournalNotesEntry != null) {
        val entryToEdit = state.journalEntries.find { it.id == editingJournalNotesEntry }
        if (entryToEdit != null) {
            FlavorWheelPickerDialog(
                initialSelected = entryToEdit.tasteNotes,
                onDismissRequest = { editingJournalNotesEntry = null },
                onConfirm = { updatedNotes ->
                    model.updateJournalEntryTasteNotes(entryToEdit.id, updatedNotes)
                    editingJournalNotesEntry = null
                }
            )
        } else {
            editingJournalNotesEntry = null
        }
    }
}

private enum class SensoryTab {
    WHEEL,
    ACCESSIBLE_LIST,
    SESSIONS,
    CONSENSUS,
    PHYSICAL_QC,
    CALIBRATION,
    ACADEMY,
    CVA_CUPPING,
    TRAINING_LAB,
    GREEN_QC,
    OPERATIONS
}

@Composable
private fun Sensory(state: CoffeeUiState, model: CoffeeViewModel) {
    var activeTab by rememberSaveable { mutableStateOf(SensoryTab.WHEEL) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryFilter by rememberSaveable { mutableStateOf("All") }

    // Cupping session dialogs
    var showCreateSessionDialog by rememberSaveable { mutableStateOf(false) }
    var newSessionTitle by rememberSaveable { mutableStateOf("") }
    var newSessionLocation by rememberSaveable { mutableStateOf("") }
    var newSessionTemplate by rememberSaveable { mutableStateOf(CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT) }
    var newSessionMasking by rememberSaveable { mutableStateOf(MaskingMode.SINGLE_BLIND_EVALUATOR) }
    var newSessionEvaluators by rememberSaveable { mutableStateOf("") }
    var newSessionSamples by rememberSaveable { mutableStateOf("") }

    // Scoring dialog
    var scoringSample by rememberSaveable { mutableStateOf<String?>(null) }
    var evaluatorIdInput by rememberSaveable { mutableStateOf("") }
    var scoreDescriptors by rememberSaveable { mutableStateOf("") }
    var scoreTaints by rememberSaveable { mutableStateOf("") }
    var scoreFaults by rememberSaveable { mutableStateOf("") }
    var scoreNotes by rememberSaveable { mutableStateOf("") }
    var showWheelPickerForSample by rememberSaveable { mutableStateOf(false) }

    // Reopen dialog
    var showReopenDialog by rememberSaveable { mutableStateOf(false) }
    var reopenReasonInput by rememberSaveable { mutableStateOf("") }

    // Physical QC State
    var greenMoistureInput by rememberSaveable { mutableStateOf("") }
    var greenAwInput by rememberSaveable { mutableStateOf("") }
    var greenFullBlackInput by rememberSaveable { mutableStateOf("") }
    var greenPartialBlackInput by rememberSaveable { mutableStateOf("") }
    var greenBrokenInput by rememberSaveable { mutableStateOf("") }
    var agtronWholeInput by rememberSaveable { mutableStateOf("") }
    var agtronGroundInput by rememberSaveable { mutableStateOf("") }

    // Calibration State
    var calibrationBatchVolumeInput by rememberSaveable { mutableStateOf("") }

    // Roast Rest Dialog
    var showLogObsDialog by rememberSaveable { mutableStateOf(false) }
    var obsBagIdInput by rememberSaveable { mutableStateOf("") }
    var obsDaysInput by rememberSaveable { mutableStateOf("") }
    var obsScoreInput by rememberSaveable { mutableStateOf("") }
    var obsAromaInput by rememberSaveable { mutableStateOf("") }
    var obsAcidityInput by rememberSaveable { mutableStateOf("") }
    var obsSweetnessInput by rememberSaveable { mutableStateOf("") }
    var obsRoastLevelInput by rememberSaveable { mutableStateOf("") }
    var obsProcessInput by rememberSaveable { mutableStateOf("") }
    var obsPerceivedCarbonic by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var obsNotesInput by rememberSaveable { mutableStateOf("") }

    val activeSession = state.cuppingSessions.find { it.id == state.activeCuppingSessionId }
        ?: state.cuppingSessions.firstOrNull()

    Page("Sensory") {
        item {
            Heading(
                "Sensory wheel & professional cupping",
                "Deterministic flavor taxonomy, blind cupping sessions, multi-evaluator consensus, physical QC, and roast-rest tracking."
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileFilterChip(
                    selected = activeTab == SensoryTab.WHEEL,
                    onClick = { activeTab = SensoryTab.WHEEL },
                    label = {
                        Text(
                            "Flavor Wheel",
                            fontWeight = if (activeTab == SensoryTab.WHEEL) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Psychology, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_wheel").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.ACCESSIBLE_LIST,
                    onClick = { activeTab = SensoryTab.ACCESSIBLE_LIST },
                    label = {
                        Text(
                            "Accessible List",
                            fontWeight = if (activeTab == SensoryTab.ACCESSIBLE_LIST) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.MenuBook, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_accessible").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.SESSIONS,
                    onClick = { activeTab = SensoryTab.SESSIONS },
                    label = {
                        Text(
                            "Cupping Sessions (${state.cuppingSessions.size})",
                            fontWeight = if (activeTab == SensoryTab.SESSIONS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Coffee, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_sessions").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.CONSENSUS,
                    onClick = { activeTab = SensoryTab.CONSENSUS },
                    label = {
                        Text(
                            "Consensus & Radar",
                            fontWeight = if (activeTab == SensoryTab.CONSENSUS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.BarChart, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_consensus").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.PHYSICAL_QC,
                    onClick = { activeTab = SensoryTab.PHYSICAL_QC },
                    label = {
                        Text(
                            "Physical QC & Rest",
                            fontWeight = if (activeTab == SensoryTab.PHYSICAL_QC) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_physical").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.CALIBRATION,
                    onClick = { activeTab = SensoryTab.CALIBRATION },
                    label = {
                        Text(
                            "Calibration Lab",
                            fontWeight = if (activeTab == SensoryTab.CALIBRATION) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Star, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_calibration").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.ACADEMY,
                    onClick = { activeTab = SensoryTab.ACADEMY },
                    label = {
                        Text(
                            "Academy (22)",
                            fontWeight = if (activeTab == SensoryTab.ACADEMY) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.MenuBook, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_academy").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.CVA_CUPPING,
                    onClick = { activeTab = SensoryTab.CVA_CUPPING },
                    label = {
                        Text(
                            "CVA Cupping",
                            fontWeight = if (activeTab == SensoryTab.CVA_CUPPING) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Science, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_cva").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.TRAINING_LAB,
                    onClick = { activeTab = SensoryTab.TRAINING_LAB },
                    label = {
                        Text(
                            "Training Lab",
                            fontWeight = if (activeTab == SensoryTab.TRAINING_LAB) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_training").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.GREEN_QC,
                    onClick = { activeTab = SensoryTab.GREEN_QC },
                    label = {
                        Text(
                            "Green Grading QC",
                            fontWeight = if (activeTab == SensoryTab.GREEN_QC) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Spa, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_green_qc").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.OPERATIONS,
                    onClick = { activeTab = SensoryTab.OPERATIONS },
                    label = {
                        Text(
                            "Pro Operations",
                            fontWeight = if (activeTab == SensoryTab.OPERATIONS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Settings, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_operations").heightIn(min = 48.dp)
                )
            }
        }

        when (activeTab) {
            SensoryTab.WHEEL -> {
                item {
                    SensoryWheelVisualizer(
                        selectedCategory = SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.firstOrNull {
                            it.name.equals(
                                selectedCategoryFilter,
                                ignoreCase = true
                            )
                        },
                        onCategorySelected = { cat ->
                            selectedCategoryFilter = cat.name
                        },
                        onDescriptorTapped = { desc ->
                            searchQuery = desc.name
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search flavor wheel & descriptors") },
                        placeholder = { Text("e.g. Grapefruit, Jasmine, Blackberry, Citrus...") },
                        modifier = Modifier.fillMaxWidth().testTag("sensory:wheel_search")
                    )
                }

                item {
                    val categories = listOf("All") + SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.map { it.name }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            TactileFilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
                                label = {
                                    Text(
                                        cat,
                                        fontWeight = if (selectedCategoryFilter == cat) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                }

                val allResults = SensoryTaxonomyRegistry.searchDescriptors(searchQuery)
                val filtered = if (selectedCategoryFilter == "All") {
                    allResults
                } else {
                    allResults.filter { desc ->
                        val cat = SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.find { it.id == desc.categoryId }
                        cat?.name.equals(selectedCategoryFilter, ignoreCase = true)
                    }
                }

                item {
                    Text(
                        "Showing ${filtered.size} flavor descriptors:",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                items(filtered, key = { it.id }) { desc ->
                    val color: Color = try {
                        Color(AndroidColor.parseColor(desc.colorHex))
                    } catch (_: IllegalArgumentException) {
                        MaterialTheme.colorScheme.primary
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .height(28.dp)
                                    .padding(horizontal = 4.dp)
                                    .animateContentSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = color),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        " ${desc.name} ",
                                        color = if (
                                            desc.colorHex == "#FFF9C4" ||
                                            desc.colorHex == "#FDD835" ||
                                            desc.colorHex == "#FFF59D"
                                        ) {
                                            Color.Black
                                        } else {
                                            Color.White
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${desc.categoryId.removePrefix(
                                        "cat_"
                                    ).replaceFirstChar { it.uppercase() }} · ${desc.subcategoryId.removePrefix(
                                        "sub_"
                                    ).replace('_', ' ').replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(desc.sensoryNotes, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            SensoryTab.ACCESSIBLE_LIST -> {
                item {
                    InfoCard(
                        "Accessible Flavor Table Alternative (S2-051)",
                        "Meets TalkBack screen reader standards with clear semantic roles, high-contrast color names, " +
                            "and touch targets >= 48dp. No interactive features rely solely on visual color hue."
                    )
                }

                SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.forEach { category ->
                    item {
                        Text(
                            "${category.name.uppercase()} CATEGORY (${category.colorHex})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    val descriptors = category.subcategories.flatMap { it.descriptors }
                    items(descriptors, key = { it.id }) { desc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .padding(vertical = 4.dp)
                                .semantics {
                                    stateDescription = desc.accessibilityDescription
                                },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(desc.name, style = MaterialTheme.typography.titleSmall)
                                    Text(desc.colorHex, style = MaterialTheme.typography.labelSmall)
                                }
                                Text(desc.accessibilityDescription, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            SensoryTab.SESSIONS -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Cupping Sessions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "SCA / CoE protocol evaluations",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TactileButton(
                            onClick = { showCreateSessionDialog = true },
                            modifier = Modifier.height(36.dp).testTag("sensory:btn_new_session"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("New Session", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (state.cuppingSessions.isEmpty()) {
                    item {
                        InfoCard("No Cupping Sessions", "Click 'New Session' to initialize an evaluation panel.")
                    }
                } else {
                    items(state.cuppingSessions, key = { it.id }) { session ->
                        val isSelected = session.id == activeSession?.id
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(session.title, style = MaterialTheme.typography.titleSmall)
                                        Text("${session.location} · ${session.template.title}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (session.status) {
                                                SessionStatus.OPEN -> MaterialTheme.colorScheme.primaryContainer
                                                SessionStatus.CLOSED -> MaterialTheme.colorScheme.surfaceVariant
                                                SessionStatus.REOPENED -> MaterialTheme.colorScheme.errorContainer
                                                SessionStatus.DRAFT -> MaterialTheme.colorScheme.secondaryContainer
                                            }
                                        )
                                    ) {
                                        Text(
                                            " ${session.status.name} ",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Evaluators: ${session.evaluators.joinToString { it.name }} | Masking: ${session.maskingMode.name}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TactileFilledTonalButton(onClick = { model.selectCuppingSession(session.id) }) {
                                        Text(if (isSelected) "Active Session" else "Select Session", maxLines = 1, softWrap = false)
                                    }
                                    if (session.status == SessionStatus.OPEN) {
                                        TactileOutlinedButton(onClick = { model.closeCuppingSession(session.id) }) {
                                            Text("Close & Lock", maxLines = 1, softWrap = false)
                                        }
                                    } else if (session.status == SessionStatus.CLOSED) {
                                        TactileOutlinedButton(onClick = { showReopenDialog = true }) {
                                            Text("Reopen...", maxLines = 1, softWrap = false)
                                        }
                                    }
                                    TactileButton(onClick = { model.exportSessionQcReport(session.id) }) {
                                        Text("Export QC", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }

                    if (activeSession != null) {
                        item {
                            HorizontalDivider(Modifier.padding(vertical = 12.dp))
                            Text(
                                "Samples for '${activeSession.title}'",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(activeSession.samples, key = { it.sampleId }) { sample ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        Text(
                                            sample.blindCode,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (sample.isRevealed) {
                                            Text("Revealed Identity", style = MaterialTheme.typography.labelSmall)
                                        } else {
                                            TactileOutlinedButton(
                                                onClick = { model.revealCuppingSample(activeSession.id, sample.sampleId) }
                                            ) {
                                                Text("Reveal Identity", maxLines = 1, softWrap = false)
                                            }
                                        }
                                    }

                                    if (sample.isRevealed) {
                                        Text(
                                            listOfNotNull(
                                                sample.beanName,
                                                sample.origin?.let { "Origin: $it" },
                                                sample.process?.let { "Process: $it" },
                                                sample.roaster?.let { "Roaster: $it" }
                                            ).joinToString(" · ").ifBlank { "Bean details not recorded" },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        Text(
                                            "Identity Masked (Blind Protocol Enforced). Evaluators score purely by cup presentation.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    val sheets = activeSession.sheets.filter { it.sampleId == sample.sampleId }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Submitted Evaluator Sheets: ${sheets.size} / ${activeSession.evaluators.size}",
                                        style = MaterialTheme.typography.labelSmall
                                    )

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TactileButton(
                                            onClick = {
                                                scoringSample = sample.sampleId
                                                evaluatorIdInput = activeSession.evaluators.firstOrNull()?.id ?: ""
                                            },
                                            enabled = activeSession.status == SessionStatus.OPEN ||
                                                activeSession.status == SessionStatus.REOPENED
                                        ) {
                                            Text("Score Sample", maxLines = 1, softWrap = false)
                                        }
                                        TactileFilledTonalButton(onClick = {
                                            model.selectCuppingSession(activeSession.id)
                                            model.selectCuppingSample(sample.sampleId)
                                            activeTab = SensoryTab.CONSENSUS
                                        }) {
                                            Text("View Consensus", maxLines = 1, softWrap = false)
                                        }
                                        TactileOutlinedButton(onClick = {
                                            model.logCuppingSampleToJournal(activeSession.id, sample.sampleId)
                                        }) {
                                            Text("Log to Journal", maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SensoryTab.CONSENSUS -> {
                if (activeSession == null || activeSession.samples.isEmpty()) {
                    item {
                        InfoCard("No Active Session", "Select or create a cupping session first to view consensus calculus.")
                    }
                } else {
                    val sampleId = state.selectedCuppingSampleId ?: activeSession.samples.first().sampleId
                    val consensus = CuppingAnalyticsEngine.computeConsensus(activeSession, sampleId)
                    val sample = activeSession.samples.first { it.sampleId == sampleId }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "Sample: ${consensus.blindCode}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Consensus Score", style = MaterialTheme.typography.labelSmall)
                                        Text("${consensus.finalConsensusScore} / 100", style = MaterialTheme.typography.headlineMedium)
                                    }
                                    Column {
                                        Text("Panel Agreement (IRAI)", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            "${consensus.overallPanelAgreementIndex} / 1.00",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(
                                        " [HUMAN OBSERVATION] Empirical Cupper Panel. " +
                                            "No uncalibrated AI predictions in official consensus score. ",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Attribute Consensus Breakdown", style = MaterialTheme.typography.titleSmall)
                    }

                    items(consensus.attributeConsensus.entries.toList(), key = { it.key }) { (key, c) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        key.replace('_', ' ').replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "Mean: ${c.mean} · Median: ${c.median} · StdDev: ${c.stdDev}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                    Text("IRAI: ${c.agreementIndex}", style = MaterialTheme.typography.labelMedium)
                                    if (c.outlierEvaluatorIds.isNotEmpty()) {
                                        Text(
                                            "Outlier: ${c.outlierEvaluatorIds.joinToString()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Consensus Descriptor Cloud",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    item {
                        if (consensus.descriptorCloud.isEmpty()) {
                            Text("No descriptors logged for this sample.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                consensus.descriptorCloud.forEach { desc ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (desc.isConsensus) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                    ) {
                                        Text(
                                            " ${desc.descriptor} (${(desc.selectionFrequency * BigDecimal(100)).toInt()}%) ",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Pairwise Evaluator Jaccard Overlap Matrix",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    items(consensus.overlapMatrix) { pair ->
                        Text(
                            "${pair.evaluatorAId} ↔ ${pair.evaluatorBId}: " +
                                "Jaccard Similarity ${(pair.jaccardSimilarity * BigDecimal(100)).toInt()}%\t",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    item {
                        Spacer(Modifier.height(12.dp))
                        SensoryRadar(
                            state.observations.lastOrNull { it.recipeId == state.selectedRecipeId },
                            state.estimates.firstOrNull { it.recipeId == state.selectedRecipeId }?.profile
                        )
                    }
                }
            }

            SensoryTab.PHYSICAL_QC -> {
                item {
                    InfoCard(
                        "Physical Green & Roasted QC (S2-061)",
                        "Enter measured moisture, water activity, and defect counts to calculate a grade. " +
                            "Agtron delta appears after both readings are entered."
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = greenMoistureInput,
                            onValueChange = { greenMoistureInput = it },
                            label = { Text("Moisture %") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = greenAwInput,
                            onValueChange = { greenAwInput = it },
                            label = { Text("Water Activity (aw)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Defect Counts (raw full count):", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = greenFullBlackInput,
                            onValueChange = { greenFullBlackInput = it },
                            label = { Text("Full Black") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = greenPartialBlackInput,
                            onValueChange = { greenPartialBlackInput = it },
                            label = { Text("Partial Black") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = greenBrokenInput,
                            onValueChange = { greenBrokenInput = it },
                            label = { Text("Broken/Chipped") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    val moisture = greenMoistureInput.toBigDecimalOrNull()
                        ?.takeIf { it >= BigDecimal.ZERO && it <= BigDecimal("100") }
                    val waterActivity = greenAwInput.toBigDecimalOrNull()
                        ?.takeIf { it >= BigDecimal.ZERO && it <= BigDecimal.ONE }
                    val defectCounts = listOf(
                        greenFullBlackInput.toIntOrNull(),
                        greenPartialBlackInput.toIntOrNull(),
                        greenBrokenInput.toIntOrNull()
                    )
                    val validDefects = defectCounts.all { it != null && it >= 0 }
                    val gradeResult = if (moisture != null && waterActivity != null && validDefects) {
                        CuppingAnalyticsEngine.gradeGreenPhysicalLot(
                            moisture,
                            waterActivity,
                            listOf(
                                GreenCoffeeDefectEntry(GreenDefectType.FULL_BLACK, defectCounts[0]!!),
                                GreenCoffeeDefectEntry(GreenDefectType.PARTIAL_BLACK, defectCounts[1]!!),
                                GreenCoffeeDefectEntry(GreenDefectType.BROKEN_CHIPPED, defectCounts[2]!!)
                            )
                        )
                    } else {
                        null
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (gradeResult == null) {
                                Text(
                                    "Enter valid measurements and defect counts to calculate a grade.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text("Calculated Grade: ${gradeResult.first}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Primary Defects: ${gradeResult.second.first} | Secondary Equivalent: ${gradeResult.second.second}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Roasted Color QC (Agtron Gourmet Scale)",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = agtronWholeInput,
                            onValueChange = { agtronWholeInput = it },
                            label = { Text("Whole Bean Agtron") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = agtronGroundInput,
                            onValueChange = { agtronGroundInput = it },
                            label = { Text("Ground Agtron") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    val whole = agtronWholeInput.toDoubleOrNull()?.takeIf { it.isFinite() }
                    val ground = agtronGroundInput.toDoubleOrNull()?.takeIf { it.isFinite() }
                    if (whole != null && ground != null) {
                        Text(
                            "Roast Delta (Ground - Whole): ${String.format(java.util.Locale.US, "%.1f", ground - whole)} Agtron points",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text("Enter both Agtron readings to calculate the color delta.", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Roast-Rest Kinetics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "S2-062 degassing curve & logs",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TactileButton(
                            onClick = { showLogObsDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Log Obs", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                item {
                    val prediction = CuppingAnalyticsEngine.predictRoastRestCurve("bag_geisha_lot1", "LIGHT", "WASHED")
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Light Washed Kinetic Curve Prediction", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Peak Flavor Window: Day ${prediction.estimatedPeakWindowStartDay} to " +
                                    "Day ${prediction.estimatedPeakWindowEndDay} post-roast",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Expected Oxidation / Staling Threshold: Day ${prediction.expectedStalingDay}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "[AI HEURISTIC] ${prediction.provenanceBadge.methodDisclaimer}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(state.roastRestObservations, key = { it.id }) { obs ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Day ${obs.daysPostRoast} Post-Roast · Score ${obs.cuppingScore}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (obs.observedPhase) {
                                            DegassingPhase.PEAK_FLAVOR_WINDOW -> MaterialTheme.colorScheme.primaryContainer
                                            DegassingPhase.TOO_FRESH_CO2_INTERFERENCE -> MaterialTheme.colorScheme.errorContainer
                                            DegassingPhase.STABLE_POST_PEAK -> MaterialTheme.colorScheme.secondaryContainer
                                            DegassingPhase.STALE_OXIDIZED -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Text(
                                        " ${obs.observedPhase.label} ",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                            Text(
                                "Aroma: ${obs.observedAroma} · Acidity: ${obs.observedAcidity} · " +
                                    "Sweetness: ${obs.observedSweetness} | Carbonic Sharpness: " +
                                    "${if (obs.perceivedCarbonicAcid) "Yes" else "No"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(obs.qualitativeNotes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            SensoryTab.CALIBRATION -> {
                item {
                    InfoCard(
                        "Safe Food-Grade Calibration Reference Lab (S2-052)",
                        "Standard sensory training exercises for palate calibration with rigorous food-grade purity " +
                            "standards and chemical safety boundaries."
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Solution Batch Volume: ", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(
                            value = calibrationBatchVolumeInput,
                            onValueChange = { calibrationBatchVolumeInput = it },
                            label = { Text("Batch mL") },
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }

                val volMl = calibrationBatchVolumeInput.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }

                if (volMl == null) {
                    item {
                        Text(
                            "Enter a positive batch volume to calculate ingredient masses.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    items(SensoryTaxonomyRegistry.STANDARD_CALIBRATION_EXERCISES, key = { it.id }) { ex ->
                        val massG = SensoryTaxonomyRegistry.calculateCalibrationBatch(ex, volMl)
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ex.compoundName, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            "${ex.chemicalFormula} · Grade: ${ex.requiredGrade.name}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            " $massG g / ${volMl.toInt()} mL ",
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(ex.preparationInstructions, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(4.dp))
                                Text("Sensory Cue: ${ex.sensoryCueDescription}", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            "⚠️ SAFETY BOUNDARY: ${ex.safetyWarning}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            "Max Daily Tasting Limit: ${ex.maxDailyConsumptionLimitMl} mL | Allergen: ${ex.allergenNotice}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SensoryTab.ACADEMY -> {
                item {
                    SensoryAcademySection()
                }
            }

            SensoryTab.CVA_CUPPING -> {
                item {
                    SensoryCvaCuppingSection()
                }
            }

            SensoryTab.TRAINING_LAB -> {
                item {
                    SensoryTrainingLabSection()
                }
            }

            SensoryTab.GREEN_QC -> {
                item {
                    SensoryGreenGradingSection()
                }
            }

            SensoryTab.OPERATIONS -> {
                item {
                    SensoryOperationsSection()
                }
            }
        }
    }

    // Dialog: Create Cupping Session
    if (showCreateSessionDialog) {
        AlertDialog(
            onDismissRequest = { showCreateSessionDialog = false },
            title = { Text("New Cupping Session") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newSessionTitle,
                        onValueChange = { newSessionTitle = it },
                        label = { Text("Session Title") },
                        placeholder = { Text("e.g. Origin Evaluation Batch #4") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSessionLocation,
                        onValueChange = { newSessionLocation = it },
                        label = { Text("Location / Lab") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Template Protocol:", style = MaterialTheme.typography.labelMedium)
                    CuppingTemplateType.values().forEach { type ->
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TactileFilterChip(
                                selected = newSessionTemplate == type,
                                onClick = { newSessionTemplate = type },
                                label = { Text(type.name.replace('_', ' ')) },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                    Text("Masking Mode:", style = MaterialTheme.typography.labelMedium)
                    MaskingMode.values().forEach { mode ->
                        TactileFilterChip(
                            selected = newSessionMasking == mode,
                            onClick = { newSessionMasking = mode },
                            label = { Text(mode.name.replace('_', ' ')) },
                            modifier = Modifier.heightIn(min = 44.dp)
                        )
                    }
                    OutlinedTextField(
                        value = newSessionEvaluators,
                        onValueChange = { newSessionEvaluators = it },
                        label = { Text("Evaluators (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSessionSamples,
                        onValueChange = { newSessionSamples = it },
                        label = { Text("Sample Bean Names (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        val evals = newSessionEvaluators.split(',').map { it.trim() }.filter { it.isNotBlank() }
                        val samples = newSessionSamples.split(',').map { it.trim() }.filter { it.isNotBlank() }
                        model.createCuppingSession(
                            title = newSessionTitle,
                            location = newSessionLocation,
                            templateType = newSessionTemplate,
                            maskingMode = newSessionMasking,
                            evaluatorNames = evals,
                            sampleBeanNames = samples
                        )
                        showCreateSessionDialog = false
                    },
                    enabled = newSessionTitle.isNotBlank() &&
                        newSessionEvaluators.split(',').any { it.isNotBlank() } &&
                        newSessionSamples.split(',').any { it.isNotBlank() },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Create Session", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { showCreateSessionDialog = false }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Dialog: Score Sample
    if (scoringSample != null && activeSession != null) {
        val sample = activeSession.samples.find { it.sampleId == scoringSample }
        val template = activeSession.template
        var dynamicScores by remember(scoringSample, activeSession.id) {
            mutableStateOf(emptyMap<String, Float>())
        }
        var selectedDescriptorsList by remember(scoringSample) {
            mutableStateOf(scoreDescriptors.split(',').map { it.trim() }.filter { it.isNotBlank() })
        }

        if (showWheelPickerForSample) {
            FlavorWheelPickerDialog(
                initialSelected = selectedDescriptorsList,
                onDismissRequest = { showWheelPickerForSample = false },
                onConfirm = { picked ->
                    selectedDescriptorsList = picked
                    scoreDescriptors = picked.joinToString(", ")
                    showWheelPickerForSample = false
                }
            )
        }

        AlertDialog(
            onDismissRequest = { scoringSample = null },
            title = { Text("Score Sample: ${sample?.blindCode}") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Evaluator:", style = MaterialTheme.typography.labelMedium)
                    activeSession.evaluators.forEach { eval ->
                        TactileFilterChip(
                            selected = evaluatorIdInput == eval.id,
                            onClick = { evaluatorIdInput = eval.id },
                            label = { Text("${eval.name} (${eval.role.name})") },
                            modifier = Modifier.heightIn(min = 44.dp)
                        )
                    }

                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Text(
                        "Protocol: ${template.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    template.attributes.forEach { attr ->
                        val minVal = attr.minValue.toFloat()
                        val maxVal = attr.maxValue.toFloat()
                        val step = attr.stepSize.toFloat()
                        val steps = (((maxVal - minVal) / step).toInt() - 1).coerceAtLeast(0)
                        val currVal = dynamicScores[attr.key] ?: minVal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(attr.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                dynamicScores[attr.key]?.let { String.format(java.util.Locale.US, "%.2f", it) } ?: "Not scored",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Slider(
                            value = currVal,
                            onValueChange = { dynamicScores = dynamicScores + (attr.key to it) },
                            valueRange = minVal..maxVal,
                            steps = steps
                        )
                    }

                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Flavor Wheel Notes (${selectedDescriptorsList.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        TactileTextButton(onClick = { showWheelPickerForSample = true }, modifier = Modifier.heightIn(min = 44.dp)) {
                            Icon(Icons.Default.Psychology, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Pick from Wheel", maxLines = 1, softWrap = false)
                        }
                    }

                    if (selectedDescriptorsList.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            selectedDescriptorsList.forEach { descName ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.clickable {
                                        selectedDescriptorsList = selectedDescriptorsList - descName
                                        scoreDescriptors = selectedDescriptorsList.joinToString(", ")
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(descName, style = MaterialTheme.typography.labelSmall)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Default.Clear, "Remove", modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = scoreDescriptors,
                        onValueChange = {
                            scoreDescriptors = it
                            selectedDescriptorsList = it.split(',').map { s -> s.trim() }.filter { s -> s.isNotBlank() }
                        },
                        label = { Text("Descriptors (comma separated or wheel-picked)") },
                        placeholder = { Text("e.g. Jasmine, Bergamot, Peach") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = scoreTaints,
                            onValueChange = { scoreTaints = it },
                            label = { Text("Taints (0 if none)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = scoreFaults,
                            onValueChange = { scoreFaults = it },
                            label = { Text("Faults (0 if none)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = scoreNotes,
                        onValueChange = { scoreNotes = it },
                        label = { Text("Evaluator Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        val scores = dynamicScores.mapValues {
                            BigDecimal(it.value.toDouble()).setScale(2, RoundingMode.HALF_UP)
                        }
                        val descList = selectedDescriptorsList.ifEmpty {
                            scoreDescriptors.split(',').map { it.trim() }.filter { it.isNotBlank() }
                        }
                        model.submitCuppingSheet(
                            sessionId = activeSession.id,
                            sampleId = scoringSample!!,
                            evaluatorId = evaluatorIdInput,
                            attributeScores = scores,
                            descriptors = descList,
                            taintCups = requireNotNull(scoreTaints.toIntOrNull()),
                            faultCups = requireNotNull(scoreFaults.toIntOrNull()),
                            notes = scoreNotes
                        )
                        scoringSample = null
                    },
                    enabled = dynamicScores.keys == template.attributes.map { it.key }.toSet() &&
                        activeSession.evaluators.any { it.id == evaluatorIdInput } &&
                        scoreTaints.toIntOrNull()?.let { it >= 0 } == true &&
                        scoreFaults.toIntOrNull()?.let { it >= 0 } == true,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Submit Score", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { scoringSample = null }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Dialog: Reopen Session
    if (showReopenDialog && activeSession != null) {
        AlertDialog(
            onDismissRequest = { showReopenDialog = false },
            title = { Text("Reopen Cupping Session") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("An immutable entry will be recorded in the audit trail log.")
                    OutlinedTextField(
                        value = reopenReasonInput,
                        onValueChange = { reopenReasonInput = it },
                        label = { Text("Reason for reopening") },
                        placeholder = { Text("e.g. Correcting typo on Sample #B2 score") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TactileButton(onClick = {
                    model.reopenCuppingSession(
                        activeSession.id,
                        activeSession.evaluators.firstOrNull()?.id ?: "lead",
                        reopenReasonInput
                    )
                    showReopenDialog = false
                }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Reopen Session", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { showReopenDialog = false }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Dialog: QC Report Receipt
    state.generatedQcReceipt?.let { receipt ->
        AlertDialog(
            onDismissRequest = { model.clearQcReceipt() },
            title = { Text("Offline QC Report Receipt") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Cryptographic Fingerprint (SHA-256):", style = MaterialTheme.typography.labelSmall)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Text(
                            receipt.sha256Fingerprint,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text("Report Preview:", style = MaterialTheme.typography.labelSmall)
                    Text(receipt.markdownReport, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TactileButton(onClick = { model.clearQcReceipt() }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Dismiss", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Dialog: Log Roast Rest Observation
    if (showLogObsDialog) {
        val days = obsDaysInput.toIntOrNull()?.takeIf { it in 0..3650 }
        val score = obsScoreInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("100") }
        val aroma = obsAromaInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("10") }
        val acidity = obsAcidityInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("10") }
        val sweetness = obsSweetnessInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("10") }
        val knownRoastLevels = setOf("LIGHT", "MEDIUM", "DARK")
        val knownProcesses = setOf("WASHED", "NATURAL", "ANAEROBIC")
        val validObservation = obsBagIdInput.isNotBlank() && days != null && score != null &&
            aroma != null && acidity != null && sweetness != null &&
            obsRoastLevelInput in knownRoastLevels && obsProcessInput in knownProcesses && obsPerceivedCarbonic != null
        AlertDialog(
            onDismissRequest = { showLogObsDialog = false },
            title = { Text("Log Roast-Rest Observation") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = obsBagIdInput,
                        onValueChange = { obsBagIdInput = it },
                        label = { Text("Bag / Lot ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Roast level used for the modeled rest estimate:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        knownRoastLevels.forEach { roastLevel ->
                            TactileFilterChip(
                                selected = obsRoastLevelInput == roastLevel,
                                onClick = { obsRoastLevelInput = roastLevel },
                                label = { Text(roastLevel) },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                    Text("Process used for the modeled rest estimate:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        knownProcesses.forEach { process ->
                            TactileFilterChip(
                                selected = obsProcessInput == process,
                                onClick = { obsProcessInput = process },
                                label = { Text(process) },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = obsDaysInput,
                        onValueChange = { obsDaysInput = it },
                        label = { Text("Days Post-Roast") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = obsScoreInput,
                        onValueChange = { obsScoreInput = it },
                        label = { Text("Cupping Score") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = obsAromaInput,
                            onValueChange = { obsAromaInput = it },
                            label = { Text("Aroma") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = obsAcidityInput,
                            onValueChange = { obsAcidityInput = it },
                            label = { Text("Acidity") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = obsSweetnessInput,
                            onValueChange = { obsSweetnessInput = it },
                            label = { Text("Sweetness") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Carbonic Acid / Sharpness:", modifier = Modifier.weight(1f))
                        TactileFilterChip(
                            selected = obsPerceivedCarbonic == true,
                            onClick = { obsPerceivedCarbonic = true },
                            label = { Text("Detected") },
                            modifier = Modifier.heightIn(min = 44.dp)
                        )
                        TactileFilterChip(
                            selected = obsPerceivedCarbonic == false,
                            onClick = { obsPerceivedCarbonic = false },
                            label = { Text("Not detected") },
                            modifier = Modifier.heightIn(min = 44.dp)
                        )
                    }
                    OutlinedTextField(
                        value = obsNotesInput,
                        onValueChange = { obsNotesInput = it },
                        label = { Text("Observation Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "The roast-rest phase is a model classification based on entered roast age, roast level, and process.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TactileButton(onClick = {
                    val roastAgeDays = requireNotNull(days)
                    val observedScore = requireNotNull(score)
                    val observedAroma = requireNotNull(aroma)
                    val observedAcidity = requireNotNull(acidity)
                    val observedSweetness = requireNotNull(sweetness)
                    val pred = CuppingAnalyticsEngine.predictRoastRestCurve(obsBagIdInput, obsRoastLevelInput, obsProcessInput)
                    val phase = CuppingAnalyticsEngine.classifyObservationPhase(roastAgeDays, pred)
                    val observationTime = System.currentTimeMillis()
                    val obs = RoastRestObservation(
                        id = UUID.randomUUID().toString(),
                        bagId = obsBagIdInput,
                        roastDateEpoch = observationTime - 86400000L * roastAgeDays,
                        observationDateEpoch = observationTime,
                        daysPostRoast = roastAgeDays,
                        cuppingScore = observedScore.setScale(2, RoundingMode.HALF_UP),
                        observedAroma = observedAroma.setScale(1, RoundingMode.HALF_UP),
                        observedAcidity = observedAcidity.setScale(1, RoundingMode.HALF_UP),
                        observedSweetness = observedSweetness.setScale(1, RoundingMode.HALF_UP),
                        perceivedCarbonicAcid = requireNotNull(obsPerceivedCarbonic),
                        qualitativeNotes = obsNotesInput,
                        observedPhase = phase
                    )
                    model.recordRoastRestObservation(obs)
                    showLogObsDialog = false
                }, enabled = validObservation, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Save Observation", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { showLogObsDialog = false }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }
}

@Composable
private fun Knowledge(state: CoffeeUiState, model: CoffeeViewModel) {
    var query by rememberSaveable { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(model::importDocument) }
    Page("Knowledge") {
        item {
            Heading(
                "Your local sources",
                "Import UTF-8 TXT or PDF. Scanned pages use bundled English OCR. Source excerpts remain untrusted data."
            )
        }
        item {
            TactileButton(onClick = {
                picker.launch(arrayOf("text/plain", "application/pdf"))
            }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp).testTag("knowledge:import")) {
                Text("Import TXT / PDF", maxLines = 1, softWrap = false)
            }
        }
        item { Field("Search imported evidence", query, { query = it }, "knowledge:query") }
        item {
            TactileOutlinedButton(onClick = {
                model.searchDocuments(query)
            }, enabled = query.isNotBlank() && !state.busy, modifier = Modifier.heightIn(min = 48.dp).testTag("knowledge:search")) {
                Text("Search locally", maxLines = 1, softWrap = false)
            }
        }
        state.searchResult?.let { result ->
            item {
                InfoCard(
                    if (result.didAbstain) "No supporting evidence" else "Local source excerpts",
                    "${result.retrievalMode}\n${result.text}"
                ) {
                    result.matchedChunks.forEach { chunk ->
                        TactileTextButton(onClick = { model.inspectChunk(chunk) }, modifier = Modifier.heightIn(min = 44.dp)) {
                            Text("Inspect ${chunk.citationText}", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }
        if (state.documents.isEmpty()) {
            item {
                InfoCard(
                    "No documents imported",
                    "Coffee Creator does not ship fictional manuals. Add documents you have permission to use."
                )
            }
        }
        items(state.documents, key = { it.id }) { document ->
            InfoCard(
                document.title,
                listOf(
                    "${document.fileType} · ${document.chunkCount} chunks",
                    if (document.fileType == "PDF") "${document.pageCount} real pages" else "Text has no page numbers",
                    "SHA-256 ${document.contentHash}"
                ).joinToString("\n")
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactileTextButton(
                        onClick = { model.reindex(document) },
                        enabled = !state.busy,
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Text("Reindex", maxLines = 1, softWrap = false)
                    }
                    TactileTextButton(
                        onClick = { model.requestDeletion("document", document.id) },
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Text("Remove source", color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun Models(state: CoffeeUiState, model: CoffeeViewModel) {
    var importingId by rememberSaveable { mutableStateOf<String?>(null) }
    var showManifest by rememberSaveable { mutableStateOf(false) }
    var manifest by rememberSaveable { mutableStateOf("") }
    var expandedModel by rememberSaveable { mutableStateOf<String?>(null) }
    val directoryPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri -> uri?.let(model::selectDirectory) }
    val modelPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val asset = state.models.find { it.id == importingId }
        if (uri != null && asset != null) model.importModel(asset, uri)
    }
    Page("Models") {
        item {
            Heading(
                "Local AI models",
                "A model is installed only after exact size and SHA-256 validation. Downloads use network only when you request them."
            )
        }
        item {
            InfoCard(
                "User-owned model directory",
                state.directory ?: "Choose a SAF folder before downloading. Verified model files and manifests are published there."
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactileButton(onClick = {
                        directoryPicker.launch(null)
                    }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp).testTag("models:directory")) {
                        Text("Choose directory", maxLines = 1, softWrap = false)
                    }
                    TactileOutlinedButton(
                        onClick = model::discoverAssets,
                        enabled = state.directory != null && !state.busy,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Recover bundles", maxLines = 1, softWrap = false)
                    }
                    TactileTextButton(onClick = { showManifest = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Add manifest", maxLines = 1, softWrap = false)
                    }
                }
            }
        }
        items(state.models, key = { it.id }) { asset ->
            InfoCard(
                asset.name,
                listOf(
                    "${asset.format} · ${"%.2f".format(java.util.Locale.ROOT, asset.expectedSizeBytes / DECIMAL_GIGABYTE_BYTES)} GB",
                    BuiltInAssets.purpose(asset),
                    "Status: ${asset.status.name.lowercase()}",
                    "License: ${asset.license.substringBefore(';').take(LICENSE_SUMMARY_CHARS)}",
                    asset.error.orEmpty()
                ).joinToString("\n")
            ) {
                if (asset.status == ModelStatus.DOWNLOADING || asset.status == ModelStatus.QUEUED) {
                    LinearProgressIndicator(
                        progress = { asset.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().semantics {
                            stateDescription =
                                "${asset.progressPercent} percent downloaded; verification pending"
                        }
                    )
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (asset.status !in
                        setOf(
                            ModelStatus.INSTALLED,
                            ModelStatus.DOWNLOADING,
                            ModelStatus.QUEUED,
                            ModelStatus.VERIFYING,
                            ModelStatus.PUBLISHING
                        )
                    ) {
                        TactileButton(
                            onClick = { model.modelAction(asset, "download") },
                            enabled = state.directory != null && !state.busy && asset.downloadUrl.isNotBlank(),
                            modifier = Modifier.heightIn(min = 48.dp).testTag("models:download:${asset.id}")
                        ) {
                            Text(
                                if (asset.downloadedBytes > 0) "Resume download" else "Download",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        if (asset.format.startsWith("GGUF", ignoreCase = true)) {
                            TactileOutlinedButton(
                                onClick = {
                                    importingId = asset.id
                                    modelPicker.launch(arrayOf("*/*"))
                                },
                                enabled = state.directory != null && !state.busy,
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) { Text("Import matching GGUF", maxLines = 1, softWrap = false) }
                        }
                    }
                    if (asset.status in
                        setOf(ModelStatus.DOWNLOADING, ModelStatus.QUEUED)
                    ) {
                        TactileOutlinedButton(onClick = { model.modelAction(asset, "pause") }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text("Pause", maxLines = 1, softWrap = false)
                        }
                    }
                    if (asset.status in
                        setOf(ModelStatus.DOWNLOADING, ModelStatus.PAUSED, ModelStatus.FAILED)
                    ) {
                        TactileTextButton(onClick = { model.modelAction(asset, "cancel") }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text("Cancel transfer", maxLines = 1, softWrap = false)
                        }
                    }
                    if (asset.status == ModelStatus.INSTALLED) {
                        if (asset.isSupportedByNativeRuntime()) {
                            TactileButton(
                                onClick = {
                                    model.modelAction(asset, "load")
                                },
                                enabled = !state.busy && !state.generating,
                                modifier = Modifier.heightIn(
                                    min = 48.dp
                                ).testTag("models:load:${asset.id}")
                            ) {
                                Text("Load local model", maxLines = 1, softWrap = false)
                            }
                        } else {
                            Text("Download-only · no compatible inference runtime in this build.")
                        }
                        TactileTextButton(onClick = {
                            model.modelAction(asset, "forget")
                        }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text("Forget registry entry", maxLines = 1, softWrap = false)
                        }
                    }
                }
                TactileTextButton(
                    onClick = { expandedModel = if (expandedModel == asset.id) null else asset.id },
                    modifier = Modifier.heightIn(min = 48.dp).testTag("models:details:${asset.id}")
                ) {
                    Text(
                        if (expandedModel == asset.id) "Hide model details" else "Source, license & integrity",
                        maxLines = 1,
                        softWrap = false
                    )
                }
                if (expandedModel == asset.id) {
                    HorizontalDivider()
                    Text(BuiltInAssets.guidance(asset).orEmpty())
                    Text("Context limit in this app: 1024 tokens. Load succeeds only after runtime and RAM validation.")
                    Text("Expected ${asset.expectedSizeBytes} bytes · ${asset.downloadedBytes} received bytes")
                    Text("Revision ${asset.revision}")
                    Text(asset.license)
                    Text("SHA-256 ${asset.sha256}")
                    Text("Source ${asset.downloadUrl}")
                }
            }
        }
        if (state.loadedModel !=
            null
        ) {
            item {
                TactileOutlinedButton(onClick = {
                    state.models.firstOrNull()?.let { model.modelAction(it, "unload") }
                }, enabled = !state.generating, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Unload native model", maxLines = 1, softWrap = false)
                }
            }
        }
        item {
            Text(
                "Reinstall revokes grants. Reselect this directory to rediscover verified bundles; restore " +
                    "a separate exported backup for recipes/chat. Forgetting an entry keeps user-owned files."
            )
        }
    }
    if (showManifest) {
        AlertDialog(onDismissRequest = { showManifest = false }, title = { Text("Add model manifest") }, text = {
            Column {
                Text("Provide id, name, revision, format, expectedSizeBytes, sha256, downloadUrl, license, runtime and tokenizerSupport.")
                OutlinedTextField(manifest, {
                    manifest =
                        it
                }, label = { Text("Manifest JSON") }, modifier = Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()))
            }
        }, confirmButton = {
            TactileTextButton(
                onClick = {
                    model.registerManifest(manifest)
                    showManifest = false
                },
                enabled = manifest.isNotBlank(),
                modifier = Modifier.heightIn(
                    min = 48.dp
                )
            ) { Text("Validate manifest", maxLines = 1, softWrap = false) }
        }, dismissButton = {
            TactileTextButton(onClick = {
                showManifest = false
            }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cancel", maxLines = 1, softWrap = false) }
        })
    }
}

@Composable
private fun Chat(state: CoffeeUiState, model: CoffeeViewModel) {
    var prompt by rememberSaveable { mutableStateOf("") }
    var showQuizDialog by rememberSaveable { mutableStateOf(false) }
    var quizRoast by rememberSaveable { mutableStateOf(RoastPreference.MEDIUM_LIGHT) }
    var quizAcidity by rememberSaveable { mutableStateOf(AcidityTolerance.HIGH_ENTHUSIAST) }
    var quizSweetness by rememberSaveable { mutableStateOf(SweetnessPriority.CORE_CRUCIAL) }
    var quizBitterness by rememberSaveable { mutableStateOf(BitternessSensitivity.AVERSE) }
    var quizBody by rememberSaveable { mutableStateOf(BodyPreference.SILKY_MEDIUM) }

    var customPersonaText by rememberSaveable { mutableStateOf("") }
    var showCustomPersonaDialog by rememberSaveable { mutableStateOf(false) }

    var selectedRecMethod by rememberSaveable { mutableStateOf("Pour Over (V60)") }

    var ovatDiagnosis by rememberSaveable { mutableStateOf<SensoryFeedbackDiagnosis?>(null) }
    var ovatActualSec by rememberSaveable { mutableStateOf("") }
    var ovatTargetSec by rememberSaveable { mutableStateOf("") }

    var elevationInput by rememberSaveable { mutableStateOf("") }
    var auditIngName by rememberSaveable { mutableStateOf("") }
    var auditIngPct by rememberSaveable { mutableStateOf("") }

    Page("AI Chat") {
        item {
            Heading(
                "Local coffee assistant & personalization",
                "${state.loadedModel ?: "No model loaded"}. Grounded on-device intelligence. " +
                    "Exact Kotlin domain cards and physics rules remain authoritative."
            )
        }

        // ====================================================================
        // 1. Barista Persona Presets & Custom Prompt (S2-072)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Barista Persona", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TactileFilterChip(
                            selected = state.selectedPersona?.id == BaristaPersonaPreset.SPECIALTY_Q_GRADER.id,
                            onClick = { model.selectBaristaPersonaPreset(BaristaPersonaPreset.SPECIALTY_Q_GRADER) },
                            label = { Text("Specialty Q-Grader", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.selectedPersona?.id == BaristaPersonaPreset.FRIENDLY_HOME_BARISTA.id,
                            onClick = { model.selectBaristaPersonaPreset(BaristaPersonaPreset.FRIENDLY_HOME_BARISTA) },
                            label = { Text("Friendly Barista", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.selectedPersona?.id == BaristaPersonaPreset.RD_COFFEE_CHEMIST.id,
                            onClick = { model.selectBaristaPersonaPreset(BaristaPersonaPreset.RD_COFFEE_CHEMIST) },
                            label = { Text("R&D Chemist", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.selectedPersona?.id == "custom_persona",
                            onClick = { showCustomPersonaDialog = true },
                            label = { Text("Custom Prompt", maxLines = 1, softWrap = false) }
                        )
                    }
                    Text(
                        state.selectedPersona?.description ?: "Select a persona to guide response tone and extraction focus.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ====================================================================
        // 2. Offline Personal Palate Quiz & Dynamic Profile (S2-063, S2-064)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val profile = state.palateProfile
                    val hasPersonalProfile = profile.isQuizCompleted || profile.qualifiedSessionCount > 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Personal Palate Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f))
                        ) {
                            Text(
                                when {
                                    profile.isQuizCompleted -> "Quiz calibrated"
                                    hasPersonalProfile -> "Evidence: ${profile.qualifiedSessionCount} sessions"
                                    else -> "No personal profile"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    if (hasPersonalProfile) {
                        Text(
                            "Roast: ${(profile.vector.roast * 10).toInt()}/10 · " +
                                "Acidity: ${(profile.vector.acidity * 10).toInt()}/10 · " +
                                "Sweetness: ${(profile.vector.sweetness * 10).toInt()}/10 · " +
                                "Bitterness: ${(profile.vector.bitterness * 10).toInt()}/10 · " +
                                "Body: ${(profile.vector.body * 10).toInt()}/10",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum")
                        )
                    } else {
                        Text(
                            "No personal palate profile yet. Complete the quiz to set your preferences.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TactileButton(onClick = { showQuizDialog = true }) {
                            Text(if (profile.isQuizCompleted) "Retake Palate Quiz" else "Take Palate Quiz", maxLines = 1, softWrap = false)
                        }
                        TactileOutlinedButton(onClick = {
                            model.updatePalateSliders(
                                acidity = ((profile.vector.acidity + 0.1).coerceAtMost(1.0))
                            )
                        }, enabled = hasPersonalProfile) {
                            Text("+ Acidity Bias", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 3. Explainable Inventory Recommendations (S2-065)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Inventory Recommendations", style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Pour Over (V60)", "Espresso", "AeroPress", "French Press").forEach { method ->
                            TactileFilterChip(
                                selected = selectedRecMethod == method,
                                onClick = {
                                    selectedRecMethod = method
                                    model.generateInventoryRecommendations(method)
                                },
                                label = { Text(method) },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                    TactileButton(
                        onClick = { model.generateInventoryRecommendations(selectedRecMethod) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text("Check evidence for $selectedRecMethod matches", maxLines = 1, softWrap = false)
                    }
                    if (state.inventoryRecommendations.isNotEmpty()) {
                        state.inventoryRecommendations.take(3).forEachIndexed { idx, item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(Modifier.padding(10.dp)) {
                                    Text(
                                        "#${idx + 1} ${item.beanName} · ${(item.cosineSimilarity * 100).toInt()}% Match",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(item.explanationText, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    } else {
                        Text(
                            "Recommendations need recorded bag mass, roast date, human sensory notes linked to each bean, " +
                                "and compatible equipment. Unknown values are not estimated.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // ====================================================================
        // 4. OVAT Brewing Feedback Loop (S2-066)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Single-Variable Dial-In (OVAT)", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Proposes one controlled physical variable adjustment without confounding other variables.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SensoryFeedbackDiagnosis.values().forEach { diag ->
                            TactileFilterChip(
                                selected = ovatDiagnosis == diag,
                                onClick = { ovatDiagnosis = diag },
                                label = { Text(diag.name.replace("_", " ")) },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                    val actualSeconds = ovatActualSec.toIntOrNull()?.takeIf { it > 0 }
                    val targetSeconds = ovatTargetSec.toIntOrNull()?.takeIf { it > 0 }
                    val canPropose = ovatDiagnosis != null && actualSeconds != null && targetSeconds != null
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ovatActualSec,
                            onValueChange = { ovatActualSec = it },
                            label = { Text("Actual (s)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ovatTargetSec,
                            onValueChange = { ovatTargetSec = it },
                            label = { Text("Target (s)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    TactileButton(
                        onClick = {
                            model.proposeSingleVariableAdjustment(
                                requireNotNull(ovatDiagnosis),
                                requireNotNull(actualSeconds),
                                requireNotNull(targetSeconds)
                            )
                        },
                        enabled = canPropose,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text("Diagnose & Propose Adjustment", maxLines = 1, softWrap = false)
                    }
                    if (!canPropose) {
                        Text("Select a diagnosis and enter positive observed and target times.", style = MaterialTheme.typography.bodySmall)
                    }
                    state.ovatAdjustmentProposal?.let { prop ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Proposed: ${prop.variableAdjusted} -> ${prop.proposedValue}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(prop.diagnosticReason, style = MaterialTheme.typography.bodySmall)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TactileButton(onClick = model::applyOvatAdjustmentToDraft, modifier = Modifier.heightIn(min = 44.dp)) {
                                        Text("Apply to Recipe Draft", maxLines = 1, softWrap = false)
                                    }
                                    TactileOutlinedButton(
                                        onClick = model::dismissOvatAdjustment,
                                        modifier = Modifier.heightIn(min = 44.dp)
                                    ) {
                                        Text("Dismiss", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 6. Offline Weather & Altitude Boiling Point (S2-073)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Local Altitude & Thermodynamics", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Approximate offline boiling-point estimate from an elevation you enter. No GPS or weather service is used.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = elevationInput,
                            onValueChange = { elevationInput = it },
                            label = { Text("Elevation (m)") },
                            modifier = Modifier.weight(1f)
                        )
                        TactileButton(
                            onClick = {
                                val elev = elevationInput.toDoubleOrNull() ?: return@TactileButton
                                model.updateWeatherParameters(elev)
                            },
                            enabled = elevationInput.toDoubleOrNull()?.isFinite() == true,
                            modifier = Modifier.padding(top = 8.dp).heightIn(min = 48.dp)
                        ) {
                            Text("Compute", maxLines = 1, softWrap = false)
                        }
                    }
                    Text(
                        state.weatherParams.calculatedBoilingPointCelsius?.let { boilingPoint ->
                            "Elevation: ${state.weatherParams.elevationMeters?.toInt()}m · " +
                                "Boiling Point: $boilingPoint°C · Safe Brew Max: " +
                                "${boilingPoint - SAFE_BREW_TEMPERATURE_MARGIN_CELSIUS}°C"
                        } ?: "Elevation and boiling point are unknown until you enter an elevation.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // ====================================================================
        // 7. Ingredient & Allergen Safety Gate (S2-074)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingredient & Allergen Safety Gate", style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = auditIngName,
                            onValueChange = { auditIngName = it },
                            label = { Text("Ingredient Name") },
                            modifier = Modifier.weight(WIDE_INPUT_WEIGHT)
                        )
                        OutlinedTextField(
                            value = auditIngPct,
                            onValueChange = { auditIngPct = it },
                            label = { Text("Dose %") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        "Food-grade status, allergens, and supplier limits come from the local ingredient record. " +
                            "Missing evidence blocks approval.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    val ingredientDosePercent = auditIngPct.toDoubleOrNull()?.takeIf { it.isFinite() && it in 0.0..100.0 }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileButton(
                            onClick = {
                                model.auditIngredientSafety(auditIngName, requireNotNull(ingredientDosePercent))
                            },
                            enabled = auditIngName.isNotBlank() && ingredientDosePercent != null,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text("Audit Safety", maxLines = 1, softWrap = false)
                        }
                    }
                    state.safetyAuditResult?.let { res ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (res.level) {
                                IngredientSafetyLevel.SAFE_FOOD_GRADE -> MaterialTheme.colorScheme.primaryContainer
                                IngredientSafetyLevel.WARNING_ALLERGEN_PRESENT -> MaterialTheme.colorScheme.secondaryContainer
                                IngredientSafetyLevel.CRITICAL_NON_FOOD_PROHIBITED -> MaterialTheme.colorScheme.errorContainer
                                IngredientSafetyLevel.CRITICAL_DOSAGE_LIMIT_EXCEEDED -> MaterialTheme.colorScheme.errorContainer
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Text("Safety Level: ${res.level.name}", style = MaterialTheme.typography.titleSmall)
                                res.flags.forEach { flag -> Text("• $flag", style = MaterialTheme.typography.bodySmall) }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 8. Grounded AI Barista Chat & Messages (S2-068, S2-071)
        // ====================================================================
        item {
            Text("Barista Chat Session", style = MaterialTheme.typography.titleMedium)
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Ask from my sources",
                    "Help plan a brew",
                    "Explain my observations",
                    "Calculate extraction yield"
                ).forEach { preset ->
                    TactileOutlinedButton(
                        onClick = { prompt = preset },
                        enabled = !state.generating,
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Text(preset, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
        items(state.messages, key = { it.id }) { message ->
            val title = when (message.role) {
                "user" -> "You"
                "tool" -> "KOTLIN TOOL · ${message.toolCallName.orEmpty()}"
                else -> "GENERATED · local model, unverified"
            }
            InfoCard(title, message.content) {
                state.chunks.filter { it.id in message.sourceChunkIds }.forEach { chunk ->
                    TactileTextButton(onClick = { model.inspectChunk(chunk) }, modifier = Modifier.heightIn(min = 44.dp)) {
                        Text(
                            "${if (chunk.citationText in message.citations) "Cited" else "Retrieved context"}: ${chunk.citationText}",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
        state.pendingToolJson?.let { json ->
            item {
                InfoCard(
                    "Review quantities before calculation",
                    "Check each value, unit and measured field. Edit by sending corrected JSON.\n$json"
                ) {
                    TactileButton(
                        onClick = model::confirmToolInputs,
                        enabled = !state.generating && !state.busy,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Confirm inputs and calculate", maxLines = 1, softWrap = false)
                    }
                    TactileTextButton(onClick = model::dismissToolInputs, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Discard", maxLines = 1, softWrap = false)
                    }
                }
            }
        }
        state.pendingRecipe?.let { recipe ->
            item {
                InfoCard(
                    "Confirm draft save",
                    "${recipe.name}: ${recipe.doseGrams} g dose / ${recipe.waterGrams} g water. No measured TDS or beverage mass supplied."
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileButton(onClick = model::confirmAiRecipe, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text("Confirm save", maxLines = 1, softWrap = false)
                        }
                        TactileOutlinedButton(onClick = model::dismissAiRecipe, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text("Discard draft", maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                prompt,
                { prompt = it },
                label = { Text("Message or typed tool JSON") },
                modifier = Modifier.fillMaxWidth().testTag("chat:input"),
                minLines = 2,
                maxLines = 5
            )
        }
        item {
            TactileButton(
                onClick = {
                    model.sendChat(prompt)
                    prompt = ""
                },
                enabled = prompt.isNotBlank() && !state.generating && !state.busy && state.ready,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("chat:send")
            ) {
                Text("Send locally", maxLines = 1, softWrap = false)
            }
        }
        if (state.generating) {
            item {
                TactileOutlinedButton(
                    onClick = model::cancelGeneration,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("chat:cancel")
                ) {
                    Text("Cancel generation", maxLines = 1, softWrap = false)
                }
            }
        }
        item {
            InfoCard(
                "Manual typed tools work without an LLM",
                "Required values are validated; missing values are rejected. Example schema: tool = " +
                    "calculate_brew_ratio; arguments = doseGrams and waterGrams. No generated code runs."
            )
        }
    }

    // Dialog for Palate Quiz
    if (showQuizDialog) {
        AlertDialog(
            onDismissRequest = { showQuizDialog = false },
            title = { Text("Offline Palate Quiz", fontWeight = FontWeight.SemiBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. Roast Preference:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        RoastPreference.values().forEach { r ->
                            TactileFilterChip(
                                selected = quizRoast == r,
                                onClick = { quizRoast = r },
                                label = { Text(r.name, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                    Text("2. Acidity Tolerance:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AcidityTolerance.values().forEach { a ->
                            TactileFilterChip(
                                selected = quizAcidity == a,
                                onClick = { quizAcidity = a },
                                label = { Text(a.name, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                    Text("3. Sweetness Priority:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SweetnessPriority.values().forEach { s ->
                            TactileFilterChip(
                                selected = quizSweetness == s,
                                onClick = { quizSweetness = s },
                                label = { Text(s.name, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                    Text("4. Bitterness Sensitivity:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        BitternessSensitivity.values().forEach { b ->
                            TactileFilterChip(selected = quizBitterness == b, onClick = {
                                quizBitterness = b
                            }, label = { Text(b.name, maxLines = 1, softWrap = false) })
                        }
                    }
                    Text("5. Body Preference:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        BodyPreference.values().forEach { m ->
                            TactileFilterChip(
                                selected = quizBody == m,
                                onClick = { quizBody = m },
                                label = { Text(m.name, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TactileButton(onClick = {
                    model.submitPalateQuiz(
                        PalateQuizAnswers(
                            roast = quizRoast,
                            acidity = quizAcidity,
                            sweetness = quizSweetness,
                            bitterness = quizBitterness,
                            body = quizBody
                        )
                    )
                    showQuizDialog = false
                }) {
                    Text("Save & Calibrate", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = {
                    showQuizDialog = false
                }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cancel", maxLines = 1, softWrap = false) }
            }
        )
    }

    // Dialog for Custom Persona
    if (showCustomPersonaDialog) {
        AlertDialog(
            onDismissRequest = { showCustomPersonaDialog = false },
            title = { Text("Custom Persona System Prompt") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Max ${MAX_CUSTOM_PERSONA_CHARS} characters. Automatically sanitized against prompt injection.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = customPersonaText,
                        onValueChange = { customPersonaText = it.take(MAX_CUSTOM_PERSONA_CHARS) },
                        label = { Text("System Prompt (${customPersonaText.length}/${MAX_CUSTOM_PERSONA_CHARS})") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TactileButton(
                    onClick = {
                        model.updateCustomPersonaPrompt(customPersonaText)
                        showCustomPersonaDialog = false
                    },
                    enabled = customPersonaText.isNotBlank(),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Save & Apply", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = {
                    showCustomPersonaDialog = false
                }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cancel", maxLines = 1, softWrap = false) }
            }
        )
    }
}

@Composable
private fun Settings(state: CoffeeUiState, model: CoffeeViewModel) {
    val context = LocalContext.current
    val isIndonesian = state.unitPreferences.language == AppLanguage.INDONESIAN
    var restoring by rememberSaveable { mutableStateOf<Uri?>(null) }
    var activeHelpDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var tokens by rememberSaveable(state.maxTokens) { mutableStateOf(state.maxTokens.toString()) }
    var temperature by rememberSaveable(state.temperature) { mutableStateOf(state.temperature.toString()) }
    val exporter =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            uri?.let { model.exportBackup(it, context.contentResolver) }
        }
    val restorer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> restoring = uri }

    Page("Settings") {
        item {
            Heading(
                if (isIndonesian) "Keandalan & Pengaturan" else "Reliability & Settings",
                if (isIndonesian) {
                    "Data Anda tersimpan lokal di perangkat. Bebas SDK telemetri atau inferensi cloud."
                } else {
                    "Your records stay 100% on-device. Zero telemetry SDKs, cloud tracking, or remote inference."
                }
            )
        }

        // ====================================================================
        // S2-078: On-Device Language Toggle (EN / ID)
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:language_card"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isIndonesian) "Bahasa Aplikasi (On-Device Language)" else "App Language (On-Device Language)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (isIndonesian) {
                            "Peralihan bahasa instan tanpa koneksi internet atau restart aplikasi."
                        } else {
                            "Instant on-device language toggle without network connection or app restart."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileFilterChip(
                            selected = state.unitPreferences.language == AppLanguage.ENGLISH,
                            onClick = { model.setLanguage(AppLanguage.ENGLISH) },
                            label = { Text("English (EN)", maxLines = 1, softWrap = false) },
                            modifier = Modifier.testTag("settings:lang_en")
                        )
                        TactileFilterChip(
                            selected = state.unitPreferences.language == AppLanguage.INDONESIAN,
                            onClick = { model.setLanguage(AppLanguage.INDONESIAN) },
                            label = { Text("Bahasa Indonesia (ID)", maxLines = 1, softWrap = false) },
                            modifier = Modifier.testTag("settings:lang_id")
                        )
                    }
                }
            }
        }

        // ====================================================================
        // S2-078: Localized Unit Preferences (Metric / Imperial)
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:units_card"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (isIndonesian) "Preferensi Satuan Fisika Seduh" else "Brewing Physics Unit Preferences",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (isIndonesian) {
                            "Konversi matematis presisi tinggi (BigDecimal) tanpa akumulasi drift pembulatan."
                        } else {
                            "Exact high-precision BigDecimal conversions with zero cumulative rounding drift."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Temperature unit
                    Text(if (isIndonesian) "Suhu Air:" else "Water Temperature:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileFilterChip(
                            selected = state.unitPreferences.temperatureUnit == TemperatureUnit.CELSIUS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(temperatureUnit = TemperatureUnit.CELSIUS)) },
                            label = { Text("Celsius (°C)", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.unitPreferences.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                            onClick = {
                                model.setUnitPreferences(
                                    state.unitPreferences.copy(temperatureUnit = TemperatureUnit.FAHRENHEIT)
                                )
                            },
                            label = { Text("Fahrenheit (°F)", maxLines = 1, softWrap = false) }
                        )
                    }

                    // Mass unit
                    Text(if (isIndonesian) "Massa Biji & Air:" else "Coffee & Water Mass:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileFilterChip(
                            selected = state.unitPreferences.massUnit == MassUnit.GRAMS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(massUnit = MassUnit.GRAMS)) },
                            label = { Text("Grams (g)", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.unitPreferences.massUnit == MassUnit.OUNCES,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(massUnit = MassUnit.OUNCES)) },
                            label = { Text("Ounces (oz)", maxLines = 1, softWrap = false) }
                        )
                    }

                    // Volume & Elevation
                    Text(if (isIndonesian) "Volume & Ketinggian:" else "Volume & Elevation:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TactileFilterChip(
                            selected = state.unitPreferences.volumeUnit == VolumeUnit.MILLILITERS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(volumeUnit = VolumeUnit.MILLILITERS)) },
                            label = { Text("mL", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.unitPreferences.volumeUnit == VolumeUnit.FLUID_OUNCES,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(volumeUnit = VolumeUnit.FLUID_OUNCES)) },
                            label = { Text("fl oz", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.unitPreferences.elevationUnit == ElevationUnit.METERS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(elevationUnit = ElevationUnit.METERS)) },
                            label = { Text("masl", maxLines = 1, softWrap = false) }
                        )
                        TactileFilterChip(
                            selected = state.unitPreferences.elevationUnit == ElevationUnit.FEET,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(elevationUnit = ElevationUnit.FEET)) },
                            label = { Text("ft", maxLines = 1, softWrap = false) }
                        )
                    }

                    // Live sample conversion preview with concentric border and tabular figures
                    val isFahr = state.unitPreferences.temperatureUnit == TemperatureUnit.FAHRENHEIT
                    val isOz = state.unitPreferences.massUnit == MassUnit.OUNCES
                    val sampleTemp = if (isFahr) "199.4 °F" else "93.0 °C"
                    val sampleDose = if (isOz) "0.635 oz" else "18.0 g"
                    val sampleWater = if (isOz) "8.818 oz" else "250.0 g"
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Live Preview: Dose $sampleDose • Water $sampleWater • Temp $sampleTemp",
                            style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // ====================================================================
        // S2-081: Graceful Degradation Status & Subsystem Health (Anti-Slop Redesign)
        // ====================================================================
        item {
            val badge = state.degradationState.activeBadge
            val badgeText = if (isIndonesian) badge.labelId else badge.labelEn
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:degradation_card"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isDegraded) {
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    }
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            if (isIndonesian) "Status Subsistem & Degradasi" else "Subsystem Degradation Status",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        // Anti-Slop: Guaranteed Single-Line Industrial Status Banner
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (badge.isDegraded) {
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.60f)
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            },
                            border = BorderStroke(
                                1.dp,
                                if (badge.isDegraded) {
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.50f)
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = if (badge.isDegraded) {
                                                MaterialTheme.colorScheme.tertiary
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            },
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = badgeText.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.5.sp,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = if (badge.isDegraded) {
                                        MaterialTheme.colorScheme.onTertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    },
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Text(
                        if (isIndonesian) {
                            "Aplikasi dirancang agar tetap 100% fungsional pada mode manual saat model AI, " +
                                "OCR, atau timbangan BLE tidak tersedia."
                        } else {
                            "Core coffee tools remain 100% operational in manual mode even when local LLM, " +
                                "OCR vision, or BLE scale are offline."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Industrial Telemetry Pill Row (3 equal columns, tabular figures, no horizontal jitter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryStatusPill(
                            label = "LLM",
                            status = if (state.loadedModel != null) "Active" else "Manual",
                            isActive = state.loadedModel != null,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatusPill(
                            label = "OCR",
                            status = if (state.degradationState.ocrStatus == ModuleStatus.AVAILABLE) "Active" else "Manual",
                            isActive = state.degradationState.ocrStatus == ModuleStatus.AVAILABLE,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatusPill(
                            label = "BLE",
                            status = if (state.brewSession?.scaleDevice != null) "Connected" else "Offline",
                            isActive = state.brewSession?.scaleDevice != null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ====================================================================
        // S2-080: On-Device Activity Diagnostics & Scrubbed Ring Buffer
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:diagnostics_card"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isIndonesian) "Diagnostik Aktivitas On-Device" else "On-Device Activity Diagnostics",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (isIndonesian) {
                            "Buffer melingkar 500 entri dengan pembersihan otomatis PII (IP, token, path). Nol SDK telemetri eksternal."
                        } else {
                            "500-entry ring buffer with automatic PII scrubbing (IPs, paths, tokens). Zero external telemetry SDKs."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TactileOutlinedButton(
                            onClick = { model.refreshDiagnostics() },
                            modifier = Modifier.testTag("settings:refresh_diag")
                        ) {
                            Text(
                                if (isIndonesian) {
                                    "Segarkan Log (${state.diagnosticLogSnapshot.size})"
                                } else {
                                    "Refresh Logs (${state.diagnosticLogSnapshot.size})"
                                },
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        TactileButton(
                            onClick = { model.exportDiagnosticLogs(context) },
                            modifier = Modifier.testTag("settings:export_diag")
                        ) {
                            Text(
                                if (isIndonesian) "Ekspor Log (Sharesheet)" else "Export Logs (Sharesheet)",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        TactileTextButton(onClick = { model.clearDiagnostics() }, modifier = Modifier.heightIn(min = 44.dp)) {
                            Text(if (isIndonesian) "Hapus Log" else "Clear Logs", maxLines = 1, softWrap = false)
                        }
                    }

                    if (state.diagnosticLogSnapshot.isNotEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 160.dp)
                        ) {
                            Column(Modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
                                state.diagnosticLogSnapshot.takeLast(6).reversed().forEach { log ->
                                    Text(
                                        text = "[#${log.sequenceId}] [${log.level}] [${log.tag}] ${log.message}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                                        color = if (log.level == LogLevel.ERROR) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // S2-078: Offline Help & Error Recovery Guides
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:help_card"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isIndonesian) "Panduan Bantuan & Pemulihan Offline" else "Offline Help & Error Recovery",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (isIndonesian) {
                            "Panduan pemecahan masalah teknis mandiri saat di kedai kopi tanpa akses internet."
                        } else {
                            "Self-contained troubleshooting guides when brewing in airplane mode."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OfflineHelpCatalog.topics.forEach { topic ->
                            TactileOutlinedButton(
                                onClick = { activeHelpDialog = topic.id },
                                modifier = Modifier.testTag("help:${topic.id}")
                            ) {
                                Text(if (isIndonesian) topic.titleId else topic.titleEn, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // S2-077: Cryptographic Full Backup & Restore
        // ====================================================================
        item {
            InfoCard(
                if (isIndonesian) "Cadangan & Pemulihan Penuh (SAF)" else "Complete Cryptographic Backup (SAF)",
                if (isIndonesian) {
                    "Ekspor resep, biji kopi, jurnal, cupping, profil rasa, dan preferensi dengan verifikasi SHA-256. " +
                        "File model GGUF dan izin SAF tersimpan terpisah."
                } else {
                    "Export recipes, beans, journal logs, cuppings, taste profiles, and preferences with SHA-256 integrity. " +
                        "Model weights and directory grants remain user-owned in SAF."
                }
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TactileButton(onClick = {
                        exporter.launch("coffee-creator-backup.json")
                    }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp).testTag("settings:export")) {
                        Text(if (isIndonesian) "Ekspor cadangan" else "Export backup", maxLines = 1, softWrap = false)
                    }
                    TactileOutlinedButton(onClick = {
                        restorer.launch(arrayOf("application/json", "text/plain"))
                    }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp).testTag("settings:restore")) {
                        Text(if (isIndonesian) "Pulihkan cadangan" else "Restore backup", maxLines = 1, softWrap = false)
                    }
                }
            }
        }

        item {
            Select(
                if (isIndonesian) "Tema Tampilan" else "Theme",
                state.theme,
                listOf("system" to "System", "light" to "Light", "dark" to "Dark"),
                model::setTheme,
                "settings:theme"
            )
        }

        item { Field("Maximum output tokens (16–256)", tokens, { tokens = it }, "settings:tokens", true) }
        item { Field("Sampling temperature (0.1–1.2)", temperature, { temperature = it }, "settings:temperature", true) }
        item {
            TactileButton(onClick = {
                model.setGeneration(tokens.toIntOrNull() ?: 0, temperature.replace(',', '.').toFloatOrNull() ?: Float.NaN)
            }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(if (isIndonesian) "Simpan pengaturan inferensi" else "Save generation settings", maxLines = 1, softWrap = false)
            }
        }

        item {
            InfoCard(
                if (isIndonesian) "Statistik Database Lokal" else "Local Database Diagnostics",
                listOf(
                    "App 1.8.0 · local records ${if (state.ready) "ready" else "unavailable"}",
                    "${state.recipes.size} recipes · ${state.journalEntries.size} journals · ${state.cuppingSessions.size} cuppings",
                    state.loadedModel ?: "No GGUF loaded",
                    "Delete specific records with confirmation in their respective workspace.",
                    "Exports remain in your directory until you delete them there."
                ).joinToString("\n")
            )
        }

        item {
            TactileOutlinedButton(
                onClick = { model.requestDeletion("all application records", "all") },
                enabled = !state.busy && !state.generating,
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text(if (isIndonesian) "Hapus semua data aplikasi…" else "Delete application records…", maxLines = 1, softWrap = false) }
        }
    }

    // ========================================================================
    // Offline Help Topic Dialog
    // ========================================================================
    activeHelpDialog?.let { topicId ->
        val topic = OfflineHelpCatalog.topics.find { it.id == topicId }
        if (topic != null) {
            AlertDialog(
                onDismissRequest = { activeHelpDialog = null },
                title = { Text(if (isIndonesian) topic.titleId else topic.titleEn) },
                text = {
                    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isIndonesian) topic.summaryId else topic.summaryEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider()
                        val steps = if (isIndonesian) topic.recoveryStepsId else topic.recoveryStepsEn
                        steps.forEachIndexed { idx, step ->
                            Text("${idx + 1}. $step", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    TactileTextButton(onClick = { activeHelpDialog = null }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(if (isIndonesian) "Tutup" else "Close", maxLines = 1, softWrap = false)
                    }
                }
            )
        }
    }

    // ========================================================================
    // Restore Confirmation Dialog
    // ========================================================================
    restoring?.let { uri ->
        AlertDialog(
            onDismissRequest = { restoring = null },
            title = {
                Text(if (isIndonesian) "Pulihkan cadangan terverifikasi?" else "Restore validated backup?")
            },
            text = {
                Text(
                    if (isIndonesian) {
                        "Seluruh data resep, jurnal, cupping, dan preferensi akan dipulihkan secara atomik. " +
                            "File rusak atau tidak kompatibel akan ditolak tanpa mengubah database yang ada."
                    } else {
                        "Supported, integrity-checked records will replace the backed-up local record groups. " +
                            "Corrupt or unsupported files are rejected before mutation."
                    }
                )
            },
            confirmButton = {
                TactileTextButton(onClick = {
                    model.restoreBackup(uri, context.contentResolver)
                    restoring = null
                }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(if (isIndonesian) "Validasi & Pulihkan" else "Validate and restore", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TactileTextButton(onClick = { restoring = null }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(if (isIndonesian) "Batal" else "Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }
}

@Preview(name = "Manual empty state", showBackground = true)
@Composable
private fun EmptyStatePreview() {
    CoffeeCreatorTheme(dynamicColor = false) { InfoCard("No brews recorded", "Create a recipe; measured TDS stays empty until supplied.") }
}

@Preview(name = "Dark unavailable model", showBackground = true)
@Composable
private fun ModelUnavailablePreview() {
    CoffeeCreatorTheme(darkTheme = true, dynamicColor = false) {
        InfoCard("AI model unavailable", "Manual mode remains available. Import a verified GGUF in Models.")
    }
}
