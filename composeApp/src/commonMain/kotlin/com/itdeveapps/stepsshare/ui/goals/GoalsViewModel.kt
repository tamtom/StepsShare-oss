package com.itdeveapps.stepsshare.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itdeveapps.stepsshare.domain.model.ActivityGoals
import com.itdeveapps.stepsshare.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class GoalsUiState(
    val steps: Int = 10_000,
    val caloriesKcal: Double = 300.0,
    val distanceKm: Double = 3.0,
    val timeMinutes: Double = 30.0
)

class GoalsViewModel(
    private val goalsRepository: GoalsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                goalsRepository.getStepsGoalStream(),
                goalsRepository.getActivityGoalsStream()
            ) { steps, goals ->
                _uiState.value.copy(
                    steps = steps,
                    caloriesKcal = goals.caloriesKcal,
                    distanceKm = goals.distanceKm,
                    timeMinutes = goals.timeMinutes
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    // --- Mutators ---
    fun setSteps(value: Int) = persistSteps(value.coerceIn(1_000, 50_000))
    fun incrementSteps(step: Int = 500) = setSteps(_uiState.value.steps + step)
    fun decrementSteps(step: Int = 500) = setSteps(_uiState.value.steps - step)

    fun setCalories(value: Double) = persistGoals(_uiState.value.copy(caloriesKcal = value.coerceIn(50.0, 5_000.0)))
    fun incrementCalories(step: Double = 50.0) = setCalories(_uiState.value.caloriesKcal + step)
    fun decrementCalories(step: Double = 50.0) = setCalories(_uiState.value.caloriesKcal - step)

    fun setDistance(value: Double) = persistGoals(_uiState.value.copy(distanceKm = value.coerceIn(0.5, 100.0)))
    fun incrementDistance(step: Double = 0.5) = setDistance(_uiState.value.distanceKm + step)
    fun decrementDistance(step: Double = 0.5) = setDistance(_uiState.value.distanceKm - step)

    fun setTime(value: Double) = persistGoals(_uiState.value.copy(timeMinutes = value.coerceIn(5.0, 600.0)))
    fun incrementTime(step: Double = 5.0) = setTime(_uiState.value.timeMinutes + step)
    fun decrementTime(step: Double = 5.0) = setTime(_uiState.value.timeMinutes - step)

    private fun persistSteps(newSteps: Int) {
        viewModelScope.launch {
            goalsRepository.saveStepsGoal(newSteps)
        }
    }

    private fun persistGoals(newState: GoalsUiState) {
        viewModelScope.launch {
            goalsRepository.saveActivityGoals(
                ActivityGoals(
                    caloriesKcal = newState.caloriesKcal,
                    distanceKm = newState.distanceKm,
                    timeMinutes = newState.timeMinutes
                )
            )
        }
    }
}


