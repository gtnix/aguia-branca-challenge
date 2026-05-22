package com.gtnix.aguiabranca.domain.util

/**
 * Wrapper genérico para respostas de Use Cases.
 *
 * Encapsula os três estados possíveis de uma operação assíncrona,
 * permitindo tratamento uniforme de erros em toda a camada de domínio.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val exception: Exception,
        val message: String? = exception.localizedMessage
    ) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
