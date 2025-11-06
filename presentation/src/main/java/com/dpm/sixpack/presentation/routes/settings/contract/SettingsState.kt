package com.dpm.sixpack.presentation.routes.settings.contract

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsState(
    val appVersion: String = "",
    val isLoading: Boolean = false,
) : UiState
