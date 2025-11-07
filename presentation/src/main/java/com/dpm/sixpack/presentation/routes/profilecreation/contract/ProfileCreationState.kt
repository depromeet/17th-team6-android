package com.dpm.sixpack.presentation.routes.profilecreation.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileCreationState(
    val phoneNumber: String = "",
    val profileName: String = "",
    val profileImageUri: String? = null,
    val deviceToken: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState,
    Parcelable {
    val isProfileNameValid: Boolean
        get() = profileName.length in 2..8

    val isCompleteButtonEnabled: Boolean
        get() = isProfileNameValid && !isLoading
}
