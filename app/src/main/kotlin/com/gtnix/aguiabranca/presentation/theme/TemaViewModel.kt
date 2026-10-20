package com.gtnix.aguiabranca.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.data.theme.ThemePreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemaViewModel @Inject constructor(
    private val store: ThemePreferenceStore
) : ViewModel() {

    val tema: StateFlow<TemaPreferencia> = store.tema.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TemaPreferencia.SISTEMA
    )

    fun selecionar(tema: TemaPreferencia) {
        viewModelScope.launch { store.set(tema) }
    }
}
