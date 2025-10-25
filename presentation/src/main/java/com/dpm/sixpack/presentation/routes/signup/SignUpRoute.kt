package com.dpm.sixpack.presentation.routes.signup

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpSideEffect
import com.dpm.sixpack.presentation.routes.signup.ui.screen.SignUpScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.NavigateToHome -> onNavigateToHome()
            is SignUpSideEffect.NavigateBack -> onNavigateBack()
            is SignUpSideEffect.ShowInvalidPhoneNumberError -> {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.signup_error_invalid_phone_number),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            is SignUpSideEffect.ShowCodeSentSuccess -> {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.signup_success_code_sent),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            is SignUpSideEffect.ShowCodeSendFailedError -> {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.signup_error_code_send_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            is SignUpSideEffect.ShowInvalidCodeLengthError -> {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.signup_error_code_length),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            is SignUpSideEffect.ShowCodeMismatchError -> {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.signup_error_code_mismatch),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
            is SignUpSideEffect.ShowCodeExpiredError -> {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.signup_error_code_expired),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }

    SignUpScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
