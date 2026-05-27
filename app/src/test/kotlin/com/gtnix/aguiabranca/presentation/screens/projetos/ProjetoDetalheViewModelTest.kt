package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProjetoDetalheViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProjetoDetalheViewModel
    private lateinit var projetoRepository: FakeProjetoRepository
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var usuarioRepository: FakeUsuarioRepository
    private lateinit var sessionManager: FakeSessionManager

    private val projeto = TestDataFactory.createProjeto(
        id = "projeto-123",
        nome = "Projeto ESG",
        status = StatusProjeto.EM_ANDAMENTO
    )

    @Before
    fun setup() {
        projetoRepository = FakeProjetoRepository(listOf(projeto))
        ideiaRepository = FakeIdeiaRepository()
        usuarioRepository = FakeUsuarioRepository()
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel(projetoId: String = "projeto-123") {
        val savedStateHandle = SavedStateHandle(mapOf(Destination.ProjetoDetalhe.ARG_PROJETO_ID to projetoId))
        viewModel = ProjetoDetalheViewModel(
            projetoRepository,
            ideiaRepository,
            usuarioRepository,
            sessionManager,
            savedStateHandle
        )
    }

    @Test
    fun `dado projeto existente quando carrega então exibe dados`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.projeto)
        assertEquals("Projeto ESG", state.projeto?.nome)
        assertEquals(PerfilUsuario.GESTOR, state.perfil)
    }

    @Test
    fun `dado projeto inexistente quando carrega então projeto é null`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel(projetoId = "inexistente")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.projeto)
    }

    @Test
    fun `dado projeto quando altera progresso então atualiza slider`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        createViewModel()
        advanceUntilIdle()

        viewModel.onProgressoChange(75f)

        val state = viewModel.uiState.value
        assertEquals(75f, state.progressoSlider)
    }

    @Test
    fun `dado projeto quando salva progresso então atualiza projeto`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        createViewModel()
        advanceUntilIdle()

        viewModel.onProgressoChange(80f)
        viewModel.salvarProgresso()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.actionSuccess)
        assertEquals(80, state.projeto?.progresso)
    }

    @Test
    fun `dado projeto quando conclui então status muda para CONCLUIDO`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        createViewModel()
        advanceUntilIdle()

        viewModel.concluirProjeto()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.actionSuccess)
        assertEquals(StatusProjeto.CONCLUIDO, state.projeto?.status)
        assertEquals(100f, state.progressoSlider)
    }

    @Test
    fun `dado projeto concluído quando conclui então progresso é 100`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        createViewModel()
        advanceUntilIdle()

        viewModel.concluirProjeto()
        advanceUntilIdle()

        assertEquals(100, viewModel.uiState.value.projeto?.progresso)
    }
}
