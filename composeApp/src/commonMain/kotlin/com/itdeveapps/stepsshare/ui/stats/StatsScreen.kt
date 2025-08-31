package com.itdeveapps.stepsshare.ui.stats

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import org.koin.compose.viewmodel.koinViewModel

import com.itdeveapps.stepsshare.ui.theme.AppColors
import com.itdeveapps.stepsshare.ui.components.GradientButton
import com.itdeveapps.stepsshare.ui.stats.components.TrendingChart
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import com.itdeveapps.stepsshare.data.PermissionState

enum class ChartDataType {
    STEPS, CALORIES, DURATION, DISTANCE
}

// Hoisted UI state for the screen
data class StatsScreenUiState(
    val selectedDataType: ChartDataType = ChartDataType.STEPS,
    val isDataTypeDropdownExpanded: Boolean = false
)

enum class OverViewDateRange {
    WEEK, MONTH
}

enum class TrendingDateRange {
    SEVEN_DAYS, THIRTY_DAYS
}

@Composable
fun StatsScreen() {
    val viewModel: StatsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Hoist UI state to screen level
    var screenUiState by remember { mutableStateOf(StatsScreenUiState()) }

    // Event handlers
    val onWeekSelected = {
        viewModel.selectWeekView()
    }
    val onMonthSelected = {
        viewModel.selectMonthView()
    }
    val onSevenDayTrending = {
        viewModel.selectSevenDayTrending()
    }
    val onThirtyDayTrending = {
        viewModel.selectThirtyDayTrending()
    }
    val onDataTypeSelected = { dataType: ChartDataType ->
        screenUiState = screenUiState.copy(
            selectedDataType = dataType,
            isDataTypeDropdownExpanded = false
        )
    }
    val onDataTypeDropdownExpandedChange = { expanded: Boolean ->
        screenUiState = screenUiState.copy(isDataTypeDropdownExpanded = expanded)
    }
    val onRequestPermission = { viewModel.requestPermission() }
    val onRetry = { viewModel.checkPermission() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            when {
                uiState.permissionState is PermissionState.Denied -> {
                    PermissionRequiredCard(
                        onRequestPermission = onRequestPermission
                    )
                }

                uiState.error != null -> {
                    ErrorCard(
                        error = uiState.error!!,
                        onRetry = onRetry
                    )
                }

                else -> {
                    UnifiedChart(
                        selectedDataType = screenUiState.selectedDataType,
                        isDropdownExpanded = screenUiState.isDataTypeDropdownExpanded,
                        onDataTypeSelected = onDataTypeSelected,
                        onDropdownExpandedChange = onDataTypeDropdownExpandedChange,
                        selectedDateRange = uiState.selectedDateRange,
                        onWeekSelected = onWeekSelected,
                        onMonthSelected = onMonthSelected,
                        totalSteps = uiState.totalSteps,
                        averageSteps = uiState.averageSteps,
                        totalCalories = uiState.totalCalories,
                        averageCalories = uiState.averageCalories,
                        totalDuration = uiState.totalDuration,
                        averageDuration = uiState.averageDuration,
                        totalDistance = uiState.totalDistance,
                        averageDistance = uiState.averageDistance,
                        stepsChartData = uiState.stepsChartData,
                        caloriesChartData = uiState.caloriesChartData,
                        durationChartData = uiState.durationChartData,
                        distanceChartData = uiState.distanceChartData
                    )

                    // Always show Trending Chart below the main chart
                    Spacer(modifier = Modifier.height(16.dp))

                    TrendingChart(
                        selectedDataType = screenUiState.selectedDataType,
                        trendingChartData = uiState.trendingChartData,
                        trendingStatsData = uiState.trendingStatsData,
                        selectedDateRange = uiState.trendingDateRange,
                        onSevenDaySelected = onSevenDayTrending,
                        onThirtyDaySelected = onThirtyDayTrending
                    )
                }
            }
        }
    }
}

@Composable
private fun UnifiedChart(
    selectedDataType: ChartDataType,
    isDropdownExpanded: Boolean,
    onDataTypeSelected: (ChartDataType) -> Unit,
    onDropdownExpandedChange: (Boolean) -> Unit,
    selectedDateRange: OverViewDateRange,
    onWeekSelected: () -> Unit,
    onMonthSelected: () -> Unit,
    totalSteps: Long,
    averageSteps: Double,
    totalCalories: Double,
    averageCalories: Double,
    totalDuration: Double,
    averageDuration: Double,
    totalDistance: Double,
    averageDistance: Double,
    stepsChartData: List<Bars>,
    caloriesChartData: List<Bars>,
    durationChartData: List<Bars>,
    distanceChartData: List<Bars>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.background),
        border = BorderStroke(
            width = 1.dp,
            color = AppColors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Chart title and data type selector
            ChartHeader(
                selectedDataType = selectedDataType,
                isDropdownExpanded = isDropdownExpanded,
                onDataTypeSelected = onDataTypeSelected,
                onDropdownExpandedChange = onDropdownExpandedChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Range Selection
            DateRangeSelector(
                selectedDateRange = selectedDateRange,
                onWeekSelected = onWeekSelected,
                onMonthSelected = onMonthSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Summary stats based on selected data type
            StatsSummary(
                selectedDataType = selectedDataType,
                totalSteps = totalSteps,
                averageSteps = averageSteps,
                totalCalories = totalCalories,
                averageCalories = averageCalories,
                totalDuration = totalDuration,
                averageDuration = averageDuration,
                totalDistance = totalDistance,
                averageDistance = averageDistance
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chart based on selected data type
            val chartData = when (selectedDataType) {
                ChartDataType.STEPS -> stepsChartData
                ChartDataType.CALORIES -> caloriesChartData
                ChartDataType.DURATION -> durationChartData
                ChartDataType.DISTANCE -> distanceChartData
            }

            if (chartData.isNotEmpty()) {
                ChartDisplay(
                    chartData = chartData,
                    selectedDataType = selectedDataType
                )
            } else {
                EmptyChartPlaceholder()
            }
        }
    }
}

@Composable
private fun ChartHeader(
    selectedDataType: ChartDataType,
    isDropdownExpanded: Boolean,
    onDataTypeSelected: (ChartDataType) -> Unit,
    onDropdownExpandedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChartTitle(selectedDataType = selectedDataType)
        DataTypeSelector(
            selectedDataType = selectedDataType,
            isDropdownExpanded = isDropdownExpanded,
            onDataTypeSelected = onDataTypeSelected,
            onDropdownExpandedChange = onDropdownExpandedChange
        )
    }
}

@Composable
private fun ChartTitle(selectedDataType: ChartDataType) {
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
            text = selectedDataType.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge,
            color = AppColors.textPrimary
        )
    }
}

@Composable
private fun DataTypeSelector(
    selectedDataType: ChartDataType,
    isDropdownExpanded: Boolean,
    onDataTypeSelected: (ChartDataType) -> Unit,
    onDropdownExpandedChange: (Boolean) -> Unit
) {
    Box {
        OutlinedButton(
            onClick = { onDropdownExpandedChange(true) },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AppColors.textPrimary
            ),
            border = BorderStroke(
                width = 1.dp,
                color = AppColors.surface
            )
        ) {
            Text(selectedDataType.name.lowercase().replaceFirstChar { it.uppercase() })
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Select data type"
            )
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { onDropdownExpandedChange(false) },
            modifier = Modifier.background(AppColors.surface)
        ) {
            ChartDataType.entries.forEach { dataType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = dataType.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (selectedDataType == dataType) AppColors.primary else AppColors.textPrimary
                        )
                    },
                    onClick = { onDataTypeSelected(dataType) },
                    trailingIcon = {
                        if (selectedDataType == dataType) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = AppColors.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun StatsSummary(
    selectedDataType: ChartDataType,
    totalSteps: Long,
    averageSteps: Double,
    totalCalories: Double,
    averageCalories: Double,
    totalDuration: Double,
    averageDuration: Double,
    totalDistance: Double,
    averageDistance: Double
) {
    val formattingUseCase = com.itdeveapps.stepsshare.domain.usecase.FormattingUseCase()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (selectedDataType) {
            ChartDataType.STEPS -> {
                StatItem(
                    label = "Total",
                    value = totalSteps.toString()
                )
                StatItem(
                    label = "Average",
                    value = averageSteps.toInt().toString()
                )
            }

            ChartDataType.CALORIES -> {
                StatItem(
                    label = "Total",
                    value = "${totalCalories.toInt()} kcal"
                )
                StatItem(
                    label = "Average",
                    value = "${averageCalories.toInt()} kcal"
                )
            }

            ChartDataType.DURATION -> {
                val (totalFormatted, totalUnit) = formattingUseCase.formatTime(totalDuration)
                val (averageFormatted, averageUnit) = formattingUseCase.formatTime(averageDuration)

                StatItem(
                    label = "Total",
                    value = "$totalFormatted $totalUnit"
                )
                StatItem(
                    label = "Average",
                    value = "$averageFormatted $averageUnit"
                )
            }

            ChartDataType.DISTANCE -> {
                val (totalFormatted, totalUnit) = formattingUseCase.formatDistance(totalDistance)
                val (averageFormatted, averageUnit) = formattingUseCase.formatDistance(
                    averageDistance
                )

                StatItem(
                    label = "Total",
                    value = "$totalFormatted $totalUnit"
                )
                StatItem(
                    label = "Average",
                    value = "$averageFormatted $averageUnit"
                )
            }
        }
    }
}

@Composable
private fun ChartDisplay(
    chartData: List<Bars>,
    selectedDataType: ChartDataType
) {
    val formattingUseCase = com.itdeveapps.stepsshare.domain.usecase.FormattingUseCase()

    ColumnChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(all = 8.dp),
        data = chartData,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = 20.dp
        ),
        labelProperties = LabelProperties(
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = AppColors.onSurface
            ),
            enabled = true
        ),
        labelHelperProperties =
            LabelHelperProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.onSurface
                )
            ),
        indicatorProperties = when (selectedDataType) {
            ChartDataType.STEPS -> HorizontalIndicatorProperties(
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.onSurface
                )
            )

            ChartDataType.CALORIES -> HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.onSurface
                ),
                count = IndicatorCount.CountBased(count = 5),
                contentBuilder = { indicator ->
                    "${indicator.toInt()} kcal"
                }
            )

            ChartDataType.DURATION -> HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.onSurface
                ),
                count = IndicatorCount.CountBased(count = 5),
                contentBuilder = { indicator ->
                    val (formattedTime, unit) = formattingUseCase.formatTime(indicator)
                    "$formattedTime $unit"
                }
            )

            ChartDataType.DISTANCE -> HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = AppColors.onSurface
                ),
                count = IndicatorCount.CountBased(count = 5),
                contentBuilder = { indicator ->
                    val (formattedDistance, unit) = formattingUseCase.formatDistance(indicator)
                    "$formattedDistance $unit"
                }
            )
        },
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = GridProperties.AxisProperties(
                enabled = false
            ),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                color = SolidColor(AppColors.onSurface.copy(alpha = 0.3f)),
                thickness = 1.dp,
                lineCount = 1
            )
        ),
        dividerProperties = DividerProperties(
            xAxisProperties = LineProperties(
                thickness = .2.dp,
                color = SolidColor(Color.Gray.copy(alpha = .5f)),
                style = StrokeStyle.Dashed(
                    intervals = floatArrayOf(15f, 15f),
                    phase = 10f
                ),
            ),
            yAxisProperties = LineProperties(
                thickness = .2.dp,
                color = SolidColor(Color.Gray.copy(alpha = .5f)),
                style = StrokeStyle.Dashed(
                    intervals = floatArrayOf(15f, 15f),
                    phase = 10f
                ),
            )
        ),

        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = AppColors.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.textSecondary
        )
    }
}

@Composable
private fun EmptyChartPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.textSecondary
            )
            Text(
                text = "Start walking to see your stats!",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textTertiary
            )
        }
    }
}

@Composable
private fun PermissionRequiredCard(
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Permission Required",
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We need access to your health data to show step statistics.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Grant Permission",
                onClick = onRequestPermission
            )
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = AppColors.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading statistics...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error Loading Data",
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Retry",
                onClick = onRetry
            )
        }
    }
}

@Composable
private fun DateRangeSelector(
    selectedDateRange: OverViewDateRange,
    onWeekSelected: () -> Unit,
    onMonthSelected: () -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onWeekSelected,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDateRange == OverViewDateRange.WEEK) AppColors.primary else AppColors.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = AppColors.surface
                ),
                shape = RoundedCornerShape(8.dp)

            ) {
                Text(
                    "Week",
                    color = if (selectedDateRange == OverViewDateRange.WEEK) AppColors.onPrimary else AppColors.textPrimary
                )
            }

            Button(
                onClick = onMonthSelected,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDateRange == OverViewDateRange.MONTH) AppColors.primary else AppColors.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = AppColors.surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Month",
                    color = if (selectedDateRange == OverViewDateRange.MONTH) AppColors.onPrimary else AppColors.textPrimary
                )
            }
        }
    }
}


