package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class UserInfo(
    val id: Long = -1L,
    val name: String = "",
    val profileImageUrl: String = "",
    val isMe: Boolean = false,
) : Parcelable

fun User.toUserInfo() =
    UserInfo(
        id = userId,
        name = nickName,
        profileImageUrl = profileImgUrl,
        isMe = isMe,
    )
