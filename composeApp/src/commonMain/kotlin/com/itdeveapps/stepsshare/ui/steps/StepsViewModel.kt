package com.itdeveapps.stepsshare.ui.steps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itdeveapps.stepsshare.data.PermissionState
import com.itdeveapps.stepsshare.data.StepsRepository
import com.itdeveapps.stepsshare.domain.model.ActivityGoals
import com.itdeveapps.stepsshare.domain.model.ActivityMetrics
import com.itdeveapps.stepsshare.domain.model.UserProfile
import com.itdeveapps.stepsshare.domain.repository.GoalsRepository
import com.itdeveapps.stepsshare.domain.repository.UserProfileRepository
import com.itdeveapps.stepsshare.domain.usecase.ActivityMetricsUseCase
import com.itdeveapps.stepsshare.domain.usecase.FormattingUseCase
import com.itdeveapps.stepsshare.domain.usecase.StreakUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class StepsViewModel(
    private val stepsRepository: StepsRepository,
    private val userProfileRepository: UserProfileRepository,
    private val goalsRepository: GoalsRepository,
    private val activityMetricsUseCase: ActivityMetricsUseCase,
    private val streakUseCase: StreakUseCase,
    private val formattingUseCase: FormattingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StepsUiState())
    val uiState: StateFlow<StepsUiState> = _uiState.asStateFlow()


    // Cache for step data to avoid repeated API calls
    private val stepsCache = mutableMapOf<LocalDate, Long>()
    private var cacheUpdateJob: Job? = null

    init {
        // Initialize today's date
        _uiState.update {
            it.copy(
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            )
        }
        viewModelScope.launch {
            stepsRepository.permissionState.collectLatest { state ->
                println("StepsDebug: Permission state changed to: $state")
                _uiState.update { it.copy(permission = state) }
                // Refresh data when permissions change
                if (state == PermissionState.Granted) {
                    println("StepsDebug: Permission granted, refreshing data...")
                    refreshStepsDataForDateRange()
                    // Also refresh current date's steps
                    refreshSteps()
                }
            }
        }
        // Observe live today steps to update UI reactively
        viewModelScope.launch {
            stepsRepository.todaySteps.collectLatest { todayLive ->
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                if (_uiState.value.date == today) {
                    _uiState.update { it.copy(steps = todayLive) }
                    stepsCache[today] = todayLive
                }
            }
        }
        initializeUserData()
        checkPermission()
        refreshSteps()
        // Don't call refreshStepsDataForDateRange() here - it will be called when permission is granted
    }

    // --- Reactive state for user profile and goals ---
    private var userProfile: UserProfile? = null
    private var goals: ActivityGoals? = null
    private var stepsGoal: Int = 0

    // Initialize profile and goals
    private fun initializeUserData() {
        viewModelScope.launch {
            userProfile = userProfileRepository.fetchUserProfile()
            goals = goalsRepository.fetchActivityGoals()
            stepsGoal = goalsRepository.fetchStepsGoal()
            // React to future updates
            launch {
                goalsRepository.getActivityGoalsStream().collectLatest { updated ->
                    goals = updated
                }
            }
            launch {
                goalsRepository.getStepsGoalStream().collectLatest { updated ->
                    stepsGoal = updated
                }
            }
        }
    }

    fun checkPermission() {
        viewModelScope.launch {
            stepsRepository.checkPermissions()
        }
    }

    fun requestPermission() {
        viewModelScope.launch {
            stepsRepository.requestPermissions()
        }
    }

    fun refreshSteps() {
        viewModelScope.launch {
            println("StepsDebug: refreshSteps() called for date: ${_uiState.value.date}")
            val steps = stepsRepository.readStepsForDate(_uiState.value.date)
            println("StepsDebug: refreshSteps() got $steps steps from repository for ${_uiState.value.date}")
            _uiState.update { it.copy(steps = steps) }
            println("StepsDebug: refreshSteps() updated UI state with $steps steps")
        }
    }

    // --- Metrics calculation based on steps and profile ---
    fun getActivityMetrics(steps: Long = _uiState.value.steps): ActivityMetrics {
        val profile = userProfile ?: return ActivityMetrics(0.0, 0.0, 0.0)
        return activityMetricsUseCase.calculateMetrics(steps, profile)
    }

    // Expose goals for UI
    fun getGoals(): ActivityGoals? = goals
    fun getStepsGoal(): Int = stepsGoal

    // --- Streaks ---
    fun getStepsStreak(goal: Int? = null): Int {
        val effectiveGoal = goal ?: stepsGoal
        return streakUseCase.getStepsStreak(effectiveGoal, ::getStepsForDate)
    }

    // --- UI formatting helpers ---
    fun formatDistance(distanceKm: Double): Pair<String, String> {
        return formattingUseCase.formatDistance(distanceKm)
    }

    fun formatTime(timeMinutes: Double): Pair<String, String> {
        return formattingUseCase.formatTime(timeMinutes)
    }

    fun setDate(newDate: LocalDate) {
        _uiState.update { it.copy(date = newDate) }

        // Use cached data if available, otherwise fetch fresh data
        val cachedSteps = stepsCache[newDate]
        println("StepsDebug: setDate($newDate) - cached steps: $cachedSteps")
        if (cachedSteps != null) {
            println("StepsDebug: Using cached steps $cachedSteps for date $newDate")
            _uiState.update { it.copy(steps = cachedSteps) }
        } else {
            println("StepsDebug: No cached data for $newDate, fetching fresh data")
            refreshSteps()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cacheUpdateJob?.cancel()
    }

    /**
     * Fetches and caches step data for the visible date range (today-29 to today+2)
     */
    private fun refreshStepsDataForDateRange() {
        cacheUpdateJob?.cancel()
        cacheUpdateJob = viewModelScope.launch {
            try {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val startDate = today.plus(DatePeriod(days = -29))
                val endDate = today.plus(DatePeriod(days = 2))

                println("StepsDebug: refreshStepsDataForDateRange() starting - permission: ${stepsRepository.permissionState.value}")
                val stepsData = stepsRepository.readStepsForDateRange(startDate, endDate)
                println("StepsDebug: Fetched steps data for range $startDate to $endDate: ${stepsData.size} entries")
                stepsData.forEach { (date, steps) ->
                    println("StepsDebug: Cache data - $date: $steps steps")
                }

                stepsCache.clear()
                stepsCache.putAll(stepsData)

                // Update chart data based on the refreshed cache
                _uiState.update { it.copy(chartData = getChartData()) }

                // Bump cache version in UI state to trigger recomposition
                _uiState.update { prev -> prev.copy(cacheVersion = prev.cacheVersion + 1) }
                println("StepsDebug: Cache updated with ${stepsData.size} entries, bumped cacheVersion=${_uiState.value.cacheVersion}")

                // Update current date's steps if it's in the cache
                stepsCache[_uiState.value.date]?.let { steps ->
                    println("StepsDebug: Updating UI state for ${_uiState.value.date} with cached steps: $steps")
                    _uiState.update { it.copy(steps = steps) }
                }
            } catch (e: Exception) {
                // Handle error gracefully - cache will remain as is
                println("StepsDebug: Error fetching steps data range: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getStepsForDate(date: LocalDate): Int {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val result = when {
            date > today -> 0 // Future dates always 0
            date == _uiState.value.date -> _uiState.value.steps.toInt() // Current selected date from UI state
            else -> stepsCache[date]?.toInt() ?: 0 // Cached data or 0 if not available
        }

        println("StepsDebug: getStepsForDate($date) -> $result (today=$today, selectedDate=${_uiState.value.date}, cacheSize=${stepsCache.size})")
        if (date == _uiState.value.date) {
            println("StepsDebug: Using UI state steps: ${_uiState.value.steps}")
        } else {
            println("StepsDebug: Cache value for $date: ${stepsCache[date]}")
        }

        return result
    }

    fun getAverageSteps(): Int {
        // Calculate average from cached data or return mock data
        val validSteps = stepsCache.values.filter { it > 0 }
        return if (validSteps.isNotEmpty()) {
            (validSteps.average()).toInt()
        } else {
            6500 // Mock average for when no data is available
        }
    }


    fun getChartData(): List<ChartDataPoint> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val chartData = mutableListOf<ChartDataPoint>()

        // Get last 7 days including today
        for (i in 6 downTo 0) {
            val date = today.minus(DatePeriod(days = i))
            val steps = stepsCache[date] ?: 0L
            chartData.add(ChartDataPoint(date = date, steps = steps))
        }

        return chartData
    }






}
data class StepsUiState(
    val permission: PermissionState = PermissionState.Unknown,
    val date: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val steps: Long = 0,
    val cacheVersion: Int = 0,
    val chartData: List<ChartDataPoint> = emptyList()
)
data class ChartDataPoint(
    val date: LocalDate,
    val steps: Long
)
