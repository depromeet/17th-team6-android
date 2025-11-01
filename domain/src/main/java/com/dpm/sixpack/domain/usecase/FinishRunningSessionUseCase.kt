package com.dpm.sixpack.domain.usecase

import android.graphics.Bitmap
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class FinishRunningSessionUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(mapImage: Bitmap): DoRunResult<RunningSessionResult> {
        val sessionId =
            userPreferenceRepository.getSessionId()
                ?: return DoRunResult.Failure(DoRunException.DataError("저장된 세션 ID가 없어 종료할 수 없습니다."))

        userPreferenceRepository.clearSessionId()

        val result = runningSessionRepository.finishSession(sessionId, mapImage)
        return result
    }
}
