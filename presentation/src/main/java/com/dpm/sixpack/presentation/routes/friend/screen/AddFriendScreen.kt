package com.dpm.sixpack.presentation.routes.friend.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.friend.contract.AddFriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.delay

@Composable
internal fun AddFriendScreen(
    state: FriendUiState.AddingFriend,
    onIntent: (FriendIntent) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    val customTextSelectionColors =
        TextSelectionColors(
            handleColor = SixpackTheme.colors.blue300,
            backgroundColor = SixpackTheme.colors.blue600.copy(alpha = 0.4f),
        )

    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(AddFriendIntent.NavigateToFriendList) },
                titleContent = {
                    Text(
                        text = stringResource(R.string.add_friend_screen_title),
                        textAlign = TextAlign.Center,
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                },
            )
        },
        bottomBar = {
            AddFriendBottomBar(
                enabled = state.enterButtonEnabled,
                onEnterClick = { onIntent(AddFriendIntent.AddFriendByCode(state.input)) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(24.dp),
        ) {
            Text(
                text = stringResource(R.string.friend_code_input_title),
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                value = state.input,
                onValueChange = { newValue ->
                    // 대문자와 숫자만 받음
                    val processedValue =
                        newValue
                            .uppercase()
                            .replace(Regex("[^A-Z0-9]"), "")
                            .take(8)

                    onIntent(AddFriendIntent.InputChanged(processedValue))
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.friend_code_input_description),
                        style = SixpackTheme.typography.b1Medium,
                        color = SixpackTheme.colors.gray600,
                    )
                },
                textStyle = SixpackTheme.typography.b1Medium,
                singleLine = true,
                shape = SixpackTheme.shapes.round8,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SixpackTheme.colors.gray900,
                        unfocusedBorderColor = SixpackTheme.colors.gray300,
                        cursorColor = SixpackTheme.colors.blue600,
                        focusedTextColor = SixpackTheme.colors.gray900,
                        unfocusedTextColor = SixpackTheme.colors.gray900,
                        selectionColors = customTextSelectionColors,
                    ),
            )
        }

        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun AddFriendBottomBar(
    onEnterClick: () -> Unit = { },
    enabled: Boolean = false,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DoRunDefaultButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            text = stringResource(R.string.friend_code_enter_done_button),
            enabled = enabled,
            onClick = onEnterClick,
        )
    }
}
