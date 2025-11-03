package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.request.AddFriendRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.FriendDeleteRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.FriendNotificationRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.AddFriendResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.FriendCodeResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.FriendsRunningStatusResponseDto
import com.dpm.sixpack.data.source.remote.service.FriendServiceApi
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendDataSource @Inject constructor(
    private val friendServiceApi: FriendServiceApi,
) {
    /**
     * 친구에게 알림 보내기 (친구 응원하기)
     */
    suspend fun postFriendNotification(requestDto: FriendNotificationRequestDto): BaseResponse<Nothing> =
        friendServiceApi.postFriendNotification(requestDto)

    /**
     * 친구 삭제
     */
    suspend fun deleteFriend(requestDto: FriendDeleteRequestDto): BaseResponse<Nothing> =
        friendServiceApi.deleteFriend(requestDto)

    /**
     * 친구 러닝 현황 조회
     */
    suspend fun getFriendsRunningStatus(
        page: Int,
        size: Int,
    ): BaseResponse<FriendsRunningStatusResponseDto> =
        friendServiceApi.getFriendsRunningStatus(page = page, size = size)

    /**
     * 내 친구 코드 조회
     */
    suspend fun getMyFriendCode(): BaseResponse<FriendCodeResponseDto> = friendServiceApi.getMyFriendCode()

    /**
     * 친구 코드로 친구 추가
     */
    suspend fun addFriendByCode(requestDto: AddFriendRequestDto): BaseResponse<AddFriendResponseDto> =
        friendServiceApi.addFriendByCode(requestDto)
}
