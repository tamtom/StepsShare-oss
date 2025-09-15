package com.itdeveapps.stepsshare.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun GradientCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    diameter: Dp = 160.dp,
    strokeWidth: Dp = 14.dp,
    backgroundAlpha: Float = 0.18f,
    progressColors: List<Color>? = null,
    trackColor: Color? = null
) {
    val clamped = progress.coerceIn(0f, 1f)

    // Resolve theme-dependent colors in composable scope
    val resolvedTrackColor = trackColor
        ?: MaterialTheme.colorScheme.outlineVariant.copy(alpha = backgroundAlpha)
    val resolvedProgressColors = progressColors
        ?: listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )

    Canvas(modifier = modifier.size(diameter)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val inset = stroke.width / 2f
        val arcSize = Size(
            width = this.size.width - stroke.width,
            height = this.size.height - stroke.width
        )

        // Track
        drawArc(
            color = resolvedTrackColor,
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = stroke
        )

        // Progress with gradient
        drawArc(
            brush = Brush.sweepGradient(colors = resolvedProgressColors),
            startAngle = 135f,
            sweepAngle = 270f * clamped,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = stroke.width, cap = StrokeCap.Round)
        )
    }
}


