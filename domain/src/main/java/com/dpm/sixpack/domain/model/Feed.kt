package com.dpm.sixpack.domain.model

data class FeedPage(
    val contents: List<FeedContent>,
    val meta: Meta,
)

data class FeedContent(
    val userSummary: UserSummary,
    val feeds: List<Feed>,
)

data class UserSummary(
    val name: String,
    val friendCount: Int,
    val totalDistance: Long,
    val selfieCount: Int,
    val imageUrl: String,
)

data class Feed(
    val feedId: Int,
    val date: String,
    val userName: String,
    val profileImageUrl: String,
    val selfieTime: String,
    val totalDistance: Int,
    val totalRunTime: Int,
    val averagePace: Int,
    val cadence: Int,
    val imageUrl: String,
    val reactions: List<Reaction>,
    val isMe: Boolean,
)

data class Reaction(
    val emojiType: String,
    val totalCount: Int,
    val users: List<ReactingUser>,
)

data class ReactingUser(
    val userId: Int,
    val nickname: String,
    val profileImageUrl: String,
    val reactedAt: String,
    val isMe: Boolean,
)

data class Meta(
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)
