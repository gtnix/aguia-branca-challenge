package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.ProjetoApiService
import com.gtnix.aguiabranca.data.remote.dto.ProgressoRequestDto
import com.gtnix.aguiabranca.data.remote.dto.ProjetoDto
import com.gtnix.aguiabranca.data.remote.dto.ProjetoRequestDto
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class ProjetoRepositoryImplTest {

    private val api = mockk<ProjetoApiService>()
    private val networkMonitor = mockk<NetworkMonitor>()

    @Test
    fun `listarTodos busca na API`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.listar() } returns listOf(sampleDto(id = "p1"))
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        val result = repository.listarTodos().first()

        assertEquals(1, result.size)
        assertEquals("ori-1", result.first().orientacaoId)
        assertEquals("ideia-1", result.first().ideiaOrigemId)
        assertEquals(20.0, result.first().roi, 0.01)
    }

    @Test
    fun `salvar sem id faz POST`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.criar(any()) } returns sampleDto(id = "novo")
        coEvery { api.listar() } returns listOf(sampleDto(id = "novo"))
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        repository.salvar(
            Projeto(
                id = "",
                nome = "Telemetria",
                objetivo = "Reduzir combustível",
                descricao = "Desc",
                area = AreaAtuacao.OPERACOES,
                status = StatusProjeto.PLANEJADO,
                ideiaOrigemId = "ideia-1",
                orientacaoId = "ori-1",
                responsavelId = "user-gestor",
                responsavelNome = "Ana",
                investimentoEstimado = 100_000.0,
                retornoEstimadoMensal = 10_000.0
            )
        )

        coVerify {
            api.criar(
                ProjetoRequestDto(
                    titulo = "Telemetria",
                    objetivo = "Reduzir combustível",
                    descricao = "Desc",
                    area = "OPERACOES",
                    orientacaoId = "ori-1",
                    ideiaId = "ideia-1",
                    etapa = "PLANEJADO",
                    status = "PLANEJADO",
                    investimento = 100_000.0,
                    retorno = 10_000.0,
                    prazo = null,
                    progressoPercentual = 0,
                    resultadosObtidos = null
                )
            )
        }
    }

    @Test
    fun `atualizarProgresso chama PATCH`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.atualizarProgresso("p1", any()) } returns sampleDto(id = "p1")
        coEvery { api.listar() } returns listOf(sampleDto(id = "p1"))
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        repository.atualizarProgresso("p1", 40)

        coVerify {
            api.atualizarProgresso("p1", ProgressoRequestDto(progressoPercentual = 40))
        }
    }

    @Test
    fun `mutacao sem rede falha`() = runTest {
        every { networkMonitor.isOnline() } returns false
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        try {
            repository.excluir("p1")
            fail("deveria exigir rede")
        } catch (_: IOException) {
            // esperado
        }
    }

    @Test
    fun `excluir chama DELETE e atualiza lista`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.excluir("p1") } returns Response.success(null)
        coEvery { api.listar() } returns emptyList()
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        repository.excluir("p1")

        coVerify { api.excluir("p1") }
    }

    @Test
    fun `buscarPorId 404 retorna null`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.obter("sumiu") } throws httpException(404)
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        assertEquals(null, repository.buscarPorId("sumiu"))
    }

    @Test
    fun `listarTodos 403 retorna lista vazia para home do operador`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.listar() } throws httpException(403)
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        val result = repository.listarTodos().first()

        assertEquals(emptyList<Projeto>(), result)
    }

    @Test
    fun `listarTodos 500 propaga HttpException`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.listar() } throws httpException(500)
        val repository = ProjetoRepositoryImpl(api, networkMonitor)

        try {
            repository.listarTodos().first()
            fail("deveria propagar HttpException")
        } catch (e: HttpException) {
            assertEquals(500, e.code())
        }
    }

    private fun sampleDto(id: String) = ProjetoDto(
        id = id,
        titulo = "Telemetria",
        objetivo = "Reduzir combustível",
        descricao = "Desc",
        area = "OPERACOES",
        orientacaoId = "ori-1",
        ideiaId = "ideia-1",
        status = "PLANEJADO",
        investimento = 100_000.0,
        retorno = 10_000.0,
        roi = 20.0,
        progressoPercentual = 0,
        dataCriacao = "2026-01-15T10:00:00Z"
    )

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }
}
