package com.itdeveapps.stepsshare.di

import com.itdeveapps.stepsshare.data.Config
import com.itdeveapps.stepsshare.data.IOSStepsRepository
import com.itdeveapps.stepsshare.data.MockStepsRepository
import com.itdeveapps.stepsshare.data.StepsRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule: Module = module {
    if (Config.IS_MOCK_DATA) {
        single<StepsRepository> { MockStepsRepository() }
    } else {
        single { IOSStepsRepository() } bind StepsRepository::class
    }
}


