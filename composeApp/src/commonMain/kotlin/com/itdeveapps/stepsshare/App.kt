package com.itdeveapps.stepsshare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.itdeveapps.stepsshare.ui.main.MainScreen
import com.itdeveapps.stepsshare.ui.onboarding.OnboardingScreen
import com.itdeveapps.stepsshare.ui.onboarding.OnboardingViewModel
import com.itdeveapps.stepsshare.ui.theme.StepsShareTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    StepsShareTheme {
        AppContent()
    }
}

@Composable
fun AppContent() {
    val onboardingViewModel = koinViewModel<OnboardingViewModel>()
    val showOnboarding by onboardingViewModel.showOnboarding.collectAsState()
    
    if (showOnboarding) {
        OnboardingScreen(
            onComplete = { userProfile ->
                onboardingViewModel.completeOnboarding(userProfile)
            }
        )
    } else {
        MainScreen()
    }
}