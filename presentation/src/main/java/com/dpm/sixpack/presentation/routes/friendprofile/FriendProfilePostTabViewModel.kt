package com.dpm.sixpack.presentation.routes.friendprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.dpm.sixpack.core.util.TimeUtil
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.usecase.GetMyUserFeedsUseCase
import com.dpm.sixpack.presentation.destinations.FriendProfile
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabState
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FriendProfilePostTabViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getMyUserFeedsUseCase: GetMyUserFeedsUseCase,
    ) : BaseViewModel<MyPagePostTabState, MyPagePostTabIntent, MyPagePostTabSideEffect>() {
        override val initialState: MyPagePostTabState = MyPagePostTabState()

        override val container: Container<MyPagePostTabState, MyPagePostTabSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        private val userId: Long = savedStateHandle.toRoute<FriendProfile>().friendId

        val postsPagingFlow: Flow<PagingData<GridItemType>> =
            getMyUserFeedsUseCase(userId)
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
                            // 첫 번째 아이템이거나 월이 바뀔 때 MonthLabel 삽입
                            val beforeYearMonth = before?.let { getYearMonthFromPost(it) }
                            val afterYearMonth = after?.let { getYearMonthFromPost(it) }

                            if (after != null && afterYearMonth != null && (before == null || beforeYearMonth != afterYearMonth)) {
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
                    // TimeUtil의 공통 날짜 파싱 함수 사용
                    TimeUtil.parseToLocalDateTime(gridItem.post.createdAt)?.let { dateTime ->
                        Pair(dateTime.year, dateTime.monthValue)
                    }
                }
                is GridItemType.MonthLabel -> null
            }

        override fun onIntent(intent: MyPagePostTabIntent) {
            when (intent) {
                is MyPagePostTabIntent.OnPostClick -> handlePostClick(intent.postId)
                is MyPagePostTabIntent.OnRetryClick -> {
                    // Paging handles retry through LazyPagingItems.retry() in UI
                    // No action needed in ViewModel
                }
            }
        }

        private fun handlePostClick(postId: Long) =
            intent {
                postSideEffect(MyPagePostTabSideEffect.NavigateToPostDetail(postId))
            }
    }
