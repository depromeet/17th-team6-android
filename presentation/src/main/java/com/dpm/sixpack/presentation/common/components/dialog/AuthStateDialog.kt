package com.dpm.sixpack.presentation.common.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.ButtonStyle
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * Dialog shown when user tries to sign in but is not registered
 * Prompts user to sign up instead
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnregisteredUserDialog(
    onSignUpClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(0.85f)
                    .background(
                        color = SixpackTheme.colors.gray0,
                        shape = RoundedCornerShape(16.dp),
                    ).padding(20.dp),
        ) {
            // Title
            Text(
                text = stringResource(R.string.signin_unregistered_user_message),
                style = SixpackTheme.typography.h3Bold,
                color = SixpackTheme.colors.gray900,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Close Button
                DoRunDefaultButton(
                    text = stringResource(R.string.common_cancel),
                    onClick = onDismiss,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    enabled = true,
                    style = ButtonStyle.SECONDARY,
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Sign Up Button
                DoRunDefaultButton(
                    text = stringResource(R.string.onboarding_sign_up),
                    onClick = onSignUpClick,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    enabled = true,
                )
            }
        }
    }
}

/**
 * Dialog shown when user tries to sign up with already registered phone number
 * Prompts user to find account instead
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlreadyRegisteredUserDialog(
    onFindAccountClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(0.85f)
                    .background(
                        color = SixpackTheme.colors.gray0,
                        shape = RoundedCornerShape(16.dp),
                    ).padding(20.dp),
        ) {
            // Title
            Text(
                text = stringResource(R.string.signup_already_registered_user_message),
                style = SixpackTheme.typography.h3Bold,
                color = SixpackTheme.colors.gray900,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Close Button
                DoRunDefaultButton(
                    text = stringResource(R.string.common_cancel),
                    onClick = onDismiss,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    enabled = true,
                    style = ButtonStyle.SECONDARY,
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Find Account Button
                DoRunDefaultButton(
                    text = stringResource(R.string.common_find_account),
                    onClick = onFindAccountClick,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    enabled = true,
                )
            }
        }
    }
}

@Preview
@Composable
private fun UnregisteredUserDialogPreview() {
    DoRunPreviewWrapper {
        UnregisteredUserDialog(
            onSignUpClick = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun AlreadyRegisteredUserDialogPreview() {
    DoRunPreviewWrapper {
        AlreadyRegisteredUserDialog(
            onFindAccountClick = {},
            onDismiss = {},
        )
    }
}
