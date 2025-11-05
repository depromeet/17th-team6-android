package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendsRunningStatusResponseDto(
    @SerialName("contents")
    val contents: List<FriendsRunningStatusDto>,
    @SerialName("meta")
    val meta: PaginationInfoDto,
)

@Serializable
data class FriendsRunningStatusDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("isMe")
    val isMe: Boolean,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("profileImage")
    val profileImage: String,
    @SerialName("latestRanAt")
    val latestRanAt: String?,
    @SerialName("latestCheeredAt")
    val latestCheeredAt: String?,
    @SerialName("distance")
    val distance: Int?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("address")
    val address: String?,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PaginationInfoDto(
    @SerialName("page")
    val page: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("totalElements")
    val totalElements: Int,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("hasNext")
    val hasNext: Boolean,
    @SerialName("hasPrevious")
    val hasPrevious: Boolean,
)
