package com.itdeveapps.stepsshare.ui.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itdeveapps.stepsshare.ui.theme.AppColors
import com.itdeveapps.stepsshare.ui.stats.model.TrendIndicator
import com.itdeveapps.stepsshare.ui.stats.model.TrendingStatsData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import com.itdeveapps.stepsshare.ui.stats.ChartDataType
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import androidx.compose.animation.core.snap
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.ui.graphics.SolidColor
import com.itdeveapps.stepsshare.ui.stats.TrendingDateRange
import ir.ehsannarmani.compose_charts.models.AnimationMode

@Composable
fun TrendingChart(
    selectedDataType: ChartDataType,
    trendingChartData: List<Bars>,
    trendingStatsData: TrendingStatsData?,
    selectedDateRange: TrendingDateRange,
    onSevenDaySelected: () -> Unit,
    onThirtyDaySelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = 1.dp,
            color = AppColors.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title with data type
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when (selectedDataType) {
                        ChartDataType.STEPS -> Icons.AutoMirrored.Filled.DirectionsWalk
                        ChartDataType.CALORIES -> Icons.Default.LocalFireDepartment
                        ChartDataType.DURATION -> Icons.Default.Schedule
                        ChartDataType.DISTANCE -> Icons.AutoMirrored.Filled.DirectionsWalk
                    },
                    contentDescription = null,
                    tint = AppColors.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${selectedDataType.name.lowercase().replaceFirstChar { it.uppercase() }} Trending",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date Range Selector
            TrendingDateRangeSelector(
                selectedDateRange = selectedDateRange,
                onSevenDaySelected = onSevenDaySelected,
                onThirtyDaySelected = onThirtyDaySelected
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Summary Stats
            trendingStatsData?.let { stats ->
                TrendingSummaryStats(stats = stats, selectedDataType = selectedDataType)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Trending Chart
            if (trendingChartData.isNotEmpty()) {
                TrendingChartDisplay(
                    chartData = trendingChartData,
                    selectedDataType = selectedDataType,
                    range = selectedDateRange
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Chart Legend
            TrendingChartLegend()
        }
    }
}

@Composable
fun TrendingDateRangeSelector(
    selectedDateRange: TrendingDateRange,
    onSevenDaySelected: () -> Unit,
    onThirtyDaySelected: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSevenDaySelected,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedDateRange == TrendingDateRange.SEVEN_DAYS)
                    AppColors.primary else AppColors.surface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = AppColors.surface
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "7 Days",
                color = if (selectedDateRange == TrendingDateRange.SEVEN_DAYS)
                    AppColors.onPrimary else AppColors.textPrimary
            )
        }
        
        Button(
            onClick = onThirtyDaySelected,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedDateRange == TrendingDateRange.THIRTY_DAYS)
                    AppColors.primary else AppColors.surface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = AppColors.surface
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "30 Days",
                color = if (selectedDateRange == TrendingDateRange.THIRTY_DAYS)
                    AppColors.onPrimary else AppColors.textPrimary
            )
        }
    }
}

@Composable
fun TrendingSummaryStats(stats: TrendingStatsData, selectedDataType: ChartDataType) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = when (selectedDataType) {
                        ChartDataType.STEPS -> "Average Steps"
                        ChartDataType.CALORIES -> "Average Calories"
                        ChartDataType.DURATION -> "Average Duration"
                        ChartDataType.DISTANCE -> "Average Distance"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textSecondary
                )
                Text(
                    text = when (selectedDataType) {
                        ChartDataType.STEPS -> "${stats.averageSteps.toInt()}"
                        ChartDataType.CALORIES -> "${stats.averageCalories.toInt()} kcal"
                        ChartDataType.DURATION -> "${stats.averageDuration.toInt()} min"
                        ChartDataType.DISTANCE -> "${(stats.averageDistance * 10).toInt() / 10.0} km"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Trend indicator
            TrendingIndicator(
                trendPercentage = stats.trendPercentage,
                trendDirection = stats.trendDirection
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Period comparison
        Text(
            text = "Compared to ${stats.baselinePeriod}",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.textSecondary
        )
    }
}

@Composable
fun TrendingIndicator(
    trendPercentage: Double,
    trendDirection: TrendIndicator
) {
    val (backgroundColor, textColor) = when (trendDirection) {
        TrendIndicator.POSITIVE -> AppColors.trendingPositive to Color.White
        TrendIndicator.NEGATIVE -> AppColors.trendingNegative to Color.White
        TrendIndicator.NEUTRAL -> AppColors.trendingBaseline to AppColors.textPrimary
    }
    
    val trendText = when (trendDirection) {
        TrendIndicator.POSITIVE -> "+${(trendPercentage * 10).toInt() / 10.0}%"
        TrendIndicator.NEGATIVE -> "${(trendPercentage * 10).toInt() / 10.0}%"
        TrendIndicator.NEUTRAL -> "${(trendPercentage * 10).toInt() / 10.0}%"
    }
    
    Row (
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = trendText,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
            //Icon for trend direction
        Icon(
            imageVector = when (trendDirection) {
                TrendIndicator.POSITIVE -> Icons.AutoMirrored.Default.TrendingUp
                TrendIndicator.NEGATIVE -> Icons.AutoMirrored.Default.TrendingDown
                TrendIndicator.NEUTRAL ->  Icons.AutoMirrored.Default.TrendingFlat
            },
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp))
    }
}

@Composable
fun TrendingChartLegend() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Chart Legend",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(
                color = AppColors.trendingBaseline,
                label = "Baseline Period"
            )
            LegendItem(
                color = AppColors.trendingPositive,
                label = "Positive Trend"
            )
            LegendItem(
                color = AppColors.trendingNegative,
                label = "Negative Trend"
            )
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.textSecondary
        )
    }
}

@Composable
fun TrendingChartDisplay(
    chartData: List<Bars>,
    selectedDataType: ChartDataType,
    range: TrendingDateRange
) {
    val formattingUseCase = com.itdeveapps.stepsshare.domain.usecase.FormattingUseCase()
    val thickness = when(range) {
        TrendingDateRange.SEVEN_DAYS -> 14.dp
        TrendingDateRange.THIRTY_DAYS -> 4.dp
    }
    ColumnChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
           ,
        data = chartData,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 4.dp, topLeft = 4.dp),
            spacing = 2.dp,
            thickness = thickness
        ),
        labelProperties = LabelProperties(
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = AppColors.onSurface
            ),
            enabled = false
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = AppColors.onSurface
            )
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = false,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = AppColors.onSurface
            ),
            count = IndicatorCount.CountBased(count = 5),
            contentBuilder = { indicator ->
                when (selectedDataType) {
                    ChartDataType.STEPS -> "${indicator.toInt()}"
                    ChartDataType.CALORIES -> "${indicator.toInt()}"
                    ChartDataType.DURATION -> {
                        val (formattedTime, unit) = formattingUseCase.formatTime(indicator)
                        "$formattedTime $unit"
                    }
                    ChartDataType.DISTANCE -> {
                        val (formattedDistance, unit) = formattingUseCase.formatDistance(indicator)
                        "$formattedDistance $unit"
                    }
                }
            }
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(enabled = false),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                color = SolidColor(AppColors.onSurface.copy(alpha = 0.3f)),
                thickness = 1.dp,
                lineCount = 1
            )
        ),
        dividerProperties = DividerProperties(
            xAxisProperties = LineProperties(
                thickness = 0.2.dp,
                color = SolidColor(Color.Gray.copy(alpha = 0.5f)),
                style = StrokeStyle.Dashed(intervals = floatArrayOf(15f, 15f), phase = 10f)
            ),
            yAxisProperties = LineProperties(
                thickness = 0.2.dp,
                color = SolidColor(Color.Gray.copy(alpha = 0.5f)),
                style = StrokeStyle.Dashed(intervals = floatArrayOf(15f, 15f), phase = 10f)
            )
        ),
        animationMode = AnimationMode.Together(),
        animationDelay = 0,
        animationSpec = snap()
    )
}
