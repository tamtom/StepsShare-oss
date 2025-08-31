package com.itdeveapps.stepsshare.data.repository

import com.itdeveapps.stepsshare.domain.model.UnitSystem
import com.itdeveapps.stepsshare.domain.model.UserProfile
import com.itdeveapps.stepsshare.domain.repository.UserProfileRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class DefaultUserProfileRepository(
    private val settings: Settings
) : UserProfileRepository {
    
    private val _userProfile = MutableStateFlow(
        loadUserProfileFromSettings() ?: UserProfile(
            gender = "Male",
            age = 25,
            weightKg = 70.0,
            heightCm = 170.0,
            unitSystem = UnitSystem.Metric
        )
    )
    
    companion object {
        private const val KEY_USER_PROFILE = "user_profile"
    }
    
    private fun loadUserProfileFromSettings(): UserProfile? {
        return try {
            val profileJson = settings.getString(KEY_USER_PROFILE, "")
            if (profileJson.isNotEmpty()) {
                Json.decodeFromString(profileJson)
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    private fun saveUserProfileToSettings(profile: UserProfile) {
        try {
            val profileJson = Json.encodeToString(profile)
            settings.putString(KEY_USER_PROFILE, profileJson)
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }
    
    override suspend fun fetchUserProfile(): UserProfile {
        return _userProfile.value
    }
    
    override suspend fun saveUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        saveUserProfileToSettings(profile)
    }
    
    override fun getUserProfileStream(): Flow<UserProfile> {
        return _userProfile.asStateFlow()
    }
}
