package com.itdeveapps.stepsshare.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itdeveapps.stepsshare.ui.steps.ChartDataPoint
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@Composable
fun StepsLineChart(
    chartData: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    if (chartData.isEmpty()) return

    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    val averageSteps = chartData.map { it.steps }.average()

    val stepData = remember(chartData) {
        listOf(
            Line(
                label = "Steps",
                values = chartData.map { it.steps.toDouble() },
                color = SolidColor(primary),
                firstGradientFillColor = primary.copy(alpha = 0.5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(3.dp),
                curvedEdges = true,
                dotProperties = DotProperties(
                    enabled = true,
                    color = SolidColor(Color.White),
                    strokeWidth = 4.dp,
                    strokeColor = SolidColor(primary)
                )
            ),
            Line(
                label = "Average",
                values = List(chartData.size) { averageSteps },
                color = SolidColor(onSurface),
                drawStyle = DrawStyle.Stroke(
                    width = 2.dp,
                    strokeStyle = StrokeStyle.Dashed(intervals = floatArrayOf(10f, 10f), phase = 5f)
                ),
                curvedEdges = false,
                dotProperties = DotProperties(enabled = false),
                popupProperties = PopupProperties(enabled = false)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxSize(),
            data = stepData,
            animationMode = AnimationMode.Together(delayBuilder = { it * 200L }),
            curvedEdges = true,
            gridProperties = GridProperties(
                xAxisProperties = GridProperties.AxisProperties(
                    thickness = .2.dp,
                    color = SolidColor(gridColor),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(15f, 15f), phase = 10f),
                ),
                yAxisProperties = GridProperties.AxisProperties(
                    enabled = false
                ),
            ),
            dividerProperties = DividerProperties(
                yAxisProperties = LineProperties(enabled = false),
                xAxisProperties = LineProperties(
                    thickness = .5.dp,
                    color = SolidColor(gridColor),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(15f, 15f), phase = 10f),
                )
            ),
            popupProperties = PopupProperties(
                mode = PopupProperties.Mode.PointMode(),
                containerColor = surfaceVariant,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = onSurface,
                    fontWeight = FontWeight.Bold
                ),

                ),
            labelProperties = LabelProperties(
                labels = chartData.map { dataPoint ->
                    dataPoint.date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                },
                textStyle = MaterialTheme.typography.labelSmall.copy(
                    color = labelColor
                ),
                rotation = LabelProperties.Rotation(
                    degree = 0f,
                ),
                enabled = true
            ),
            indicatorProperties = HorizontalIndicatorProperties(enabled = false),
            labelHelperProperties =
                LabelHelperProperties(
                    enabled = false,
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = labelColor
                    )
                ),
        )

    }
}


