package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.AddFriendRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.FriendDeleteRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.FriendNotificationRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.AddFriendResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.DeleteFriendResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.FriendCodeResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.FriendNotifyResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.FriendsRunningStatusResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.FRIENDS
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendServiceApi {
    @POST("$API/$FRIENDS/reaction")
    suspend fun postFriendNotification(
        @Body requestDto: FriendNotificationRequestDto,
    ): BaseResponse<FriendNotifyResponseDto>

    @POST("$API/$FRIENDS/delete")
    suspend fun deleteFriend(
        @Body requestDto: FriendDeleteRequestDto,
    ): BaseResponse<DeleteFriendResponseDto>

    @GET("$API/$FRIENDS/running/status")
    suspend fun getFriendsRunningStatus(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<FriendsRunningStatusResponseDto>

    @GET("$API/$FRIENDS/will-you-friend-me")
    suspend fun getMyFriendCode(): BaseResponse<FriendCodeResponseDto>

    @POST("$API/$FRIENDS/will-you-friend-me")
    suspend fun addFriendByCode(
        @Body requestDto: AddFriendRequestDto,
    ): BaseResponse<AddFriendResponseDto>
}
