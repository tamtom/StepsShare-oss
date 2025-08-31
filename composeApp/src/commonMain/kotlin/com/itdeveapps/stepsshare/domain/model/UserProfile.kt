package com.itdeveapps.stepsshare.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UnitSystem { Metric, Imperial }

@Serializable
data class UserProfile(
    val gender: String,
    val age: Int,
    val weightKg: Double,
    val heightCm: Double,
    val unitSystem: UnitSystem = UnitSystem.Metric
)
