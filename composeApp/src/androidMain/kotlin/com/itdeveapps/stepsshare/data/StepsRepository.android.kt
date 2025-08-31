package com.itdeveapps.stepsshare.data

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.itdeveapps.stepsshare.data.local.DailyStepsDao
import com.itdeveapps.stepsshare.data.local.TrackerStateDao
import com.itdeveapps.stepsshare.data.local.TrackerStateEntity
import com.itdeveapps.stepsshare.data.sensor.StepsSensorManager
import com.itdeveapps.stepsshare.data.work.StepsSyncWorker
import com.itdeveapps.stepsshare.permissions.ActivityRecognitionPermissionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class AndroidStepsRepository(
    private val context: Context,
    private val dailyStepsDao: DailyStepsDao,
    private val trackerStateDao: TrackerStateDao,
    private val sensorManager: StepsSensorManager
) : StepsRepository {

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Unknown)
    override val permissionState: StateFlow<PermissionState> = _permissionState
    private val _todaySteps = MutableStateFlow(0L)
    override val todaySteps: Flow<Long> = _todaySteps

    // Real-time step tracking
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isTrackingActive = false

    // Track denial count to decide when to redirect to settings
    private val prefs: SharedPreferences =
        context.getSharedPreferences("activity_recognition_prefs", Context.MODE_PRIVATE)
    private val keyDenialCount = "denial_count"
    private var denialCount: Int
        get() = prefs.getInt(keyDenialCount, 0)
        set(value) { prefs.edit().putInt(keyDenialCount, value).apply() }

    private fun canRequestPermissionAgain(): Boolean = denialCount < 2

    private fun openAppSettings() {
        val intent = android.content.Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun hasActivityRecognitionPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    override suspend fun checkPermissions() {
        val available = withContext(Dispatchers.Default) {
            hasActivityRecognitionPermission() && sensorManager.isAnyStepSensorAvailable()
        }
        _permissionState.value = if (available) PermissionState.Granted
        else if (!sensorManager.isAnyStepSensorAvailable()) PermissionState.NotAvailable("No step sensor available")
        else PermissionState.Denied(canRequestAgain = canRequestPermissionAgain())
        
        if (available) {
            // Ensure periodic work scheduled and trigger an immediate sync
            StepsSyncWorker.schedulePeriodic(context)
            StepsSyncWorker.scheduleOneTime(context)
            startRealtimeTracking()
        }
    }

    override suspend fun requestPermissions() {
        if (!sensorManager.isAnyStepSensorAvailable()) {
            _permissionState.value = PermissionState.NotAvailable("No step sensor available")
            return
        }

        if (hasActivityRecognitionPermission()) {
            _permissionState.value = PermissionState.Granted
            StepsSyncWorker.schedulePeriodic(context)
            StepsSyncWorker.scheduleOneTime(context)
            startRealtimeTracking()
            return
        }

        if (!canRequestPermissionAgain()) {
            openAppSettings()
            _permissionState.value = PermissionState.Denied(canRequestAgain = false)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val result = ActivityRecognitionPermissionActivity.request(context)
            result.onSuccess { granted ->
                if (granted) {
                    denialCount = 0
                    _permissionState.value = PermissionState.Granted
                    StepsSyncWorker.schedulePeriodic(context)
                    StepsSyncWorker.scheduleOneTime(context)
                    startRealtimeTracking()
                } else {
                    denialCount = (denialCount + 1).coerceAtMost(2)
                    val canAgain = canRequestPermissionAgain()
                    if (!canAgain) openAppSettings()
                    _permissionState.value = PermissionState.Denied(canRequestAgain = canAgain)
                }
            }.onFailure {
                denialCount = (denialCount + 1).coerceAtMost(2)
                val canAgain = canRequestPermissionAgain()
                if (!canAgain) openAppSettings()
                _permissionState.value = PermissionState.Denied(canRequestAgain = canAgain)
            }
        } else {
            _permissionState.value = PermissionState.Granted
            StepsSyncWorker.schedulePeriodic(context)
            StepsSyncWorker.scheduleOneTime(context)
            startRealtimeTracking()
        }
    }

    /**
     * Start real-time step tracking while app is active
     */
    fun startRealtimeTracking() {
        if (isTrackingActive || !hasActivityRecognitionPermission()) return
        isTrackingActive = true

        sensorManager.startContinuousTracking()
        // Seed today's value from DB
        scope.launch(Dispatchers.IO) {
            val now = Clock.System.now()
            val todayEpochDay = now.toEpochDayLocal()
            val current = dailyStepsDao.getByDate(todayEpochDay)?.steps ?: 0L
            _todaySteps.value = current
        }
        scope.launch {
            sensorManager.liveStepUpdates.collect { reading ->
                reading?.let { processLiveReading(it) }
            }
        }
    }

    /**
     * Stop real-time step tracking when app goes background
     */
    fun stopRealtimeTracking() {
        if (!isTrackingActive) return
        isTrackingActive = false
        sensorManager.stopContinuousTracking()
    }

    private suspend fun processLiveReading(reading: StepsSensorManager.Reading) {
        withContext(Dispatchers.IO) {
            val now = Clock.System.now()
            val todayEpochDay = now.toEpochDayLocal()
            val state = trackerStateDao.get()

            when (reading) {
                is StepsSensorManager.Reading.Counter -> {
                    val current = reading.cumulativeStepsSinceBoot.toLong()
                    val last = state?.lastCumulative
                    
                    if (last == null) {
                        // First reading - set baseline
                        trackerStateDao.upsert(
                            TrackerStateEntity(
                                lastCumulative = current,
                                lastReadingAt = now.toEpochMillis(),
                                lastSensorType = "COUNTER",
                            )
                        )
                    } else if (current >= last) {
                        val delta = current - last
                        if (delta > 0) {
                            dailyStepsDao.addStepsForDate(
                                todayEpochDay,
                                delta,
                                now.toEpochMillis()
                            )
                            val total = dailyStepsDao.getByDate(todayEpochDay)?.steps ?: 0L
                            _todaySteps.value = total
                        }
                        trackerStateDao.upsert(
                            TrackerStateEntity(
                                lastCumulative = current,
                                lastReadingAt = now.toEpochMillis(),
                                lastSensorType = "COUNTER",
                            )
                        )
                    } else {
                        // Device reboot/reset detected
                        trackerStateDao.upsert(
                            TrackerStateEntity(
                                lastCumulative = current,
                                lastReadingAt = now.toEpochMillis(),
                                lastSensorType = "COUNTER",
                            )
                        )
                    }
                }
                is StepsSensorManager.Reading.Detector -> {
                    // For detector, we get incremental steps
                    val steps = reading.stepsDetected.toLong()
                    if (steps > 0) {
                        dailyStepsDao.addStepsForDate(
                            todayEpochDay,
                            steps,
                            now.toEpochMillis()
                        )
                        val total = dailyStepsDao.getByDate(todayEpochDay)?.steps ?: 0L
                        _todaySteps.value = total
                    }
                    trackerStateDao.upsert(
                        TrackerStateEntity(
                            lastCumulative = state?.lastCumulative,
                            lastReadingAt = now.toEpochMillis(),
                            lastSensorType = "DETECTOR",
                        )
                    )
                }
            }
        }
    }

    override suspend fun readSteps(start: LocalDateTime, end: LocalDateTime): Long {
        val startDate = start.date
        val endDateExclusive = end.date
        if (endDateExclusive < startDate) return 0
        val days = generateSequence(startDate) { d -> if (d < endDateExclusive) d.plus(DatePeriod(days = 1)) else null }
            .toList()
        var total = 0L
        for (d in days) {
            total += readStepsForDate(d)
        }
        return total
    }

    override suspend fun readStepsForDate(date: LocalDate): Long {
        val epochDay = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth).toEpochDay()
        val entity = withContext(Dispatchers.Default) { dailyStepsDao.getByDate(epochDay) }
        return entity?.steps ?: 0L
    }

    override suspend fun readStepsForDateRange(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Long> {
        if (endDate < startDate) return emptyMap()
        val startEpoch = java.time.LocalDate.of(startDate.year, startDate.monthNumber, startDate.dayOfMonth).toEpochDay()
        val endEpoch = java.time.LocalDate.of(endDate.year, endDate.monthNumber, endDate.dayOfMonth).toEpochDay()
        val rows = withContext(Dispatchers.Default) { dailyStepsDao.getRange(startEpoch, endEpoch) }
        val result = mutableMapOf<LocalDate, Long>()
        var d = startDate
        while (d <= endDate) {
            result[d] = 0L
            d = d.plus(DatePeriod(days = 1))
        }
        rows.forEach { row ->
            val jd = java.time.LocalDate.ofEpochDay(row.dateEpochDay)
            val kdate = LocalDate(jd.year, jd.monthValue, jd.dayOfMonth)
            result[kdate] = row.steps
        }
        return result
    }
}

private fun Instant.toEpochDayLocal(): Long {
    val local = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return LocalDate(local.year, local.month, local.dayOfMonth).toEpochDay()
}

private fun LocalDate.toEpochDay(): Long {
    val jd = java.time.LocalDate.of(year, monthNumber, dayOfMonth)
    return jd.toEpochDay()
}

private fun Instant.toEpochMillis(): Long = this.toEpochMilliseconds()



