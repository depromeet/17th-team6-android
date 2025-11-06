package com.dpm.sixpack.domain.model

data class UserSummary(
    val name: String,
    val imageUrl: String,
    val friendCount: Int,
    val totalDistance: Long,
    val selfieCount: Int,
)

data class Feed(
    val feedId: Long, // feedId
    val date: String,
    val user: User, // user 정보
    val selfieTime: String, // 피드 인증 시간
    val imageUrl: String, // 피드 이미지
    val runningSessionResult: RunningSessionResult, // 러닝 기록
    val reactions: List<Reaction>,
)

data class Reaction(
    val emojiType: String,
    val totalCount: Int,
    val isReacted: Boolean,
    val users: List<ReactingUser>,
)

data class ReactingUser(
    val user: User,
    val reactedAt: String, // 이모티콘 남긴 시간
)

data class FeedPage(
    val contents: FeedContent,
    val meta: Meta,
)

data class FeedContent(
    val userSummary: UserSummary?,
    val feeds: List<Feed>,
)

data class Meta(
    val page: Int, // 현재 페이지 번호
    val size: Int, // 한 페이지에 보여줄 데이터의 수
    val totalElements: Long, // 전체 데이터의 총 개수
    val totalPages: Int, // 전체 페이지 수
    val first: Boolean, // 현재 페이지가 첫 번째 페이지인지 여부
    val last: Boolean, // 현재 페이지가 마지막 페이지인지 여부
    val hasNext: Boolean, // 다음 페이지가 있는지 여부
    val hasPrevious: Boolean, // 이전 페이지가 있는지 여부
)
