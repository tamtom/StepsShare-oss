package com.itdeveapps.stepsshare.ui.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itdeveapps.stepsshare.data.PermissionState
import com.itdeveapps.stepsshare.domain.model.ActivityGoals
import com.itdeveapps.stepsshare.domain.model.ActivityMetrics
import com.itdeveapps.stepsshare.ui.components.DateSelectionRow
import com.itdeveapps.stepsshare.ui.components.GoalMetricDial
import com.itdeveapps.stepsshare.ui.components.MainStepsDisplay
import com.itdeveapps.stepsshare.ui.components.StreakBadge
import com.itdeveapps.stepsshare.ui.components.StepsLineChart
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt


@Composable
fun StepsScreen(viewModel: StepsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    StepsScreenContent(
        state = state,
        onCheckPermission = viewModel::checkPermission,
        onRequestPermission = {
            viewModel.requestPermission()
            viewModel.refreshSteps()
        },
        onDateSelected = viewModel::setDate,
        getStepsForDate = { date ->
            // Depend on cacheVersion from UI state to recompute when cache updates
            state.cacheVersion
            viewModel.getStepsForDate(date)
        },
        stepsGoal = viewModel.getStepsGoal(),
        averageSteps = viewModel.getAverageSteps(),
        stepsStreak = viewModel.getStepsStreak(),
        activityMetrics = viewModel.getActivityMetrics(state.steps),
        goals = viewModel.getGoals(),
        formatDistance = viewModel::formatDistance,
        formatTime = viewModel::formatTime
    )
}

@Composable
fun StepsScreenContent(
    state: StepsUiState,
    onCheckPermission: () -> Unit,
    onRequestPermission: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    getStepsForDate: (LocalDate) -> Int,
    stepsGoal: Int,
    averageSteps: Int,
    stepsStreak: Int,
    activityMetrics: ActivityMetrics,
    goals: ActivityGoals?,
    formatDistance: (Double) -> Pair<String, String>,
    formatTime: (Double) -> Pair<String, String>
) {
    if (state.permission != PermissionState.Granted) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val description =
                "We don't have permission to access your steps data.\nPlease enable \"Motion & Fitness\" for this application in Settings."
            when (val permission = state.permission) {
                PermissionState.Unknown -> {
                    PermissionPrompt(
                        title = "Permission required",
                        description = description,
                        buttonText = "Check Permission",
                        onClick = onCheckPermission
                    )
                }

                is PermissionState.Denied -> {
                    val actionLabel =
                        if (permission.canRequestAgain) "Enable \"Motion & Fitness\"" else "Open Settings"
                    PermissionPrompt(
                        title = "Permission required",
                        description = description,
                        buttonText = actionLabel,
                        onClick = onRequestPermission
                    )
                }

                is PermissionState.NotAvailable -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconBadge()
                        Text(
                            text = "Feature not available",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = permission.reason
                                ?: "This device does not support step counting.",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Button(onClick = onCheckPermission) { Text("OK") }
                    }
                }

                PermissionState.Granted -> { /* No-op */
                }
            }
        }
    }
    Column(

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 16.dp).then(
            if (state.permission != PermissionState.Granted) Modifier.blur(18.dp) else Modifier.blur(
                0.dp
            ).verticalScroll(
                rememberScrollState()
            )
        )
    ) {
        // Date selection row with circular progress
        DateSelectionRow(
            selectedDate = state.date,
            onDateSelected = onDateSelected,
            getStepsForDate = getStepsForDate,
            stepsGoal = stepsGoal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main steps display with large circular progress
        MainStepsDisplay(
            currentSteps = state.steps,
            goalSteps = stepsGoal,
            averageSteps = averageSteps
        )

        // Steps streak badge
        StreakBadge(days = stepsStreak, modifier = Modifier.padding(top = 4.dp))

        if (goals != null) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val kcalProgress = (activityMetrics.caloriesKcal / goals.caloriesKcal).toFloat()
                GoalMetricDial(
                    progress = kcalProgress,
                    icon = androidx.compose.material.icons.Icons.Outlined.Whatshot,
                    valueText = activityMetrics.caloriesKcal.toInt().toString(),
                    unitText = "kcal"
                )

                val distanceProgress = (activityMetrics.distanceKm / goals.distanceKm).toFloat()
                val (distanceValue, distanceUnit) = formatDistance(activityMetrics.distanceKm)
                GoalMetricDial(
                    progress = distanceProgress,
                    icon = androidx.compose.material.icons.Icons.Outlined.Route,
                    valueText = distanceValue,
                    unitText = distanceUnit
                )

                val timeProgress = (activityMetrics.timeMinutes / goals.timeMinutes).toFloat()
                val (timeValue, timeUnit) = formatTime(activityMetrics.timeMinutes)
                GoalMetricDial(
                    progress = timeProgress,
                    icon = androidx.compose.material.icons.Icons.Outlined.AccessTime,
                    valueText = timeValue,
                    unitText = timeUnit
                )
            }
        }

        // Steps Chart
        if (state.chartData.isNotEmpty()) {
            StepsLineChart(
                chartData = state.chartData,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

    }
}

@Composable
private fun PermissionPrompt(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        IconBadge()
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onClick) {
            Text(buttonText)
        }
    }
}

@Composable
private fun IconBadge() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Outlined.DirectionsRun,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
@Preview
fun StepsScreenContentPreview() {
    StepsScreenContent(
        state = StepsUiState(permission = PermissionState.Granted, steps = 8500L),
        onCheckPermission = {},
        onRequestPermission = {},
        onDateSelected = {},
        getStepsForDate = { 0 },
        stepsGoal = 10000,
        averageSteps = 7500,
        stepsStreak = 5,
        activityMetrics = ActivityMetrics(caloriesKcal = 350.0, distanceKm = 6.8, timeMinutes = 85.0),
        goals = ActivityGoals(caloriesKcal = 400.0, distanceKm = 8.0, timeMinutes = 90.0),
        formatDistance = { distance -> Pair("${distance.roundToInt()}", "km") },
        formatTime = { time -> Pair("${time.roundToInt()}", "min") }
    )
}