package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.request.StartRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.RunSessionListResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.RUNS
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.SESSIONS
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RunningSessionService {
    @POST("$API/$RUNS/$SESSIONS/{sessionId}/complete")
    suspend fun postFinishRunning(
        @Path("sessionId") sessionId: Long,
        @Body finishRunningRequestDto: FinishRunningRequestDto,
    ): BaseResponse<FinishRunningResponseDto>

    @POST("$API/$RUNS/$SESSIONS/{sessionId}/segments")
    suspend fun postSegmentData(
        @Path("sessionId") sessionId: Long,
        @Body saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto,
    ): BaseResponse<SaveSegmentResponseDto>

    @POST("$API/$RUNS/$SESSIONS/start")
    suspend fun postStartRunning(
        @Body startRunningRequestDto: StartRunningRequestDto,
    ): BaseResponse<StartRunningResponseDto>

    @GET("$API/$RUNS/$SESSIONS")
    suspend fun getRunSessions(
        @Query("isSelfied") isSelfied: Boolean? = null,
        @Query("startDateTime") startDateTime: String? = null,
    ): BaseResponse<List<RunSessionListResponseDto>>
}
