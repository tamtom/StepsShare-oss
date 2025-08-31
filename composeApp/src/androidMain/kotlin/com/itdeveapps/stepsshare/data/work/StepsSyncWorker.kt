package com.itdeveapps.stepsshare.data.work

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.itdeveapps.stepsshare.data.local.DailyStepsDao
import com.itdeveapps.stepsshare.data.local.TrackerStateDao
import com.itdeveapps.stepsshare.data.local.TrackerStateEntity
import com.itdeveapps.stepsshare.data.sensor.StepsSensorManager
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit

class StepsSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // Permission check for API 29+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return Result.success()
        }

        val sensorManager = StepsSensorManager(applicationContext)
        if (!sensorManager.isAnyStepSensorAvailable()) return Result.success()

        val reading = sensorManager.readOnce(timeoutMs = 3000L) ?: return Result.success()

        val now = Clock.System.now()
        val todayEpochDay = now.toEpochDayLocal()

        // Open DB on-demand (no custom WorkerFactory)
        val db = androidx.room.Room.databaseBuilder(
            applicationContext,
            com.itdeveapps.stepsshare.data.local.StepsDatabase::class.java,
            "steps.db"
        ).build()
        val dailyStepsDao = db.dailyStepsDao()
        val trackerStateDao = db.trackerStateDao()
        val state = trackerStateDao.get()
        when (reading) {
            is StepsSensorManager.Reading.Counter -> {
                val current = reading.cumulativeStepsSinceBoot.toLong()
                val last = state?.lastCumulative
                if (last == null) {
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
                    }
                    trackerStateDao.upsert(
                        TrackerStateEntity(
                            lastCumulative = current,
                            lastReadingAt = now.toEpochMillis(),
                            lastSensorType = "COUNTER",
                        )
                    )
                } else {
                    // reset/reboot: re-baseline
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
                val steps = reading.stepsDetected.toLong()
                if (steps > 0) {
                    dailyStepsDao.addStepsForDate(
                        todayEpochDay,
                        steps,
                        now.toEpochMillis()
                    )
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

        return Result.success()
    }

    companion object Scheduler {
        private const val UNIQUE_PERIODIC = "steps-sync-periodic"
        private const val UNIQUE_ONE_TIME = "steps-sync-once"

        fun schedulePeriodic(context: Context) {
            val req = PeriodicWorkRequestBuilder<StepsSyncWorker>(60, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }

        fun scheduleOneTime(context: Context) {
            val req = OneTimeWorkRequestBuilder<StepsSyncWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                req
            )
        }
    }
}

private fun Instant.toEpochDayLocal(): Long {
    val local = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return LocalDate(local.year, local.month, local.dayOfMonth).toEpochDay()
}

private fun LocalDate.toEpochDay(): Long {
    // java.time LocalDate available on Android
    val jd = java.time.LocalDate.of(year, monthNumber, dayOfMonth)
    return jd.toEpochDay()
}

private fun Instant.toEpochMillis(): Long = this.toEpochMilliseconds()


