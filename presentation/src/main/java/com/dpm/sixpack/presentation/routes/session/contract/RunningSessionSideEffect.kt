package com.dpm.sixpack.presentation.routes.session.contract

import com.dpm.sixpack.presentation.util.base.SideEffect

sealed interface RunningSessionSideEffect : SideEffect {
    data class ShowToast(
        val messageResId: Int,
    ) : RunningSessionSideEffect
}
