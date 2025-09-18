package com.dpm.sixpack.presentation.common.util.base

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost

abstract class BaseViewModel<UI_STATE : UiState, UI_INTENT : UiIntent, SIDE_EFFECT : SideEffect> :
    ViewModel(),
    ContainerHost<UI_STATE, SIDE_EFFECT> {
    abstract val initialState: UI_STATE
    abstract override val container: Container<UI_STATE, SIDE_EFFECT>

    // UI에서 호출하는 단일 진입점
    abstract fun onIntent(intent: UI_INTENT)
}
