package com.dpm.sixpack.presentation.routes.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.session.component.MapConstants
import com.dpm.sixpack.presentation.routes.session.component.ScreenSelectTab
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.ui.RunningSessionSubRoute
import com.dpm.sixpack.presentation.theme.SixpackTheme
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionRoute(
    modifier: Modifier = Modifier,
    viewModel: RunningSessionViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit = { },
    navigateToSessionReport: () -> Unit = { },
) {
    val tabItems = RunningScreenTabItem.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabItems.size })

    val uiState by viewModel.collectAsState()

    // Location
    val locationSource = rememberFusedLocationSource()

    // State
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    viewModel.collectSideEffect { sideEffect ->
        // Collect
        when (sideEffect) {
            is RunningSessionSideEffect.NavigateBackToHome -> {
                onNavigateToBack()
            }

            is RunningSessionSideEffect.NavigateToReport -> {
                navigateToSessionReport()
            }

            is RunningSessionSideEffect.ChangeTab -> {
                when (sideEffect.tab) {
                    RunningScreenTabItem.GOAL -> {
                        pagerState.animateScrollToPage(0)
                    }

                    RunningScreenTabItem.MAP -> {
                        pagerState.animateScrollToPage(1)
                    }
                }
            }

            is RunningSessionSideEffect.SetLocation -> {
                cameraPositionState.move(CameraUpdate.scrollTo(sideEffect.latLng))
            }
        }
    }

    if (uiState.sessionState !is RunningSessionState.Initial) {
        BackHandler {
            // 뒤로가기 못하게
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = SixpackTheme.colors.gray0,
        topBar = {
            if (uiState.sessionState is RunningSessionState.Initial) {
                CenterAlignedTopAppBar(
                    title = {
                        ScreenSelectTab(
                            modifier =
                            Modifier,
                            tabItems = tabItems,
                            selectedIndex = pagerState.currentPage,
                            onSelectionChange = { tab ->
                                viewModel.onIntent(RunningSessionIntent.TabChange(tab))
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onIntent(RunningSessionIntent.ClickBackIcon)
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_left),
                                contentDescription = null,
                                tint = SixpackTheme.colors.gray900,
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults
                            .topAppBarColors()
                            .copy(containerColor = Color.Transparent),
                    actions = {
                        IconButton(
                            modifier = Modifier.alpha(0f),
                            onClick = {
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_left),
                                contentDescription = null,
                                tint = SixpackTheme.colors.gray900,
                            )
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = pagerState.currentPage != 1,
            ) { page ->

                when (page) {
                    0 -> {
                        (uiState.sessionState as? RunningSessionState.Initial)?.let { sessionState ->
                        }
                    }

                    1 -> {
                        // do nothing
                    }
                }
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        // 목표 탭에서 지도 터치되는것 막기 위함
                        .pointerInput(pagerState.currentPage) {
                            if (pagerState.currentPage != 1) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        event.changes.forEach { it.consume() }
                                    }
                                }
                            }
                        },
            ) {
                RunningSessionSubRoute(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = if (pagerState.currentPage == 1) 1f else 0f
                            },
                    viewModel = viewModel,
                    cameraPositionState = cameraPositionState,
                    locationSource = locationSource,
                )
            }

            if (uiState.sessionState is RunningSessionState.Initial) {
                InitialContent(
                    onStartClick = {
                        if (pagerState.currentPage == 0) {
                            // 목표 -> 지도 이동 후 러닝 시작
                            viewModel.onIntent(RunningSessionIntent.TabChange(RunningScreenTabItem.MAP))
                            viewModel.onIntent(RunningSessionIntent.SessionStart)
                        } else {
                            viewModel.onIntent(RunningSessionIntent.SessionStart)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun InitialContent(onStartClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        DoRunDefaultButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            onClick = {
                Timber.d("Running Session Start Clicked")
                onStartClick()
            },
            text = stringResource(id = R.string.session_start_running_button),
        )
    }
}
