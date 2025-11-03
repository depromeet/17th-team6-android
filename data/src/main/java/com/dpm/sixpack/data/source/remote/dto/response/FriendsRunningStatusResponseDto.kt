package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FriendsRunningStatusResponseDto(
    val contents: List<FriendsRunningStatusDto>,
    val meta: PaginationInfoDto,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FriendsRunningStatusDto(
    val userId: Int,
    val isMe: Boolean,
    val nickname: String,
    val profileImage: String,
    val latestRanAt: String?,
    val latestCheeredAt: String?,
    val distance: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PaginationInfoDto(
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)
