package com.itdeveapps.stepsshare.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Cross-platform contract for accessing step counts.
 */
interface StepsRepository {
    /** Emits current permission state for the platform's step source. */
    val permissionState: StateFlow<PermissionState>

    /** Emits today's steps reactively while the app is active (may be 0 if not supported). */
    val todaySteps: Flow<Long>

    /** Check current permission state without requesting. Updates [permissionState]. */
    suspend fun checkPermissions()

    /** Request permissions if not granted. Emits updates on [permissionState]. */
    suspend fun requestPermissions()

    /**
     * Returns total steps in the inclusive range [start, end).
     * Implementations should aggregate appropriately to avoid double counting.
     */
    suspend fun readSteps(start: LocalDateTime, end: LocalDateTime): Long

    /** Convenience for reading steps for a single [date] from midnight to midnight in local time. */
    suspend fun readStepsForDate(date: LocalDate): Long
    
    /** 
     * Efficiently reads steps for a range of dates (inclusive).
     * Returns a map where keys are dates and values are step counts.
     * Missing dates in the result map should be treated as 0 steps.
     */
    suspend fun readStepsForDateRange(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Long>
}

sealed interface PermissionState {
    data object Unknown: PermissionState
    data object Granted: PermissionState
    data class Denied(val canRequestAgain: Boolean): PermissionState
    data class NotAvailable(val reason: String? = null): PermissionState
}


