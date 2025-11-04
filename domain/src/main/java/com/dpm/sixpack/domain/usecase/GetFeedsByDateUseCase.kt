package com.dpm.sixpack.domain.usecase

import androidx.paging.PagingData
import com.dpm.sixpack.domain.model.*
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.repository.FeedType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * 특정 날짜의 피드 리스트를 가져오는 UseCase
 *
 * - 실제 Repository에서 데이터를 가져오거나,
 * - USE_MOCK_DATA 플래그가 true면 목데이터를 반환합니다.
 */
class GetFeedsByDateUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {

    companion object {
        /** 목데이터 사용 여부 */
        private const val USE_MOCK_DATA = true
    }

    // ------------------------
    // Mock Users
    // ------------------------
    private val mockUser1 = User(
        userId = 1L,
        nickName = "러너홍길동",
        isMe = true,
        profileImgUrl = "https://example.com/profile1.jpg"
    )

    private val mockUser2 = User(
        userId = 2L,
        nickName = "마라톤킹",
        isMe = false,
        profileImgUrl = "https://example.com/profile2.jpg"
    )

    private val mockUser3 = User(
        userId = 3L,
        nickName = "스피드러너",
        isMe = false,
        profileImgUrl = "https://example.com/profile3.jpg"
    )

    // ------------------------
    // Mock Running Results
    // ------------------------
    private val mockRunningResult1 = RunningSessionResult(
        totalDistanceMeter = 5000,   // 5 km
        totalDurationSec = 1800L,     // 30분
        avgPace = 360,               // 6분/km
        avgCadence = 170,
        maxCadence = 185
    )

    private val mockRunningResult2 = RunningSessionResult(
        totalDistanceMeter = 10000,  // 10 km
        totalDurationSec = 3300L,     // 55분
        avgPace = 330,               // 5분 30초/km
        avgCadence = 175,
        maxCadence = 190
    )

    private val mockRunningResult3 = RunningSessionResult(
        totalDistanceMeter = 3000,   // 3 km
        totalDurationSec = 1200L,     // 20분
        avgPace = 400,               // 6분 40초/km
        avgCadence = 165,
        maxCadence = 180
    )

    // ------------------------
    // Mock Reactions
    // ------------------------
    private val mockReactingUser1 = ReactingUser(
        user = mockUser2,
        reactedAt = "2025-11-02T10:30:00Z"
    )

    private val mockReactingUser2 = ReactingUser(
        user = mockUser3,
        reactedAt = "2025-11-02T10:35:00Z"
    )

    private val mockReaction1 = Reaction(
        emojiType = "THUMBS_UP",
        totalCount = 2,
        isReacted = false,
        users = listOf(mockReactingUser1, mockReactingUser2)
    )

    private val mockReaction2 = Reaction(
        emojiType = "FIRE",
        totalCount = 1,
        isReacted = true,
        users = listOf(mockReactingUser1)
    )

    private val mockReaction3 = Reaction(
        emojiType = "HEART",
        totalCount = 3,
        isReacted = false,
        users = listOf(mockReactingUser1, mockReactingUser2)
    )

    // ------------------------
    // Mock Feeds
    // ------------------------
    private val mockFeed1 = Feed(
        feedId = 1L,
        date = "2025-11-02",
        user = mockUser1,
        selfieTime = "2025-11-02T11:53:07.374Z",
        imageUrl = "https://example.com/feed1.jpg",
        runningSessionResult = mockRunningResult1,
        reactions = listOf(mockReaction1, mockReaction2)
    )

    private val mockFeed2 = Feed(
        feedId = 2L,
        date = "2025-11-02",
        user = mockUser2,
        selfieTime = "2025-11-02T11:53:07.374Z",
        imageUrl = "https://example.com/feed2.jpg",
        runningSessionResult = mockRunningResult2,
        reactions = listOf(mockReaction3)
    )

    private val mockFeed3 = Feed(
        feedId = 3L,
        date = "2025-11-02",
        user = mockUser3,
        selfieTime = "2025-11-02T11:53:07.374Z",
        imageUrl = "https://example.com/feed3.jpg",
        runningSessionResult = mockRunningResult3,
        reactions = listOf(mockReaction1, mockReaction3)
    )

    // ------------------------
    // Mock FeedListItems
    // ------------------------
    private val mockFeedListItems = listOf(
        FeedListItem.PostItem(mockFeed1),
        FeedListItem.PostItem(mockFeed2),
        FeedListItem.PostItem(mockFeed3)
    )

    // ------------------------
    // UseCase 실행 함수
    // ------------------------
    operator fun invoke(currentDate: String): Flow<PagingData<FeedListItem>> {
        return if (USE_MOCK_DATA) {
            flowOf(PagingData.from(mockFeedListItems))
        } else {
            feedRepository.getFeedPagingStream(
                pageSize = 10,
                initialLoadSize = 20,
                feedType = FeedType.MAIN_FEED,
                currentDate = currentDate,
                userId = null
            )
        }
    }
}
