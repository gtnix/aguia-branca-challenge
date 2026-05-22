package com.gtnix.aguiabranca.domain.usecase.ideia

import app.cash.turbine.test
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetIdeiasUseCaseTest {

    private lateinit var useCase: GetIdeiasUseCase
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository
    private lateinit var fakeSessionManager: FakeSessionManager

    private val operadorUser = Usuario(
        id = "user-operador",
        nome = "João Operador",
        email = "joao@aguiabranca.com.br",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    private val gestorUser = Usuario(
        id = "user-gestor",
        nome = "Maria Gestora",
        email = "maria@aguiabranca.com.br",
        perfil = PerfilUsuario.GESTOR,
        area = AreaAtuacao.LOGISTICA
    )

    private val liderUser = Usuario(
        id = "user-lider",
        nome = "Carlos Líder",
        email = "carlos@aguiabranca.com.br",
        perfil = PerfilUsuario.LIDER,
        area = AreaAtuacao.OPERACOES
    )

    private val allIdeias = listOf(
        createIdeia("ideia-1", "Ideia do Operador", autorId = "user-operador"),
        createIdeia("ideia-2", "Ideia do Gestor", autorId = "user-gestor"),
        createIdeia("ideia-3", "Outra Ideia", autorId = "user-outro"),
        createIdeia("ideia-4", "Segunda do Operador", autorId = "user-operador")
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
    fun `GESTOR ve todas as ideias`() = runTest {
        fakeSessionManager.setUser(gestorUser)

        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val ideias = (result as Result.Success).data
            
            assertEquals(4, ideias.size)
            
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

    private fun createIdeia(id: String, titulo: String, autorId: String) = Ideia(
        id = id,
        titulo = titulo,
        descricao = "Descrição da $titulo",
        tipo = TipoIdeia.IDEIA,
        area = AreaAtuacao.OPERACOES,
        status = StatusIdeia.PENDENTE,
        autorId = autorId,
        autorNome = "Autor"
    )

    private class FakeIdeiaRepository(private val ideias: List<Ideia>) : IdeiaRepository {
        override fun listarTodas(): Flow<List<Ideia>> = flowOf(ideias)
        override fun listarPorAutor(autorId: String) = flowOf(ideias.filter { it.autorId == autorId })
        override fun listarPorArea(area: AreaAtuacao) = flowOf(ideias.filter { it.area == area })
        override fun listarPorStatus(status: StatusIdeia) = flowOf(ideias.filter { it.status == status })
        override fun listarPorOrientacao(orientacaoId: String) = flowOf(ideias.filter { it.orientacaoId == orientacaoId })
        override suspend fun buscarPorId(id: String) = ideias.find { it.id == id }
        override suspend fun salvar(ideia: Ideia) {}
        override suspend fun atualizarStatus(id: String, novoStatus: StatusIdeia, feedback: String?) {}
        override suspend fun vincularProjeto(ideiaId: String, projetoId: String) {}
        override suspend fun excluir(id: String) {}
        override suspend fun contarPorStatus() = emptyMap<StatusIdeia, Int>()
    }

    private class FakeSessionManager : SessionManager {
        private val _currentUser = MutableStateFlow<Usuario?>(null)
        override val currentUserFlow: Flow<Usuario?> = _currentUser

        fun setUser(user: Usuario?) {
            _currentUser.value = user
        }

        override suspend fun login(usuario: Usuario) {
            _currentUser.value = usuario
        }

        override suspend fun logout() {
            _currentUser.value = null
        }

        override fun getCurrentUser(): Usuario? = _currentUser.value
    }
}
