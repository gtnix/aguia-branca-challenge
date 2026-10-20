package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
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

class SalvarEstimativaUseCaseTest {

    private lateinit var repository: FakeIdeiaRepository
    private lateinit var sessionManager: FakeSessionManager
    private lateinit var useCase: SalvarEstimativaUseCase

    @Before
    fun setup() {
        repository = FakeIdeiaRepository(listOf(TestDataFactory.createIdeia(id = "ideia-1")))
        sessionManager = FakeSessionManager()
        useCase = SalvarEstimativaUseCase(repository, sessionManager)
    }

    @Test
    fun `gestor define valores oficiais`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ideia-1", impacto = 4, esforco = 2, motivoAjuste = "Afeta todas as linhas")

        assertTrue(result is Result.Success)
        val ideia = (result as Result.Success).data
        assertEquals(4, ideia.impactoEstimado)
        assertEquals(OrigemEstimativa.GESTOR, ideia.analiseIa?.estimativaOrigem)
    }

    @Test
    fun `operador e lider nao definem impacto e esforco`() = runTest {
        for (perfil in listOf(PerfilUsuario.OPERADOR, PerfilUsuario.LIDER)) {
            sessionManager.setUser(TestDataFactory.createUsuario(perfil = perfil))

            val result = useCase("ideia-1", impacto = 5, esforco = 1)

            assertTrue(result is Result.Error)
            assertEquals(0, repository.getById("ideia-1")?.impactoEstimado)
        }
    }

    @Test
    fun `valores fora da escala sao rejeitados antes da rede`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        val result = useCase("ideia-1", impacto = 0, esforco = 6)

        assertTrue(result is Result.Error)
        assertEquals(0, repository.getById("ideia-1")?.impactoEstimado)
    }
}
