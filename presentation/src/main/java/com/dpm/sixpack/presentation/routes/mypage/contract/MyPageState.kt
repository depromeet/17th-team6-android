package com.dpm.sixpack.presentation.routes.mypage.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPageState(
    val selectedTab: MyPageTab = MyPageTab.CERTIFICATION,
    val profileInfo: ProfileInfo = ProfileInfo(),
    val posts: List<Post> = emptyList(),
    val records: List<RecordItem> = emptyList(),
    val currentYearMonth: YearMonth = YearMonth(),
    val isLoading: Boolean = false,
) : UiState,
    Parcelable

@Parcelize
data class ProfileInfo(
    val nickname: String = "",
    val friendCount: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val certificationCount: Int = 0,
) : Parcelable

@Parcelize
data class Post(
    val id: Long,
    val imageUrl: String? = null,
    val createdAt: String, // ISO 8601 format: "2025-10-14T10:30:00Z"
) : Parcelable

@Parcelize
data class RecordItem(
    val id: Long,
    val date: String, // "2025.09.30 (화)"
    val time: String, // "오전 10:11"
    val distanceKm: Double, // 8.02
    val durationFormatted: String, // "01:12:03"
    val paceFormatted: String, // "6'74''"
    val cadence: Int, // 128
    val certificationStatus: CertificationStatus? = null,
) : Parcelable

enum class CertificationStatus {
    AVAILABLE, // 인증 가능
    COMPLETED, // 인증 완료
}

@Parcelize
data class YearMonth(
    val year: Int = 2025,
    val month: Int = 9,
) : Parcelable {
    fun format(): String = "${year}년 ${month}월"

    fun addMonths(offset: Int): YearMonth {
        val totalMonths = year * 12 + month - 1 + offset
        return YearMonth(
            year = totalMonths / 12,
            month = totalMonths % 12 + 1,
        )
    }
}

enum class MyPageTab {
    CERTIFICATION,
    RECORD,
}

sealed interface GridItemType : Parcelable {
    @Parcelize
    data class MonthLabel(
        val year: Int,
        val month: Int,
    ) : GridItemType

    @Parcelize
    data class PostItem(
        val post: Post,
    ) : GridItemType
}
