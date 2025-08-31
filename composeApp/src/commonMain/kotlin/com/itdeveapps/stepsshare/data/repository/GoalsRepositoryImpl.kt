package com.itdeveapps.stepsshare.data.repository

import com.itdeveapps.stepsshare.domain.model.ActivityGoals
import com.itdeveapps.stepsshare.domain.repository.GoalsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalSettingsApi::class)
class DefaultGoalsRepository(
    private val settings: Settings,
    private val flowSettings: FlowSettings
) : GoalsRepository {

    private companion object Keys {
        const val STEPS = "goals.steps"
        const val CALORIES = "goals.caloriesKcal"
        const val DISTANCE = "goals.distanceKm"
        const val TIME = "goals.timeMinutes"

        const val DEFAULT_STEPS = 10_000
        const val DEFAULT_CALORIES = 300.0
        const val DEFAULT_DISTANCE = 3.0
        const val DEFAULT_TIME = 30.0
    }

    override suspend fun fetchActivityGoals(): ActivityGoals {
        return ActivityGoals(
            caloriesKcal = settings.getDouble(CALORIES, DEFAULT_CALORIES),
            distanceKm = settings.getDouble(DISTANCE, DEFAULT_DISTANCE),
            timeMinutes = settings.getDouble(TIME, DEFAULT_TIME)
        )
    }

    override suspend fun saveActivityGoals(goals: ActivityGoals) {
        settings.putDouble(CALORIES, goals.caloriesKcal)
        settings.putDouble(DISTANCE, goals.distanceKm)
        settings.putDouble(TIME, goals.timeMinutes)
    }

    override fun getActivityGoalsStream(): Flow<ActivityGoals> {
        val caloriesFlow = flowSettings.getDoubleFlow(CALORIES, DEFAULT_CALORIES)
        val distanceFlow = flowSettings.getDoubleFlow(DISTANCE, DEFAULT_DISTANCE)
        val timeFlow = flowSettings.getDoubleFlow(TIME, DEFAULT_TIME)
        return combine(caloriesFlow, distanceFlow, timeFlow) { calories, distance, time ->
            ActivityGoals(caloriesKcal = calories, distanceKm = distance, timeMinutes = time)
        }
    }

    override suspend fun fetchStepsGoal(): Int {
        return settings.getInt(STEPS, DEFAULT_STEPS)
    }

    override suspend fun saveStepsGoal(goal: Int) {
        settings.putInt(STEPS, goal)
    }

    override fun getStepsGoalStream(): Flow<Int> {
        return flowSettings.getIntFlow(STEPS, DEFAULT_STEPS)
    }
}
