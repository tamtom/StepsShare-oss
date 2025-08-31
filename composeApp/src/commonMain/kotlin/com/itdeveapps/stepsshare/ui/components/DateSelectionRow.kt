package com.itdeveapps.stepsshare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itdeveapps.stepsshare.ui.theme.AppColors
import kotlinx.datetime.*

data class DayInfo(
    val date: LocalDate,
    val steps: Int,
    val isSelected: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean
)

@Composable
fun DayProgressItem(
    dayInfo: DayInfo,
    onDateSelected: (LocalDate) -> Unit,
    stepsGoal: Int,
    modifier: Modifier = Modifier
) {
    val progress = (dayInfo.steps / stepsGoal.toFloat()).coerceIn(0f, 1f)
    val dateNumber = dayInfo.date.dayOfMonth.toString().padStart(2, '0')
    val dayName = dayInfo.date.dayOfWeek.name.take(3) // MON, TUE, etc.
    
    println("StepsDebug: DayProgressItem for ${dayInfo.date} - steps: ${dayInfo.steps}, progress: $progress (${dayInfo.steps}/$stepsGoal = ${dayInfo.steps / stepsGoal.toFloat()})")
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .width(64.dp)
            .clickable(enabled = !dayInfo.isFuture) {
                onDateSelected(dayInfo.date)
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(56.dp)
        ) {
            // Background circle for selection state
            if (dayInfo.isSelected) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AppColors.primary.copy(alpha = 0.1f))
                )
            }
            
            // Circular progress
            GradientCircularProgress(
                progress = progress,
                diameter = 48.dp,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            
            // Date number in center
            Text(
                text = dateNumber,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (dayInfo.isSelected) AppColors.primary else AppColors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Day name below
        Text(
            text = dayName,
            fontSize = 10.sp,
            fontWeight = if (dayInfo.isToday) FontWeight.Bold else FontWeight.Normal,
            color = when {
                dayInfo.isFuture -> AppColors.textTertiary
                dayInfo.isSelected -> AppColors.primary
                dayInfo.isToday -> AppColors.primary
                else -> AppColors.textSecondary
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DateSelectionRow(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    getStepsForDate: (LocalDate) -> Int = { 0 },
    stepsGoal: Int,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val listState = rememberLazyListState()
    
    // Generate date range: today-29 to today+2 (32 days total)
    // NOTE: Do NOT wrap in remember so that step updates trigger fresh evaluation
    val dateRange = (-29..2).map { dayOffset ->
        val date = today + DatePeriod(days = dayOffset)
        val steps = getStepsForDate(date)
        println("StepsDebug: DateSelectionRow creating DayInfo for $date - getStepsForDate returned: $steps")
        DayInfo(
            date = date,
            steps = steps,
            isSelected = date == selectedDate,
            isToday = date == today,
            isFuture = date > today
        )
    }
    
    // Auto-scroll to selected date on first composition
    LaunchedEffect(selectedDate) {
        val selectedIndex = dateRange.indexOfFirst { it.date == selectedDate }
        if (selectedIndex != -1) {
            listState.animateScrollToItem(
                index = (selectedIndex - 2).coerceAtLeast(0)
            )
        }
    }
    
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = dateRange,
            key = { it.date.toString() }
        ) { dayInfo ->
            DayProgressItem(
                dayInfo = dayInfo.copy(isSelected = dayInfo.date == selectedDate),
                onDateSelected = onDateSelected,
                stepsGoal = stepsGoal
            )
        }
    }
}
