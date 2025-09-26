package com.dpm.sixpack.presentation.routes.goaledit.routes.result

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.model.params.SaveTotalGoalParams
import com.dpm.sixpack.domain.usecase.GetRecommendedGoalsUseCase
import com.dpm.sixpack.domain.usecase.SaveTotalGoalUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.components.goal.model.state.asUiState
import com.dpm.sixpack.presentation.common.components.goal.model.type.GoalType
import com.dpm.sixpack.presentation.routes.goaledit.routes.result.contract.GoalEditResultIntent
import com.dpm.sixpack.presentation.routes.goaledit.routes.result.contract.GoalEditResultScreenState
import com.dpm.sixpack.presentation.routes.goaledit.routes.result.contract.GoalEditResultSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class GoalEditResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val getRecommendedGoalsUseCase: GetRecommendedGoalsUseCase,
    val saveTotalGoalUseCase: SaveTotalGoalUseCase,
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
            val recommendedGoals = getRecommendedGoalsUseCase(selectedGoalType.runningGoal)
            reduce {
                state.copy(
                    loading = false,
                    recommendedGoals = recommendedGoals.mapIndexed { index, data -> data.asUiState(index) },
                )
            }
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
            saveTotalGoalUseCase(
                SaveTotalGoalParams(
                    title = selectedRecommendedGoal.title,
                    subTitle = selectedRecommendedGoal.subTitle,
                    type = (state.selectedGoal ?: GoalType.getDefault()).runningGoal,
                    pace = selectedRecommendedGoal.goalTarget.pace,
                    distance = selectedRecommendedGoal.goalTarget.distance,
                    duration = selectedRecommendedGoal.goalTarget.duration,
                    totalRoundCount = selectedRecommendedGoal.goalTarget.roundCount,
                ),
            )
            postSideEffect(GoalEditResultSideEffect.NavigateToHome)
        }
    }
}
