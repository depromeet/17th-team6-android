package com.dpm.sixpack.presentation.routes.profilecreation.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface ProfileCreationSideEffect : SideEffect {
    data object LaunchImagePicker : ProfileCreationSideEffect

    data object NavigateToHome : ProfileCreationSideEffect

    data object NavigateBack : ProfileCreationSideEffect
}
