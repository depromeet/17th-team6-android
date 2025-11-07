package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.CertifiedUsersDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.dto.response.SelfieCountsDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedService {
    // 1. 유저의 인증피드 목록 조회
    @GET("/api/selfie/feeds")
    suspend fun getFeeds(
        @Query("currentDate") currentDate: String?,
        @Query("userId") userId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): BaseResponse<FeedPageDto>

    // 인증피드 상세 조회
    @GET("/api/selfie/feeds/{feedId}")
    suspend fun getPostDetail(
        @Path("feedId") feedId: Long,
    ): BaseResponse<FeedDto>

    // 인증피드 업로드
    @Multipart
    @POST("/api/selfie/feeds")
    suspend fun uploadPost(
        @Part("data") data: RequestBody,
        @Part selfieImage: MultipartBody.Part?,
    ): BaseResponse<Unit>

    // 인증피드 수정
    @Multipart
    @PUT("/api/selfie/feeds/{feedId}")
    suspend fun updatePost(
        @Path("feedId") feedId: Long,
        @Part("data") data: RequestBody,
        @Part selfieImage: MultipartBody.Part?,
    ): BaseResponse<Unit>

    // 인증피드 삭제
    @DELETE("/api/selfie/feeds/{feedId}")
    suspend fun deletePost(
        @Path("feedId") feedId: Long,
    ): BaseResponse<Unit>

    // 친구 인증 반응 남기기 (추가/취소)
    @POST("/api/selfie/feeds/reaction")
    suspend fun postReaction(
        @Body body: ReactionRequestDto,
    ): BaseResponse<ReactionResultDto>

    // 특정 날짜 인증 유저 목록 조회
    @GET("/api/selfie/users")
    suspend fun getCertifiedUsers(
        @Query("date") date: String,
    ): BaseResponse<CertifiedUsersDto>

    // 주차별 친구들의 인증수 조회 (캘린더용)
    @GET("/api/selfie/week")
    suspend fun getWeeklyPostCount(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): BaseResponse<SelfieCountsDto>
}
