package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.RUNS
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.SESSIONS
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface RunningSessionService {
    @POST("$API/$RUNS/$SESSIONS/start")
    suspend fun postStartSession(): BaseResponse<StartRunningResponseDto>

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
}
