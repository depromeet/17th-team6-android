package com.dpm.sixpack.presentation.routes.feed.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedUiState(
    val isLoading: Boolean = true,
    val error: String? = null
) : UiState, Parcelable
