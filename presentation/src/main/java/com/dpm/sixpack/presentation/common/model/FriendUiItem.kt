package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import com.dpm.sixpack.domain.model.Friend
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendUiItem(
    val userId: Long, // 123
    val nickName: String,
    val isMe: Boolean, // true
    val profileImgUrl: String, // "https://example.com/profile.jpg",
    val latestRunAt: String? = null, // "2025-09-13T19:57:13Z",
    val distanceInMeter: Int? = null, // 5000
    val latitude: Double? = null, // 37.5301
    val longitude: Double? = null, // 127.12345
    val address: String? = null, // 마지막 뛴 위치 주소
    val latestCheeredAt: String? = null, // 마지막 응원 보낸 시각
) : Parcelable

fun Friend.toUiItem() =
    FriendUiItem(
        userId = userInfo.userId,
        nickName = userInfo.nickName,
        isMe = userInfo.isMe,
        profileImgUrl = userInfo.profileImgUrl,
        latestRunAt = lastRunInfo?.lastestRunAt,
        distanceInMeter = lastRunInfo?.distanceInMeter,
        latitude = lastRunInfo?.latitude,
        longitude = lastRunInfo?.longitude,
        address = lastRunInfo?.address,
        latestCheeredAt = latestCheeredAt,
    )
