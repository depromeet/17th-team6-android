package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.service.FeedService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import javax.inject.Inject

class FeedDataSource @Inject constructor(
    private val feedService: FeedService,
) {
    suspend fun getFeeds(
        currentDate: String?,
        userId: Int?,
        page: Int,
        size: Int,
    ): BaseResponse<FeedPageDto> = feedService.getFeeds(
        currentDate = currentDate,
        userId = userId,
        page = page,
        size = size,
    )

    suspend fun postReaction(
        selfieId: Int,
        emojiType: String
    ): BaseResponse<ReactionResultDto> = feedService.postReaction(
        selfieId = selfieId,
        body = ReactionRequestDto(emojiType = emojiType)
    )
}
