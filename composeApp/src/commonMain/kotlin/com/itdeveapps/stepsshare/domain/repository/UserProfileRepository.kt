package com.itdeveapps.stepsshare.domain.repository

import com.itdeveapps.stepsshare.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun fetchUserProfile(): UserProfile
    suspend fun saveUserProfile(profile: UserProfile)
    fun getUserProfileStream(): Flow<UserProfile>
}
