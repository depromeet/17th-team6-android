package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.response.TodayGoalResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.API
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.GOALS
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.RUNS
import retrofit2.http.GET


interface RunningGoalService {
    @GET("${API}/${RUNS}/${GOALS}/latest")
    suspend fun getTodayRunningGoal(
    ): BaseResponse<TodayGoalResponseDto>
}
