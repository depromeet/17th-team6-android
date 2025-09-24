package com.dpm.sixpack.presentation.routes.onboarding.level.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingLevelUiState (
    val selectedLevel : RunningLevel? = null,
) : UiState, Parcelable{
    val isButtonEnabled : Boolean
        get() = selectedLevel != null
}
