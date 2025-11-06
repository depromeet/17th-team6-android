package com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.UserInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class CertifiedUsersUiState(
    val users: List<PostingUserInfo> =
        listOf(
            PostingUserInfo(
                user =
                    UserInfo(
                        id = 1,
                        name = "비락식혜",
                        profileImageUrl = "",
                        isMe = true,
                    ),
                postingTime = "2025-11-03T09:30:00Z",
            ),
            PostingUserInfo(
                user =
                    UserInfo(
                        id = 2,
                        name = "비락식혜",
                        profileImageUrl = "",
                        isMe = false,
                    ),
                postingTime = "2025-11-03T09:30:00Z",
            ),
        ),
    val isLoading: Boolean = false,
) : UiState,
    Parcelable
