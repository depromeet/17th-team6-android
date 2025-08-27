package com.dpm.sixpack.presentation.example.contract

import com.dpm.sixpack.presentation.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExampleState(
    val isLoading: Boolean = false,
    val count: Int = 0,
) : UiState
