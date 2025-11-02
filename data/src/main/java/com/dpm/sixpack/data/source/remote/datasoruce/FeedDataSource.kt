package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.CertifiedUsersDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.dto.response.SelfieCountsDto
import com.dpm.sixpack.data.source.remote.service.FeedService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import javax.inject.Inject

class FeedDataSource @Inject constructor(
    private val feedService: FeedService,
) {
    suspend fun getFeeds(
        currentDate: String?,
        userId: Long?,
        page: Int,
        size: Int,
    ): BaseResponse<FeedPageDto> = feedService.getFeeds(
        currentDate = currentDate,
        userId = userId,
        page = page,
        size = size,
    )

    suspend fun postReaction(
        feedId: Long,
        emojiType: String
    ): BaseResponse<ReactionResultDto> = feedService.postReaction(
        body = ReactionRequestDto(
            feedId = feedId,
            emojiType = emojiType
        )
    )

    suspend fun deleteFeed(
        feedId: Long
    ): BaseResponse<Unit> = feedService.deleteFeed(
        feedId = feedId
    )

    suspend fun getCertifiedUsers(
        date: String
    ): BaseResponse<CertifiedUsersDto> = feedService.getCertifiedUsers(
        date = date
    )

    suspend fun getSelfieWeek(
        startDate: String,
        endDate: String
    ): BaseResponse<SelfieCountsDto> = feedService.getSelfieWeek(
        startDate = startDate,
        endDate = endDate
    )
}
