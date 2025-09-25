package com.dpm.sixpack.presentation.routes.session_list

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.session_list.contract.SessionListIntent
import com.dpm.sixpack.presentation.routes.session_list.contract.SessionListScreenState
import com.dpm.sixpack.presentation.routes.session_list.contract.SessionListSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SessionListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SessionListScreenState, SessionListIntent, SessionListSideEffect>() {
    override val initialState: SessionListScreenState = SessionListScreenState()

    override val container: Container<SessionListScreenState, SessionListSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: SessionListIntent) {

    }

}
