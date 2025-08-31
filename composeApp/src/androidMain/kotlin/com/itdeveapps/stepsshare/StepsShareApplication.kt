package com.itdeveapps.stepsshare

import android.app.Application
import com.itdeveapps.stepsshare.di.Flags
import com.itdeveapps.stepsshare.di.initApp
import com.itdeveapps.stepsshare.di.platformModule
import com.itdeveapps.stepsshare.utils.AndroidLogger

class StepsShareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initApp(
            platformLogger = AndroidLogger(),
            platformModule = platformModule(this),
            flags = Flags(debugLogging = false)
        )
    }
}


