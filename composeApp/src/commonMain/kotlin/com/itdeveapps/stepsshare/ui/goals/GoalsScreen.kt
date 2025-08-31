package com.itdeveapps.stepsshare.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Goals", style = MaterialTheme.typography.headlineMedium)

        GoalRow(
            title = "Steps",
            value = formatWithGrouping(state.steps),
            unit = "Steps",
            onMinus = { viewModel.decrementSteps() },
            onPlus = { viewModel.incrementSteps() }
        )

        GoalRow(
            title = "Calories",
            value = state.caloriesKcal.toInt().toString(),
            unit = "kcal",
            onMinus = { viewModel.decrementCalories() },
            onPlus = { viewModel.incrementCalories() }
        )

        GoalRow(
            title = "Distance",
            value = state.distanceKm.toString(),
            unit = "km",
            onMinus = { viewModel.decrementDistance() },
            onPlus = { viewModel.incrementDistance() }
        )

        GoalRow(
            title = "Time",
            value = state.timeMinutes.toInt().toString(),
            unit = "min",
            onMinus = { viewModel.decrementTime() },
            onPlus = { viewModel.incrementTime() }
        )
    }
}

private fun formatWithGrouping(value: Int): String {
    // Simple grouping formatter that works in common code without java.text
    val text = value.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in text.length - 1 downTo 0) {
        sb.append(text[i])
        count++
        if (count % 3 == 0 && i != 0) sb.append(',')
    }
    return sb.reverse().toString()
}

@Composable
private fun GoalRow(
    title: String,
    value: String,
    unit: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(value, style = MaterialTheme.typography.headlineMedium)
                    Text(unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMinus) { Icon(Icons.Outlined.Remove, contentDescription = "Decrease") }
                    Spacer(Modifier.height(0.dp))
                    IconButton(onClick = onPlus) { Icon(Icons.Outlined.Add, contentDescription = "Increase") }
                }
            }
        }
    }
}


