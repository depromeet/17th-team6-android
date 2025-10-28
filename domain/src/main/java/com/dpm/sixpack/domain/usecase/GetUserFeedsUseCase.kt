package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.FeedRepository
import javax.inject.Inject

/*
    특정 유저의 피드를 pageSize 만큼 반환합니다.
 */
class GetUserFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(
        userId: Int,
        pageNum: Int,
        pageSize: Int
    ) = feedRepository.getFeeds(
        currentDate = null,
        userId = userId,
        pageNum = pageNum,
        pageSize = pageSize
    )
}
