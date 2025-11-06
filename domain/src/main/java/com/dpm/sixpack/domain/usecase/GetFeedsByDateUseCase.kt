package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 특정 날짜의 피드 리스트를 가져오는 UseCase
 *
 * - 실제 Repository에서 데이터를 가져오거나,
 * - USE_MOCK_DATA 플래그가 true면 목데이터를 반환합니다.
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
