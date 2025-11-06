package com.dpm.sixpack.presentation.routes.settings.profileedit.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface ProfileEditIntent : UiIntent {
    data object OnBackButtonClick : ProfileEditIntent

    data class OnProfileNameChanged(
        val name: String,
    ) : ProfileEditIntent

    data object OnPickImageClick : ProfileEditIntent

    data class OnProfileImageSelected(
        val imageUri: String,
    ) : ProfileEditIntent

    data object OnCompleteClick : ProfileEditIntent
}
