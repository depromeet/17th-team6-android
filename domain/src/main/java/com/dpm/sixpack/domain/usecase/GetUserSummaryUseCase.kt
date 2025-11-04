package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.UserSummary
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

/**
 * Use case for retrieving user summary information.
 *
 * Fetches the summary data for the current logged-in user,
 * including friend count, total distance, and selfie count.
 */
class GetUserSummaryUseCase
@Inject
constructor(
    private val feedRepository: FeedRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) {
    /**
     * Retrieves the user summary for the current logged-in user.
     *
     * @return DoRunResult containing UserSummary on success, or error on failure
     */
    suspend operator fun invoke(): DoRunResult<UserSummary> {
        val userId = userPreferenceRepository.getUserId()
        return feedRepository.getUserSummary(userId)
    }
}
