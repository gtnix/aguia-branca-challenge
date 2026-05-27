package com.gtnix.aguiabranca.presentation.screens.orientacoes

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrientacoesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OrientacoesViewModel
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var sessionManager: FakeSessionManager

    private val orientacoes = listOf(
        TestDataFactory.createOrientacao(id = "o1", titulo = "Orientação 1", ativa = true),
        TestDataFactory.createOrientacao(id = "o2", titulo = "Orientação 2", ativa = true),
        TestDataFactory.createOrientacao(id = "o3", titulo = "Orientação 3", ativa = false)
    )

    @Before
    fun setup() {
        orientacaoRepository = FakeOrientacaoRepository(orientacoes)
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel() {
        viewModel = OrientacoesViewModel(orientacaoRepository, sessionManager)
    }

    @Test
    fun `dado orientacoes existentes quando carrega então exibe lista`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.orientacoes.size)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario lider quando verifica isLider então true`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLider)
    }

    @Test
    fun `dado usuario operador quando verifica isLider então false`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLider)
    }

    @Test
    fun `dado lider quando desativa orientacao então ela fica inativa`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        createViewModel()
        advanceUntilIdle()

        viewModel.toggleAtivacao("o1", estaAtiva = true)
        advanceUntilIdle()

        val orientacao = orientacaoRepository.buscarPorId("o1")
        assertFalse(orientacao!!.ativa)
    }

    @Test
    fun `dado lider quando ativa orientacao então ela fica ativa`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        createViewModel()
        advanceUntilIdle()

        viewModel.toggleAtivacao("o3", estaAtiva = false)
        advanceUntilIdle()

        val orientacao = orientacaoRepository.buscarPorId("o3")
        assertTrue(orientacao!!.ativa)
    }

    @Test
    fun `dado operador quando tenta desativar então exibe erro de permissão`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.toggleAtivacao("o1", estaAtiva = true)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.error_only_leader_orientacoes, state.errorMessageRes)
    }

    @Test
    fun `dado lider quando exclui orientacao então ela é removida`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.excluirOrientacao("o1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.orientacoes.size)
        assertFalse(state.orientacoes.any { it.id == "o1" })
    }

    @Test
    fun `dado operador quando tenta excluir então exibe erro de permissão`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.excluirOrientacao("o1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.error_only_leader_orientacoes, state.errorMessageRes)
    }

    @Test
    fun `dado erro exibido quando clearError então limpa erro`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.toggleAtivacao("o1", estaAtiva = true)
        advanceUntilIdle()

        viewModel.clearError()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessageRes)
    }
}
