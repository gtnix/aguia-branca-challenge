package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
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

class AprovarIdeiaUseCaseTest {

    private lateinit var useCase: AprovarIdeiaUseCase
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository
    private lateinit var fakeSessionManager: FakeSessionManager

    private val gestorUser = TestDataFactory.createUsuario(
        id = "user-gestor",
        perfil = PerfilUsuario.GESTOR,
        area = AreaAtuacao.OPERACOES
    )

    private val operadorUser = TestDataFactory.createUsuario(
        id = "user-operador",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    private val ideiaPendente = TestDataFactory.createIdeia(
        id = "ideia-1",
        titulo = "Ideia Pendente",
        autorId = "user-operador",
        area = AreaAtuacao.OPERACOES,
        status = StatusIdeia.PENDENTE
    )

    @Before
    fun setup() {
        fakeIdeiaRepository = FakeIdeiaRepository(listOf(ideiaPendente))
        fakeSessionManager = FakeSessionManager()
        useCase = AprovarIdeiaUseCase(fakeIdeiaRepository, fakeSessionManager)
    }

    @Test
    fun `GESTOR aprova ideia com sucesso`() = runTest {
        fakeSessionManager.setUser(gestorUser)

        val result = useCase(ideiaId = "ideia-1", aprovada = true)

        assertTrue(result is Result.Success)
        assertEquals(StatusIdeia.APROVADA, fakeIdeiaRepository.getById("ideia-1")?.status)
    }

    @Test
    fun `OPERADOR nao tem permissao para avaliar`() = runTest {
        fakeSessionManager.setUser(operadorUser)

        val result = useCase(ideiaId = "ideia-1", aprovada = true)

        assertTrue(result is Result.Error)
        assertEquals("Sem permissão para avaliar ideias", (result as Result.Error).message)
    }

    @Test
    fun `reprovar sem feedback retorna erro`() = runTest {
        fakeSessionManager.setUser(gestorUser)

        val result = useCase(ideiaId = "ideia-1", aprovada = false, feedback = null)

        assertTrue(result is Result.Error)
        assertEquals("Feedback obrigatório ao reprovar", (result as Result.Error).message)
    }

    @Test
    fun `reprovar com feedback atualiza status`() = runTest {
        fakeSessionManager.setUser(gestorUser)

        val result = useCase(
            ideiaId = "ideia-1",
            aprovada = false,
            feedback = "Não alinhada com prioridades atuais"
        )

        assertTrue(result is Result.Success)
        val ideia = fakeIdeiaRepository.getById("ideia-1")
        assertEquals(StatusIdeia.REPROVADA, ideia?.status)
        assertEquals("Não alinhada com prioridades atuais", ideia?.feedback)
    }
}
