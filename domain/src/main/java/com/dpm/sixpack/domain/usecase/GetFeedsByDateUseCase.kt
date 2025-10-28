package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.FeedRepository
import javax.inject.Inject

/*
    해당 날짜의 인증 피드를 pageSize만큼 반환합니다..
 */
class GetFeedsByDateUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(
        currentDate: String,
        pageNum: Int,
        pageSize: Int
    ) {
        feedRepository.getFeeds(
            currentDate = currentDate,
            userId = null,
            pageNum = pageNum,
            pageSize = pageSize
        )
    }
}
