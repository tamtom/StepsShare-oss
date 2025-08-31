package com.itdeveapps.stepsshare.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface DailyStepsDao {
    @Query("SELECT * FROM daily_steps WHERE dateEpochDay = :epochDay")
    suspend fun getByDate(epochDay: Long): DailyStepsEntity?

    @Query("SELECT * FROM daily_steps WHERE dateEpochDay BETWEEN :start AND :end ORDER BY dateEpochDay ASC")
    suspend fun getRange(start: Long, end: Long): List<DailyStepsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyStepsEntity)

    @Transaction
    suspend fun addStepsForDate(epochDay: Long, delta: Long, nowUtcMillis: Long) {
        val current = getByDate(epochDay)
        val newTotal = (current?.steps ?: 0L) + delta
        upsert(
            DailyStepsEntity(
                dateEpochDay = epochDay,
                steps = newTotal,
                lastUpdatedAt = nowUtcMillis,
            )
        )
    }
}

@Dao
interface TrackerStateDao {
    @Query("SELECT * FROM tracker_state WHERE id = 1")
    suspend fun get(): TrackerStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TrackerStateEntity)
}


