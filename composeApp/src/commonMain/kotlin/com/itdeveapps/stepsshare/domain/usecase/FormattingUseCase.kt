package com.itdeveapps.stepsshare.domain.usecase

import kotlin.math.roundToInt

class FormattingUseCase {
    
    fun formatDistance(distanceKm: Double): Pair<String, String> {
        return if (distanceKm < 1.0) {
            val meters = (distanceKm * 1000).toInt()
            Pair(meters.toString(), "m")
        } else {
            val rounded = (distanceKm * 10.0).roundToInt() / 10.0
            val text = if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
            Pair(text, "km")
        }
    }

    fun formatTime(timeMinutes: Double): Pair<String, String> {
        return if (timeMinutes < 60.0) {
            val minutes = timeMinutes.toInt()
            Pair(minutes.toString(), "min")
        } else {
            val hours = (timeMinutes / 60).toInt()
            val minutes = (timeMinutes % 60).toInt()
            val timeString = hours.toString().padStart(2, '0') + ":" + minutes.toString().padStart(2, '0')
            Pair(timeString, "h")
        }
    }
}
