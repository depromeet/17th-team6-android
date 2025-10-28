package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.FEEDS
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.SELFIE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @GET("/$API/$SELFIE/$FEEDS")
    suspend fun getFeeds(
        @Query("currentDate") currentDate: String?,
        @Query("userId") userId: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<FeedPageDto>

    @POST("$API/$SELFIE/{selfieId}/reaction")
    suspend fun postReaction(
        @Path("selfieId") selfieId: Int,
        @Body body: ReactionRequestDto
    ): BaseResponse<ReactionResultDto>
}
