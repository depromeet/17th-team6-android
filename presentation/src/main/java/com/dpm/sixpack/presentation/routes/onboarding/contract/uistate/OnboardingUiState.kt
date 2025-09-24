package com.dpm.sixpack.presentation.routes.onboarding.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.RunningLevel
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.permission.TermType
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingUiState(
    // Permission Screen State
    val termsState: Map<TermType, Boolean> = TermType.entries.associateWith { false },
    // Level Screen State
    val selectedLevel: RunningLevel? = null
) : UiState, Parcelable {
    val isAllTermsChecked: Boolean
        get() = termsState.values.all { it }
    val isPermissionNextEnabled: Boolean
        get() = TermType.entries.filter { it.isRequired }.all { termsState[it] == true }
    val isLevelNextEnabled: Boolean
        get() = selectedLevel != null
}
