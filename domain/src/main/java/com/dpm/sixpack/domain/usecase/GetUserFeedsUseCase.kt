package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*
    특정 유저의 피드를 pageSize 만큼 반환합니다.
 */
class GetUserFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    operator fun invoke(userId: Long): Flow<PagingData<FeedListItem>> =
        feedRepository.getFeedPagingStream(
            pageSize = 10,
            initialLoadSize = 20,
            feedType = FeedType.USER_PAGE_FEED,
            currentDate = null,
            userId = userId,
        )
}
