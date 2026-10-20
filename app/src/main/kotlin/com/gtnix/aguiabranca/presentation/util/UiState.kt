package com.gtnix.aguiabranca.presentation.util

/**
 * Estado genérico da UI para ViewModels que seguem o padrão selado.
 *
 * Usado com `StateFlow<UiState<T>>` para representar o ciclo de vida
 * completo de uma operação: carregamento, sucesso ou erro.
 */
sealed class UiState<out T> {
    data object Initial : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
