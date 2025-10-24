package com.dpm.sixpack.presentation.routes.signup

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpSideEffect
import com.dpm.sixpack.presentation.routes.signup.ui.screen.SignUpScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToTermsAgreement: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.NavigateToTermsAgreement -> onNavigateToTermsAgreement()
            is SignUpSideEffect.NavigateBack -> onNavigateBack()
            is SignUpSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    SignUpScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
