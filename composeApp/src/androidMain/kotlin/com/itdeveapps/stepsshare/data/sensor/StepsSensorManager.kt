package com.itdeveapps.stepsshare.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.resume

class StepsSensorManager(private val context: Context) {
    private val sensorManager: SensorManager? =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    sealed class Reading {
        data class Counter(val cumulativeStepsSinceBoot: Float) : Reading()
        data class Detector(val stepsDetected: Int) : Reading()
    }

    // Real-time step updates
    private val _liveStepUpdates = MutableStateFlow<Reading?>(null)
    val liveStepUpdates: StateFlow<Reading?> = _liveStepUpdates

    private var currentListener: SensorEventListener? = null
    private var stepEventCount = 0
    
    companion object {
        private const val TAG = "StepsDebug"
    }

    fun isAnyStepSensorAvailable(): Boolean {
        val mgr = sensorManager ?: return false
        return mgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null ||
                mgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null
    }

    suspend fun readOnce(timeoutMs: Long = 3000L): Reading? {
        val mgr = sensorManager ?: return null

        val counter = mgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (counter != null) {
            return readCounterOnce(mgr, counter, timeoutMs)
        }

        val detector = mgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (detector != null) {
            return readDetectorWindow(mgr, detector, timeoutMs)
        }

        return null
    }

    /**
     * Start continuous step monitoring for real-time updates
     */
    @Synchronized
    fun startContinuousTracking() {
        val mgr = sensorManager ?: return
        if (currentListener != null) {
            mgr.unregisterListener(currentListener)
            currentListener = null
        }

        val counter = mgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val detector = mgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (counter != null) {
            startCounterTracking(mgr, counter)
        } else if (detector != null) {
            startDetectorTracking(mgr, detector)
        } 
    }


    @Synchronized
    fun stopContinuousTracking() {
        currentListener?.let { listener ->
            sensorManager?.unregisterListener(listener)
            currentListener = null
        }
    }

    private fun startCounterTracking(mgr: SensorManager, sensor: Sensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    val steps = event.values.firstOrNull() ?: 0f
                    _liveStepUpdates.value = Reading.Counter(steps)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "📊 COUNTER sensor accuracy changed: $accuracy")
            }
        }
        currentListener = listener
        // Use SENSOR_DELAY_NORMAL for step tracking (200ms sampling rate)
        // SENSOR_DELAY_UI is too fast and causes sensor inaccuracies
        mgr.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun startDetectorTracking(mgr: SensorManager, sensor: Sensor) {
        stepEventCount = 0
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    stepEventCount++
                    // Emit delta = 1 per step event for correct aggregation upstream
                    _liveStepUpdates.value = Reading.Detector(1)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "👣 DETECTOR sensor accuracy changed: $accuracy")
            }
        }
        currentListener = listener
        // Use SENSOR_DELAY_NORMAL for step tracking
        mgr.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private suspend fun readCounterOnce(
        mgr: SensorManager,
        sensor: Sensor,
        timeoutMs: Long
    ): Reading? = withTimeoutOrNull(timeoutMs) {
        suspendCancellableCoroutine { cont ->
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                        mgr.unregisterListener(this)
                        cont.resume(Reading.Counter(event.values.firstOrNull() ?: 0f))
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            mgr.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            cont.invokeOnCancellation { mgr.unregisterListener(listener) }
        }
    }

    private suspend fun readDetectorWindow(
        mgr: SensorManager,
        sensor: Sensor,
        timeoutMs: Long
    ): Reading? {
        var steps = 0
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    steps += event.values.size
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        mgr.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        try {
            delay(timeoutMs)
        } finally {
            mgr.unregisterListener(listener)
        }
        return if (steps > 0) Reading.Detector(steps) else null
    }
}



