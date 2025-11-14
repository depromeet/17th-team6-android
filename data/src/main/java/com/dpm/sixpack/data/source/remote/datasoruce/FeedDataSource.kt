package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.CertifiedUsersDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.dto.response.SelfieCountsDto
import com.dpm.sixpack.data.source.remote.dto.response.UploadableResponseDto
import com.dpm.sixpack.data.source.remote.service.FeedService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class FeedDataSource @Inject constructor(
    private val feedService: FeedService,
) {
    suspend fun getFeeds(
        currentDate: String?,
        userId: Long?,
        page: Int,
        size: Int,
    ): BaseResponse<FeedPageDto> =
        feedService.getFeeds(
            currentDate = currentDate,
            userId = userId,
            page = page,
            size = size,
        )

    suspend fun getPostDetail(feedId: Long): BaseResponse<FeedDto> =
        feedService.getPostDetail(
            feedId = feedId,
        )

    suspend fun uploadPost(
        data: RequestBody,
        selfieImage: MultipartBody.Part?,
    ): BaseResponse<Unit> =
        feedService.uploadPost(
            data = data,
            selfieImage = selfieImage,
        )

    suspend fun postReaction(
        feedId: Long,
        emojiType: String,
    ): BaseResponse<ReactionResultDto> =
        feedService.postReaction(
            body =
                ReactionRequestDto(
                    feedId = feedId,
                    emojiType = emojiType,
                ),
        )

    suspend fun deletePost(feedId: Long): BaseResponse<Unit> =
        feedService.deletePost(
            feedId = feedId,
        )

    suspend fun getCertifiedUsers(date: String): BaseResponse<CertifiedUsersDto> =
        feedService.getCertifiedUsers(
            date = date,
        )

    suspend fun getWeeklyPostCount(
        startDate: String,
        endDate: String,
    ): BaseResponse<SelfieCountsDto> =
        feedService.getWeeklyPostCount(
            startDate = startDate,
            endDate = endDate,
        )

    suspend fun updatePost(
        feedId: Long,
        data: RequestBody,
        selfieImage: MultipartBody.Part?,
    ): BaseResponse<Unit> =
        feedService.updatePost(
            feedId = feedId,
            data = data,
            selfieImage = selfieImage,
        )

    suspend fun getUploadable(sessionId: Long): BaseResponse<UploadableResponseDto> =
        feedService.getUploadable(
            sessionId = sessionId,
        )
}
