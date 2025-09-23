package com.dpm.sixpack.presentation.routes.home

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.home.contract.HomeIntent
import com.dpm.sixpack.presentation.routes.home.contract.HomeSideEffect
import com.dpm.sixpack.presentation.routes.home.contract.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<HomeScreenState, HomeIntent, HomeSideEffect>() {
    override val initialState: HomeScreenState = HomeScreenState()

    override val container: Container<HomeScreenState, HomeSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: HomeIntent) {
        TODO("Not yet implemented")
    }
}
