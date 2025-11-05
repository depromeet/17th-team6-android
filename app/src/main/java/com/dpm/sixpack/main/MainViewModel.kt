package com.dpm.sixpack.main

import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.usecase.CheckUserLoggedInUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.destinations.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val userPreferenceRepository: UserPreferenceRepository,
) : BaseViewModel<MainState, MainIntent, MainSideEffect>() {
    override val initialState: MainState = MainState()

    override val container: Container<MainState, MainSideEffect> =
        container(initialState = initialState)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Route>(MainRoute.Running)
    val startDestination: StateFlow<Route> = _startDestination.asStateFlow()

    private val _showFullScreenLoading = MutableStateFlow(false)
    val showFullScreenLoading: StateFlow<Boolean> = _showFullScreenLoading.asStateFlow()

    init {
        initializeStartDestination()
        observeAuthEvents()
    }

    override fun onIntent(intent: MainIntent) {
        // MainViewModel은 현재 UI Intent를 처리하지 않음
    }

    fun setFullScreenLoading(show: Boolean) {
        _showFullScreenLoading.value = show
    }

    private fun initializeStartDestination() =
        viewModelScope.launch {
            // 로그인 여부에 따라 시작 화면 결정
            val isLoggedIn = checkUserLoggedInUseCase()
            _startDestination.value =
                if (isLoggedIn) {
                    MainRoute.Running
                } else {
                    OnboardingRoute
                }

            _isLoading.value = false
        }

    /**
     * AuthEvent 관찰하여 토큰 만료/갱신 실패 시 Onboarding 화면으로 이동
     */
    private fun observeAuthEvents() {
        viewModelScope.launch {
            userPreferenceRepository.authEvents.collect { event ->
                when (event) {
                    is AuthEvent.LoggedOut -> {
                        Timber.d("User logged out, navigating to Onboarding screen")
                        intent { postSideEffect(MainSideEffect.NavigateToOnboarding) }
                    }

                    is AuthEvent.TokenRefreshFailed -> {
                        Timber.e("Token refresh failed: ${event.reason}, navigating to Onboarding screen")
                        intent { postSideEffect(MainSideEffect.NavigateToOnboarding) }
                    }

                    else -> {
                        // Do nothing.
                    }
                }
            }
        }
    }
}
