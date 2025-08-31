package com.itdeveapps.stepsshare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private fun createDarkColorScheme(colors: ColorPalette) = darkColorScheme(
    // Primary colors
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryVariant,
    onPrimaryContainer = colors.textPrimary,
    
    // Secondary colors
    secondary = colors.primaryLight,
    onSecondary = colors.onSecondary,
    secondaryContainer = colors.surface,
    onSecondaryContainer = colors.textSecondary,
    
    // Background colors
    background = colors.background,
    onBackground = colors.onBackground,
    
    // Surface colors
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.textSecondary,
    
    // Additional colors
    tertiary = colors.accentGreen,
    onTertiary = colors.textPrimary,
    tertiaryContainer = colors.surface,
    onTertiaryContainer = colors.textSecondary,
    
    // Outline and divider
    outline = colors.divider,
    outlineVariant = colors.unselected,
    
    // Container colors
    surfaceContainer = colors.surface,
    surfaceContainerHigh = colors.surfaceVariant,
    surfaceContainerHighest = colors.surfaceVariant
)

private fun createLightColorScheme(colors: ColorPalette) = lightColorScheme(
    // Primary colors
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryLight,
    onPrimaryContainer = colors.primary,
    
    // Secondary colors
    secondary = colors.primaryVariant,
    onSecondary = Color.White,
    secondaryContainer = colors.surfaceVariant,
    onSecondaryContainer = colors.primary,
    
    // Background colors
    background = colors.background,
    onBackground = colors.onBackground,
    
    // Surface colors
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.textSecondary,
    
    // Additional colors
    tertiary = colors.accentGreen,
    onTertiary = Color.White,
    tertiaryContainer = colors.surfaceVariant,
    onTertiaryContainer = colors.accentGreen,
    
    // Outline and divider
    outline = colors.divider,
    outlineVariant = colors.unselected,
    
    // Container colors
    surfaceContainer = colors.surfaceVariant,
    surfaceContainerHigh = colors.surface,
    surfaceContainerHighest = Color.White
)

// CompositionLocal for providing current color palette
val LocalColorPalette = staticCompositionLocalOf<ColorPalette> { 
    error("No ColorPalette provided") 
}

@Composable
fun StepsShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorPalette = if (darkTheme) DarkColorPalette else LightColorPalette
    val colorScheme = if (darkTheme) {
        createDarkColorScheme(colorPalette)
    } else {
        createLightColorScheme(colorPalette)
    }
    
    // Set the current color palette for AppColors
    AppColors.current = colorPalette
    
    CompositionLocalProvider(LocalColorPalette provides colorPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
