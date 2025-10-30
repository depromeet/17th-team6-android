package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*
    해당 날짜의 인증 피드를 pageSize만큼 반환합니다..
 */
class GetFeedsByDateUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(currentDate: String): Flow<PagingData<FeedListItem>> =
        feedRepository.getFeedPagingStream(
            pageSize = 10,
            initialLoadSize = 20,
            feedType = FeedType.MAIN_FEED,
            currentDate = currentDate,
            userId = null
        )
}

