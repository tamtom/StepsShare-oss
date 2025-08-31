package com.itdeveapps.stepsshare

import com.itdeveapps.stepsshare.di.Flags
import com.itdeveapps.stepsshare.di.initApp
import com.itdeveapps.stepsshare.di.platformModule
import com.itdeveapps.stepsshare.utils.IOSLogger

@Suppress("unused")
fun doInitApp() = initApp(
    platformLogger = IOSLogger(),
    platformModule = platformModule,
    flags = Flags(debugLogging = false)
)


