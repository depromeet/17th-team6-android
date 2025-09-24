package com.dpm.sixpack.presentation.routes.onboarding.permission

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.OnboardingPermissionIntent
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.OnboardingPermissionSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate.OnboardingPermissionUiState
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate.TermType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class OnboardingPermissionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OnboardingPermissionUiState, OnboardingPermissionIntent, OnboardingPermissionSideEffect>() {
    override val initialState: OnboardingPermissionUiState = OnboardingPermissionUiState()
    override val container: Container<OnboardingPermissionUiState, OnboardingPermissionSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: OnboardingPermissionIntent) {
        when (intent) {
            is OnboardingPermissionIntent.ClickBackButton -> {
                intent {
                    postSideEffect(OnboardingPermissionSideEffect.NavigateToBack)
                }
            }

            is OnboardingPermissionIntent.ClickNextButton -> {
                intent {
                    postSideEffect(OnboardingPermissionSideEffect.NavigateToNext)
                }
            }

            is OnboardingPermissionIntent.ShowTermDetails -> {
                TODO()
            }

            is OnboardingPermissionIntent.ToggleAllTerms -> handleToggleAllTerms(intent.isChecked)

            is OnboardingPermissionIntent.ToggleTerm -> handleToggleTerm(intent.type, intent.isChecked)
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
                    termsState =
                        state.termsState.toMutableMap().apply {
                            this[type] = isChecked
                        },
                )
            }
        }
    }
}
