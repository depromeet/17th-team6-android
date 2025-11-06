package com.dpm.sixpack.presentation.routes.settings.profileedit.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface ProfileEditSideEffect : SideEffect {
    data object NavigateBack : ProfileEditSideEffect

    data object LaunchImagePicker : ProfileEditSideEffect

    data object ProfileEditCompleted : ProfileEditSideEffect
}
