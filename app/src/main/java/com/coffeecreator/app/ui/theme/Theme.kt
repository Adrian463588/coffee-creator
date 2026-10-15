package com.coffeecreator.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = CoffeePrimary,
    onPrimary = CoffeeOnPrimary,
    primaryContainer = CoffeePrimaryContainer,
    onPrimaryContainer = CoffeeOnPrimaryContainer,
    secondary = CoffeeSecondary,
    onSecondary = CoffeeOnSecondary,
    secondaryContainer = CoffeeSecondaryContainer,
    onSecondaryContainer = CoffeeOnSecondaryContainer,
    tertiary = CoffeeAccent,
    onTertiary = CoffeeOnAccent,
    background = CoffeeBackground,
    onBackground = CoffeeOnBackground,
    surface = CoffeeSurface,
    onSurface = CoffeeOnSurface,
    surfaceContainerLow = Color(0xFFF6F0EB),
    surfaceVariant = CoffeeSurfaceVariant,
    onSurfaceVariant = CoffeeOnSurfaceVariant,
    outline = Color(0xFF7E7570),
    error = CoffeeError,
    onError = CoffeeOnError
)

private val DarkColorScheme = darkColorScheme(
    primary = CoffeePrimaryContainer,
    onPrimary = CoffeeOnPrimaryContainer,
    primaryContainer = Color(0xFF513A2E),
    onPrimaryContainer = Color(0xFFF4DED1),
    secondary = Color(0xFFD6C4B6),
    onSecondary = Color(0xFF382F27),
    secondaryContainer = Color(0xFF50453C),
    onSecondaryContainer = Color(0xFFF3E0D1),
    tertiary = Color(0xFFF1BE8E),
    onTertiary = Color(0xFF492904),
    background = Color(0xFF1B1614),
    onBackground = Color(0xFFEDE0DC),
    surface = Color(0xFF221C1A),
    onSurface = Color(0xFFEDE0DC),
    surfaceContainerLow = Color(0xFF2A221E),
    surfaceVariant = Color(0xFF382F2C),
    onSurfaceVariant = Color(0xFFD4C4BF),
    outline = Color(0xFF9D8E86)
)

private val CoffeeShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun CoffeeCreatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = CoffeeShapes,
        content = content
    )
}
