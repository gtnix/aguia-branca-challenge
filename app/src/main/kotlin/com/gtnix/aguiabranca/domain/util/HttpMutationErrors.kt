package com.gtnix.aguiabranca.domain.util

import retrofit2.HttpException
import java.io.IOException

fun mutationFailure(e: Exception, forbiddenMessage: String): Result.Error {
    return when {
        e is IOException -> Result.Error(Exception("Sem conexão. Esta ação exige rede na Sprint 2."))
        e is HttpException && e.code() == 403 -> Result.Error(Exception(forbiddenMessage))
        e is HttpException && e.code() == 400 -> Result.Error(
            Exception(httpErrorMessage(e, "Dados inválidos. Verifique os campos e tente novamente."))
        )
        e is HttpException && e.code() == 404 -> Result.Error(Exception("Registro não encontrado."))
        e is HttpException && e.code() == 409 -> Result.Error(
            Exception(httpErrorMessage(e, "Operação já realizada ou indisponível no momento."))
        )
        e is HttpException && e.code() == 401 -> Result.Error(Exception("Sessão expirada. Entre novamente."))
        else -> Result.Error(e)
    }
}

fun Result.Error.isForbidden(): Boolean {
    val text = message.orEmpty()
    return text.contains("permissão", ignoreCase = true)
        || text.contains("Somente o Gestor", ignoreCase = true)
}

fun Result.Error.isOffline(): Boolean {
    val text = message.orEmpty().lowercase()
    return text.contains("sem conexão") || text.contains("offline")
}

internal fun httpErrorMessage(e: HttpException, fallback: String): String {
    val body = runCatching { e.response()?.errorBody()?.string().orEmpty() }.getOrDefault("")
    val match = Regex("\"error\"\\s*:\\s*\"([^\"]+)\"").find(body)
    return match?.groupValues?.get(1)?.takeIf { it.isNotBlank() } ?: fallback
}
