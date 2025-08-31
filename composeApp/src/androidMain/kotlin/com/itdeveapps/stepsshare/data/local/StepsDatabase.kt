package com.itdeveapps.stepsshare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DailyStepsEntity::class, TrackerStateEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class StepsDatabase : RoomDatabase() {
    abstract fun dailyStepsDao(): DailyStepsDao
    abstract fun trackerStateDao(): TrackerStateDao
}


