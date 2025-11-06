package com.dpm.sixpack.presentation.routes.postupload.contract

import android.net.Uri
import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface PostUploadIntent : UiIntent {
    data object OnBackClick : PostUploadIntent

    data object OnImageEditButtonClick : PostUploadIntent

    data class OnImageSelected(
        val imageUri: Uri,
    ) : PostUploadIntent

    data class OnImagePermissionResult(
        val isGranted: Boolean,
    ) : PostUploadIntent

    data object OnUploadButtonClick : PostUploadIntent
}
