package com.dpm.sixpack.presentation.routes.mypage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.usecase.GetMyUserFeedsUseCase
import com.dpm.sixpack.domain.usecase.GetUserSummaryUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.mypage.contract.CertificationStatus
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageState
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageTab
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import com.dpm.sixpack.presentation.routes.mypage.contract.RecordItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
@Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getMyUserFeedsUseCase: GetMyUserFeedsUseCase,
        private val getUserSummaryUseCase: GetUserSummaryUseCase,
    ) : BaseViewModel<MyPageState, MyPageIntent, MyPageSideEffect>() {
        override val initialState: MyPageState = MyPageState()

        override val container: Container<MyPageState, MyPageSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        val postsPagingFlow: Flow<PagingData<GridItemType>> =
            getMyUserFeedsUseCase()
                .map { pagingData ->
                    pagingData
                        .filter { feedListItem ->
                            // Filter out UserSummaryItem, only keep PostItem
                            feedListItem is FeedListItem.PostItem
                        }.map { feedListItem ->
                            val feed = (feedListItem as FeedListItem.PostItem).feed
                            Post(
                                id = feed.feedId,
                                imageUrl = feed.imageUrl,
                                createdAt = feed.date,
                            )
                        }
                }.map { pagingData ->
                    pagingData
                        .map<Post, GridItemType> { post ->
                            GridItemType.PostItem(post)
                        }.insertSeparators { before, after ->
                            // Insert MonthLabel when month changes
                            val beforeYearMonth = before?.let { getYearMonthFromPost(it) }
                            val afterYearMonth = after?.let { getYearMonthFromPost(it) }

                            if (after != null && afterYearMonth != null && beforeYearMonth != afterYearMonth) {
                                GridItemType.MonthLabel(
                                    year = afterYearMonth.first,
                                    month = afterYearMonth.second,
                                )
                            } else {
                                null
                            }
                        }
                }.cachedIn(viewModelScope)

        private fun getYearMonthFromPost(gridItem: GridItemType): Pair<Int, Int>? =
            when (gridItem) {
                is GridItemType.PostItem -> {
                    try {
                        val dateTime =
                            LocalDateTime.parse(
                                gridItem.post.createdAt,
                                DateTimeFormatter.ISO_DATE_TIME,
                            )
                        Pair(dateTime.year, dateTime.monthValue)
                    } catch (_: Exception) {
                        null
                    }
                }
                is GridItemType.MonthLabel -> null
            }

        init {
            loadUserProfile()
            loadMockData()
        }

        private fun loadUserProfile() {
            viewModelScope.launch {
                when (val result = getUserSummaryUseCase()) {
                    is DoRunResult.Success -> {
                        val userSummary = result.data
                        intent {
                            reduce {
                                state.copy(
                                    profileInfo =
                                        ProfileInfo(
                                            nickname = userSummary.name,
                                            friendCount = userSummary.friendCount,
                                            totalDistanceKm = userSummary.totalDistance / 1000.0,
                                            certificationCount = userSummary.selfieCount,
                                        ),
                                )
                            }
                        }
                    }
                    is DoRunResult.Failure -> {
                        // Handle error - keep default ProfileInfo
                        // Could show error message via SideEffect if needed
                    }
                }
            }
        }

        override fun onIntent(intent: MyPageIntent) {
            when (intent) {
                is MyPageIntent.OnTabClick -> handleTabClick(intent.tab)
                is MyPageIntent.OnPreviousMonthClick -> handlePreviousMonthClick()
                is MyPageIntent.OnNextMonthClick -> handleNextMonthClick()
                is MyPageIntent.OnRecordClick -> handleRecordClick(intent.recordId)
                is MyPageIntent.OnSettingClick -> handleSettingClick()
            }
        }

        private fun handleTabClick(tab: MyPageTab) =
            intent {
                reduce {
                    state.copy(selectedTab = tab)
                }
            }

        private fun handlePreviousMonthClick() =
            intent {
                reduce {
                    state.copy(
                        currentYearMonth = state.currentYearMonth.addMonths(-1),
                    )
                }
            }

        private fun handleNextMonthClick() =
            intent {
                reduce {
                    state.copy(
                        currentYearMonth = state.currentYearMonth.addMonths(1),
                    )
                }
            }

        private fun handleRecordClick(recordId: Long) =
            intent {
                postSideEffect(MyPageSideEffect.NavigateToRecordDetail(recordId))
            }

        private fun handleSettingClick() =
            intent {
                postSideEffect(MyPageSideEffect.NavigateToSettings)
            }

        private fun loadMockData() {
            viewModelScope.launch {
                // TODO: Replace with real data from repository
                val mockProfileInfo =
                    ProfileInfo(
                        nickname = "두런두런",
                        friendCount = 7,
                        totalDistanceKm = 400.0,
                        certificationCount = 120,
                    )

                val mockRecords =
                    listOf(
                        RecordItem(
                            id = 1,
                            date = "2025.09.30 (화)",
                            time = "오전 10:11",
                            distanceKm = 8.02,
                            durationFormatted = "01:12:03",
                            paceFormatted = "6'74\"",
                            cadence = 128,
                            certificationStatus = null,
                        ),
                        RecordItem(
                            id = 2,
                            date = "2025.09.29 (월)",
                            time = "오전 10:11",
                            distanceKm = 8.02,
                            durationFormatted = "01:12:03",
                            paceFormatted = "6'74\"",
                            cadence = 128,
                            certificationStatus = CertificationStatus.AVAILABLE,
                        ),
                        RecordItem(
                            id = 3,
                            date = "2025.09.27 (토)",
                            time = "오전 10:11",
                            distanceKm = 8.02,
                            durationFormatted = "01:12:03",
                            paceFormatted = "6'74\"",
                            cadence = 128,
                            certificationStatus = CertificationStatus.COMPLETED,
                        ),
                    )

                intent {
                    reduce {
                        state.copy(
                            profileInfo = mockProfileInfo,
                            records = mockRecords,
                        )
                    }
                }
            }
        }
    }
