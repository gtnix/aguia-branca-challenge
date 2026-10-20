package com.gtnix.aguiabranca.presentation.screens.perfil

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PerfilViewModel
    private lateinit var usuarioRepository: FakeUsuarioRepository
    private lateinit var sessionManager: FakeSessionManager

    private val usuario = TestDataFactory.createUsuario(
        id = "user-perfil",
        nome = "João Silva",
        email = "joao@aguiabranca.com.br",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    @Before
    fun setup() {
        usuarioRepository = FakeUsuarioRepository()
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel() {
        viewModel = PerfilViewModel(usuarioRepository, sessionManager)
    }

    @Test
    fun `dado usuario logado quando iniciar então expõe os dados do cadastro`() = runTest {
        sessionManager.setUser(usuario)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("João Silva", state.nome)
        assertEquals("joao@aguiabranca.com.br", state.email)
        assertEquals(AreaAtuacao.OPERACOES, state.area)
        assertEquals(DivisaoNegocio.LOGISTICA, state.divisao)
        assertEquals(PerfilUsuario.OPERADOR, state.perfil)
        assertEquals('J', state.inicialNome)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario nao logado quando iniciar então não mostra dados`() = runTest {
        sessionManager.setUser(null)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.perfil)
    }

    @Test
    fun `dado usuario logado quando fazer logout então marca sucesso`() = runTest {
        sessionManager.setUser(usuario)
        createViewModel()
        advanceUntilIdle()

        viewModel.onLogout()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.logoutSuccess)
        assertNull(sessionManager.getCurrentUser())
    }
}
