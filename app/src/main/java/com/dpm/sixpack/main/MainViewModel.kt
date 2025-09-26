package com.dpm.sixpack.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.usecase.GetOnboardingStatusUseCase
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.destinations.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Route>(OnboardingRoute.Onboarding)
    val startDestination: StateFlow<Route> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isOnboardingComplete = getOnboardingStatusUseCase()

            _startDestination.value =
                if (isOnboardingComplete) MainRoute.Home else OnboardingRoute.Onboarding

            delay(1000L)
            _isLoading.value = false
        }
    }
}
