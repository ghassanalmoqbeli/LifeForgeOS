package com.lifeforge.os.presentation.onboarding

import com.lifeforge.os.core.preferences.PreferencesManager
import com.lifeforge.os.core.preferences.UserProfile
import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.domain.repository.SettingsRepository
import com.lifeforge.os.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingState(
    val name: String = "",
    val waterGoal: Int = 2500,
    val caloriesGoal: Int = 2400,
    val language: String = "ar",
    val isSaving: Boolean = false,
    val error: String? = null,
)

class OnboardingViewModel(
    private val preferencesManager: PreferencesManager,
    private val settingsRepository: SettingsRepository,
    private val userProfileRepository: UserProfileRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    val isCompleted: StateFlow<Boolean> = preferencesManager.onboardingCompleted
        .stateIn(scopeProvider.defaultScope, SharingStarted.Eagerly, false)

    fun setName(name: String) {
        _state.value = _state.value.copy(name = name, error = null)
    }

    fun setWaterGoal(goal: Int) {
        _state.value = _state.value.copy(waterGoal = goal.coerceIn(0, 10_000))
    }

    fun setCaloriesGoal(goal: Int) {
        _state.value = _state.value.copy(caloriesGoal = goal.coerceIn(0, 20_000))
    }

    fun setLanguage(language: String) {
        _state.value = _state.value.copy(language = language)
    }

    fun complete() {
        val current = _state.value
        if (current.name.isBlank()) {
            _state.value = current.copy(error = "name_required")
            return
        }
        if (current.isSaving) return
        _state.value = current.copy(isSaving = true, error = null)

        scopeProvider.ioScope.launch {
            try {
                preferencesManager.setLanguage(current.language)
                settingsRepository.setWaterGoal(current.waterGoal)
                settingsRepository.setCaloriesGoal(current.caloriesGoal)
                userProfileRepository.saveProfile(
                    UserProfile(name = current.name.trim())
                )
                preferencesManager.setOnboardingStep(3)
                preferencesManager.setOnboardingCompleted(true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}