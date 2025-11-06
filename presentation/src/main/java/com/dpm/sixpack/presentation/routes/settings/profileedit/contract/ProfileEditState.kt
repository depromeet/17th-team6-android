package com.dpm.sixpack.presentation.routes.settings.profileedit.contract

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileEditState(
    val profileName: String = "",
    val profileImageUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    val isProfileNameValid: Boolean
        get() = profileName.length in 2..10

    val isCompleteButtonEnabled: Boolean
        get() = isProfileNameValid && !isLoading
}
