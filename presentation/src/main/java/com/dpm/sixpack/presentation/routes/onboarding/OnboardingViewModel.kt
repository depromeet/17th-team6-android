package com.dpm.sixpack.presentation.routes.onboarding

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.RunningLevel
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.permission.TermType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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
            is OnboardingUiIntent.ClickPermissionNextButton -> intent {
                consentTimestamp = Instant.now().toString()
                postSideEffect(OnboardingSideEffect.NavigateToLevelScreen)
            }

            is OnboardingUiIntent.ShowTermDetails -> TODO()

            // --- Level Screen Intents ---
            is OnboardingUiIntent.SelectLevel -> handleSelectLevel(intent.level)
            is OnboardingUiIntent.ClickLevelNextButton -> intent {
                postSideEffect(OnboardingSideEffect.NavigateToGoalScreen)
            }

            // --- Common Intents ---
            is OnboardingUiIntent.ClickBackButton -> intent {
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

    private fun handleToggleTerm(type: TermType, isChecked: Boolean) {
        intent {
            reduce {
                state.copy(
                    termsState = state.termsState.toMutableMap().apply { this[type] = isChecked },
                )
            }
        }
    }

    private fun handleSelectLevel(level: RunningLevel) {
        intent {
            reduce {
                state.copy(selectedLevel = level)
            }
        }
    }
}
