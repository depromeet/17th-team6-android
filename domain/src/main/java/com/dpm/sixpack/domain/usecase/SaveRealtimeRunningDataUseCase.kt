package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class SaveRealtimeRunningDataUseCase
    @Inject
    constructor(
        private val repository: RunningSessionRepository,
    ) {
        suspend operator fun invoke(param: SaveRealtimeRunningDataParam): DoRunResult<SaveRealtimeRunningDataResult> =
            when (param) {
                is SaveRealtimeRunningDataParam.LocalParam -> repository.saveRealtimeData(data = param.data)
                is SaveRealtimeRunningDataParam.SyncParam -> repository.saveSegmentData()
            }
    }

sealed class SaveRealtimeRunningDataParam {
    data class LocalParam(
        val data: RealtimeRunningData,
    ) : SaveRealtimeRunningDataParam()

    data class SyncParam(
        val sessionId: Long,
    ) : SaveRealtimeRunningDataParam()
}

sealed class SaveRealtimeRunningDataResult {
    data object LocalResult : SaveRealtimeRunningDataResult()

    data class SyncResult(
        val segmentId: Long, // 구간 ID
        val savedCount: Int, // 저장된 데이터 개수
    ) : SaveRealtimeRunningDataResult()
}
