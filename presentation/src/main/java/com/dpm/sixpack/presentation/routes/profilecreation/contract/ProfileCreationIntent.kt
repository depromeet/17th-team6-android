package com.dpm.sixpack.presentation.routes.profilecreation.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface ProfileCreationIntent : UiIntent {
    data class OnProfileNameChanged(
        val name: String,
    ) : ProfileCreationIntent

    data object OnPickImageClick : ProfileCreationIntent

    data class OnProfileImageSelected(
        val imageUri: String,
    ) : ProfileCreationIntent

    data object OnCompleteProfileClick : ProfileCreationIntent

    data object OnBackButtonClick : ProfileCreationIntent
}
