package com.gtnix.aguiabranca.domain.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class HttpMutationErrorsTest {

    @Test
    fun `403 usa mensagem de perfil`() {
        val error = mutationFailure(httpException(403, "{\"error\":\"Acesso negado\"}"), "Sem permissão para avaliar ideias")
        assertEquals("Sem permissão para avaliar ideias", error.message)
        assertTrue(error.isForbidden())
    }

    @Test
    fun `400 usa error da API`() {
        val error = mutationFailure(
            httpException(400, "{\"error\":\"Ideia não pode ser editada neste status\",\"status\":400}"),
            "x"
        )
        assertEquals("Ideia não pode ser editada neste status", error.message)
    }

    @Test
    fun `404 informa registro ausente`() {
        val error = mutationFailure(httpException(404, "{\"error\":\"Ideia não encontrada\"}"), "x")
        assertEquals("Registro não encontrado.", error.message)
    }

    @Test
    fun `401 pede novo login`() {
        val error = mutationFailure(httpException(401, "{\"error\":\"Não autenticado\"}"), "x")
        assertEquals("Sessão expirada. Entre novamente.", error.message)
    }

    @Test
    fun `offline exige rede`() {
        val error = mutationFailure(IOException("failed to connect"), "x")
        assertTrue(error.isOffline())
    }

    private fun httpException(code: Int, body: String): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, responseBody))
    }
}
