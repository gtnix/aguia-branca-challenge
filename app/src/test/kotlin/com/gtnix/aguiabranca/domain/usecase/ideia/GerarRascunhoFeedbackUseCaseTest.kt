package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GerarRascunhoFeedbackUseCaseTest {

    private lateinit var repository: FakeIdeiaRepository
    private lateinit var sessionManager: FakeSessionManager
    private lateinit var useCase: GerarRascunhoFeedbackUseCase

    @Before
    fun setup() {
        repository = FakeIdeiaRepository(listOf(TestDataFactory.createIdeia(id = "ideia-1", titulo = "Manifesto")))
        sessionManager = FakeSessionManager()
        useCase = GerarRascunhoFeedbackUseCase(repository, sessionManager)
    }

    @Test
    fun `gestor recebe rascunho sem que a ideia seja avaliada`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ideia-1", "sem orçamento")

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.texto.contains("Manifesto"))
        assertEquals(StatusIdeia.PENDENTE, repository.getById("ideia-1")?.status)
    }

    @Test
    fun `lider nao gera rascunho`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER))

        assertTrue(useCase("ideia-1") is Result.Error)
    }
}
