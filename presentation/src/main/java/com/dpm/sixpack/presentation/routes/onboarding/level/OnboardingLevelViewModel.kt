package com.dpm.sixpack.presentation.routes.onboarding.level

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.OnboardingLevelUiIntent
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.OnboardingLevelSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.uistate.OnboardingLevelUiState
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.uistate.RunningLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class OnboardingLevelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) :
    BaseViewModel<OnboardingLevelUiState, OnboardingLevelUiIntent, OnboardingLevelSideEffect>() {
    override val initialState: OnboardingLevelUiState
        get() = OnboardingLevelUiState()
    override val container: Container<OnboardingLevelUiState, OnboardingLevelSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: OnboardingLevelUiIntent) {
        when (intent) {
            is OnboardingLevelUiIntent.SelectLevel -> handleSelectLevel(intent.level)
            is OnboardingLevelUiIntent.ClickNextButton -> {
                intent {
                    postSideEffect(OnboardingLevelSideEffect.NavigateToNext)
                }
            }

            OnboardingLevelUiIntent.ClickBackButton -> {
                intent {
                    postSideEffect(OnboardingLevelSideEffect.NavigateToBack)
                }
            }
        }
    }

    private fun handleSelectLevel(level: RunningLevel) {
        intent {
            reduce {
                state.copy(
                    selectedLevel = level,
                )
            }
        }
    }
}
