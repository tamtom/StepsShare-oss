package com.itdeveapps.stepsshare.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

/**
 * Mock implementation of StepsRepository for testing purposes.
 * Generates realistic step data based on deterministic algorithms.
 */
class MockStepsRepository : StepsRepository {
    
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Granted)
    private val _todaySteps = MutableStateFlow(0L)
    
    override val permissionState: StateFlow<PermissionState> = _permissionState
    override val todaySteps: Flow<Long> = _todaySteps
    
    init {
        // Initialize with today's mock steps
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        _todaySteps.value = generateMockStepsForDate(today)
    }
    
    override suspend fun checkPermissions() {
        // Simulate permission check delay
        delay(100)
        _permissionState.value = PermissionState.Granted
    }
    
    override suspend fun requestPermissions() {
        // Simulate permission request delay
        delay(500)
        _permissionState.value = PermissionState.Granted
    }
    
    override suspend fun readSteps(start: LocalDateTime, end: LocalDateTime): Long {
        val startDate = start.date
        val endDateExclusive = end.date
        if (endDateExclusive < startDate) return 0L
        
        val days = generateSequence(startDate) { d -> 
            if (d < endDateExclusive) d.plus(DatePeriod(days = 1)) else null 
        }.toList()
        
        return days.sumOf { date -> generateMockStepsForDate(date) }
    }
    
    override suspend fun readStepsForDate(date: LocalDate): Long {
        return generateMockStepsForDate(date)
    }
    
    override suspend fun readStepsForDateRange(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Long> {
        val result = mutableMapOf<LocalDate, Long>()
        val days = generateSequence(startDate) { d -> 
            if (d <= endDate) d.plus(DatePeriod(days = 1)) else null 
        }.toList()
        
        for (date in days) {
            result[date] = generateMockStepsForDate(date)
        }
        
        return result
    }
    
    /**
     * Generates realistic mock step data for a given date.
     * Uses deterministic algorithm based on date to ensure consistent results.
     */
    private fun generateMockStepsForDate(date: LocalDate): Long {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysDiff = date.toEpochDays() - today.toEpochDays()
        
        return when {
            // Future dates - no steps
            daysDiff > 0 -> 0L
            
            // Today - higher steps, more realistic
            daysDiff == 0 -> {
                val baseSteps = 8000L
                val variation = (date.dayOfYear % 3000).toLong() // 0-2999 variation
                val timeOfDay = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
                val timeMultiplier = when {
                    timeOfDay < 6 -> 0.1 // Early morning - very few steps
                    timeOfDay < 12 -> 0.4 // Morning - some steps
                    timeOfDay < 18 -> 0.7 // Afternoon - more steps
                    else -> 1.0 // Evening - full steps
                }
                ((baseSteps + variation) * timeMultiplier).toLong()
            }
            
            // Yesterday - full steps
            daysDiff == -1 -> {
                val baseSteps = 12500L
                val variation = (date.dayOfYear % 2500).toLong()
                baseSteps + variation
            }
            
            // Recent days (last 7 days)
            daysDiff >= -7 -> {

                Random.nextLong(8000, 14000)

            }
            
            // Older days - more random, generally lower
            else -> {
                val baseSteps = 8000L
                val variation = (date.dayOfYear % 3000).toLong()
                val randomFactor = (date.dayOfYear * 137) % 1000 // Additional randomness
                baseSteps + variation + randomFactor
            }
        }
    }
    
}
