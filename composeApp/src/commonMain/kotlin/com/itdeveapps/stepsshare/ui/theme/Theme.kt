package com.itdeveapps.stepsshare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Custom colors that don't exist in Material3
object CustomColors {
    // Accent colors for specific UI elements
    val AccentGreen = Color(0xFF10B981)
    val AccentYellow = Color(0xFFFBBF24)
    
    // Trending colors for charts
    val TrendingPositive = Color(0xFF10B981)
    val TrendingNegative = Color(0xFFEF4444)
    
    // Custom gradients
    val ButtonGradientStart = Color(0xFF8B5CF6)
    val ButtonGradientEnd = Color(0xFFC084FC)
    val ProgressGradientStart = Color(0xFF8B5CF6)
    val ProgressGradientEnd = Color(0xFFC084FC)
}

// Extended color scheme with custom colors
data class ExtendedColorScheme(
    val material3: androidx.compose.material3.ColorScheme,
    val custom: CustomColors = CustomColors
)

private fun createDarkColorScheme() = darkColorScheme(
    // Primary colors - your custom purple theme
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA855F7),
    onPrimaryContainer = Color.White,
    
    // Secondary colors - your custom pink theme
    secondary = Color(0xFFC084FC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2D2E42),
    onSecondaryContainer = Color(0xFFB8BCC8),
    
    // Background colors - your custom dark theme
    background = Color(0xFF1A1B2E),
    onBackground = Color.White,
    
    // Surface colors - your custom surface theme
    surface = Color(0xFF2D2E42),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF383A4E),
    onSurfaceVariant = Color(0xFFB8BCC8),
    
    // Additional colors
    tertiary = Color(0xFF10B981), // Accent green
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2D2E42),
    onTertiaryContainer = Color(0xFFB8BCC8),
    
    // Outline and divider
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF4B5563),
    
    // Container colors
    surfaceContainer = Color(0xFF2D2E42),
    surfaceContainerHigh = Color(0xFF383A4E),
    surfaceContainerHighest = Color(0xFF383A4E),
    
    // Error colors
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFF991B1B),
    onErrorContainer = Color(0xFFFEE2E2)
)

private fun createLightColorScheme() = lightColorScheme(
    // Primary colors - your custom purple theme
    primary = Color(0xFF7C3AED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA855F7),
    onPrimaryContainer = Color(0xFF7C3AED),
    
    // Secondary colors - your custom pink theme
    secondary = Color(0xFF8B5CF6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3F4F6),
    onSecondaryContainer = Color(0xFF7C3AED),
    
    // Background colors - your custom light theme
    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF1F2937),
    
    // Surface colors - your custom surface theme
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF6B7280),
    
    // Additional colors
    tertiary = Color(0xFF059669), // Accent green
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3F4F6),
    onTertiaryContainer = Color(0xFF059669),
    
    // Outline and divider
    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFF9CA3AF),
    
    // Container colors
    surfaceContainer = Color(0xFFF3F4F6),
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    
    // Error colors
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

// CompositionLocal for providing extended color scheme
val LocalExtendedColorScheme = staticCompositionLocalOf<ExtendedColorScheme> { 
    error("No ExtendedColorScheme provided") 
}

@Composable
fun StepsShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val material3ColorScheme = if (darkTheme) {
        createDarkColorScheme()
    } else {
        createLightColorScheme()
    }
    
    val extendedColorScheme = ExtendedColorScheme(
        material3 = material3ColorScheme,
        custom = CustomColors
    )
    
    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColorScheme) {
        MaterialTheme(
            colorScheme = material3ColorScheme,
            content = content
        )
    }
}

