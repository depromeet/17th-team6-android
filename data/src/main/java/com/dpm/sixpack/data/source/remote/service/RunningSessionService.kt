package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.RUNS
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.SESSIONS
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RunningSessionService {
    @POST("${API}/${RUNS}/${SESSIONS}/{sessionId}/complete")
    suspend fun postFinishRunning(
        @Path("planId") planId: Long,
        @Body finishRunningResponseDto: FinishRunningResponseDto
    ): BaseResponse<FinishRunningResponseDto>


    @POST("${API}/${RUNS}/${SESSIONS}/{sessionId}/segments")
    suspend fun getAllGoalSessions(
        @Path("sessionId") sessionId: Long,
        @Body saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto
    ): BaseResponse<SaveSegmentResponseDto>


    @POST("${API}/${RUNS}/${SESSIONS}/start")
    suspend fun postStartRunning(
    ): BaseResponse<StartRunningResponseDto>
}
