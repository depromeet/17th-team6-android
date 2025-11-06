package com.dpm.sixpack.main

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

/**
 * MainViewModel의 UI 상태
 */
@Parcelize
data class MainState(
    val placeholder: Unit = Unit,
) : UiState
