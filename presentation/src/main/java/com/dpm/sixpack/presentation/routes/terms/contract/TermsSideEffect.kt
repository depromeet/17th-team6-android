package com.dpm.sixpack.presentation.routes.terms.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface TermsSideEffect : SideEffect {
    data object NavigateToSignUp : TermsSideEffect

    data object NavigateBack : TermsSideEffect

    data class OpenTermUrl(
        val url: String,
    ) : TermsSideEffect
}
