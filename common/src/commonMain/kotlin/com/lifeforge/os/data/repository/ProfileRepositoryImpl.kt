package com.lifeforge.os.data.repository

import com.lifeforge.os.core.preferences.PreferencesManager
import com.lifeforge.os.core.preferences.Units
import com.lifeforge.os.core.preferences.UserProfile
import com.lifeforge.os.domain.repository.SettingsRepository
import com.lifeforge.os.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl(
    private val preferences: PreferencesManager,
) : UserProfileRepository {

    override val profile: Flow<UserProfile?> =
        combine(preferences.userName, preferences.userAge, preferences.userHeight, preferences.userWeight, preferences.userGoalWeight) { name, age, height, weight, goalWeight ->
            if (name.isNullOrBlank()) null
            else UserProfile(
                name = name,
                age = age,
                height = height,
                weight = weight,
                goalWeight = goalWeight,
            )
        }

    override suspend fun getProfile(): UserProfile? {
        val name = preferences.userName.firstNullable()
        return if (name.isNullOrBlank()) null
        else UserProfile(
            name = name,
            age = preferences.userAge.firstOrNull(),
            height = preferences.userHeight.firstOrNull(),
            weight = preferences.userWeight.firstOrNull(),
            goalWeight = preferences.userGoalWeight.firstOrNull(),
        )
    }

    override suspend fun saveProfile(profile: UserProfile) =
        preferences.setUserProfile(profile)

    override suspend fun clearProfile() {
        preferences.setUserProfile(UserProfile(name = ""))
    }
}

class SettingsRepositoryImpl(
    private val preferences: PreferencesManager,
) : SettingsRepository {

    override val units: Flow<Units> = preferences.units
    override val caloriesGoal: Flow<Int> = preferences.caloriesGoal
    override val proteinGoal: Flow<Int> = preferences.proteinGoal
    override val waterGoal: Flow<Int> = preferences.waterGoal

    override suspend fun getUnits(): Units = preferences.units.first()
    override suspend fun setUnits(units: Units) = preferences.setUnits(units)
    override suspend fun getCaloriesGoal(): Int = preferences.caloriesGoal.first()
    override suspend fun setCaloriesGoal(calories: Int) = preferences.setCaloriesGoal(calories)
    override suspend fun getProteinGoal(): Int = preferences.proteinGoal.first()
    override suspend fun setProteinGoal(protein: Int) = preferences.setProteinGoal(protein)
    override suspend fun getWaterGoal(): Int = preferences.waterGoal.first()
    override suspend fun setWaterGoal(water: Int) = preferences.setWaterGoal(water)
}

private suspend fun <T> Flow<T>.firstOrNull(): T? = kotlinx.coroutines.flow.firstOrNull()
private suspend fun <T> Flow<T?>.firstNullable(): T? = kotlinx.coroutines.flow.firstOrNull()

private suspend fun <T> Flow<T>.first(): T = kotlinx.coroutines.flow.first()