# Architectural & Visual Design: Anti-Slop Tactile Navigation

## Design Principles
1. **Vertical Efficiency (Zero Multi-Row Sprawl)**:
   - Screen headers and filter toolbars must occupy predictable, bounded vertical height (<= 56dp per filter band).
   - Multi-option selectors use horizontally scrollable single-line bands (`Row + horizontalScroll(rememberScrollState())`).
2. **Tactile Microinteraction**:
   - `TactileFilterChip`: Uses `MutableInteractionSource.collectIsPressedAsState()` with `animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))`.
   - Active state feedback: `FontWeight.SemiBold` label, high-contrast container color, tactile scale depression.
3. **Ergonomic Touch Targets**:
   - Minimum height of 48dp on primary navigation chips (`Modifier.heightIn(min = 48.dp)`), complying with WCAG 2.5.5 and Android Material 3 guidelines.
   - Spacing: 8.dp horizontal item separation for thumb navigation.

## Component Specifications

### 1. Journal Navigation Row
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    TactileFilterChip(
        selected = activeTab == JournalTab.BREWS,
        onClick = { activeTab = JournalTab.BREWS },
        label = { Text("Brews & Logs", fontWeight = if (activeTab == JournalTab.BREWS) FontWeight.SemiBold else FontWeight.Normal) },
        leadingIcon = { Icon(Icons.Default.Book, null, Modifier.padding(end = 4.dp)) },
        modifier = Modifier.testTag("journal:tab_brews").heightIn(min = 48.dp)
    )
    // Repeat for DIAL_IN, COMPARISON, ANALYTICS, CAFE_CUPS, TOOLS
}
```

### 2. Journal Method Filter Row
```kotlin
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
            label = { Text(filter, fontWeight = if (selectedMethodFilter == filter) FontWeight.SemiBold else FontWeight.Normal) },
            modifier = Modifier.heightIn(min = 44.dp)
        )
    }
}
```

### 3. Sensory Navigation & Taxonomy Rows
- Sensory main tabs: Single-row horizontal scroll with icons (`Psychology`, `MenuBook`, `Coffee`, `BarChart`, `Tune`, `Star`).
- Sensory flavor taxonomy categories: Single-row horizontal scroll (`All`, `Fruity`, `Floral`, `Sweet`, `Nutty/Cocoa`, `Spices`, `Roasted`, `Vegetal`, `Sour/Fermented`, `Other`).
