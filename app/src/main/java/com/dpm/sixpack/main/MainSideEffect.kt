package com.dpm.sixpack.main

import com.dpm.sixpack.presentation.common.base.SideEffect

/**
 * MainViewModel의 Side Effect
 */
sealed interface MainSideEffect : SideEffect {
    data object NavigateToOnboarding : MainSideEffect
}
