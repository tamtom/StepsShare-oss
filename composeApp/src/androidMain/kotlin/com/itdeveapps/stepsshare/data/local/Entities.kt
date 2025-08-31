package com.itdeveapps.stepsshare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailyStepsEntity(
    @PrimaryKey val dateEpochDay: Long,
    val steps: Long,
    val lastUpdatedAt: Long,
)

@Entity(tableName = "tracker_state")
data class TrackerStateEntity(
    @PrimaryKey val id: Int = 1,
    val lastCumulative: Long?,
    val lastReadingAt: Long?,
    val lastSensorType: String?,
)


