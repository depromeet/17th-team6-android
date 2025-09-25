package com.dpm.sixpack.presentation.routes.onboarding

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.CompleteOnboardingUseCase
import com.dpm.sixpack.domain.usecase.GetRecommendedGoalsUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish.GoalUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish.RecommendedGoalUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.goal.GoalType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.LevelType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.permission.TermType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val getRecommendedGoalsUseCase: GetRecommendedGoalsUseCase,
    val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : BaseViewModel<OnboardingUiState, OnboardingUiIntent, OnboardingSideEffect>() {
    override val initialState: OnboardingUiState = OnboardingUiState()
    override val container: Container<OnboardingUiState, OnboardingSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private var consentTimestamp: String? = null

    override fun onIntent(intent: OnboardingUiIntent) {
        when (intent) {
            // --- Permission Screen Intents ---
            is OnboardingUiIntent.ToggleAllTerms -> handleToggleAllTerms(intent.isChecked)
            is OnboardingUiIntent.ToggleTerm -> handleToggleTerm(intent.type, intent.isChecked)
            is OnboardingUiIntent.ClickPermissionNextButton ->
                intent {
                    consentTimestamp = Instant.now().toString()
                    postSideEffect(OnboardingSideEffect.NavigateToLevel)
                }

            is OnboardingUiIntent.ShowTermDetails -> TODO()

            // --- Level Screen Intents ---
            is OnboardingUiIntent.SelectLevel -> handleSelectLevel(intent.level)
            is OnboardingUiIntent.ClickLevelNextButton ->
                intent {
                    postSideEffect(OnboardingSideEffect.NavigateToGoal)
                }

            // --- Goal Screen Intents ---
            is OnboardingUiIntent.SelectGoal -> handleSelectGoal(intent.goal)
            is OnboardingUiIntent.ClickGoalNextButton ->
                intent {
                    getRecommendedGoalList()
                    postSideEffect(OnboardingSideEffect.NavigateToFinish)
                }

            // --- Finish Screen Intents ---
            is OnboardingUiIntent.SelectRecommendedGoal -> handleSelectRecommendedGoal(intent.id)
            is OnboardingUiIntent.CompleteOnboarding -> completeOnboarding()

            // --- Common Intents ---
            is OnboardingUiIntent.ClickBackButton ->
                intent {
                    postSideEffect(OnboardingSideEffect.NavigateToBack)
                }
        }
    }

    private fun handleToggleAllTerms(isChecked: Boolean) {
        intent {
            reduce {
                state.copy(
                    termsState = state.termsState.mapValues { isChecked },
                )
            }
        }
    }

    private fun handleToggleTerm(
        type: TermType,
        isChecked: Boolean,
    ) {
        intent {
            reduce {
                state.copy(
                    termsState = state.termsState.toMutableMap().apply { this[type] = isChecked },
                )
            }
        }
    }

    private fun handleSelectLevel(level: LevelType) {
        intent {
            reduce {
                state.copy(selectedLevel = level)
            }
        }
    }

    private fun handleSelectGoal(goal: GoalType) {
        intent {
            reduce {
                state.copy(selectedGoal = goal)
            }
        }
    }

    private fun handleSelectRecommendedGoal(index: Int) {
        intent {
            val currentState = state.recommendedGoals
            val updatedGoals =
                currentState.mapIndexed { i, goal ->
                    goal.copy(isSelected = i == index)
                }

            reduce {
                state.copy(
                    recommendedGoals = updatedGoals,
                )
            }
        }
    }

    private fun completeOnboarding() {
        intent {
            postSideEffect(OnboardingSideEffect.NavigateToHome)
            completeOnboardingUseCase()
        }
    }

    private fun getRecommendedGoalList() {
        intent {
            val uiGoals =
                getRecommendedGoalsUseCase(state.selectedGoal!!.runningGoal)
                    .mapIndexed { index, data ->
                        RecommendedGoalUiState(
                            title = data.title,
                            subTitle = data.subTitle,
                            isRecommended = index == 0,
                            isSelected = false,
                            goalTarget =
                                GoalUiState(
                                    pace = data.goal.pace,
                                    distance = data.goal.distance,
                                    duration = data.goal.duration,
                                    roundCount = data.goal.roundCount,
                                ),
                        )
                    }

            reduce {
                state.copy(recommendedGoals = uiGoals)
            }
        }
    }
}
