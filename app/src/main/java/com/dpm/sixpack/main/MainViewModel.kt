package com.dpm.sixpack.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.usecase.CheckUserLoggedInUseCase
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.destinations.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Route>(MainRoute.Running)
    val startDestination: StateFlow<Route> = _startDestination.asStateFlow()

    private val _showFullScreenLoading = MutableStateFlow(false)
    val showFullScreenLoading: StateFlow<Boolean> = _showFullScreenLoading.asStateFlow()

    init {
        initializeStartDestination()
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
}
