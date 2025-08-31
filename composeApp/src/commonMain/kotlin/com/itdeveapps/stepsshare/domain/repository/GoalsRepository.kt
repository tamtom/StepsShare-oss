package com.itdeveapps.stepsshare.domain.repository

import com.itdeveapps.stepsshare.domain.model.ActivityGoals
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    suspend fun fetchActivityGoals(): ActivityGoals
    suspend fun saveActivityGoals(goals: ActivityGoals)
    fun getActivityGoalsStream(): Flow<ActivityGoals>
    
    suspend fun fetchStepsGoal(): Int
    suspend fun saveStepsGoal(goal: Int)
    fun getStepsGoalStream(): Flow<Int>
}
