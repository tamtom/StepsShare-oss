package com.itdeveapps.stepsshare.domain.usecase

import com.itdeveapps.stepsshare.ui.stats.model.TrendIndicator
import com.itdeveapps.stepsshare.ui.stats.model.TrendingPeriod
import com.itdeveapps.stepsshare.ui.stats.model.TrendingChartData
import com.itdeveapps.stepsshare.ui.stats.model.TrendingDataPoint
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class TrendingUseCase {
    
    fun calculateTrendPercentage(currentAverage: Double, baselineAverage: Double): Double {
        if (baselineAverage == 0.0) return 0.0
        return ((currentAverage - baselineAverage) / baselineAverage) * 100
    }
    
    fun determineTrendDirection(trendPercentage: Double): TrendIndicator {
        return when {
            trendPercentage >= 5.0 -> TrendIndicator.POSITIVE
            trendPercentage <= -5.0 -> TrendIndicator.NEGATIVE
            else -> TrendIndicator.NEUTRAL
        }
    }
    
    fun generateTrendingPeriods(trendingRange: TrendingRange): TrendingPeriod {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        return when (trendingRange) {
            TrendingRange.SEVEN_DAYS -> {
                val currentEnd = today
                val currentStart = today.plus(DatePeriod(days = -6))
                val baselineEnd = currentStart.plus(DatePeriod(days = -1))
                val baselineStart = baselineEnd.plus(DatePeriod(days = -6))
                
                TrendingPeriod(
                    baselineStartDate = baselineStart,
                    baselineEndDate = baselineEnd,
                    currentStartDate = currentStart,
                    currentEndDate = currentEnd
                )
            }
            
            TrendingRange.THIRTY_DAYS -> {
                val currentEnd = today
                val currentStart = today.plus(DatePeriod(days = -29))
                val baselineEnd = currentStart.plus(DatePeriod(days = -1))
                val baselineStart = baselineEnd.plus(DatePeriod(days = -29))
                
                TrendingPeriod(
                    baselineStartDate = baselineStart,
                    baselineEndDate = baselineEnd,
                    currentStartDate = currentStart,
                    currentEndDate = currentEnd
                )
            }
        }
    }
    
    fun aggregateDataByPeriod(
        stepsData: Map<LocalDate, Long>,
        trendingPeriod: TrendingPeriod
    ): Pair<List<TrendingDataPoint>, List<TrendingDataPoint>> {
        
        val baselineData = generateDateRange(trendingPeriod.baselineStartDate, trendingPeriod.baselineEndDate)
            .map { date ->
                val steps = stepsData[date] ?: 0L
                TrendingDataPoint(
                    date = date,
                    value = steps.toDouble(),
                    label = formatDateForLabel(date, trendingPeriod)
                )
            }
        
        val currentData = generateDateRange(trendingPeriod.currentStartDate, trendingPeriod.currentEndDate)
            .map { date ->
                val steps = stepsData[date] ?: 0L
                TrendingDataPoint(
                    date = date,
                    value = steps.toDouble(),
                    label = formatDateForLabel(date, trendingPeriod)
                )
            }
        
        return Pair(baselineData, currentData)
    }
    
    fun generateTrendingChartData(
        stepsData: Map<LocalDate, Long>,
        trendingRange: TrendingRange
    ): TrendingChartData {
        val trendingPeriod = generateTrendingPeriods(trendingRange)
        val (baselineData, currentData) = aggregateDataByPeriod(stepsData, trendingPeriod)
        
        val baselineAverage = baselineData.map { it.value }.average()
        val currentAverage = currentData.map { it.value }.average()
        
        val trendPercentage = calculateTrendPercentage(currentAverage, baselineAverage)
        val trendDirection = determineTrendDirection(trendPercentage)
        
        return TrendingChartData(
            baselineData = baselineData,
            currentData = currentData,
            trendPercentage = trendPercentage,
            trendDirection = trendDirection,
            baselineAverage = baselineAverage,
            currentAverage = currentAverage
        )
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
    
    private fun formatDateForLabel(date: LocalDate, trendingPeriod: TrendingPeriod): String {
        return when {
            trendingPeriod.baselineStartDate.year == trendingPeriod.currentStartDate.year -> {
                "${date.monthNumber}/${date.dayOfMonth}"
            }
            else -> {
                "${date.monthNumber}/${date.year}"
            }
        }
    }
}

enum class TrendingRange {
    SEVEN_DAYS, THIRTY_DAYS
}
