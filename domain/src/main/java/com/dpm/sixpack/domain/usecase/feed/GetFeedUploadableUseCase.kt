package com.dpm.sixpack.domain.usecase.feed

import com.dpm.sixpack.domain.model.Uploadable
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class GetFeedUploadableUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    suspend operator fun invoke(sessionId: Long): DoRunResult<Uploadable> = feedRepository.getUploadable(sessionId)
}
