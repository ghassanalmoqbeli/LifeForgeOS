package com.lifeforge.os.domain.repository

import com.lifeforge.os.core.preferences.Units
import com.lifeforge.os.core.preferences.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    val profile: Flow<UserProfile?>
    suspend fun getProfile(): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
    suspend fun clearProfile()
}

interface SettingsRepository {
    val units: Flow<Units>
    val caloriesGoal: Flow<Int>
    val proteinGoal: Flow<Int>
    val waterGoal: Flow<Int>
    suspend fun getUnits(): Units
    suspend fun setUnits(units: Units)
    suspend fun getCaloriesGoal(): Int
    suspend fun setCaloriesGoal(calories: Int)
    suspend fun getProteinGoal(): Int
    suspend fun setProteinGoal(protein: Int)
    suspend fun getWaterGoal(): Int
    suspend fun setWaterGoal(water: Int)
}