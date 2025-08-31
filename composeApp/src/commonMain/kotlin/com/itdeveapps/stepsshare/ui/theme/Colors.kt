package com.itdeveapps.stepsshare.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Custom colors that extend Material3's color scheme
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
    
    // Gradient brushes
    val ButtonGradient = Brush.horizontalGradient(
        colors = listOf(ButtonGradientStart, ButtonGradientEnd)
    )
    
    val ProgressGradient = Brush.sweepGradient(
        colors = listOf(ProgressGradientStart, ProgressGradientEnd)
    )
}

// Legacy color constants for backward compatibility (deprecated)
@Deprecated("Use MaterialTheme.colorScheme instead", ReplaceWith("MaterialTheme.colorScheme.primary"))
object LegacyColors {
    val Primary = Color(0xFF8B5CF6)
    val Secondary = Color(0xFFEC4899)
    val Background = Color(0xFF1A1B2E)
    val Surface = Color(0xFF2D2E42)
}
