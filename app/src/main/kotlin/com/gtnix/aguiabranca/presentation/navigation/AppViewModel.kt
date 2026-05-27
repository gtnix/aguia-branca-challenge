package com.gtnix.aguiabranca.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AppStartState {
    data object Loading : AppStartState
    data object Unauthenticated : AppStartState
    data class Authenticated(val perfil: PerfilUsuario) : AppStartState
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _startState = MutableStateFlow<AppStartState>(AppStartState.Loading)
    val startState: StateFlow<AppStartState> = _startState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.restoreSession()
            val user = sessionManager.getCurrentUser()
            _startState.value = if (user != null) {
                AppStartState.Authenticated(user.perfil)
            } else {
                AppStartState.Unauthenticated
            }
        }
    }
}
