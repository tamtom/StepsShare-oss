package com.itdeveapps.stepsshare.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itdeveapps.stepsshare.domain.model.UnitSystem
import com.itdeveapps.stepsshare.domain.model.UserProfile
import com.itdeveapps.stepsshare.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()
    

    
    init {
        loadUserProfile()
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val profile = userProfileRepository.fetchUserProfile()
                _uiState.value = UserProfileUiState(
                    userProfile = profile,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = UserProfileUiState(
                    error = e.message ?: "Failed to load profile",
                    isLoading = false
                )
            }
        }
    }
    
    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                userProfileRepository.saveUserProfile(profile)
                _uiState.value = UserProfileUiState(
                    userProfile = profile,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update profile",
                    isLoading = false
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun calculateCaloriesBurned(steps: Int): Int {
        val profile = _uiState.value.userProfile ?: return 0
        
        // Basic calorie calculation based on steps, weight, and age
        // This is a simplified formula - in a real app you'd use more sophisticated calculations
        val baseCaloriesPerStep = 0.04
        val weightFactor = profile.weightKg / 70.0 // Normalize to 70kg
        val ageFactor = when {
            profile.age < 30 -> 1.1
            profile.age < 50 -> 1.0
            else -> 0.9
        }
        val genderFactor = if (profile.gender == "Male") 1.1 else 1.0
        
        return (steps * baseCaloriesPerStep * weightFactor * ageFactor * genderFactor).toInt()
    }
    
    fun calculateDistance(steps: Int): Double {
        val profile = _uiState.value.userProfile ?: return 0.0
        
        // Calculate distance based on average stride length
        // Average stride length is roughly height * 0.413
        val strideLength = profile.heightCm * 0.413 / 100000 // Convert to km
        return steps * strideLength
    }
    
    fun calculateActiveMinutes(steps: Int): Int {
        // Rough estimate: 1 minute of moderate walking = 100 steps
        return (steps / 100.0).toInt()
    }
}

data class UserProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
