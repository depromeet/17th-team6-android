package com.dpm.sixpack.presentation.routes.postupload.contract

import android.net.Uri
import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.RunningSummary
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostUploadUiState(
    val sessionId: String = "",
    val mapImageUrl: String = "",
    val runningSummary: RunningSummary = RunningSummary(),
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = true,
    val hasImagePermission: Boolean = false,
) : UiState,
    Parcelable
