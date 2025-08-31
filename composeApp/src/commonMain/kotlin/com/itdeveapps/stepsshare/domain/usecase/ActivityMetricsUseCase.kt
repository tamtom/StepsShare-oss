package com.itdeveapps.stepsshare.domain.usecase

import com.itdeveapps.stepsshare.domain.model.ActivityMetrics
import com.itdeveapps.stepsshare.domain.model.UserProfile

class ActivityMetricsUseCase {
    
    fun calculateMetrics(steps: Long, userProfile: UserProfile): ActivityMetrics {
        val heightM = userProfile.heightCm / 100.0
        val stepLengthM = if (userProfile.gender.lowercase() == "male") {
            heightM * 0.415
        } else {
            heightM * 0.413
        }

        val cadenceSpm = when {
            userProfile.age < 50 -> 110.0
            userProfile.age < 65 -> 105.0
            else -> 100.0
        }

        val distanceKm = steps * stepLengthM / 1000.0
        val timeHours = (steps / cadenceSpm) / 60.0
        val calories = (userProfile.weightKg / 200.0) * (
            (3.5 * steps / cadenceSpm) +
            (0.1 * stepLengthM * steps)
        )

        return ActivityMetrics(
            caloriesKcal = calories,
            distanceKm = distanceKm,
            timeMinutes = timeHours * 60.0
        )
    }
}
