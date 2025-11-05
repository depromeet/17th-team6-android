package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.request.StartRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.RunSessionListResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.service.RunningSessionService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import javax.inject.Inject

class RunningSessionDataSource
    @Inject
    constructor(
        private val runningSessionService: RunningSessionService,
    ) {
        suspend fun postFinishRunning(
            sessionId: Long,
            finishRunningRequestDto: FinishRunningRequestDto,
        ): BaseResponse<FinishRunningResponseDto> =
            runningSessionService.postFinishRunning(sessionId, finishRunningRequestDto)

        suspend fun postSegmentData(
            sessionId: Long,
            saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto,
        ): BaseResponse<SaveSegmentResponseDto> =
            runningSessionService.postSegmentData(sessionId, saveSegmentDataRequestsDto)

        suspend fun postStartRunning(
            startRunningRequestDto: StartRunningRequestDto,
        ): BaseResponse<StartRunningResponseDto> = runningSessionService.postStartRunning(startRunningRequestDto)

        suspend fun getRunSessions(
            isSelfied: Boolean?,
            startDateTime: String?,
        ): BaseResponse<List<RunSessionListResponseDto>> =
            runningSessionService.getRunSessions(isSelfied, startDateTime)
    }
