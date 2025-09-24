package com.dpm.sixpack.presentation.routes.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.home.component.session.edit.HomeGoalEditComponent
import com.dpm.sixpack.presentation.routes.home.component.session.next.HomeNextSessionComponent
import com.dpm.sixpack.presentation.routes.home.component.session.previous.HomePreviousSessionComponent
import com.dpm.sixpack.presentation.routes.home.component.total.HomeTotalGoalComponent
import com.dpm.sixpack.presentation.routes.home.contract.HomeScreenState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeScreenState,
    onClickPreviousSession: () -> Unit,
    onClickNextSession: () -> Unit,
    onNavigateToGoalList: () -> Unit,
    onNavigateToGoalEdit: () -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier,
        containerColor = SixpackTheme.colors.gray0,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = SixpackTheme.colors.gray0)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = SixpackTheme.colors.gray50)
                .fillMaxSize()
                .padding(paddingValues)
                .scrollable(
                    state = scrollState,
                    orientation = Orientation.Vertical
                )
        ) {
            HomeTotalGoalComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SixpackTheme.colors.gray0),
                state = uiState.totalGoalComponentState,
                onNavigateToGoalList = onNavigateToGoalList
            )

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 56.dp)
                    .padding(horizontal = 20.dp)
            ) {
                if (uiState.totalGoalCompleted.not()) {
                    HomeNextSessionComponent(
                        modifier = Modifier
                            .fillMaxWidth(),
                        state = uiState.sessionComponentState,
                        onClick = onClickNextSession
                    )

                    if (uiState.sessionComponentState.showPreviousSession) {
                        HomePreviousSessionComponent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            onClick = onClickPreviousSession
                        )
                    }
                } else {
                    HomeGoalEditComponent(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = onNavigateToGoalEdit
                    )
                }
            }
        }

    }
}
