package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapState(
    val isLoading: Boolean = false,
    val count: Int = 0
) : UiState
