package com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingPermissionUiState(
    val termsState: Map<TermType, Boolean> = TermType.entries.associateWith { false }
) : UiState,
    Parcelable{
        val isAllTermsChecked: Boolean
        get() = termsState.values.all { it }

    val isNextButtonEnabled: Boolean
        get() = TermType.entries
            .filter { it.isRequired }
            .all { termsState[it] == true }
}

