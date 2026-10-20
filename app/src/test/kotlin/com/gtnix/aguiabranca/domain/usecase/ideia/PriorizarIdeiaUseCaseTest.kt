package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PriorizarIdeiaUseCaseTest {

    private lateinit var useCase: PriorizarIdeiaUseCase
    private lateinit var repository: FakeIdeiaRepository
    private lateinit var sessionManager: FakeSessionManager

    @Before
    fun setup() {
        repository = FakeIdeiaRepository(
            listOf(TestDataFactory.createIdeia(id = "ideia-1"))
        )
        sessionManager = FakeSessionManager()
        useCase = PriorizarIdeiaUseCase(repository, sessionManager)
    }

    @Test
    fun `gestor prioriza ideia pendente`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ideia-1", 80)

        assertTrue(result is Result.Success)
        assertEquals(80, repository.getById("ideia-1")?.prioridade)
    }

    @Test
    fun `lider nao prioriza`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER))

        val result = useCase("ideia-1", 80)

        assertTrue(result is Result.Error)
        assertEquals("Sem permissão para priorizar ideias", (result as Result.Error).message)
    }

    @Test
    fun `prioridade fora do intervalo retorna erro`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ideia-1", 0)

        assertTrue(result is Result.Error)
        assertEquals("Prioridade deve estar entre 1 e 100", (result as Result.Error).message)
    }
}
