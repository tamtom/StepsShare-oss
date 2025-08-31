package com.itdeveapps.stepsshare.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

object OnboardingPreferences {
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    private const val KEY_USER_PROFILE_CREATED = "user_profile_created"
    
    fun isOnboardingComplete(settings: Settings): Boolean {
        return settings.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }
    
    fun setOnboardingComplete(settings: Settings, complete: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_COMPLETE, complete)
    }
    
    fun isUserProfileCreated(settings: Settings): Boolean {
        return settings.getBoolean(KEY_USER_PROFILE_CREATED, false)
    }
    
    fun setUserProfileCreated(settings: Settings, created: Boolean) {
        settings.putBoolean(KEY_USER_PROFILE_CREATED, created)
    }
}
