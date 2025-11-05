package com.dpm.sixpack.presentation.routes.postedit.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface PostEditSideEffect : SideEffect {
    data object NavigateBack : PostEditSideEffect

    data object OpenImagePicker : PostEditSideEffect

    data object RequestImagePermission : PostEditSideEffect

    data class SaveImageToGallery(
        val imageUrl: String,
    ) : PostEditSideEffect

    data class ShowToast(
        val message: String,
    ) : PostEditSideEffect

    data class ShowError(
        val message: String,
    ) : PostEditSideEffect
}
