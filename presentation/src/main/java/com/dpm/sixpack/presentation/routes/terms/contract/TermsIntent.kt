package com.dpm.sixpack.presentation.routes.terms.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.signup.ui.component.terms.model.TermType

sealed interface TermsIntent : UiIntent {
    data class OnTermToggled(
        val termType: TermType,
        val isChecked: Boolean,
    ) : TermsIntent

    data class OnAllTermsToggled(
        val isChecked: Boolean,
    ) : TermsIntent

    data object OnAgreeClick : TermsIntent

    data object OnBackButtonClick : TermsIntent

    data class OnTermDetailClick(
        val url: String,
    ) : TermsIntent
}
