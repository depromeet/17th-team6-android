package com.dpm.sixpack.presentation.routes.feed.postedit.contract

import android.net.Uri
import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostResource
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostEditUiState(
    val originalPost: PostResource = PostResource(),
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = true,
    val hasImagePermission: Boolean = false,
) : UiState,
    Parcelable
