package com.dpm.sixpack.presentation.routes.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.util.constant.Url
import com.dpm.sixpack.presentation.common.util.context.openUrlInBrowser
import com.dpm.sixpack.presentation.routes.signin.contract.SignInSideEffect
import com.dpm.sixpack.presentation.routes.signin.ui.screen.SignInScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignInRoute(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: (phoneNumber: String) -> Unit,
    onNavigateBack: () -> Unit,
    onShowSnackBar: (String, String?) -> Unit,
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignInSideEffect.NavigateToHome -> onNavigateToHome()
            is SignInSideEffect.NavigateBack -> onNavigateBack()
            is SignInSideEffect.NavigateToSignUp -> onNavigateToSignUp(sideEffect.phoneNumber)
            is SignInSideEffect.NavigateToFindAccount -> {
                context.openUrlInBrowser(Url.ACCOUNT_FIND_URL)
            }

            is SignInSideEffect.ShowCodeSentSuccess -> {
                onShowSnackBar(context.getString(R.string.auth_success_code_sent), null)
            }

            is SignInSideEffect.ShowInvalidPhoneNumberError -> {
                onShowSnackBar(context.getString(R.string.auth_error_invalid_phone_number), null)
            }

            is SignInSideEffect.ShowRateLimitError -> {
                onShowSnackBar(context.getString(R.string.auth_error_rate_limit), null)
            }

            is SignInSideEffect.ShowCodeSendFailedError -> {
                onShowSnackBar(context.getString(R.string.auth_error_code_send_failed), null)
            }

            is SignInSideEffect.ShowInvalidCodeLengthError -> {
                onShowSnackBar(context.getString(R.string.signin_error_code_length), null)
            }

            is SignInSideEffect.ShowCodeMismatchError -> {
                onShowSnackBar(context.getString(R.string.auth_error_code_mismatch), null)
            }

            is SignInSideEffect.ShowCodeExpiredError -> {
                onShowSnackBar(context.getString(R.string.auth_error_code_expired), null)
            }
        }
    }

    SignInScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
