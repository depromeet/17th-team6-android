package com.dpm.sixpack.presentation.routes.postupload.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface PostUploadSideEffect : SideEffect {
    data object NavigateBack : PostUploadSideEffect

    data object OpenImagePicker : PostUploadSideEffect

    data object RequestImagePermission : PostUploadSideEffect

    data class ShowToast(
        val message: String,
    ) : PostUploadSideEffect

    data class ShowError(
        val message: String,
    ) : PostUploadSideEffect

    data object NavigateToFeed : PostUploadSideEffect
}
