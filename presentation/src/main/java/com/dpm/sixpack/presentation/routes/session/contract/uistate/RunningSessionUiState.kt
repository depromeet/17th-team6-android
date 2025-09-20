package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningSessionUiState(
    val sessionState: RunningSessionState = RunningSessionState.Initial(),
    val isFollowingModeEnabled: Boolean = true,
) : UiState,
    Parcelable
