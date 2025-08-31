package com.itdeveapps.stepsshare.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.*
import platform.CoreMotion.CMPedometer
import platform.CoreMotion.CMPedometerData
import platform.Foundation.NSDate
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.update

class IOSStepsRepository : StepsRepository {
    private val pedometer = CMPedometer()
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Unknown)
    override val permissionState: StateFlow<PermissionState> = _permissionState
    private val _todaySteps = MutableStateFlow(0L)
    override val todaySteps: Flow<Long> = _todaySteps

    // Real-time tracking state
    private val _liveStepsToday = MutableStateFlow(0L)
    private var isTrackingActive = false

    // Coroutine scope for permission monitoring
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override suspend fun checkPermissions() {
        if (!CMPedometer.isStepCountingAvailable()) {
            _permissionState.update { PermissionState.NotAvailable("Step counting not available") }
            return
        }
        // iOS enum values: 0 = NotDetermined, 1 = Restricted, 2 = Denied, 3 = Authorized
        val statusValue = CMPedometer.authorizationStatus().toInt()
        _permissionState.update { 
            when (statusValue) {
                3 -> {
                    startRealtimeTracking()
                    PermissionState.Granted
                }
                2, 1 -> PermissionState.Denied(canRequestAgain = false)
                0 -> PermissionState.Denied(canRequestAgain = true)
                else -> PermissionState.Denied(canRequestAgain = true)
            }
        }
    }

    override suspend fun requestPermissions() {
        checkPermissions()

        // If permission is denied and cannot be requested again, open settings
        val currentState = _permissionState.value
        if (currentState is PermissionState.Denied && !currentState.canRequestAgain) {
            openAppSettings()
        } else if (currentState is PermissionState.Denied && currentState.canRequestAgain) {
            // For iOS, requesting permission happens automatically when we first call CMPedometer
            // Try to start tracking which will trigger permission request
            startRealtimeTracking()

            // Monitor permission status change after requesting
            monitorPermissionStatusAfterRequest()
        }
    }

    /**
     * Monitor permission status after a request to ensure UI updates when permission is granted
     */
    private fun monitorPermissionStatusAfterRequest() {
        repositoryScope.launch {
            // Check permission status multiple times over the next few seconds
            // This handles cases where the iOS permission callback might be delayed
            repeat(20) { // Reduced from 100 to 20 (10 seconds total)
                delay(500) // Check every 500ms
                val newStatus = CMPedometer.authorizationStatus().toInt()

                if (newStatus == 3) { // Authorized
                    _permissionState.update { currentState ->
                        if (currentState != PermissionState.Granted) {
                            if (!isTrackingActive) {
                                startRealtimeTracking()
                            }
                            PermissionState.Granted
                        } else {
                            currentState
                        }
                    }
                    return@launch // Stop monitoring once granted
                } else if (newStatus == 2 || newStatus == 1) { // Denied or Restricted
                    _permissionState.update { currentState ->
                        currentState as? PermissionState.Denied ?: PermissionState.Denied(canRequestAgain = false)
                    }
                    return@launch // Stop monitoring once definitively denied
                }
            }
            
            // After monitoring period, do a final check and try to read steps
            // This catches cases where permission was granted but the callback didn't fire
            try {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val start = today.atTime(LocalTime(0, 0))
                val end = today.plus(DatePeriod(days = 1)).atTime(LocalTime(0, 0))
                
                val steps = suspendCancellablePedometerQuery(start, end)
                if (steps > 0) {
                    // We can read steps, so permission must be granted
                    _permissionState.update { PermissionState.Granted }
                    if (!isTrackingActive) {
                        startRealtimeTracking()
                    }
                }
            } catch (e: Exception) {
                // If this fails, permission is still not granted
                val finalStatus = CMPedometer.authorizationStatus().toInt()
                if (finalStatus != 3) {
                    _permissionState.update { 
                        when (finalStatus) {
                            2, 1 -> PermissionState.Denied(canRequestAgain = false)
                            0 -> PermissionState.Denied(canRequestAgain = true)
                            else -> PermissionState.Denied(canRequestAgain = true)
                        }
                    }
                }
            }
        }
    }

    /**
     * Start real-time step tracking for today
     */
    fun startRealtimeTracking() {
        if (isTrackingActive || !CMPedometer.isStepCountingAvailable()) return

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfToday = today.atTime(LocalTime(0, 0))
        val startDate = startOfToday.toInstant(TimeZone.currentSystemDefault()).toNSDate()

        isTrackingActive = true

        pedometer.startPedometerUpdatesFromDate(startDate) { data: CMPedometerData?, error ->
                if (error == null && data != null) {
                    val steps = data.numberOfSteps.longLongValue
                    _liveStepsToday.update { steps }
                    _todaySteps.update { steps }

                    // Update permission state to granted if this succeeds
                    _permissionState.update { currentState ->
                        if (currentState != PermissionState.Granted) {
                            PermissionState.Granted
                        } else {
                            currentState
                        }
                    }
                } else if (error != null) {
                    // Permission was denied or error occurred
                    stopRealtimeTracking()

                    // Check current authorization status to determine the exact state
                    val authStatus = CMPedometer.authorizationStatus().toInt()
                    _permissionState.update { 
                        when (authStatus) {
                            2, 1 -> PermissionState.Denied(canRequestAgain = false) // Denied or Restricted
                            0 -> PermissionState.Denied(canRequestAgain = true) // Not determined
                            else -> PermissionState.Denied(canRequestAgain = false)
                        }
                    }
                }
            }
    }

    /**
     * Stop real-time step tracking
     */
    fun stopRealtimeTracking() {
        if (!isTrackingActive) return
        isTrackingActive = false
        pedometer.stopPedometerUpdates()
    }

    override suspend fun readSteps(start: LocalDateTime, end: LocalDateTime): Long {
        // If reading for today and real-time tracking is active, use live data
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = start.date
        val endDate = end.date

        if (startDate == today && endDate == today && isTrackingActive) {
            return _liveStepsToday.value
        }

        // Otherwise, query historical data
        try {
            val steps = suspendCancellablePedometerQuery(start, end)
            
            // If we successfully got steps data, update permission state to granted
            if (steps > 0 && _permissionState.value != PermissionState.Granted) {
                println("StepsDebug: Successfully read $steps steps, updating permission to Granted")
                _permissionState.update { PermissionState.Granted }
                // Start real-time tracking if not already active
                if (!isTrackingActive) {
                    startRealtimeTracking()
                }
            }
            
            return steps
        } catch (e: Exception) {
            // If query fails, check if it's due to permission issues
            val authStatus = CMPedometer.authorizationStatus().toInt()
            if (authStatus != 3) { // Not authorized
                _permissionState.update { 
                    when (authStatus) {
                        2, 1 -> PermissionState.Denied(canRequestAgain = false)
                        0 -> PermissionState.Denied(canRequestAgain = true)
                        else -> PermissionState.Denied(canRequestAgain = true)
                    }
                }
            }
            return 0L
        }
    }

    override suspend fun readStepsForDate(date: LocalDate): Long {
        // Always try to get today's data - either from live tracking or historical query
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        if (date == today) {
            // For today, prefer live data if tracking is active, otherwise query historical
            if (isTrackingActive && _liveStepsToday.value > 0) {
                println("StepsDebug: readStepsForDate($date) using live data: ${_liveStepsToday.value}")
                return _liveStepsToday.value
            }
            println("StepsDebug: readStepsForDate($date) - today but tracking inactive or no live data (tracking: $isTrackingActive, live: ${_liveStepsToday.value})")
            // If tracking isn't active or has no data, fall through to historical query
        }

        // Query historical data using the proper readSteps method
        val start = date.atTime(LocalTime(0, 0))
        val end = date.plus(DatePeriod(days = 1)).atTime(LocalTime(0, 0))
        val result = readSteps(start, end)
        println("StepsDebug: readStepsForDate($date) historical query result: $result")
        return result
    }

    override suspend fun readStepsForDateRange(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Long> {
        println("StepsDebug: readStepsForDateRange($startDate to $endDate) - stepCountingAvailable: ${CMPedometer.isStepCountingAvailable()}")
        if (!CMPedometer.isStepCountingAvailable()) {
            println("StepsDebug: Step counting not available, returning empty map")
            return emptyMap()
        }

        return withContext(Dispatchers.Default) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val dateRange = generateSequence(startDate) { date ->
                if (date < endDate) date.plus(DatePeriod(days = 1)) else null
            }.toList()

            println("StepsDebug: readStepsForDateRange processing ${dateRange.size} dates from $startDate to $endDate")

            // Make parallel requests for better performance
            val deferredResults = dateRange.map { date ->
                async {
                    try {
                        // Always use readStepsForDate - it handles today's logic internally
                        val steps = readStepsForDate(date)
                        println("StepsDebug: readStepsForDateRange - $date: $steps steps")
                        date to steps
                    } catch (e: Exception) {
                        println("StepsDebug: readStepsForDateRange - $date: ERROR ${e.message}")
                        date to 0L
                    }
                }
            }

            val result = deferredResults.awaitAll().toMap()
            println("StepsDebug: readStepsForDateRange completed - ${result.size} entries, non-zero: ${result.values.count { it > 0 }}")
            result
        }
    }

    private fun openAppSettings() {
        val settingsUrl = NSURL.URLWithString("app-settings:")
        if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
            UIApplication.sharedApplication.openURL(
                url = settingsUrl,
                options = emptyMap<Any?, Any?>(),
                completionHandler = { success: Boolean ->
                    // Modern API with completion handler
                }
            )
        }
    }

    private suspend fun suspendCancellablePedometerQuery(
        start: LocalDateTime,
        end: LocalDateTime
    ): Long = suspendCancellableCoroutine { cont ->
        val startDate = start.toInstant(TimeZone.currentSystemDefault()).toNSDate()
        val endDate = end.toInstant(TimeZone.currentSystemDefault()).toNSDate()
        pedometer.queryPedometerDataFromDate(startDate, toDate = endDate) { data: CMPedometerData?, error ->
            if (cont.isCancelled) return@queryPedometerDataFromDate
            val value = data?.numberOfSteps?.longLongValue ?: 0L
            cont.resume(value) { }
        }
    }
}

private fun Instant.toNSDate(): NSDate = NSDate.dateWithTimeIntervalSince1970(this.epochSeconds.toDouble())
