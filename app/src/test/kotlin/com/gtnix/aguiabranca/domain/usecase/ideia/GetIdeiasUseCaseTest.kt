package com.gtnix.aguiabranca.domain.usecase.ideia

import app.cash.turbine.test
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
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

class GetIdeiasUseCaseTest {

    private lateinit var useCase: GetIdeiasUseCase
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository
    private lateinit var fakeSessionManager: FakeSessionManager

    private val operadorUser = TestDataFactory.createUsuario(
        id = "user-operador",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    private val gestorUser = TestDataFactory.createUsuario(
        id = "user-gestor",
        perfil = PerfilUsuario.GESTOR,
        area = AreaAtuacao.LOGISTICA
    )

    private val liderUser = TestDataFactory.createUsuario(
        id = "user-lider",
        perfil = PerfilUsuario.LIDER,
        area = AreaAtuacao.OPERACOES
    )

    private val allIdeias = listOf(
        TestDataFactory.createIdeia("ideia-1", "Ideia do Operador", autorId = "user-operador", area = AreaAtuacao.OPERACOES),
        TestDataFactory.createIdeia("ideia-2", "Ideia do Gestor", autorId = "user-gestor", area = AreaAtuacao.LOGISTICA),
        TestDataFactory.createIdeia("ideia-3", "Outra Ideia", autorId = "user-outro", area = AreaAtuacao.OPERACOES),
        TestDataFactory.createIdeia("ideia-4", "Segunda do Operador", autorId = "user-operador", area = AreaAtuacao.OPERACOES)
    )

    @Before
    fun setup() {
        fakeIdeiaRepository = FakeIdeiaRepository(allIdeias)
        fakeSessionManager = FakeSessionManager()
        useCase = GetIdeiasUseCase(fakeIdeiaRepository, fakeSessionManager)
    }

    @Test
    fun `OPERADOR ve apenas suas proprias ideias`() = runTest {
        fakeSessionManager.setUser(operadorUser)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val ideias = (result as Result.Success).data

            assertEquals(2, ideias.size)
            assertTrue(ideias.all { it.autorId == operadorUser.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GESTOR ve ideias da sua area de atuacao`() = runTest {
        fakeSessionManager.setUser(gestorUser)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val ideias = (result as Result.Success).data

            assertEquals(1, ideias.size)
            assertEquals(AreaAtuacao.LOGISTICA, ideias.first().area)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LIDER ve todas as ideias`() = runTest {
        fakeSessionManager.setUser(liderUser)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val ideias = (result as Result.Success).data

            assertEquals(4, ideias.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `usuario nao logado retorna erro`() = runTest {
        fakeSessionManager.setUser(null)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            assertEquals("Usuário não logado", (result as Result.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
