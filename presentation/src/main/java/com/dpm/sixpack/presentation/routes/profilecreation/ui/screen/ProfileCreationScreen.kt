package com.dpm.sixpack.presentation.routes.profilecreation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationIntent
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationState
import com.dpm.sixpack.presentation.routes.signup.ui.component.profile.ProfileCreationComponent
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun ProfileCreationScreen(
    state: ProfileCreationState,
    onIntent: (ProfileCreationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                modifier = Modifier.padding(start = 10.dp),
                navigateToBack = { onIntent(ProfileCreationIntent.OnBackButtonClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = SixPackDimen.defaultSideMargin)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = stringResource(R.string.signup_title_profile_creation),
                    style = SixpackTheme.typography.h2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Profile Creation Component
                ProfileCreationComponent(
                    profileName = state.profileName,
                    profileImageUri = state.profileImageUri,
                    onNameChanged = { onIntent(ProfileCreationIntent.OnProfileNameChanged(it)) },
                    onImagePickerClick = {
                        // TODO: Implement image picker
                        // For now, this will be handled by the ViewModel with a file picker intent
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                // Error Message
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage,
                        style = SixpackTheme.typography.c1Regular,
                        color = SixpackTheme.colors.red,
                    )
                }
            }

            // Bottom Button
            DoRunDefaultButton(
                text = stringResource(R.string.common_complete),
                onClick = {
                    onIntent(ProfileCreationIntent.OnCompleteProfileClick)
                },
                enabled = state.isCompleteButtonEnabled,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
                        .padding(horizontal = SixPackDimen.defaultSideMargin)
                        .padding(bottom = 12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ProfileCreationScreenPreview() {
    DoRunPreviewWrapper {
        ProfileCreationScreen(
            state =
                ProfileCreationState(
                    profileName = "홍길동",
                ),
            onIntent = {},
        )
    }
}
