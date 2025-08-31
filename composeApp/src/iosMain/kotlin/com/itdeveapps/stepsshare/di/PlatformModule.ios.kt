package com.itdeveapps.stepsshare.di

import com.itdeveapps.stepsshare.data.IOSStepsRepository
import com.itdeveapps.stepsshare.data.StepsRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule: Module = module {
    single { IOSStepsRepository() } bind StepsRepository::class
}


