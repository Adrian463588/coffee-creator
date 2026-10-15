package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme

internal enum class BrewSection(val label: String, val detail: String) {
    Recipe("Recipe", "Plan dose, water and timing"),
    Measurements("Measurements", "Add values you have measured"),
    Ingredients("Ingredients", "Check additions and supplier limits"),
    Tools("Tools", "Calculate with explicit quantities")
}

@Preview(name = "Brew sections · compact", widthDp = 320, showBackground = true)
@Composable
private fun CompactBrewSectionsPreview() {
    CoffeeCreatorTheme(dynamicColor = false) { BrewSectionTabs(BrewSection.Recipe) {} }
}

@Preview(name = "Brew sections · large text", widthDp = 360, fontScale = 2f, showBackground = true)
@Composable
private fun LargeTextBrewSectionsPreview() {
    CoffeeCreatorTheme(darkTheme = true, dynamicColor = false) { BrewSectionTabs(BrewSection.Measurements) {} }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BrewSectionTabs(selected: BrewSection, modifier: Modifier = Modifier, onSelect: (BrewSection) -> Unit) {
    Column(modifier.fillMaxWidth().testTag("brew:sections"), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BrewSection.entries.forEach { section ->
                FilterChip(
                    selected = section == selected,
                    onClick = { onSelect(section) },
                    label = { Text(section.label) },
                    leadingIcon = {
                        val icon = if (section == selected) {
                            Icons.Default.Check
                        } else {
                            when (section) {
                                BrewSection.Recipe -> Icons.Default.Coffee
                                BrewSection.Measurements -> Icons.Default.WaterDrop
                                BrewSection.Ingredients -> Icons.Default.Restaurant
                                BrewSection.Tools -> Icons.Default.Science
                            }
                        }
                        Icon(icon, contentDescription = null)
                    },
                    modifier = Modifier.heightIn(min = 48.dp).testTag("brew:section:${section.name}").semantics {
                        stateDescription = if (section == selected) "Selected brew section" else "Open brew section"
                    }
                )
            }
        }
        Text(selected.detail, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
