package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedService {
    @GET("/api/selfie/feeds")
    suspend fun getFeeds(
        @Query("currentDate") currentDate: String?,
        @Query("userId") userId: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<FeedPageDto>
}
