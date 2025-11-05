package com.dpm.sixpack.presentation.routes.postedit.contract

import android.net.Uri
import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface PostEditIntent : UiIntent {
    data object OnBackClick : PostEditIntent

    data object OnSaveClick : PostEditIntent

    data object OnImageEditButtonClick : PostEditIntent

    data class OnImageSelected(
        val imageUri: Uri,
    ) : PostEditIntent

    data class OnImagePermissionResult(
        val isGranted: Boolean,
    ) : PostEditIntent

    data object OnSubmitClick : PostEditIntent
}
