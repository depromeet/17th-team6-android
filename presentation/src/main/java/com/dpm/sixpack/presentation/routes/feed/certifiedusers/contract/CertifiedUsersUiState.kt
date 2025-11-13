package com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class CertifiedUsersUiState(
    val users: List<PostingUserInfo> = listOf(),
    val isLoading: Boolean = false,
) : UiState,
    Parcelable
