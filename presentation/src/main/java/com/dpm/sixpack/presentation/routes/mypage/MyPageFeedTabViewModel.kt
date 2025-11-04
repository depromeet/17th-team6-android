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
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageFeedTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageFeedTabSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageFeedTabState
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MyPageFeedTabViewModel
@Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getMyUserFeedsUseCase: GetMyUserFeedsUseCase,
    ) : BaseViewModel<MyPageFeedTabState, MyPageFeedTabIntent, MyPageFeedTabSideEffect>() {
        override val initialState: MyPageFeedTabState = MyPageFeedTabState()

        override val container: Container<MyPageFeedTabState, MyPageFeedTabSideEffect> =
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

        override fun onIntent(intent: MyPageFeedTabIntent) {
            when (intent) {
                is MyPageFeedTabIntent.OnPostClick -> handlePostClick(intent.postId)
            }
        }

        private fun handlePostClick(postId: Long) =
            intent {
                postSideEffect(MyPageFeedTabSideEffect.NavigateToPostDetail(postId))
            }
    }
