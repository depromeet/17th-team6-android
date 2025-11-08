package com.dpm.sixpack.domain.usecase.running

import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.repository.SessionDetailRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class GetSessionDetailUseCase @Inject constructor(
    private val sessionDetailRepository: SessionDetailRepository,
) {
    suspend operator fun invoke(sessionId: Long): DoRunResult<SessionDetail> =
        sessionDetailRepository.getSessionDetail(sessionId)
}
