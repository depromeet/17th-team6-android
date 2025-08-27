package com.dpm.sixpack.presentation.base

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

abstract class BaseViewModel<UI_STATE : UiState, UI_INTENT : UiIntent, SIDE_EFFECT : SideEffect>(
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    ContainerHost<UI_STATE, SIDE_EFFECT> {
    abstract val initialState: UI_STATE

    override val container: Container<UI_STATE, SIDE_EFFECT> by lazy {
        container(
            initialState = initialState,
            savedStateHandle = savedStateHandle,
        )
    }

    // 1. UI에서 호출하는 단일 진입점
    abstract fun onIntent(intent: UI_INTENT)

    // 2. UI에서 호출하는 단일 진입점
    fun onEvent(intent: UI_INTENT) =
        intent {
            handleEvent(intent)
        }

    // 2-1. 하위 뷰모델에서 반드시 구현해야 하는 이벤트 처리 로직
    protected abstract suspend fun Syntax<UI_STATE, SIDE_EFFECT>.handleEvent(intent: UI_INTENT)
}
