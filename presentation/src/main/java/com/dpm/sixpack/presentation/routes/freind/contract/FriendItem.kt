package com.dpm.sixpack.presentation.routes.freind.contract

import android.os.Parcelable
import com.dpm.sixpack.domain.model.Friend
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendItem(
    val userId: Long, // 123
    val nickName: String,
    val isMe: Boolean, // true
    val profileImgUrl: String, // "https://example.com/profile.jpg",
    val lastestRunAt: String, // "2025-09-13T19:57:13Z",
    val distanceInMeter: Int, // 5000
    val latitude: Double, // 37.5301
    val longitude: Double, // 127.12345
) : Parcelable

fun Friend.toUiItem() =
    FriendItem(
        userId = userInfo.userId,
        nickName = userInfo.nickName,
        isMe = userInfo.isMe,
        profileImgUrl = userInfo.profileImgUrl,
        lastestRunAt = lastestRunAt,
        distanceInMeter = distanceInMeter,
        latitude = latitude,
        longitude = longitude,
    )
