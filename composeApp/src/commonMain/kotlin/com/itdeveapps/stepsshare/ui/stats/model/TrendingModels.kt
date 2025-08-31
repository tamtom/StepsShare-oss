package com.itdeveapps.stepsshare.ui.stats.model

import kotlinx.datetime.LocalDate

enum class TrendIndicator {
    POSITIVE, NEGATIVE, NEUTRAL
}

data class TrendingPeriod(
    val baselineStartDate: LocalDate,
    val baselineEndDate: LocalDate,
    val currentStartDate: LocalDate,
    val currentEndDate: LocalDate
)

data class TrendingChartData(
    val baselineData: List<TrendingDataPoint>,
    val currentData: List<TrendingDataPoint>,
    val trendPercentage: Double,
    val trendDirection: TrendIndicator,
    val baselineAverage: Double,
    val currentAverage: Double
)

data class TrendingDataPoint(
    val date: LocalDate,
    val value: Double,
    val label: String
)

data class TrendingStatsData(
    val totalSteps: Long,
    val averageSteps: Double,
    val totalCalories: Double,
    val averageCalories: Double,
    val totalDuration: Double,
    val averageDuration: Double,
    val totalDistance: Double,
    val averageDistance: Double,
    val trendPercentage: Double,
    val trendDirection: TrendIndicator,
    val baselinePeriod: String,
    val currentPeriod: String
)
