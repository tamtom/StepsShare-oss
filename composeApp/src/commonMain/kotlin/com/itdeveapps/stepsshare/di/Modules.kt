package com.itdeveapps.stepsshare.di

import com.itdeveapps.stepsshare.data.repository.DefaultGoalsRepository
import com.itdeveapps.stepsshare.data.repository.DefaultUserProfileRepository
import com.itdeveapps.stepsshare.domain.repository.GoalsRepository
import com.itdeveapps.stepsshare.domain.repository.UserProfileRepository
import com.itdeveapps.stepsshare.domain.usecase.ActivityMetricsUseCase
import com.itdeveapps.stepsshare.domain.usecase.FormattingUseCase
import com.itdeveapps.stepsshare.domain.usecase.StreakUseCase
import com.itdeveapps.stepsshare.domain.usecase.TrendingUseCase
import com.itdeveapps.stepsshare.ui.steps.StepsViewModel
import com.itdeveapps.stepsshare.ui.goals.GoalsViewModel
import com.itdeveapps.stepsshare.ui.stats.StatsViewModel
import com.itdeveapps.stepsshare.ui.profile.UserProfileViewModel
import com.itdeveapps.stepsshare.ui.onboarding.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf
import com.russhwolf.settings.Settings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable

@OptIn(ExperimentalSettingsApi::class)
fun commonAppModule(): Module = module {
    // Repositories
    single<UserProfileRepository> { DefaultUserProfileRepository(get()) }
    // Multiplatform Settings
    single<Settings> { Settings() }
    single<ObservableSettings> {
        val s: Settings = get()
        s as? ObservableSettings ?: s.makeObservable()
    }
    single<FlowSettings> { get<ObservableSettings>().toFlowSettings() }
    single<GoalsRepository> { DefaultGoalsRepository(get(), get()) }
    
    // Use Cases
    single { ActivityMetricsUseCase() }
    single { FormattingUseCase() }
    single { StreakUseCase(get()) }
    single { TrendingUseCase() }
}

fun commonViewModelModule(): Module = module {
    viewModelOf(::StepsViewModel)
    viewModelOf(::GoalsViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::UserProfileViewModel)
    viewModelOf(::OnboardingViewModel)
}


