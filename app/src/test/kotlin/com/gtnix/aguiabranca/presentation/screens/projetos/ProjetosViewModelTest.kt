package com.gtnix.aguiabranca.presentation.screens.projetos

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProjetosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProjetosViewModel
    private lateinit var projetoRepository: FakeProjetoRepository
    private lateinit var sessionManager: FakeSessionManager

    private val projetos = listOf(
        TestDataFactory.createProjeto(id = "p1", nome = "Projeto 1", status = StatusProjeto.EM_ANDAMENTO),
        TestDataFactory.createProjeto(id = "p2", nome = "Projeto 2", status = StatusProjeto.CONCLUIDO),
        TestDataFactory.createProjeto(id = "p3", nome = "Projeto 3", status = StatusProjeto.PLANEJADO)
    )

    @Before
    fun setup() {
        projetoRepository = FakeProjetoRepository(projetos)
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel() {
        viewModel = ProjetosViewModel(projetoRepository, sessionManager)
    }

    @Test
    fun `dado usuario logado quando carrega então exibe todos projetos`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.projetos.size)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario não logado quando carrega então exibe erro`() = runTest {
        sessionManager.setUser(null)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessageRes)
    }

    @Test
    fun `dado filtro EM_ANDAMENTO quando seleciona então filtra projetos`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroProjeto.EM_ANDAMENTO)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.projetos.size)
        assertEquals("Projeto 1", state.projetos.first().nome)
    }

    @Test
    fun `dado filtro CONCLUIDO quando seleciona então filtra projetos`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroProjeto.CONCLUIDO)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.projetos.size)
        assertEquals("Projeto 2", state.projetos.first().nome)
    }

    @Test
    fun `dado gestor quando carrega então vê apenas projetos da sua area`() = runTest {
        val gestor = TestDataFactory.createUsuario(
            id = "user-gestor",
            perfil = PerfilUsuario.GESTOR,
            area = AreaAtuacao.OPERACOES
        )
        sessionManager.setUser(gestor)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        state.projetos.forEach { projeto ->
            assert(projeto.area == AreaAtuacao.OPERACOES || projeto.responsavelId == gestor.id)
        }
    }

    @Test
    fun `dado filtro TODOS quando volta ao filtro então exibe todos`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroProjeto.EM_ANDAMENTO)
        advanceUntilIdle()
        viewModel.selecionarFiltro(FiltroProjeto.TODOS)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.projetos.size)
    }

}
