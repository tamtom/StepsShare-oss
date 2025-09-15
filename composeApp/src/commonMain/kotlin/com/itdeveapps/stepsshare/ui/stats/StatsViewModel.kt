package com.itdeveapps.stepsshare.ui.stats

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itdeveapps.stepsshare.data.PermissionState
import com.itdeveapps.stepsshare.data.StepsRepository
import com.itdeveapps.stepsshare.domain.model.UserProfile
import com.itdeveapps.stepsshare.domain.usecase.ActivityMetricsUseCase
import com.itdeveapps.stepsshare.domain.usecase.FormattingUseCase
import com.itdeveapps.stepsshare.domain.usecase.TrendingRange
import com.itdeveapps.stepsshare.domain.usecase.TrendingUseCase
import com.itdeveapps.stepsshare.ui.stats.model.TrendIndicator
import com.itdeveapps.stepsshare.ui.stats.model.TrendingStatsData
import com.itdeveapps.stepsshare.ui.theme.CustomColors
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class StatsViewModel(
    private val stepsRepository: StepsRepository,
    private val formattingUseCase: FormattingUseCase,
    private val activityMetricsUseCase: ActivityMetricsUseCase,
    private val trendingUseCase: TrendingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    // Mock user profile for calories calculation - in real app this would come from UserProfileRepository
    private val mockUserProfile = UserProfile(
        weightKg = 70.0,
        heightCm = 170.0,
        age = 30,
        gender = "male"
    )

    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = today.plus(DatePeriod(days = -6)) // Last 7 days including today
        setDateRange(startDate, today)
        setTrendingDateRange(
            startDate = today.plus(DatePeriod(days = -13)), // Last 14 days
            endDate = today
        )
    }

    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.update { prevValue ->
            prevValue.copy(
                startDate = startDate,
                endDate = endDate,
                isLoading = true
            )
        }

        refreshOverviewData()
    }

    fun setTrendingDateRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.update { prevValue ->
            prevValue.copy(
                isLoading = true
            )
        }

        refreshTrendingData()
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

    private fun refreshOverviewData() {
        viewModelScope.launch {
            try {
                _uiState.update { prevValue ->
                    prevValue.copy(isLoading = true, error = null)
                }
                
                // Check permissions first
                val permissionState = stepsRepository.permissionState.value
                if (permissionState != PermissionState.Granted) {
                    _uiState.update { prevValue ->
                        prevValue.copy(
                            permissionState = permissionState,
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // Fetch steps data for the date range
                val stepsData = stepsRepository.readStepsForDateRange(_uiState.value.startDate, _uiState.value.endDate)
                
                // Compute all derived values for overview chart only
                val totalSteps = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    stepsData.values.sum()
                } else {
                    generateMockTotalSteps()
                }
                
                val averageSteps = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    stepsData.values.filter { it > 0 }.average()
                } else {
                    generateMockAverageSteps()
                }
                
                val totalCalories = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    stepsData.values.sumOf { steps ->
                        activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
                    }
                } else {
                    generateMockTotalCalories()
                }
                
                val averageCalories = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    val calories = stepsData.values.filter { it > 0 }.map { steps ->
                        activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
                    }
                    calories.average()
                } else {
                    generateMockAverageCalories()
                }
                
                val totalDuration = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    stepsData.values.sumOf { steps ->
                        activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
                    }
                } else {
                    generateMockTotalDuration()
                }
                
                val averageDuration = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    val durations = stepsData.values.filter { it > 0 }.map { steps ->
                        activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
                    }
                    durations.average()
                } else {
                    generateMockAverageDuration()
                }
                
                val totalDistance = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    stepsData.values.sumOf { steps ->
                        activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
                    }
                } else {
                    generateMockTotalDistance()
                }
                
                val averageDistance = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    val distances = stepsData.values.filter { it > 0 }.map { steps ->
                        activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
                    }
                    distances.average()
                } else {
                    generateMockAverageDistance()
                }
                
                val stepsChartData = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    generateStepsChartData(stepsData)
                } else {
                    generateMockStepsData()
                }
                
                val caloriesChartData = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    generateCaloriesChartData(stepsData)
                } else {
                    generateMockCaloriesData()
                }
                
                val durationChartData = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    generateDurationChartData(stepsData)
                } else {
                    generateMockDurationData()
                }
                
                val distanceChartData = if (stepsData.isNotEmpty() && stepsData.values.any { it > 0 }) {
                    generateDistanceChartData(stepsData)
                } else {
                    generateMockDistanceData()
                }
                
                _uiState.update { prevValue ->
                    prevValue.copy(
                        stepsData = stepsData,
                        permissionState = permissionState,
                        isLoading = false,
                        error = null,
                        totalSteps = totalSteps,
                        averageSteps = averageSteps,
                        totalCalories = totalCalories,
                        averageCalories = averageCalories,
                        totalDuration = totalDuration,
                        averageDuration = averageDuration,
                        totalDistance = totalDistance,
                        averageDistance = averageDistance,
                        stepsChartData = stepsChartData,
                        caloriesChartData = caloriesChartData,
                        durationChartData = durationChartData,
                        distanceChartData = distanceChartData
                    )
                }
            } catch (e: Exception) {
                _uiState.update { prevValue ->
                    prevValue.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    private fun refreshTrendingData() {
        viewModelScope.launch {
            try {
                _uiState.update { prevValue ->
                    prevValue.copy(isLoading = true, error = null)
                }

                // Check permissions first
                val permissionState = stepsRepository.permissionState.value
                if (permissionState != PermissionState.Granted) {
                    _uiState.update { prevValue ->
                        prevValue.copy(
                            permissionState = permissionState,
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // Calculate trending date range based on selected trending range
                val (trendingStartDate, trendingEndDate) = when (_uiState.value.trendingDateRange) {
                    TrendingDateRange.SEVEN_DAYS -> {
                        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val startDate = today.plus(DatePeriod(days = -13)) // Last 14 days
                        startDate to today
                    }
                    TrendingDateRange.THIRTY_DAYS -> {
                        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val startDate = today.plus(DatePeriod(days = -59)) // Last 60 days
                        startDate to today
                    }
                }

                // Fetch steps data for the trending date range
                val trendingStepsData = stepsRepository.readStepsForDateRange(trendingStartDate, trendingEndDate)

                // Generate trending chart data
                val trendingChartData = if (trendingStepsData.isNotEmpty() && trendingStepsData.values.any { it > 0 }) {
                    generateTrendingChartData(trendingStepsData)
                } else {
                    generateMockTrendingData()
                }

                // Generate trending stats data
                val trendingStatsData = if (trendingStepsData.isNotEmpty() && trendingStepsData.values.any { it > 0 }) {
                    generateTrendingStatsData(trendingStepsData)
                } else {
                    generateMockTrendingStatsData()
                }

                _uiState.update { prevValue ->
                    prevValue.copy(
                        trendingChartData = trendingChartData,
                        trendingStatsData = trendingStatsData,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { prevValue ->
                    prevValue.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    private fun generateStepsChartData(stepsData: Map<LocalDate, Long>): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            generateMonthlyStepsData(stepsData)
        } else {
            // Generate all dates in the range for weekly view
            val dateRange = generateDateRange(_uiState.value.startDate, _uiState.value.endDate)
            
            dateRange.map { date ->
                val steps = stepsData[date] ?: 0L
                Bars(
                    label = formatDateForLabel(date),
                    values = listOf(
                        Bars.Data(
                            label = "Steps",
                            value = steps.toDouble(),
                            color = createStepsGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateCaloriesChartData(stepsData: Map<LocalDate, Long>): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            generateMonthlyCaloriesData(stepsData)
        } else {
            // Generate all dates in the range for weekly view
            val dateRange = generateDateRange(_uiState.value.startDate, _uiState.value.endDate)

            dateRange.map { date ->
                val steps = stepsData[date] ?: 0L
                val calories =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
                Bars(
                    label = formatDateForLabel(date),
                    values = listOf(
                        Bars.Data(
                            label = "Calories",
                            value = calories,
                            color = createCaloriesGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateDurationChartData(stepsData: Map<LocalDate, Long>): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            generateMonthlyDurationData(stepsData)
        } else {
            // Generate all dates in the range for weekly view
            val dateRange = generateDateRange(_uiState.value.startDate, _uiState.value.endDate)

            dateRange.map { date ->
                val steps = stepsData[date] ?: 0L
                val duration =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes

                Bars(
                    label = formatDateForLabel(date),
                    values = listOf(
                        Bars.Data(
                            label = "Duration",
                            value = duration,
                            color = createDurationGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateDistanceChartData(stepsData: Map<LocalDate, Long>): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            generateMonthlyDistanceData(stepsData)
        } else {
            // Generate all dates in the range for weekly view
            val dateRange = generateDateRange(_uiState.value.startDate, _uiState.value.endDate)

            dateRange.map { date ->
                val steps = stepsData[date] ?: 0L
                val distance =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm

                Bars(
                    label = formatDateForLabel(date),
                    values = listOf(
                        Bars.Data(
                            label = "Distance",
                            value = distance,
                            color = createDistanceGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateTrendingChartData(stepsData: Map<LocalDate, Long>): List<Bars> {
        val trendingRange = when (_uiState.value.trendingDateRange) {
            TrendingDateRange.SEVEN_DAYS -> TrendingRange.SEVEN_DAYS
            TrendingDateRange.THIRTY_DAYS -> TrendingRange.THIRTY_DAYS
        }
        
        val trendingData = trendingUseCase.generateTrendingChartData(stepsData, trendingRange)
        
        // Combine baseline and current data into a single chart
        val allBars = mutableListOf<Bars>()
        
        // Add baseline data (grey bars)
        trendingData.baselineData.forEach { dataPoint ->
            allBars.add(
                Bars(
                    label = dataPoint.label,
                    values = listOf(
                        Bars.Data(
                            label = "Baseline",
                            value = dataPoint.value,
                            color = createTrendingBaselineGradient()
                        )
                    )
                )
            )
        }
        
        // Add current data (colored based on trend)
        trendingData.currentData.forEach { dataPoint ->
            allBars.add(
                Bars(
                    label = dataPoint.label,
                    values = listOf(
                        Bars.Data(
                            label = "Current",
                            value = dataPoint.value,
                            color = when (trendingData.trendDirection) {
                                TrendIndicator.POSITIVE -> createTrendingPositiveGradient()
                                TrendIndicator.NEGATIVE -> createTrendingNegativeGradient()
                                TrendIndicator.NEUTRAL -> createTrendingBaselineGradient()
                            }
                        )
                    )
                )
            )
        }
        
        return allBars
    }

    private fun generateTrendingStatsData(stepsData: Map<LocalDate, Long>): TrendingStatsData {
        val trendingRange = when (_uiState.value.trendingDateRange) {
            TrendingDateRange.SEVEN_DAYS -> TrendingRange.SEVEN_DAYS
            TrendingDateRange.THIRTY_DAYS -> TrendingRange.THIRTY_DAYS
        }
        
        val trendingData = trendingUseCase.generateTrendingChartData(stepsData, trendingRange)
        
        val baselinePeriod = when (trendingRange) {
            TrendingRange.SEVEN_DAYS -> "Last 7 Days"
            TrendingRange.THIRTY_DAYS -> "Last 30 Days"
        }
        
        val currentPeriod = when (trendingRange) {
            TrendingRange.SEVEN_DAYS -> "This Week"
            TrendingRange.THIRTY_DAYS -> "This Month"
        }
        
        // Calculate all metrics for trending
        val totalSteps = stepsData.values.sum()
        val averageSteps = trendingData.currentAverage
        val totalCalories = stepsData.values.sumOf { steps ->
            activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
        }
        val averageCalories = if (stepsData.isNotEmpty()) totalCalories / stepsData.size else 0.0
        val totalDuration = stepsData.values.sumOf { steps ->
            activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
        }
        val averageDuration = if (stepsData.isNotEmpty()) totalDuration / stepsData.size else 0.0
        val totalDistance = stepsData.values.sumOf { steps ->
            activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
        }
        val averageDistance = if (stepsData.isNotEmpty()) totalDistance / stepsData.size else 0.0
        
        return TrendingStatsData(
            totalSteps = totalSteps,
            averageSteps = averageSteps,
            totalCalories = totalCalories,
            averageCalories = averageCalories,
            totalDuration = totalDuration,
            averageDuration = averageDuration,
            totalDistance = totalDistance,
            averageDistance = averageDistance,
            trendPercentage = trendingData.trendPercentage,
            trendDirection = trendingData.trendDirection,
            baselinePeriod = baselinePeriod,
            currentPeriod = currentPeriod
        )
    }

    private fun generateMockTrendingStatsData(): TrendingStatsData {
        return TrendingStatsData(
            totalSteps = 85000L,
            averageSteps = 12142.86,
            totalCalories = 4250.0,
            averageCalories = 607.14,
            totalDuration = 425.0,
            averageDuration = 60.71,
            totalDistance = 63.75,
            averageDistance = 9.11,
            trendPercentage = 15.2,
            trendDirection = TrendIndicator.POSITIVE,
            baselinePeriod = "Last 7 Days",
            currentPeriod = "This Week"
        )
    }

    private fun generateMonthlyStepsData(stepsData: Map<LocalDate, Long>): List<Bars> {
        val months = generateLast6Months()

        return months.map { month ->
            val monthlySteps = aggregateStepsForMonth(stepsData, month)
            Bars(
                label = formatMonthForLabel(month),
                values = listOf(
                    Bars.Data(
                        label = "Steps",
                        value = monthlySteps.toDouble(),
                        color = createStepsGradient()
                    )
                )
            )
        }
    }

    private fun generateMonthlyCaloriesData(stepsData: Map<LocalDate, Long>): List<Bars> {
        val months = generateLast6Months()

        return months.map { month ->
            val monthlySteps = aggregateStepsForMonth(stepsData, month)
            val monthlyCalories =
                activityMetricsUseCase.calculateMetrics(monthlySteps, mockUserProfile).caloriesKcal
            Bars(
                label = formatMonthForLabel(month),
                values = listOf(
                    Bars.Data(
                        label = "Calories",
                        value = monthlyCalories,
                        color = createCaloriesGradient()
                    )
                )
            )
        }
    }

    private fun generateMonthlyDurationData(stepsData: Map<LocalDate, Long>): List<Bars> {
        val months = generateLast6Months()

        return months.map { month ->
            val monthlySteps = aggregateStepsForMonth(stepsData, month)
            val monthlyDuration =
                activityMetricsUseCase.calculateMetrics(monthlySteps, mockUserProfile).timeMinutes

            Bars(
                label = formatMonthForLabel(month),
                values = listOf(
                    Bars.Data(
                        label = "Duration",
                        value = monthlyDuration,
                        color = createDurationGradient()
                    )
                )
            )
        }
    }

    private fun generateMonthlyDistanceData(stepsData: Map<LocalDate, Long>): List<Bars> {
        val months = generateLast6Months()

        return months.map { month ->
            val monthlySteps = aggregateStepsForMonth(stepsData, month)
            val monthlyDistance =
                activityMetricsUseCase.calculateMetrics(monthlySteps, mockUserProfile).distanceKm

            Bars(
                label = formatMonthForLabel(month),
                values = listOf(
                    Bars.Data(
                        label = "Distance",
                        value = monthlyDistance,
                        color = createDistanceGradient()
                    )
                )
            )
        }
    }

    private fun aggregateStepsForMonth(stepsData: Map<LocalDate, Long>, month: LocalDate): Long {
        // Filter steps data for the specific month and sum them up
        return stepsData.entries
            .filter { (date, _) ->
                date.year == month.year && date.month == month.month
            }
            .sumOf { it.value }
    }

    private fun generateLast6Months(): List<LocalDate> {
        val months = mutableListOf<LocalDate>()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // Start from 5 months ago (to get 6 months total including current month)
        for (i in 5 downTo 0) {
            val monthDate = today.plus(DatePeriod(months = -i))
            // Set to first day of each month for consistent month representation
            val firstDayOfMonth = LocalDate(monthDate.year, monthDate.month, 1)
            months.add(firstDayOfMonth)
        }

        return months
    }

    private fun formatMonthForLabel(date: LocalDate): String {
        return when (date.month) {
            Month.JANUARY -> "Jan"
            Month.FEBRUARY -> "Feb"
            Month.MARCH -> "Mar"
            Month.APRIL -> "Apr"
            Month.MAY -> "May"
            Month.JUNE -> "Jun"
            Month.JULY -> "Jul"
            Month.AUGUST -> "Aug"
            Month.SEPTEMBER -> "Sep"
            Month.OCTOBER -> "Oct"
            Month.NOVEMBER -> "Nov"
            Month.DECEMBER -> "Dec"
            else -> "Unknown"
        }
    }

    private fun generateDateRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            dates.add(currentDate)
            currentDate = currentDate.plus(DatePeriod(days = 1))
        }
        return dates
    }

    private fun formatDateForLabel(date: LocalDate): String {
        return when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Mon"
            DayOfWeek.TUESDAY -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fri"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
            else -> "Unknown"
        }
    }

    private fun createStepsGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                CustomColors.ProgressGradientStart,
                CustomColors.ProgressGradientEnd
            )
        )
    }

    private fun createCaloriesGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                CustomColors.ButtonGradientStart,
                CustomColors.ButtonGradientEnd
            )
        )
    }

    private fun createDurationGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                CustomColors.ButtonGradientStart,
                CustomColors.ButtonGradientEnd
            )
        )
    }

    private fun createDistanceGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                CustomColors.ButtonGradientStart,
                CustomColors.ButtonGradientEnd
            )
        )
    }

    private fun createTrendingBaselineGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                Color.Gray,
                Color.Gray
            )
        )
    }

    private fun createTrendingPositiveGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                CustomColors.TrendingPositive,
                CustomColors.TrendingPositive
            )
        )
    }

    private fun createTrendingNegativeGradient(): Brush {
        return Brush.verticalGradient(
            colors = listOf(
                CustomColors.TrendingNegative,
                CustomColors.TrendingNegative
            )
        )
    }

    fun selectWeekView() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = today.plus(DatePeriod(days = -6))
        _uiState.update { it.copy(selectedDateRange = OverViewDateRange.WEEK) }
        setDateRange(startDate, today)
    }

    fun selectMonthView() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = today.plus(DatePeriod(months = -5)) // Last 6 months including current month
        _uiState.update { it.copy(selectedDateRange = OverViewDateRange.MONTH) }
        setDateRange(startDate, today)
    }

    fun selectSevenDayTrending() {
        _uiState.update { it.copy(trendingDateRange = TrendingDateRange.SEVEN_DAYS) }
        refreshTrendingData()
    }

    fun selectThirtyDayTrending() {
        _uiState.update { it.copy(trendingDateRange = TrendingDateRange.THIRTY_DAYS) }
        refreshTrendingData()
    }

    // Mock data generation methods for testing on emulator
    private fun generateMockStepsData(): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, generate mock monthly data
            val months = generateLast6Months()
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)

            months.zip(mockMonthlySteps).map { (month, steps) ->
                Bars(
                    label = formatMonthForLabel(month),
                    values = listOf(
                        Bars.Data(
                            label = "Steps",
                            value = steps.toDouble(),
                            color = createStepsGradient()
                        )
                    )
                )
            }
        } else {
            // For weekly view, generate mock daily data
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            dayLabels.zip(mockSteps).map { (day, steps) ->
                Bars(
                    label = day,
                    values = listOf(
                        Bars.Data(
                            label = "Steps",
                            value = steps.toDouble(),
                            color = createStepsGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateMockCaloriesData(): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, generate mock monthly data
            val months = generateLast6Months()
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)

            months.zip(mockMonthlySteps).map { (month, steps) ->
                val calories =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
                Bars(
                    label = formatMonthForLabel(month),
                    values = listOf(
                        Bars.Data(
                            label = "Calories",
                            value = calories,
                            color = createCaloriesGradient()
                        )
                    )
                )
            }
        } else {
            // For weekly view, generate mock daily data
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            dayLabels.zip(mockSteps).map { (day, steps) ->
                val calories =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
                Bars(
                    label = day,
                    values = listOf(
                        Bars.Data(
                            label = "Calories",
                            value = calories,
                            color = createCaloriesGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateMockDurationData(): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, generate mock monthly data
            val months = generateLast6Months()
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)

            months.zip(mockMonthlySteps).map { (month, steps) ->
                val duration =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes

                Bars(
                    label = formatMonthForLabel(month),
                    values = listOf(
                        Bars.Data(
                            label = "Duration",
                            value = duration,
                            color = createDurationGradient()
                        )
                    )
                )
            }
        } else {
            // For weekly view, generate mock daily data
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            dayLabels.zip(mockSteps).map { (day, steps) ->
                val duration =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes

                Bars(
                    label = day,
                    values = listOf(
                        Bars.Data(
                            label = "Duration",
                            value = duration,
                            color = createDurationGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateMockDistanceData(): List<Bars> {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, generate mock monthly data
            val months = generateLast6Months()
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)

            months.zip(mockMonthlySteps).map { (month, steps) ->
                val distance =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm

                Bars(
                    label = formatMonthForLabel(month),
                    values = listOf(
                        Bars.Data(
                            label = "Distance",
                            value = distance,
                            color = createDistanceGradient()
                        )
                    )
                )
            }
        } else {
            // For weekly view, generate mock daily data
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            dayLabels.zip(mockSteps).map { (day, steps) ->
                val distance =
                    activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm

                Bars(
                    label = day,
                    values = listOf(
                        Bars.Data(
                            label = "Distance",
                            value = distance,
                            color = createDistanceGradient()
                        )
                    )
                )
            }
        }
    }

    private fun generateMockTrendingData(): List<Bars> {
        return when (_uiState.value.trendingDateRange) {
            TrendingDateRange.SEVEN_DAYS -> {
                // For 7-day trending: 14 days total (7 baseline + 7 current)
                val mockBaselineSteps = listOf(8000L, 8500L, 9000L, 8200L, 8800L, 9200L, 8700L)
                val mockCurrentSteps = listOf(9500L, 10200L, 9800L, 10500L, 10100L, 10800L, 10300L)
                
                val allBars = mutableListOf<Bars>()
                
                // Add baseline data (grey bars)
                mockBaselineSteps.forEachIndexed { index, steps ->
                    allBars.add(
                        Bars(
                            label = "Day ${index + 1}",
                            values = listOf(
                                Bars.Data(
                                    label = "Baseline",
                                    value = steps.toDouble(),
                                    color = createTrendingBaselineGradient()
                                )
                            )
                        )
                    )
                }
                
                // Add current data (green bars for positive trend)
                mockCurrentSteps.forEachIndexed { index, steps ->
                    allBars.add(
                        Bars(
                            label = "Day ${index + 8}",
                            values = listOf(
                                Bars.Data(
                                    label = "Current",
                                    value = steps.toDouble(),
                                    color = createTrendingPositiveGradient()
                                )
                            )
                        )
                    )
                }
                
                allBars
            }
            
            TrendingDateRange.THIRTY_DAYS -> {
                // For 30-day trending: 60 days total (30 baseline + 30 current)
                val mockBaselineSteps = List(30) { (8000..12000).random().toLong() }
                val mockCurrentSteps = List(30) { (9000..13000).random().toLong() }
                
                val allBars = mutableListOf<Bars>()
                
                // Add baseline data (grey bars)
                mockBaselineSteps.forEachIndexed { index, steps ->
                    allBars.add(
                        Bars(
                            label = "Day ${index + 1}",
                            values = listOf(
                                Bars.Data(
                                    label = "Baseline",
                                    value = steps.toDouble(),
                                    color = createTrendingBaselineGradient()
                                )
                            )
                        )
                    )
                }
                
                // Add current data (green bars for positive trend)
                mockCurrentSteps.forEachIndexed { index, steps ->
                    allBars.add(
                        Bars(
                            label = "Day ${index + 31}",
                            values = listOf(
                                Bars.Data(
                                    label = "Current",
                                    value = steps.toDouble(),
                                    color = createTrendingPositiveGradient()
                                )
                            )
                        )
                    )
                }
                
                allBars
            }
        }
    }

    private fun generateMockTotalSteps(): Long {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, generate mock monthly totals
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            mockMonthlySteps.sum()
        } else {
            76100L // Sum of mock steps data for weekly view
        }
    }

    private fun generateMockAverageSteps(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, average of 6 months
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            mockMonthlySteps.average()
        } else {
            10871.43 // Average of mock steps data for weekly view
        }
    }

    private fun generateMockTotalCalories(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, sum up 6 months of mock calories
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            mockMonthlySteps.sumOf { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
            }
        } else {
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            mockSteps.sumOf { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
            }
        }
    }

    private fun generateMockAverageCalories(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, average of 6 months
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            val calories = mockMonthlySteps.map { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
            }
            calories.average()
        } else {
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val calories = mockSteps.map { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).caloriesKcal
            }
            calories.average()
        }
    }

    private fun generateMockTotalDuration(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, sum up 6 months of mock duration
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            mockMonthlySteps.sumOf { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
            }
        } else {
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            mockSteps.sumOf { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
            }
        }
    }

    private fun generateMockAverageDuration(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, average of 6 months
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            val durations = mockMonthlySteps.map { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
            }
            durations.average()
        } else {
            val mockSteps = listOf(8500L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val durations = mockSteps.map { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).timeMinutes
            }
            durations.average()
        }
    }

    private fun generateMockTotalDistance(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, sum up 6 months of mock distance
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            mockMonthlySteps.sumOf { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
            }
        } else {
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            mockSteps.sumOf { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
            }
        }
    }

    private fun generateMockAverageDistance(): Double {
        return if (_uiState.value.selectedDateRange == OverViewDateRange.MONTH) {
            // For monthly view, average of 6 months
            val mockMonthlySteps = listOf(240000L, 220000L, 260000L, 280000L, 250000L, 270000L)
            val distances = mockMonthlySteps.map { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
            }
            distances.average()
        } else {
            val mockSteps = listOf(8500L, 9200L, 7800L, 10500L, 8900L, 11200L, 9500L)
            val distances = mockSteps.map { steps ->
                activityMetricsUseCase.calculateMetrics(steps, mockUserProfile).distanceKm
            }
            distances.average()
        }
    }
}

data class StatsUiState(
    val startDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val endDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedDateRange: OverViewDateRange = OverViewDateRange.WEEK,
    val trendingDateRange: TrendingDateRange = TrendingDateRange.SEVEN_DAYS,
    val stepsData: Map<LocalDate, Long> = emptyMap(),
    val permissionState: PermissionState = PermissionState.Unknown,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Computed values for UI
    val totalSteps: Long = 0L,
    val averageSteps: Double = 0.0,
    val totalCalories: Double = 0.0,
    val averageCalories: Double = 0.0,
    val totalDuration: Double = 0.0,
    val averageDuration: Double = 0.0,
    val totalDistance: Double = 0.0,
    val averageDistance: Double = 0.0,
    val stepsChartData: List<Bars> = emptyList(),
    val caloriesChartData: List<Bars> = emptyList(),
    val durationChartData: List<Bars> = emptyList(),
    val distanceChartData: List<Bars> = emptyList(),
    val trendingChartData: List<Bars> = emptyList(),
    val trendingStatsData: TrendingStatsData? = null
)
