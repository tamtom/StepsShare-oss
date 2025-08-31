package com.itdeveapps.stepsshare.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

interface Logger {
    fun log(tag: String, lazyMessage: () -> String)
}

data class Flags(
    val debugLogging: Boolean = false
)

fun initApp(
    platformLogger: Logger,
    platformModule: Module,
    flags: Flags = Flags(),
): Koin {
    return initKoin(platformLogger, platformModule, flags)
}

private fun initKoin(
    platformLogger: Logger,
    platformModule: Module,
    flags: Flags,
): Koin {
    return startKoin {
        modules(
            platformModule,
            commonAppModule(),
            commonViewModelModule(),
            // Register the platform logger so it can be injected
            module {
                single<Logger> { platformLogger }
            }
        )
    }.koin
}


