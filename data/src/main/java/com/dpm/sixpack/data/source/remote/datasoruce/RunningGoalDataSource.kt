package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.response.TodayGoalResponseDto
import com.dpm.sixpack.data.source.remote.service.RunningGoalService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import javax.inject.Inject

class RunningGoalDataSource
    @Inject
    constructor(
        private val runningGoalService: RunningGoalService,
    ) {
        suspend fun getTodayRunningGoal(): BaseResponse<TodayGoalResponseDto> = runningGoalService.getTodayRunningGoal()
    }
