package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.model.FeedContent
import com.dpm.sixpack.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*
    특정 유저의 피드를 pageSize 만큼 반환합니다.
 */
class GetUserFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(userId: Long): Flow<PagingData<FeedContent>> =
    feedRepository.getFeedPagingStream(
    pageSize = 10,
    initialLoadSize = 20,
    currentDate = null,
    userId = userId
    )
}
