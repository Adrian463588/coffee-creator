package com.coffeecreator.app.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeecreator.app.core.sensory.FlavorCategory
import com.coffeecreator.app.core.sensory.FlavorDescriptor
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


private const val FULL_CIRCLE_DEGREES = 360f

/**
 * Interactive, tactile Compose Canvas Sensory Wheel component (S2-051).
 * Multi-ring concentric sunburst wheel with touch-arc detection, category focus,
 * and high-contrast color tokens adhering to SCA/WCR + Roda Rasa Kopi standards.
 */
@Composable
fun SensoryWheelVisualizer(
    selectedCategory: FlavorCategory? = null,
    onCategorySelected: (FlavorCategory) -> Unit,
    onDescriptorTapped: ((FlavorDescriptor) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categories = SensoryTaxonomyRegistry.DEFAULT_CATEGORIES
    val haptic = LocalHapticFeedback.current
    var focusedCategory by rememberSaveable { mutableStateOf(selectedCategory?.id ?: categories.first().id) }
    val activeCategory = categories.firstOrNull { it.id == focusedCategory } ?: categories.first()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Multi-tier Interactive Canvas Sunburst Wheel
        Box(
            modifier = Modifier
                .size(280.dp)
                .semantics {
                    contentDescription =
                    "Interactive Coffee Flavor Wheel. 9 primary categories. " +
                        "Currently focused on ${activeCategory.name}. Tap any sector to explore subcategories and descriptors."
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(280.dp)
                    .pointerInput(categories) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = tapOffset.x - center.x
                            val dy = tapOffset.y - center.y
                            val dist = sqrt(dx * dx + dy * dy)
                            val minDim = Math.min(size.width, size.height).toFloat()
                            val maxRadius = minDim / 2f
                            val innerDeadZone = maxRadius * 0.28f

                            // Ignore taps inside dead center or outside wheel
                            if (dist >= innerDeadZone && dist <= maxRadius) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0f) angle += FULL_CIRCLE_DEGREES

                                val sweepPerCategory = FULL_CIRCLE_DEGREES / categories.size
                                val categoryIndex = (angle / sweepPerCategory).toInt().coerceIn(0, categories.lastIndex)
                                val chosen = categories[categoryIndex]
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                focusedCategory = chosen.id
                                onCategorySelected(chosen)
                            }
                        }
                    }
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = size.minDimension * 0.48f
                val midRadius = size.minDimension * 0.36f
                val innerRadius = size.minDimension * 0.24f

                val sweepPerCat = FULL_CIRCLE_DEGREES / categories.size

                categories.forEachIndexed { idx, cat ->
                    val startAngle = idx * sweepPerCat
                    val isSelected = cat.id == focusedCategory
                    val parsedColor = try {
                        Color(AndroidColor.parseColor(cat.colorHex))
                    } catch (_: IllegalArgumentException) {
                        Color.Gray
                    }

                    // Outer arc: Subcategories / descriptors
                    val subCount = cat.subcategories.size.coerceAtLeast(1)
                    val sweepSub = sweepPerCat / subCount

                    cat.subcategories.forEachIndexed { subIdx, sub ->
                        val subStart = startAngle + (subIdx * sweepSub)
                        val subColor = parsedColor.copy(alpha = if (isSelected) 0.95f else 0.70f)
                        drawArc(
                            color = subColor,
                            startAngle = subStart,
                            sweepAngle = sweepSub - 1.5f,
                            useCenter = false,
                            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                            size = Size(outerRadius * 2, outerRadius * 2),
                            style = Stroke(width = (outerRadius - midRadius) * if (isSelected) 1.15f else 1.0f)
                        )
                    }

                    // Inner arc: Primary category
                    val innerColor = if (isSelected) parsedColor else parsedColor.copy(alpha = 0.85f)
                    drawArc(
                        color = innerColor,
                        startAngle = startAngle,
                        sweepAngle = sweepPerCat - 2f,
                        useCenter = false,
                        topLeft = Offset(center.x - midRadius, center.y - midRadius),
                        size = Size(midRadius * 2, midRadius * 2),
                        style = Stroke(width = (midRadius - innerRadius) * if (isSelected) 1.15f else 1.0f)
                    )

                    // Sector divider line
                    val rad = Math.toRadians(startAngle.toDouble())
                    val lineEnd = center + Offset(cos(rad).toFloat(), sin(rad).toFloat()) * outerRadius
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = center,
                        end = lineEnd,
                        strokeWidth = 2f
                    )
                }

                // Center hub circle
                drawCircle(
                    color = Color.White,
                    radius = innerRadius * 0.95f,
                    center = center,
                    style = Fill
                )
                drawCircle(
                    color = try {
                        Color(AndroidColor.parseColor(activeCategory.colorHex))
                    } catch (_: IllegalArgumentException) {
                        Color.DarkGray
                    },
                    radius = innerRadius * 0.95f,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Center Text Hub
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = activeCategory.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = Color.Black,
                    maxLines = 2
                )
                Text(
                    text = "${activeCategory.subcategories.sumOf { it.descriptors.size }} notes",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color.DarkGray
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Quick Category Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat.id == focusedCategory
                val catColor = try {
                    Color(AndroidColor.parseColor(cat.colorHex))
                } catch (_: IllegalArgumentException) {
                    MaterialTheme.colorScheme.primary
                }
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        focusedCategory = cat.id
                        onCategorySelected(cat)
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(catColor)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(cat.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.heightIn(min = 36.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Subcategory & Descriptors Flow
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "${activeCategory.name.uppercase()} SUB-TAXONOMY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = try {
                        Color(AndroidColor.parseColor(activeCategory.colorHex))
                    } catch (_: IllegalArgumentException) {
                        MaterialTheme.colorScheme.primary
                    }
                )
                Spacer(Modifier.height(6.dp))
                activeCategory.subcategories.forEach { sub ->
                    Text(
                        text = "• ${sub.name}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                    OptInFlowRow(sub.descriptors, onDescriptorTapped)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptInFlowRow(descriptors: List<FlavorDescriptor>, onDescriptorTapped: ((FlavorDescriptor) -> Unit)?) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        descriptors.forEach { desc ->
            val color = try {
                Color(AndroidColor.parseColor(desc.colorHex))
            } catch (_: IllegalArgumentException) {
                MaterialTheme.colorScheme.primary
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = color.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(enabled = onDescriptorTapped != null) {
                        onDescriptorTapped?.invoke(desc)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = desc.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (desc.isDefect) {
                        Text(
                            text = " (Defect)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Dialog for baristas and cuppers to visually pick multiple flavor descriptors
 * directly from the Flavor Wheel taxonomy for cupping scoring and journal entries.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlavorWheelPickerDialog(initialSelected: List<String>, onDismissRequest: () -> Unit, onConfirm: (List<String>) -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryFilter by rememberSaveable { mutableStateOf("All") }
    val selectedList = remember { mutableStateListOf<String>().apply { addAll(initialSelected) } }

    val haptic = LocalHapticFeedback.current
    val categories = listOf("All") + SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.map { it.name }
    val allResults = if (searchQuery.isBlank()) {
        if (selectedCategoryFilter == "All") {
            SensoryTaxonomyRegistry.getAllDescriptors()
        } else {
            SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.firstOrNull { it.name == selectedCategoryFilter }
                ?.subcategories?.flatMap { it.descriptors } ?: emptyList()
        }
    } else {
        SensoryTaxonomyRegistry.searchDescriptors(searchQuery).let { list ->
            if (selectedCategoryFilter == "All") {
                list
            } else {
                list.filter { desc ->
                    val cat = SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.find { it.id == desc.categoryId }
                    cat?.name.equals(selectedCategoryFilter, ignoreCase = true)
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Sensory Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "${selectedList.size} selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search 90+ flavor descriptors...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedCategoryFilter = cat
                            },
                            label = { Text(cat, fontSize = 11.sp) },
                            modifier = Modifier.heightIn(min = 32.dp)
                        )
                    }
                }

                // Currently Selected Bar
                if (selectedList.isNotEmpty()) {
                    Text(
                        "Active Selection:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedList.forEach { descName ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedList.remove(descName)
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

                // Descriptors Grid/Flow
                Text(
                    "Taxonomy Descriptors (${allResults.size}):",
                    style = MaterialTheme.typography.labelSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allResults.forEach { desc ->
                        val isChecked = selectedList.contains(desc.name)
                        val color = try {
                            Color(AndroidColor.parseColor(desc.colorHex))
                        } catch (_: IllegalArgumentException) {
                            MaterialTheme.colorScheme.primary
                        }

                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isChecked) selectedList.remove(desc.name) else selectedList.add(desc.name)
                            },
                            label = {
                                Text(
                                    desc.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                if (isChecked) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.25f),
                                selectedLabelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.heightIn(min = 32.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onConfirm(selectedList.toList())
                    onDismissRequest()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply Notes (${selectedList.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
