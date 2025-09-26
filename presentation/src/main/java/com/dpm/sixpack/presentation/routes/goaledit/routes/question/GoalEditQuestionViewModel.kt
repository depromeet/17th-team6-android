package com.dpm.sixpack.presentation.routes.goaledit.routes.question

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract.GoalEditQuestionIntent
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract.GoalEditQuestionScreenState
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract.GoalEditQuestionSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class GoalEditQuestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<GoalEditQuestionScreenState, GoalEditQuestionIntent, GoalEditQuestionSideEffect>() {
    override val initialState: GoalEditQuestionScreenState = GoalEditQuestionScreenState()

    override val container: Container<GoalEditQuestionScreenState, GoalEditQuestionSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: GoalEditQuestionIntent) {
        when (intent) {
            is GoalEditQuestionIntent.BackClick -> handleBackClick()
            is GoalEditQuestionIntent.GoalTypeClick -> handleSelectGoalTypeClick(intent)
            is GoalEditQuestionIntent.NextClick -> handleNextClick()
        }
    }

    private fun handleBackClick() {
        intent {
            postSideEffect(GoalEditQuestionSideEffect.NavigateToBack)
        }
    }

    private fun handleSelectGoalTypeClick(intent: GoalEditQuestionIntent.GoalTypeClick) {
        intent {
            reduce {
                state.copy(
                    goalTypes =
                        state.goalTypes.map {
                            if (it.goalType == intent.goalType) {
                                it.copy(isSelected = true)
                            } else {
                                it.copy(isSelected = false)
                            }
                        },
                )
            }
        }
    }

    private fun handleNextClick() {
        intent {
            state.goalTypes.firstOrNull { it.isSelected }?.goalType?.let {
                postSideEffect(GoalEditQuestionSideEffect.NavigateToGoalEditResult(it))
            }
        }
    }
}
