package com.dpm.sixpack.presentation.routes.terms

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.terms.ui.component.model.TermType
import com.dpm.sixpack.presentation.routes.terms.contract.TermsIntent
import com.dpm.sixpack.presentation.routes.terms.contract.TermsSideEffect
import com.dpm.sixpack.presentation.routes.terms.contract.TermsState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

private typealias TermsSyntax = Syntax<TermsState, TermsSideEffect>

@HiltViewModel
class TermsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TermsState, TermsIntent, TermsSideEffect>() {
    override val initialState: TermsState = TermsState()

    override val container: Container<TermsState, TermsSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: TermsIntent) {
        when (intent) {
            is TermsIntent.OnTermToggled -> handleTermToggled(intent.termType, intent.isChecked)
            is TermsIntent.OnAllTermsToggled -> handleAllTermsToggled(intent.isChecked)
            is TermsIntent.OnAgreeClick -> handleAgreeClick()
            is TermsIntent.OnBackButtonClick -> handleBackButtonClick()
            is TermsIntent.OnTermDetailClick -> handleTermDetailClick(intent.url)
        }
    }

    private fun handleTermToggled(
        termType: TermType,
        isChecked: Boolean,
    ) = intent {
        val updatedTermsState = state.termsState.toMutableMap()
        updatedTermsState[termType] = isChecked
        reduce {
            state.copy(termsState = updatedTermsState)
        }
    }

    private fun handleAllTermsToggled(isChecked: Boolean) =
        intent {
            val updatedTermsState = TermType.entries.associateWith { isChecked }
            reduce {
                state.copy(termsState = updatedTermsState)
            }
        }

    private fun handleAgreeClick() =
        intent {
            if (!state.isAllRequiredTermsAgreed) {
                Timber.w("Not all required terms agreed")
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            try {
                // TODO: Save terms agreement to storage/API if needed
                reduce { state.copy(isLoading = false) }
                postSideEffect(TermsSideEffect.NavigateToSignUp)
                Timber.d("Terms agreement completed successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to complete terms agreement")
                reduce {
                    state.copy(isLoading = false)
                }
            }
        }

    private fun handleBackButtonClick() =
        intent {
            postSideEffect(TermsSideEffect.NavigateBack)
        }

    private fun handleTermDetailClick(url: String) =
        intent {
            postSideEffect(TermsSideEffect.OpenTermUrl(url))
        }
}
