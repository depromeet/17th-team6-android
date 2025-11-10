package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import com.dpm.sixpack.domain.model.Friend
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendItem(
    val userId: Long, // 123
    val nickName: String,
    val isMe: Boolean, // true
    val profileImgUrl: String, // "https://example.com/profile.jpg",
    val lastRunInfo: LastRunInfoUi? = null, // 마지막 러닝 정보
    val latestCheeredAt: String? = null, // 마지막 응원 보낸 시각
) : Parcelable

@Parcelize
data class LastRunInfoUi(
    val lastestRunAt: String, // "2025-09-13T19:57:13Z",
    val distanceInMeter: Int, // 5000
    val latitude: Double, // 37.5301,
    val longitude: Double, // 127.12345
    val address: String,
) : Parcelable

fun Friend.toUiItem() =
    FriendItem(
        userId = userInfo.userId,
        nickName = userInfo.nickName,
        isMe = userInfo.isMe,
        profileImgUrl = userInfo.profileImgUrl,
        lastRunInfo =
            lastRunInfo?.let {
                LastRunInfoUi(
                    lastestRunAt = it.lastestRunAt,
                    distanceInMeter = it.distanceInMeter,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    address = it.address,
                )
            },
        latestCheeredAt = latestCheeredAt,
    )
