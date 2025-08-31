package com.itdeveapps.stepsshare.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itdeveapps.stepsshare.data.OnboardingPreferences
import com.itdeveapps.stepsshare.domain.model.UserProfile
import com.itdeveapps.stepsshare.domain.repository.UserProfileRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val showOnboarding: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class OnboardingViewModel(
    private val settings: Settings,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    val showOnboarding: StateFlow<Boolean> = _uiState.map { it.showOnboarding }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    init {
        checkOnboardingStatus()
    }
    
    fun checkOnboardingStatus() {
        viewModelScope.launch {
            try {
                val isProfileCreated = OnboardingPreferences.isUserProfileCreated(settings)
                val isOnboardingComplete = OnboardingPreferences.isOnboardingComplete(settings)
                
                _uiState.value = _uiState.value.copy(
                    showOnboarding = !isProfileCreated || !isOnboardingComplete
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showOnboarding = true, // Default to showing onboarding on error
                    errorMessage = "Failed to check onboarding status: ${e.message}"
                )
            }
        }
    }
    
    fun completeOnboarding(userProfile: UserProfile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Save user profile
                userProfileRepository.saveUserProfile(userProfile)
                
                // Mark profile as created and onboarding as complete
                OnboardingPreferences.setUserProfileCreated(settings, true)
                OnboardingPreferences.setOnboardingComplete(settings, true)
                
                // Hide onboarding
                _uiState.value = _uiState.value.copy(
                    showOnboarding = false,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to complete onboarding: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

}