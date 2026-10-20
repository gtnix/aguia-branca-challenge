package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ScoreIdeiasUseCaseTest {

    private lateinit var useCase: ScoreIdeiasUseCase
    private lateinit var repository: FakeIdeiaRepository
    private lateinit var sessionManager: FakeSessionManager

    @Before
    fun setup() {
        repository = FakeIdeiaRepository(
            listOf(
                TestDataFactory.createIdeia(
                    id = "ideia-1",
                    status = StatusIdeia.PENDENTE,
                    impactoEstimado = 5,
                    esforcoEstimado = 1
                )
            )
        )
        sessionManager = FakeSessionManager()
        useCase = ScoreIdeiasUseCase(repository, sessionManager)
    }

    @Test
    fun `gestor pontua ideias pendentes`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ori-1", listOf("ideia-1"))

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertTrue(data.fallback)
        assertEquals(95, data.ranking.first().score)
        assertEquals(95, repository.getById("ideia-1")?.scoreIa)
        assertEquals(StatusIdeia.PENDENTE, repository.getById("ideia-1")?.status)
    }

    @Test
    fun `fila grande e dividida em lotes do tamanho aceito pelo backend`() = runTest {
        val ids = (1..45).map { "ideia-$it" }
        repository = FakeIdeiaRepository(
            ids.map { TestDataFactory.createIdeia(id = it, impactoEstimado = 3, esforcoEstimado = 2) }
        )
        useCase = ScoreIdeiasUseCase(repository, sessionManager)
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ori-1", ids + "ideia-1")

        assertTrue(result is Result.Success)
        assertEquals(listOf(20, 20, 5), repository.scoreCalls.map { it.first })
        assertEquals(45, (result as Result.Success).data.ranking.size)
    }

    @Test
    fun `ideia sem impacto e esforco fica sem score no fallback`() = runTest {
        repository = FakeIdeiaRepository(listOf(TestDataFactory.createIdeia(id = "ideia-1")))
        useCase = ScoreIdeiasUseCase(repository, sessionManager)
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val data = (useCase("ori-1", listOf("ideia-1")) as Result.Success).data

        assertNull(data.ranking.single().score)
        assertEquals(StatusAnaliseIa.DADOS_INSUFICIENTES, data.ranking.single().status)
        assertTrue(data.semDados)
    }

    @Test
    fun `lider nao pontua`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER))

        val result = useCase("ori-1", listOf("ideia-1"))

        assertTrue(result is Result.Error)
        assertEquals("Somente o Gestor pode pontuar ideias com IA", (result as Result.Error).message)
    }

    @Test
    fun `http 403 vira mensagem de gestor`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        val repo = mockk<IdeiaRepository>()
        val body = "{}".toResponseBody("application/json".toMediaType())
        coEvery { repo.scoreIdeias(any(), any()) } throws HttpException(Response.error<Any>(403, body))
        val forbiddenUseCase = ScoreIdeiasUseCase(repo, sessionManager)

        val result = forbiddenUseCase("ori-1", listOf("ideia-1"))

        assertTrue(result is Result.Error)
        assertEquals("Somente o Gestor pode pontuar ideias com IA", (result as Result.Error).message)
    }
}
