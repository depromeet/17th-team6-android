package com.dpm.sixpack.presentation.routes.profilecreation.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileCreationState(
    val profileName: String = "",
    val profileImageUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState,
    Parcelable {
    val isProfileNameValid: Boolean
        get() = profileName.isNotBlank()

    val isCompleteButtonEnabled: Boolean
        get() = isProfileNameValid && !isLoading
}
