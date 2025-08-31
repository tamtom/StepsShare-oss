package com.itdeveapps.stepsshare.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object DarkColors {
    // Background colors
    val Background = Color(0xFF1A1B2E)  // Main dark background
    val Surface = Color(0xFF2D2E42)     // Card/surface background
    val SurfaceVariant = Color(0xFF383A4E)  // Lighter surface for elevated components
    
    // Primary purple colors
    val Primary = Color(0xFF8B5CF6)     // Main purple
    val PrimaryVariant = Color(0xFFA855F7)  // Slightly lighter purple
    val PrimaryLight = Color(0xFFC084FC) // Light purple for highlights
    
    // Secondary colors
    val Secondary = Color(0xFFEC4899)   // Pink for secondary elements
    val SecondaryVariant = Color(0xFFF472B6) // Lighter pink
    
    // Accent colors
    val AccentGreen = Color(0xFF10B981)  // Green for positive indicators (crown, etc.)
    val AccentYellow = Color(0xFFFBBF24) // Yellow for warnings or special highlights
    
    // Text colors
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB8BCC8)  // Light gray for secondary text
    val TextTertiary = Color(0xFF6B7280)   // Darker gray for labels
    
    // Button and interactive element gradients
    val ButtonGradientStart = Color(0xFF8B5CF6)  // Primary purple
    val ButtonGradientEnd = Color(0xFFC084FC)    // Light purple
    
    // Progress and chart colors
    val ProgressPrimary = Color(0xFF8B5CF6)
    val ProgressSecondary = Color(0xFFC084FC)
    val ChartBarColor = Color(0xFF8B5CF6)
    
    // Trending colors
    val TrendingBaseline = Color(0xFF6B7280)  // Grey for baseline period
    val TrendingPositive = Color(0xFF10B981)  // Green for positive trends
    val TrendingNegative = Color(0xFFEF4444)  // Red for negative trends
    
    // Gradient brushes
    val ButtonGradient = Brush.horizontalGradient(
        colors = listOf(ButtonGradientStart, ButtonGradientEnd)
    )
    
    val ProgressGradient = Brush.sweepGradient(
        colors = listOf(ProgressPrimary, ProgressSecondary)
    )
    
    val CardGradient = Brush.verticalGradient(
        colors = listOf(Surface, SurfaceVariant)
    )
    
    // State colors
    val Selected = Primary
    val Unselected = Color(0xFF4B5563)  // Gray for unselected tabs
    val Divider = Color(0xFF374151)     // Subtle divider color
    
    // Material3 compatibility
    val OnBackground = TextPrimary
    val OnSurface = TextPrimary
    val OnPrimary = TextPrimary
    val OnSecondary = TextPrimary
}

object LightColors {
    // Background colors
    val Background = Color(0xFFF8F9FF)  // Very light purple-tinted white
    val Surface = Color.White           // Pure white for cards
    val SurfaceVariant = Color(0xFFF3F4F6)  // Light gray for elevated components
    
    // Primary purple colors (same as dark but adjusted for light backgrounds)
    val Primary = Color(0xFF7C3AED)     // Slightly darker purple for better contrast
    val PrimaryVariant = Color(0xFF8B5CF6)  // Original purple
    val PrimaryLight = Color(0xFFA855F7) // Light purple for highlights
    
    // Secondary colors
    val Secondary = Color(0xFFDB2777)   // Darker pink for better contrast on light
    val SecondaryVariant = Color(0xFFEC4899) // Original pink
    
    // Accent colors
    val AccentGreen = Color(0xFF059669)  // Darker green for better contrast on light
    val AccentYellow = Color(0xFFD97706) // Darker yellow for better contrast
    
    // Text colors
    val TextPrimary = Color(0xFF1F2937)     // Dark gray instead of white
    val TextSecondary = Color(0xFF6B7280)   // Medium gray for secondary text
    val TextTertiary = Color(0xFF9CA3AF)    // Light gray for labels
    
    // Button and interactive element gradients
    val ButtonGradientStart = Color(0xFF7C3AED)  // Darker purple for light theme
    val ButtonGradientEnd = Color(0xFFA855F7)    // Light purple
    
    // Progress and chart colors
    val ProgressPrimary = Color(0xFF7C3AED)
    val ProgressSecondary = Color(0xFFA855F7)
    val ChartBarColor = Color(0xFF7C3AED)
    
    // Trending colors
    val TrendingBaseline = Color(0xFF9CA3AF)  // Light grey for baseline period
    val TrendingPositive = Color(0xFF059669)  // Dark green for positive trends
    val TrendingNegative = Color(0xFFDC2626)  // Dark red for negative trends
    
    // Gradient brushes
    val ButtonGradient = Brush.horizontalGradient(
        colors = listOf(ButtonGradientStart, ButtonGradientEnd)
    )
    
    val ProgressGradient = Brush.sweepGradient(
        colors = listOf(ProgressPrimary, ProgressSecondary)
    )
    
    val CardGradient = Brush.verticalGradient(
        colors = listOf(Surface, SurfaceVariant)
    )
    
    // State colors
    val Selected = Primary
    val Unselected = Color(0xFF9CA3AF)  // Light gray for unselected tabs
    val Divider = Color(0xFFE5E7EB)     // Light divider color
    
    // Material3 compatibility
    val OnBackground = TextPrimary
    val OnSurface = TextPrimary
    val OnPrimary = Color.White         // White text on purple buttons
    val OnSecondary = Color.White
}

// Convenience object for accessing current theme colors
object AppColors {
    // These will be set by the theme composable
     var current: ColorPalette = DarkColorPalette
    
    // Access current theme colors
    val background get() = current.background
    val surface get() = current.surface
    val surfaceVariant get() = current.surfaceVariant
    val primary get() = current.primary
    val primaryVariant get() = current.primaryVariant
    val primaryLight get() = current.primaryLight
    val secondary get() = current.secondary
    val secondaryVariant get() = current.secondaryVariant
    val accentGreen get() = current.accentGreen
    val accentYellow get() = current.accentYellow
    val textPrimary get() = current.textPrimary
    val textSecondary get() = current.textSecondary
    val textTertiary get() = current.textTertiary
    val buttonGradientStart get() = current.buttonGradientStart
    val buttonGradientEnd get() = current.buttonGradientEnd
    val progressPrimary get() = current.progressPrimary
    val progressSecondary get() = current.progressSecondary
    val chartBarColor get() = current.chartBarColor
    val trendingBaseline get() = current.trendingBaseline
    val trendingPositive get() = current.trendingPositive
    val trendingNegative get() = current.trendingNegative
    val buttonGradient get() = current.buttonGradient
    val progressGradient get() = current.progressGradient
    val cardGradient get() = current.cardGradient
    val selected get() = current.selected
    val unselected get() = current.unselected
    val divider get() = current.divider
    val onBackground get() = current.onBackground
    val onSurface get() = current.onSurface
    val onPrimary get() = current.onPrimary
    val onSecondary get() = current.onSecondary
}

interface ColorPalette {
    val background: Color
    val surface: Color
    val surfaceVariant: Color
    val primary: Color
    val primaryVariant: Color
    val primaryLight: Color
    val secondary: Color
    val secondaryVariant: Color
    val accentGreen: Color
    val accentYellow: Color
    val textPrimary: Color
    val textSecondary: Color
    val textTertiary: Color
    val buttonGradientStart: Color
    val buttonGradientEnd: Color
    val progressPrimary: Color
    val progressSecondary: Color
    val chartBarColor: Color
    val trendingBaseline: Color
    val trendingPositive: Color
    val trendingNegative: Color
    val buttonGradient: Brush
    val progressGradient: Brush
    val cardGradient: Brush
    val selected: Color
    val unselected: Color
    val divider: Color
    val onBackground: Color
    val onSurface: Color
    val onPrimary: Color
    val onSecondary: Color
}

object DarkColorPalette : ColorPalette {
    override val background = DarkColors.Background
    override val surface = DarkColors.Surface
    override val surfaceVariant = DarkColors.SurfaceVariant
    override val primary = DarkColors.Primary
    override val primaryVariant = DarkColors.PrimaryVariant
    override val primaryLight = DarkColors.PrimaryLight
    override val secondary = DarkColors.Secondary
    override val secondaryVariant = DarkColors.SecondaryVariant
    override val accentGreen = DarkColors.AccentGreen
    override val accentYellow = DarkColors.AccentYellow
    override val textPrimary = DarkColors.TextPrimary
    override val textSecondary = DarkColors.TextSecondary
    override val textTertiary = DarkColors.TextTertiary
    override val buttonGradientStart = DarkColors.ButtonGradientStart
    override val buttonGradientEnd = DarkColors.ButtonGradientEnd
    override val progressPrimary = DarkColors.ProgressPrimary
    override val progressSecondary = DarkColors.ProgressSecondary
    override val chartBarColor = DarkColors.ChartBarColor
    override val trendingBaseline = DarkColors.TrendingBaseline
    override val trendingPositive = DarkColors.TrendingPositive
    override val trendingNegative = DarkColors.TrendingNegative
    override val buttonGradient = DarkColors.ButtonGradient
    override val progressGradient = DarkColors.ProgressGradient
    override val cardGradient = DarkColors.CardGradient
    override val selected = DarkColors.Selected
    override val unselected = DarkColors.Unselected
    override val divider = DarkColors.Divider
    override val onBackground = DarkColors.OnBackground
    override val onSurface = DarkColors.OnSurface
    override val onPrimary = DarkColors.OnPrimary
    override val onSecondary = DarkColors.OnSecondary
}

object LightColorPalette : ColorPalette {
    override val background = LightColors.Background
    override val surface = LightColors.Surface
    override val surfaceVariant = LightColors.SurfaceVariant
    override val primary = LightColors.Primary
    override val primaryVariant = LightColors.PrimaryVariant
    override val primaryLight = LightColors.PrimaryLight
    override val secondary = LightColors.Secondary
    override val secondaryVariant = LightColors.SecondaryVariant
    override val accentGreen = LightColors.AccentGreen
    override val accentYellow = LightColors.AccentYellow
    override val textPrimary = LightColors.TextPrimary
    override val textSecondary = LightColors.TextSecondary
    override val textTertiary = LightColors.TextTertiary
    override val buttonGradientStart = LightColors.ButtonGradientStart
    override val buttonGradientEnd = LightColors.ButtonGradientEnd
    override val progressPrimary = LightColors.ProgressPrimary
    override val progressSecondary = LightColors.ProgressSecondary
    override val chartBarColor = LightColors.ChartBarColor
    override val trendingBaseline = LightColors.TrendingBaseline
    override val trendingPositive = LightColors.TrendingPositive
    override val trendingNegative = LightColors.TrendingNegative
    override val buttonGradient = LightColors.ButtonGradient
    override val progressGradient = LightColors.ProgressGradient
    override val cardGradient = LightColors.CardGradient
    override val selected = LightColors.Selected
    override val unselected = LightColors.Unselected
    override val divider = LightColors.Divider
    override val onBackground = LightColors.OnBackground
    override val onSurface = LightColors.OnSurface
    override val onPrimary = LightColors.OnPrimary
    override val onSecondary = LightColors.OnSecondary
}
