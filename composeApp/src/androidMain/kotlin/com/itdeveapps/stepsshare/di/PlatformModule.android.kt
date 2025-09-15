package com.itdeveapps.stepsshare.di

import android.app.Application
import androidx.room.Room
import com.itdeveapps.stepsshare.data.AndroidStepsRepository
import com.itdeveapps.stepsshare.data.Config
import com.itdeveapps.stepsshare.data.MockStepsRepository
import com.itdeveapps.stepsshare.data.StepsRepository
import com.itdeveapps.stepsshare.data.local.StepsDatabase
import com.itdeveapps.stepsshare.data.sensor.StepsSensorManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

fun platformModule(application: Application): Module = module {
    factory {
        Room.databaseBuilder(application, StepsDatabase::class.java, "steps.db").build()
    }
    factory { get<StepsDatabase>().dailyStepsDao() }
    factory { get<StepsDatabase>().trackerStateDao() }
    // Use mock or real StepsRepository based on flag
    if (Config.IS_MOCK_DATA) {
        factory<StepsRepository> { MockStepsRepository() }
    } else {
        factory { AndroidStepsRepository(application, get(), get(), get()) } bind StepsRepository::class
    }
    factory { StepsSensorManager(application) }
}


