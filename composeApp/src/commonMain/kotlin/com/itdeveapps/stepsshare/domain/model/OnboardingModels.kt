package com.itdeveapps.stepsshare.domain.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: String,
    val color: ColorVariant,
    val features: List<Feature>,
)

data class ColorVariant(
    val light: Color,
    val dark: Color
) {
    @Composable
    fun getThemeAwareColor(): Color {
        if (LocalInspectionMode.current) {
            return light
        }
        val useDarkTheme = isSystemInDarkTheme()
        return if (useDarkTheme) dark else light
    }
}

data class Feature(
    val text: String,
    val icon: ImageVector? = null
)

