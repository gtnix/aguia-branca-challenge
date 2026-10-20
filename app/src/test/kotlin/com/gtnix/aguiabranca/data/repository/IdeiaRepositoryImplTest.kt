package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.AiApiService
import com.gtnix.aguiabranca.data.remote.api.IdeiaApiService
import com.gtnix.aguiabranca.data.remote.dto.AnaliseIaDto
import com.gtnix.aguiabranca.data.remote.dto.AvaliacaoRequestDto
import com.gtnix.aguiabranca.data.remote.dto.EstimativaRequestDto
import com.gtnix.aguiabranca.data.remote.dto.IdeaScoreItemDto
import com.gtnix.aguiabranca.data.remote.dto.IdeiaDto
import com.gtnix.aguiabranca.data.remote.dto.IdeiaRequestDto
import com.gtnix.aguiabranca.data.remote.dto.RascunhoFeedbackRequestDto
import com.gtnix.aguiabranca.data.remote.dto.RascunhoFeedbackResponseDto
import com.gtnix.aguiabranca.data.remote.dto.ScoreIdeasRequestDto
import com.gtnix.aguiabranca.data.remote.dto.ScoreIdeasResponseDto
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class IdeiaRepositoryImplTest {

    private val api = mockk<IdeiaApiService>()
    private val aiApi = mockk<AiApiService>(relaxed = true)
    private val networkMonitor = mockk<NetworkMonitor>()

    @Test
    fun `listarTodas busca na API`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns listOf(
            sampleDto(id = "a")
        )
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        val result = repository.listarTodas().first()

        assertEquals(1, result.size)
        assertEquals("ori-1", result.first().orientacaoId)
    }

    @Test
    fun `salvar sem id faz POST`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.criar(any()) } returns sampleDto(id = "novo")
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns listOf(sampleDto(id = "novo"))
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        repository.salvar(
            Ideia(
                id = "",
                titulo = "Nova",
                descricao = "Desc",
                tipo = TipoIdeia.IDEIA,
                area = AreaAtuacao.OPERACOES,
                autorId = "user-operador",
                autorNome = "Pedro",
                orientacaoId = "ori-1",
                impactoEstimado = 5,
                esforcoEstimado = 1,
                problema = "Fila no guichê",
                frequencia = FrequenciaProblema.DIARIA
            )
        )

        coVerify {
            api.criar(
                IdeiaRequestDto(
                    titulo = "Nova",
                    descricao = "Desc",
                    tipo = "IDEIA",
                    area = "OPERACOES",
                    orientacaoId = "ori-1",
                    problema = "Fila no guichê",
                    frequencia = "DIARIA"
                )
            )
        }
    }

    @Test
    fun `salvarEstimativa chama PATCH e descarta motivo em branco`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.salvarEstimativa("ideia-1", any()) } returns sampleDto(id = "ideia-1").copy(
            impacto = 4,
            esforco = 2,
            analiseIa = AnaliseIaDto(estimativaOrigem = "GESTOR")
        )
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns emptyList()
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        val atualizada = repository.salvarEstimativa("ideia-1", 4, 2, "  ")

        coVerify { api.salvarEstimativa("ideia-1", EstimativaRequestDto(4, 2, null)) }
        assertEquals(OrigemEstimativa.GESTOR, atualizada.analiseIa?.estimativaOrigem)
        assertNull(atualizada.analiseIa?.status)
    }

    @Test
    fun `avaliar envia concordancia com a IA`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.avaliar("ideia-1", any()) } returns sampleDto(id = "ideia-1")
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns emptyList()
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        repository.atualizarStatus("ideia-1", StatusIdeia.REPROVADA, "Obrigado", false, "Ignorou o custo")

        coVerify {
            api.avaliar(
                "ideia-1",
                AvaliacaoRequestDto("REPROVADA", "Obrigado", concordaIa = false, motivoDiscordanciaIa = "Ignorou o custo")
            )
        }
    }

    @Test
    fun `gerarRascunhoFeedback chama endpoint de IA`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery {
            aiApi.rascunhoFeedback("ideia-1", RascunhoFeedbackRequestDto("sem orçamento"))
        } returns RascunhoFeedbackResponseDto(rascunho = "Olá!", modelo = "gemini", fallback = false)
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        val rascunho = repository.gerarRascunhoFeedback("ideia-1", "sem orçamento")

        assertEquals("Olá!", rascunho.texto)
        assertFalse(rascunho.fallback)
    }

    @Test
    fun `mutacao sem rede falha`() = runTest {
        every { networkMonitor.isOnline() } returns false
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        try {
            repository.excluir("ideia-1")
            fail("deveria exigir rede")
        } catch (_: IOException) {
            // esperado
        }
    }

    @Test
    fun `excluir chama DELETE e atualiza lista`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.excluir("ideia-1") } returns Response.success(null)
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns emptyList()
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        repository.excluir("ideia-1")

        coVerify { api.excluir("ideia-1") }
    }

    @Test
    fun `excluir propaga erro HTTP`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.excluir("ideia-1") } returns Response.error(
            400,
            "".toResponseBody("application/json".toMediaType())
        )
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        try {
            repository.excluir("ideia-1")
            fail("deveria lançar HttpException")
        } catch (e: HttpException) {
            assertEquals(400, e.code())
        }
    }

    @Test
    fun `avaliar chama PATCH e atualiza lista`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.avaliar("ideia-1", any()) } returns sampleDto(id = "ideia-1")
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns emptyList()
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        repository.atualizarStatus("ideia-1", StatusIdeia.APROVADA, null)

        coVerify { api.avaliar("ideia-1", any()) }
    }

    @Test
    fun `scoreIdeias chama API de IA e atualiza lista`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery {
            aiApi.scoreIdeas(
                ScoreIdeasRequestDto(orientacaoId = "ori-1", ideiaIds = listOf("ideia-1"))
            )
        } returns ScoreIdeasResponseDto(
            ranking = listOf(
                IdeaScoreItemDto("ideia-1", 87, "Alto alinhamento", "ESTIMATIVA", "ALTA"),
                IdeaScoreItemDto("ideia-2", null, "Sem dados", "DADOS_INSUFICIENTES", null)
            ),
            modelo = "heuristic-local",
            fallback = true
        )
        coEvery { api.listar(status = null, area = null, orientacaoId = null) } returns listOf(
            sampleDto(id = "ideia-1")
        )
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        val result = repository.scoreIdeias("ori-1", listOf("ideia-1"))

        assertEquals(87, result.ranking.first().score)
        assertEquals(StatusAnaliseIa.ESTIMATIVA, result.ranking.first().status)
        assertNull(result.ranking[1].score)
        assertTrue(result.semDados)
        assertTrue(result.fallback)
    }

    @Test
    fun `buscarPorId 404 retorna null`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.obter("sumiu") } throws httpException(404)
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        assertEquals(null, repository.buscarPorId("sumiu"))
    }

    @Test
    fun `buscarPorId 403 relanca HttpException`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.obter("alheia") } throws httpException(403)
        val repository = IdeiaRepositoryImpl(api, aiApi, networkMonitor)

        try {
            repository.buscarPorId("alheia")
            fail("deveria relancar 403")
        } catch (e: HttpException) {
            assertEquals(403, e.code())
        }
    }

    private fun sampleDto(id: String) = IdeiaDto(
        id = id,
        autorUserId = "user-operador",
        titulo = "Título",
        descricao = "Desc",
        tipo = "IDEIA",
        area = "OPERACOES",
        orientacaoId = "ori-1",
        status = "PENDENTE",
        dataCriacao = "2026-01-15T10:00:00Z"
    )

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }
}
