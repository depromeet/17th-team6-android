package com.dpm.sixpack.presentation.routes.deprecated.home.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface HomeIntent : UiIntent {
    data object GoalList : HomeIntent

    data object NextSession : HomeIntent

    data object PreviousSession : HomeIntent

    // 달성 완료한 경우에만 가능
    data object GoalEdit : HomeIntent
}
