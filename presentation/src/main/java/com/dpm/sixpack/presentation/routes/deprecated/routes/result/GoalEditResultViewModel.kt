package com.dpm.sixpack.presentation.routes.deprecated.routes.result

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.GoalType
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract.GoalEditResultIntent
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract.GoalEditResultScreenState
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract.GoalEditResultSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class GoalEditResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<GoalEditResultScreenState, GoalEditResultIntent, GoalEditResultSideEffect>() {
    val selectedGoalType by lazy {
        savedStateHandle.get<GoalType>(GoalType::class.toString()) ?: GoalType.getDefault()
    }

    override val initialState: GoalEditResultScreenState = GoalEditResultScreenState(selectedGoal = selectedGoalType)

    override val container: Container<GoalEditResultScreenState, GoalEditResultSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    init {
        initializeState()
    }

    private fun initializeState() {
        intent {
        }
    }

    override fun onIntent(intent: GoalEditResultIntent) {
        when (intent) {
            is GoalEditResultIntent.BackClick -> handleBackClick()
            is GoalEditResultIntent.RecommendedGoalClick -> handleRecommendedGoalClick(intent)
            is GoalEditResultIntent.CompleteClick -> handleCompleteClick()
        }
    }

    private fun handleBackClick() {
        intent {
            postSideEffect(GoalEditResultSideEffect.NavigateToBack)
        }
    }

    private fun handleRecommendedGoalClick(intent: GoalEditResultIntent.RecommendedGoalClick) {
        intent {
            val selectedGoal = state.recommendedGoals.getOrNull(intent.index) ?: return@intent
            val updatedGoals =
                state.recommendedGoals.map {
                    if (it.id == selectedGoal.id) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it.copy(isSelected = false)
                    }
                }
            reduce {
                state.copy(recommendedGoals = updatedGoals)
            }
        }
    }

    private fun handleCompleteClick() {
        intent {
            val selectedRecommendedGoal = state.recommendedGoals.firstOrNull { it.isSelected } ?: return@intent
            postSideEffect(GoalEditResultSideEffect.NavigateToHome)
        }
    }
}
