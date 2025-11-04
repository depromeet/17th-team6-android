package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.Feed
import com.dpm.sixpack.domain.model.FeedContent
import com.dpm.sixpack.domain.model.FeedPage
import com.dpm.sixpack.domain.model.Meta
import com.dpm.sixpack.domain.model.ReactingUser
import com.dpm.sixpack.domain.model.Reaction
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.model.User
import com.dpm.sixpack.domain.model.UserSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedPageDto(
    @SerialName("contents")
    val contents: FeedContentDto,
    @SerialName("meta")
    val meta: MetaDto,
) {
    fun toDomain(): FeedPage =
        FeedPage(
            contents = contents.toDomain(),
            meta = meta.toDomain(),
        )
}

@Serializable
data class FeedContentDto(
    @SerialName("userSummary")
    val userSummary: UserSummaryDto,
    @SerialName("feeds")
    val feeds: List<FeedDto>,
) {
    fun toDomain(): FeedContent =
        FeedContent(
            userSummary = userSummary.toDomain(),
            feeds = feeds.map { it.toDomain() },
        )
}

@Serializable
data class UserSummaryDto(
    @SerialName("name")
    val name: String,
    @SerialName("friendCount")
    val friendCount: Int,
    @SerialName("totalDistance")
    val totalDistance: Long,
    @SerialName("selfieCount")
    val selfieCount: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
) {
    fun toDomain(): UserSummary =
        UserSummary(
            name = name,
            friendCount = friendCount,
            totalDistance = totalDistance,
            selfieCount = selfieCount,
            imageUrl = imageUrl,
        )
}

@Serializable
data class FeedDto(
    @SerialName("feedId")
    val feedId: Long,
    @SerialName("date")
    val date: String,
    @SerialName("userName")
    val userName: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String,
    @SerialName("selfieTime")
    val selfieTime: String,
    @SerialName("totalDistance")
    val totalDistance: Long,
    @SerialName("totalRunTime")
    val totalRunTime: Long,
    @SerialName("averagePace")
    val averagePace: Long,
    @SerialName("cadence")
    val cadence: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("reactions")
    val reactions: List<ReactionDto>,
    @SerialName("isMe")
    val isMe: Boolean,
) {
    fun toDomain(): Feed =
        Feed(
            feedId = feedId,
            date = date,
            user =
                User(
                    nickName = userName,
                    profileImgUrl = profileImageUrl,
                    isMe = isMe,
                ),
            selfieTime = selfieTime,
            runningSessionResult =
                RunningSessionResult(
                    totalDistanceMeter = totalDistance.toInt(),
                    totalDurationSec = totalRunTime,
                    avgPace = averagePace.toInt(),
                    avgCadence = cadence,
                ),
            imageUrl = imageUrl,
            reactions = reactions.map { it.toDomain() },
        )
}

// TODO 서버값으로 바꾸기
@Serializable
data class ReactionDto(
    @SerialName("emojiType")
    val emojiType: String,
    @SerialName("totalCount")
    val totalCount: Int,
    @SerialName("users")
    val users: List<ReactingUserDto>,
    @SerialName("isReacted")
    val isReacted: Boolean = false,
) {
    fun toDomain(): Reaction =
        Reaction(
            emojiType = emojiType,
            totalCount = totalCount,
            users = users.map { it.toDomain() },
            isReacted = isReacted,
        )
}

@Serializable
data class ReactingUserDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String,
    @SerialName("reactedAt")
    val reactedAt: String,
    @SerialName("isMe")
    val isMe: Boolean = false,
) {
    fun toDomain(): ReactingUser =
        ReactingUser(
            User(
                userId = userId,
                nickName = nickname,
                profileImgUrl = profileImageUrl,
                isMe = isMe,
            ),
            reactedAt = reactedAt,
        )
}

@Serializable
data class MetaDto(
    @SerialName("page")
    val page: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("totalElements")
    val totalElements: Long,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("hasNext")
    val hasNext: Boolean,
    @SerialName("hasPrevious")
    val hasPrevious: Boolean,
) {
    fun toDomain(): Meta =
        Meta(
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            first = first,
            last = last,
            hasNext = hasNext,
            hasPrevious = hasPrevious,
        )
}
