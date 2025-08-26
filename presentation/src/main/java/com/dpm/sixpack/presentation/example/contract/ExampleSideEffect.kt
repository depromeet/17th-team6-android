package com.dpm.sixpack.presentation.example.contract

import com.dpm.sixpack.presentation.base.SideEffect

sealed interface ExampleSideEffect: SideEffect {
    data class ShowToast(val message: String) : ExampleSideEffect
    data object NavigateNext : ExampleSideEffect
    data object NavigateBack : ExampleSideEffect
}
