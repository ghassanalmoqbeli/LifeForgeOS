package com.lifeforge.os.presentation.nutrition

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.core.utils.startOfTodayMillis
import com.lifeforge.os.domain.model.Food
import com.lifeforge.os.domain.model.Meal
import com.lifeforge.os.domain.model.MealItem
import com.lifeforge.os.domain.model.MealType
import com.lifeforge.os.domain.repository.FoodRepository
import com.lifeforge.os.domain.repository.NutritionRepository
import com.lifeforge.os.domain.repository.SettingsRepository
import com.lifeforge.os.domain.repository.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class NutritionState(
    val meals: List<Meal> = emptyList(),
    val waterToday: Int = 0,
    val waterGoal: Int = 2500,
    val caloriesGoal: Int = 2200,
    val calorieTotal: Double = 0.0,
    val proteinTotal: Double = 0.0,
    val carbsTotal: Double = 0.0,
    val fatTotal: Double = 0.0,
    val searchResults: List<Food> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class NutritionViewModel(
    private val nutritionRepository: NutritionRepository,
    private val waterRepository: WaterRepository,
    private val foodRepository: FoodRepository,
    private val settingsRepository: SettingsRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            combine(
                nutritionRepository.observeDay(startOfTodayMillis()),
                waterRepository.observeDay(startOfTodayMillis()),
                settingsRepository.waterGoal,
                settingsRepository.caloriesGoal,
            ) { meals, waterLogs, waterGoal, caloriesGoal ->
                var cal = 0.0
                var prot = 0.0
                var carb = 0.0
                var fat = 0.0
                meals.forEach { meal ->
                    cal += meal.total.calories
                    prot += meal.total.protein
                    carb += meal.total.carbs
                    fat += meal.total.fat
                }
                NutritionState(
                    meals = meals,
                    waterToday = waterLogs.sumOf { it.amountMl },
                    waterGoal = waterGoal,
                    caloriesGoal = caloriesGoal,
                    calorieTotal = cal,
                    proteinTotal = prot,
                    carbsTotal = carb,
                    fatTotal = fat,
                    isLoading = false,
                    error = null,
                )
            }.catch { e ->
                _state.value = NutritionState(isLoading = false, error = e.message)
            }.collect { _state.value = it }
        }
    }

    fun addWater() {
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            waterRepository.addWater(amountMl = 250, date = startOfTodayMillis(), time = now)
        }
    }

    fun removeWater(id: String) {
        scopeProvider.ioScope.launch { waterRepository.removeWater(id) }
    }

    fun searchFoods(query: String) {
        if (query.isBlank()) {
            _state.value = _state.value.copy(searchResults = emptyList())
            return
        }
        scopeProvider.ioScope.launch {
            val results = foodRepository.search(query)
            _state.value = _state.value.copy(searchResults = results)
        }
    }

    fun addFoodToMeal(food: Food) {
        scopeProvider.ioScope.launch {
            val today = startOfTodayMillis()
            val snack = _state.value.meals.firstOrNull { it.type == MealType.Snack }
            val item = MealItem(
                id = newEntityId(),
                mealId = snack?.id ?: "",
                foodId = food.id,
                foodName = food.name,
                servingMultiplier = 1.0,
                facts = food.facts,
            )
            if (snack != null) {
                nutritionRepository.addItemToMeal(snack.id, item)
            } else {
                nutritionRepository.saveMeal(
                    Meal(
                        id = newEntityId(),
                        date = today,
                        type = MealType.Snack,
                        items = listOf(item),
                        total = food.facts,
                    )
                )
            }
        }
    }

    fun deleteMeal(id: String) {
        scopeProvider.ioScope.launch { nutritionRepository.deleteMeal(id) }
    }
}