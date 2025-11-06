package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SessionDetailResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.RUNS
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.SESSIONS
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RunningServiceApi {
    @POST("$API/$RUNS/$SESSIONS/start")
    suspend fun postStartSession(): BaseResponse<StartRunningResponseDto>

    @Multipart
    @POST("$API/$RUNS/$SESSIONS/{sessionId}/complete")
    suspend fun postFinishRunning(
        @Path("sessionId") sessionId: Long,
        @Part("data") data: RequestBody,
        @Part mapImage: MultipartBody.Part,
    ): BaseResponse<FinishRunningResponseDto>

    @POST("$API/$RUNS/$SESSIONS/{sessionId}/segments")
    suspend fun postSegmentData(
        @Path("sessionId") sessionId: Long,
        @Body saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto,
    ): BaseResponse<SaveSegmentResponseDto>

    @POST("$API/$RUNS/$SESSIONS/{sessionId}")
    suspend fun getSessionDetail(
        @Path("sessionId") sessionId: Long,
    ): BaseResponse<SessionDetailResponseDto>
}
