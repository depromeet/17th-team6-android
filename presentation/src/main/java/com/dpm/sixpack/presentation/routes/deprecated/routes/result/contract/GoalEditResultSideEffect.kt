package com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface GoalEditResultSideEffect : SideEffect {
    data object NavigateToBack : GoalEditResultSideEffect

    data object NavigateToHome : GoalEditResultSideEffect
}
