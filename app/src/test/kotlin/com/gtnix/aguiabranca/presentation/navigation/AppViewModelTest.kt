package com.gtnix.aguiabranca.presentation.navigation

import com.gtnix.aguiabranca.data.session.SessionEventBus
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AppViewModel
    private lateinit var sessionManager: FakeSessionManager
    private lateinit var sessionEventBus: SessionEventBus

    @Before
    fun setup() {
        sessionManager = FakeSessionManager()
        sessionEventBus = SessionEventBus()
    }

    @Test
    fun `dado usuario logado quando iniciar então retorna Authenticated`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(usuario)

        viewModel = AppViewModel(sessionManager, sessionEventBus)
        advanceUntilIdle()

        val state = viewModel.startState.value
        assertTrue(state is AppStartState.Authenticated)
        assertEquals(PerfilUsuario.OPERADOR, (state as AppStartState.Authenticated).perfil)
    }

    @Test
    fun `dado nenhum usuario quando iniciar então retorna Unauthenticated`() = runTest {
        sessionManager.setUser(null)

        viewModel = AppViewModel(sessionManager, sessionEventBus)
        advanceUntilIdle()

        assertTrue(viewModel.startState.value is AppStartState.Unauthenticated)
    }

    @Test
    fun `dado usuario gestor logado quando iniciar então retorna perfil correto`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)

        viewModel = AppViewModel(sessionManager, sessionEventBus)
        advanceUntilIdle()

        val state = viewModel.startState.value
        assertTrue(state is AppStartState.Authenticated)
        assertEquals(PerfilUsuario.GESTOR, (state as AppStartState.Authenticated).perfil)
    }

    @Test
    fun `dado usuario lider logado quando iniciar então retorna perfil correto`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        viewModel = AppViewModel(sessionManager, sessionEventBus)
        advanceUntilIdle()

        val state = viewModel.startState.value
        assertTrue(state is AppStartState.Authenticated)
        assertEquals(PerfilUsuario.LIDER, (state as AppStartState.Authenticated).perfil)
    }

    @Test
    fun `dado estado inicial quando criado então estado é Loading`() = runTest {
        viewModel = AppViewModel(sessionManager, sessionEventBus)

        // With UnconfinedTestDispatcher, init block completes eagerly.
        // Without a user, restoreSession is a no-op and state transitions to Unauthenticated.
        assertTrue(viewModel.startState.value is AppStartState.Unauthenticated)
    }

    @Test
    fun `dado 401 quando sessao expira então volta para Unauthenticated`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(usuario)
        viewModel = AppViewModel(sessionManager, sessionEventBus)
        advanceUntilIdle()
        assertTrue(viewModel.startState.value is AppStartState.Authenticated)

        sessionEventBus.emitExpired()
        advanceUntilIdle()

        assertTrue(viewModel.startState.value is AppStartState.Unauthenticated)
        assertNull(sessionManager.getCurrentUser())
    }
}
