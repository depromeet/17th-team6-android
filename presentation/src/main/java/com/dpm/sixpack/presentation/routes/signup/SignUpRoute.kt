package com.dpm.sixpack.presentation.routes.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.util.context.openUrlInBrowser
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpSideEffect
import com.dpm.sixpack.presentation.routes.signup.ui.screen.SignUpScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToProfileCreation: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onShowSnackBar: (String, String?) -> Unit,
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.NavigateToProfileCreation -> onNavigateToProfileCreation(sideEffect.phoneNumber)
            is SignUpSideEffect.NavigateBack -> onNavigateBack()
            is SignUpSideEffect.NavigateToFindAccount -> {
                context.openUrlInBrowser("") // TODO SR-N 채우기
            }
            is SignUpSideEffect.ShowInvalidPhoneNumberError -> {
                onShowSnackBar(context.getString(R.string.auth_error_invalid_phone_number), null)
            }
            is SignUpSideEffect.ShowCodeSentSuccess -> {
                onShowSnackBar(context.getString(R.string.auth_success_code_sent), null)
            }
            is SignUpSideEffect.ShowCodeSendFailedError -> {
                onShowSnackBar(context.getString(R.string.auth_error_code_send_failed), null)
            }
            is SignUpSideEffect.ShowRateLimitError -> {
                onShowSnackBar(context.getString(R.string.auth_error_rate_limit), null)
            }
            is SignUpSideEffect.ShowInvalidCodeLengthError -> {
                onShowSnackBar(context.getString(R.string.signup_error_code_length), null)
            }
            is SignUpSideEffect.ShowCodeMismatchError -> {
                onShowSnackBar(context.getString(R.string.auth_error_code_mismatch), null)
            }
            is SignUpSideEffect.ShowCodeExpiredError -> {
                onShowSnackBar(context.getString(R.string.auth_error_code_expired), null)
            }
        }
    }

    SignUpScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
