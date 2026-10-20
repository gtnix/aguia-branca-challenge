package com.gtnix.aguiabranca.presentation.screens.auth

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
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
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var usuarioRepository: FakeUsuarioRepository
    private lateinit var sessionManager: FakeSessionManager
    private val networkMonitor = mockk<NetworkMonitor>()

    @Before
    fun setup() {
        every { networkMonitor.isOnline() } returns true
        usuarioRepository = FakeUsuarioRepository()
        sessionManager = FakeSessionManager()
        viewModel = LoginViewModel(usuarioRepository, sessionManager, networkMonitor)
    }

    @Test
    fun `login com campos vazios retorna erro de validacao`() = runTest {
        viewModel.onLoginClick()

        assertEquals(R.string.login_error_empty_fields, viewModel.uiState.value.errorMessageRes)
        assertFalse(viewModel.uiState.value.loginSuccess)
    }

    @Test
    fun `login com credenciais validas retorna sucesso`() = runTest {
        val usuario = TestDataFactory.createUsuario(email = "ana.oliveira@aguiabranca.com.br")
        usuarioRepository.setAuthResult(usuario)

        viewModel.onEmailChange("ana.oliveira@aguiabranca.com.br")
        viewModel.onSenhaChange("123456")
        viewModel.onLoginClick()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.loginSuccess)
        assertEquals(usuario, sessionManager.getCurrentUser())
    }

    @Test
    fun `login com credenciais invalidas retorna erro`() = runTest {
        usuarioRepository.setAuthResult(null)

        viewModel.onEmailChange("invalido@aguiabranca.com.br")
        viewModel.onSenhaChange("errada")
        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals(R.string.login_error_invalid_credentials, viewModel.uiState.value.errorMessageRes)
        assertFalse(viewModel.uiState.value.loginSuccess)
    }

    @Test
    fun `login sem rede retorna erro de conexao`() = runTest {
        every { networkMonitor.isOnline() } returns false
        viewModel.onEmailChange("ana.oliveira@aguiabranca.com.br")
        viewModel.onSenhaChange("123456")
        viewModel.onLoginClick()

        assertEquals(R.string.login_error_network, viewModel.uiState.value.errorMessageRes)
        assertFalse(viewModel.uiState.value.loginSuccess)
        assertNull(sessionManager.getCurrentUser())
    }

    @Test
    fun `demo login do gestor autentica com usuario seed`() = runTest {
        val gestor = TestDataFactory.createUsuario(
            email = "ana.oliveira@aguiabranca.com.br",
            perfil = PerfilUsuario.GESTOR
        )
        usuarioRepository.setAuthResult(gestor)

        viewModel.onDemoLogin(PerfilUsuario.GESTOR)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.loginSuccess)
        assertEquals(PerfilUsuario.GESTOR, viewModel.uiState.value.perfil)
        assertEquals(gestor, sessionManager.getCurrentUser())
    }

    @Test
    fun `alterar email limpa mensagem de erro`() = runTest {
        viewModel.onLoginClick()
        assertEquals(R.string.login_error_empty_fields, viewModel.uiState.value.errorMessageRes)

        viewModel.onEmailChange("teste@aguiabranca.com.br")

        assertNull(viewModel.uiState.value.errorMessageRes)
    }
}
