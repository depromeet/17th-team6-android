package com.dpm.sixpack.domain.usecase

import android.graphics.Bitmap
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class FinishRunningSessionUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
    private val userPreferenceRepository: UserRepository,
) {
    suspend operator fun invoke(mapImage: Bitmap): DoRunResult<Long> {
        val sessionId =
            userPreferenceRepository.getSessionId()
                ?: return DoRunResult.Failure(DoRunException.DataError("저장된 세션 ID가 없어 종료할 수 없습니다."))

        val result = runningSessionRepository.finishSession(sessionId, mapImage)
        // TODO SK: 성공시에만 로컬 세션ID 지워야하는지?
        userPreferenceRepository.clearSessionId()
        return result
    }
}
