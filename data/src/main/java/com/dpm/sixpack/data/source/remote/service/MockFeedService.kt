package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.ReactionRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.CertifiedUsersDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedPageDto
import com.dpm.sixpack.data.source.remote.dto.response.FeedsWrapperDto
import com.dpm.sixpack.data.source.remote.dto.response.MetaDto
import com.dpm.sixpack.data.source.remote.dto.response.ReactionResultDto
import com.dpm.sixpack.data.source.remote.dto.response.SelfieCountsDto
import com.dpm.sixpack.data.source.remote.dto.response.UserSummaryDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Mock implementation of FeedService for testing and development.
 * Returns mock feed data with realistic pagination.
 */
class MockFeedService @Inject constructor() : FeedService {
    private fun getCurrentTimestamp(): String =
        LocalDateTime
            .now()
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    // Track offset for consistent pagination across varying page sizes
    private val offsetMap = mutableMapOf<String, Int>()

    private fun generateMockFeeds(
        page: Int,
        size: Int,
        userId: Long?,
    ): List<FeedDto> {
        // Calculate offset based on accumulated sizes for this user
        val key = "user_$userId"
        val startIndex =
            if (page == 0) {
                offsetMap[key] = 0
                0
            } else {
                offsetMap.getOrDefault(key, 0)
            }

        // Update offset for next call
        offsetMap[key] = startIndex + size

        return (startIndex until startIndex + size).map { index ->
            val monthOffset = index / 10
            val dayOffset = index % 30 + 1
            val createdAt =
                LocalDateTime
                    .now()
                    .minusMonths(monthOffset.toLong())
                    .minusDays(dayOffset.toLong())
                    .format(DateTimeFormatter.ISO_DATE_TIME)

            FeedDto(
                feedId = (index + 1).toLong(),
                date = createdAt,
                userName = "Runner${index % 5 + 1}",
                userId = 0,
                profileImageUrl = "https://picsum.photos/200?random=$index",
                selfieTime = createdAt,
                totalDistance = (5000L..15000L).random(),
                totalRunTime = (1800L..3600L).random(),
                averagePace = (300L..400L).random(),
                cadence = (160..180).random(),
                imageUrl = "https://picsum.photos/400/600?random=${index + 100}",
                reactions = emptyList(),
                isMyFeed = index % 5 == 0,
            )
        }
    }

    override suspend fun getFeeds(
        currentDate: String?,
        userId: Long?,
        page: Int,
        size: Int,
    ): BaseResponse<FeedPageDto> {
        delay(500L) // Simulate network delay

        // Reset offset on page 0 (refresh)
        if (page == 0) {
            offsetMap["user_$userId"] = 0
        }

        val totalElements = 100L
        val totalPages = (totalElements / 10).toInt() // Use fixed pageSize for calculation
        val currentOffset = offsetMap.getOrDefault("user_$userId", 0)
        val hasNext = currentOffset + size < totalElements

        val feedPage =
            FeedPageDto(
                userSummary =
                    UserSummaryDto(
                        name = "두런두런",
                        friendCount = 12,
                        totalDistance = 42195,
                        selfieCount = 35,
                        profileImageUrl = "https://picsum.photos/200",
                    ),
                feeds =
                    FeedsWrapperDto(
                        contents = generateMockFeeds(page, size, userId),
                        meta =
                            MetaDto(
                                page = page,
                                size = size,
                                totalElements = totalElements,
                                totalPages = totalPages,
                                first = page == 0,
                                last = !hasNext,
                                hasNext = hasNext,
                                hasPrevious = page > 0,
                            ),
                    ),
            )

        return BaseResponse(
            status = "200",
            message = "피드 조회 성공",
            timestamp = getCurrentTimestamp(),
            data = feedPage,
        )
    }

    override suspend fun deletePost(feedId: Long): BaseResponse<Unit> {
        delay(300L)
        return BaseResponse(
            status = "200",
            message = "피드 삭제 성공",
            timestamp = getCurrentTimestamp(),
            data = Unit,
        )
    }

    override suspend fun postReaction(body: ReactionRequestDto): BaseResponse<ReactionResultDto> {
        delay(300L)
        return BaseResponse(
            status = "200",
            message = "리액션 추가 성공",
            timestamp = getCurrentTimestamp(),
            data =
                ReactionResultDto(
                    selfieId = body.feedId.toInt(),
                    emojiType = body.emojiType,
                    action = "ADDED",
                    totalReactionCount = 1,
                ),
        )
    }

    override suspend fun getCertifiedUsers(date: String): BaseResponse<CertifiedUsersDto> {
        delay(300L)
        return BaseResponse(
            status = "200",
            message = "인증 유저 목록 조회 성공",
            timestamp = getCurrentTimestamp(),
            data = CertifiedUsersDto(users = emptyList()),
        )
    }

    override suspend fun getWeeklyPostCount(
        startDate: String,
        endDate: String,
    ): BaseResponse<SelfieCountsDto> {
        delay(300L)
        return BaseResponse(
            status = "200",
            message = "주간 인증 수 조회 성공",
            timestamp = getCurrentTimestamp(),
            data = SelfieCountsDto(countList = emptyList()),
        )
    }

    override suspend fun getPostDetail(feedId: Long): BaseResponse<FeedDto> {
        delay(300L)
        val mockFeed =
            FeedDto(
                feedId = feedId,
                date = getCurrentTimestamp(),
                userName = "두런두런",
                userId = 0,
                profileImageUrl = "https://picsum.photos/200",
                selfieTime = getCurrentTimestamp(),
                totalDistance = 10000L,
                totalRunTime = 3600L,
                averagePace = 360L,
                cadence = 170,
                imageUrl = "https://picsum.photos/400/600",
                reactions = emptyList(),
                isMyFeed = true,
            )
        return BaseResponse(
            status = "200",
            message = "피드 상세 조회 성공",
            timestamp = getCurrentTimestamp(),
            data = mockFeed,
        )
    }

    override suspend fun uploadPost(
        data: RequestBody,
        selfieImage: MultipartBody.Part?,
    ): BaseResponse<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePost(
        feedId: Long,
        data: RequestBody,
        selfieImage: MultipartBody.Part?,
    ): BaseResponse<Unit> {
        delay(300L)
        return BaseResponse(
            status = "200",
            message = "피드 수정 성공",
            timestamp = getCurrentTimestamp(),
            data = Unit,
        )
    }
}
