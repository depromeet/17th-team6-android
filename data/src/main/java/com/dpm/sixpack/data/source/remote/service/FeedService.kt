package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.dto.response.SelfieCountsDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    @GET("/api/selfie/feeds")
    suspend fun getFeeds(
        @Query("currentDate") currentDate: String?,
        @Query("userId") userId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<FeedPageDto>

    @POST("/api/selfie/{selfieId}/reaction")
    suspend fun postReaction(
        @Path("selfieId") selfieId: Int,
        @Body body: ReactionRequestDto
    ): BaseResponse<ReactionResultDto>

    @GET("/api/selfie/calendar")
    suspend fun getSelfieCalendar(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): BaseResponse<SelfieCountsDto>
}
