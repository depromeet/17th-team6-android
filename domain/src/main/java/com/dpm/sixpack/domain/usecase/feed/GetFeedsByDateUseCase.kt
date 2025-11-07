package com.dpm.sixpack.domain.usecase.feed

import androidx.paging.PagingData
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 특정 날짜의 피드 리스트를 가져오는 UseCase
 */
class GetFeedsByDateUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    operator fun invoke(currentDate: String): Flow<PagingData<FeedListItem>> =
        feedRepository.getFeedPagingStream(
            pageSize = 10,
            initialLoadSize = 20,
            feedType = FeedType.MAIN_FEED,
            currentDate = currentDate,
            userId = null,
        )
}
