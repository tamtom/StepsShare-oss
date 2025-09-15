package com.itdeveapps.stepsshare.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun GradientCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    diameter: Dp = 160.dp,
    strokeWidth: Dp = 14.dp,
    backgroundAlpha: Float = 0.18f,
    progressColors: List<Color>? = null,
    trackColor: Color? = null,
    // glow controls
    glowRadius: Dp = 4.dp,
    glowAlpha: Float = 0.2f,
    glowLayers: Int = 8
) {
    val clamped = progress.coerceIn(0f, 1f)
    val startAngle = 135f
    val totalSweep = 270f
    val seamRotation = 90f // keep sweep seam in the 90° gap

    // Resolve theme colors
    val resolvedTrackColor = trackColor
        ?: MaterialTheme.colorScheme.outlineVariant.copy(alpha = backgroundAlpha)
    val resolvedProgressColors = progressColors
        ?: listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.primary
        )

    Canvas(modifier = modifier.size(diameter)) {
        val baseStroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val inset = baseStroke.width / 2f
        val arcSize = Size(size.width - baseStroke.width, size.height - baseStroke.width)

        // Track
        drawArc(
            color = resolvedTrackColor,
            startAngle = startAngle,
            sweepAngle = totalSweep,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = baseStroke
        )

        val brush = Brush.sweepGradient(
            colors = resolvedProgressColors,
            center = center
        )

        // Glow layers behind main arc
        val glowPx = glowRadius.toPx()
        if (glowLayers > 0 && glowAlpha > 0f && clamped > 0f) {
            withTransform({ rotate(degrees = seamRotation, pivot = center) }) {
                for (i in glowLayers downTo 1) {
                    val t = i / glowLayers.toFloat()
                    val width = baseStroke.width + t * glowPx
                    val alpha = glowAlpha * t * t
                    drawArc(
                        brush = brush,
                        startAngle = startAngle - seamRotation,
                        sweepAngle = totalSweep * clamped,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = width, cap = StrokeCap.Round),
                        alpha = alpha
                    )
                }
            }
        }

        // Main arc on top
        withTransform({ rotate(degrees = seamRotation, pivot = center) }) {
            drawArc(
                brush = brush,
                startAngle = startAngle - seamRotation,
                sweepAngle = totalSweep * clamped,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = baseStroke
            )
        }
    }
}

@Preview
@Composable
fun GradientCircularProgressPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GradientCircularProgress(progress = 0.75f)
    }
}
