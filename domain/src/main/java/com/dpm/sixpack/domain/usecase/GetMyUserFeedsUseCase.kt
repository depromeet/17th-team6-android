package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case for retrieving the current user's feed.
 *
 * Fetches paginated feed data for the logged-in user from the FeedRepository.
 * The user ID is obtained from UserPreferenceRepository.
 */
class GetMyUserFeedsUseCase
    @Inject
    constructor(
        private val feedRepository: FeedRepository,
        private val userPreferenceRepository: UserPreferenceRepository,
    ) {
        /**
         * Retrieves a paginated flow of feed items for the current user.
         *
         * @return Flow emitting PagingData containing FeedListItem (UserSummaryItem or PostItem)
         */
        operator fun invoke(): Flow<PagingData<FeedListItem>> =
            userPreferenceRepository.getUserIdFlow().flatMapLatest { userId ->
                feedRepository.getFeedPagingStream(
                    pageSize = 10,
                    initialLoadSize = 20,
                    feedType = FeedType.USER_PAGE_FEED,
                    currentDate = null,
                    userId = userId,
                )
            }
    }
