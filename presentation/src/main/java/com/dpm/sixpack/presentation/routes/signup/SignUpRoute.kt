package com.dpm.sixpack.presentation.routes.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.dialog.CommonDialog
import com.dpm.sixpack.presentation.common.util.context.openUrlInBrowser
import com.dpm.sixpack.presentation.common.util.context.showToastByResId
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpIntent
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpSideEffect
import com.dpm.sixpack.presentation.routes.signup.ui.screen.SignUpScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToProfileCreation: () -> Unit,
    onNavigateToSignIn: (phoneNumber: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.NavigateToProfileCreation -> onNavigateToProfileCreation()
            is SignUpSideEffect.NavigateBack -> onNavigateBack()
            is SignUpSideEffect.NavigateToFindAccount -> {
                context.openUrlInBrowser("") // TODO SR-N 채우기
            }
            is SignUpSideEffect.ShowInvalidPhoneNumberError -> {
                context.showToastByResId(R.string.signup_error_invalid_phone_number)
            }
            is SignUpSideEffect.ShowCodeSentSuccess -> {
                context.showToastByResId(R.string.signup_success_code_sent)
            }
            is SignUpSideEffect.ShowCodeSendFailedError -> {
                context.showToastByResId(R.string.signup_error_code_send_failed)
            }
            is SignUpSideEffect.ShowInvalidCodeLengthError -> {
                context.showToastByResId(R.string.signup_error_code_length)
            }
            is SignUpSideEffect.ShowCodeMismatchError -> {
                context.showToastByResId(R.string.signup_error_code_mismatch)
            }
            is SignUpSideEffect.ShowCodeExpiredError -> {
                context.showToastByResId(R.string.signup_error_code_expired)
            }
        }
    }

    SignUpScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
