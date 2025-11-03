package com.dpm.sixpack.presentation.routes.feed.certifiedusers.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.toTimeAgoString
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract.CertifiedUsersIntent
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract.CertifiedUsersUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun CertifiedUsersScreen(
    state: CertifiedUsersUiState,
    onIntent: (CertifiedUsersIntent) -> Unit,
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        DoRunNavigationTopBar(
            navigateToBack = { onIntent(CertifiedUsersIntent.OnBackIconClick) },
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = SixpackTheme.colors.blue600,
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
            ) {
                Text(
                    text =
                        stringResource(
                            id = R.string.certified_users_title,
                        ),
                    style = SixpackTheme.typography.t2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = state.users.size.toString(),
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray500,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(
                    items = state.users,
                    key = { it.user.id },
                ) { userInfo ->
                    CertifiedUserItem(
                        name = userInfo.user.name,
                        profileImgUrl = userInfo.user.profileImageUrl,
                        isMe = userInfo.user.isMe,
                        postingTime = userInfo.postingTime.toTimeAgoString(context),
                        onUserClick = {
                            onIntent(
                                CertifiedUsersIntent.OnUserProfileClick(
                                    userId = userInfo.user.id,
                                    isMe = userInfo.user.isMe,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CertifiedUsersScreenPreview() {
    DoRunPreviewWrapper {
        CertifiedUsersScreen(
            state = CertifiedUsersUiState(),
            onIntent = {},
        )
    }
}
