package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.SideEffect

sealed interface MapSideEffect : SideEffect {
    data class ShowToast(val message: String) : MapSideEffect
    data object NavigateNext : MapSideEffect
    data object NavigateBack : MapSideEffect
}
