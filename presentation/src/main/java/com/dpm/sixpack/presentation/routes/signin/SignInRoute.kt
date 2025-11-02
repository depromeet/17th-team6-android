package com.dpm.sixpack.presentation.routes.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.util.context.openUrlInBrowser
import com.dpm.sixpack.presentation.common.util.context.showToastByResId
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
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignInSideEffect.NavigateToHome -> onNavigateToHome()
            is SignInSideEffect.NavigateBack -> onNavigateBack()
            is SignInSideEffect.NavigateToSignUp -> onNavigateToSignUp(sideEffect.phoneNumber)
            is SignInSideEffect.NavigateToFindAccount -> {
                context.openUrlInBrowser("") // TODO SR-N 채우기
            }
            is SignInSideEffect.ShowCodeSentSuccess -> {
                context.showToastByResId(R.string.signin_success_code_sent)
            }
            is SignInSideEffect.ShowInvalidPhoneNumberError -> {
                context.showToastByResId(R.string.signin_error_invalid_phone_number)
            }
            is SignInSideEffect.ShowRateLimitError -> {
                context.showToastByResId(R.string.signin_error_rate_limit)
            }
            is SignInSideEffect.ShowCodeSendFailedError -> {
                context.showToastByResId(R.string.signin_error_code_send_failed)
            }

            is SignInSideEffect.ShowInvalidCodeLengthError -> {
                context.showToastByResId(R.string.signin_error_code_length)
            }
            is SignInSideEffect.ShowCodeMismatchError -> {
                context.showToastByResId(R.string.signin_error_code_mismatch)
            }
            is SignInSideEffect.ShowCodeExpiredError -> {
                context.showToastByResId(R.string.signin_error_code_expired)
            }
        }
    }

    SignInScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
