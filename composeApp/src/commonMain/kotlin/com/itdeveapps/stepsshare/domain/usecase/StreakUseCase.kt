package com.itdeveapps.stepsshare.domain.usecase

import com.itdeveapps.stepsshare.domain.model.ActivityGoals
import com.itdeveapps.stepsshare.domain.model.UserProfile
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class StreakUseCase(
    private val activityMetricsUseCase: ActivityMetricsUseCase
) {
    
    private fun computeStreakDays(
        maxDays: Int = 365, 
        predicate: (kotlinx.datetime.LocalDate) -> Boolean
    ): Int {
        var streak = 0
        var dayCounter = 0
        var date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        while (dayCounter < maxDays) {
            if (predicate(date)) {
                streak += 1
                date = date.plus(DatePeriod(days = -1))
                dayCounter += 1
            } else {
                break
            }
        }
        return streak
    }

    fun getStepsStreak(
        goal: Int,
        getStepsForDate: (kotlinx.datetime.LocalDate) -> Int
    ): Int {
        return computeStreakDays { date ->
            val stepsForDay = getStepsForDate(date)
            stepsForDay >= goal
        }
    }

    fun getCaloriesStreak(
        goal: Double,
        userProfile: UserProfile,
        getStepsForDate: (kotlinx.datetime.LocalDate) -> Int
    ): Int {
        return computeStreakDays { date ->
            val stepsForDay = getStepsForDate(date).toLong()
            val metrics = activityMetricsUseCase.calculateMetrics(stepsForDay, userProfile)
            metrics.caloriesKcal >= goal
        }
    }

    fun getDistanceStreak(
        goal: Double,
        userProfile: UserProfile,
        getStepsForDate: (kotlinx.datetime.LocalDate) -> Int
    ): Int {
        return computeStreakDays { date ->
            val stepsForDay = getStepsForDate(date).toLong()
            val metrics = activityMetricsUseCase.calculateMetrics(stepsForDay, userProfile)
            metrics.distanceKm >= goal
        }
    }

    fun getTimeStreak(
        goal: Double,
        userProfile: UserProfile,
        getStepsForDate: (kotlinx.datetime.LocalDate) -> Int
    ): Int {
        return computeStreakDays { date ->
            val stepsForDay = getStepsForDate(date).toLong()
            val metrics = activityMetricsUseCase.calculateMetrics(stepsForDay, userProfile)
            metrics.timeMinutes >= goal
        }
    }
}
