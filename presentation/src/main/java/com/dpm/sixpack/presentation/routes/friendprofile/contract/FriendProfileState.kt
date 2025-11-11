package com.dpm.sixpack.presentation.routes.friendprofile.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.NetworkErrorType
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import kotlinx.parcelize.Parcelize

/**
 * 친구 프로필 화면의 UI 상태
 */
@Parcelize
data class FriendProfileState(
    val profileInfo: ProfileInfo = ProfileInfo(),
    val isLoading: Boolean = false,
    val error: NetworkErrorType? = null,
) : UiState,
    Parcelable
