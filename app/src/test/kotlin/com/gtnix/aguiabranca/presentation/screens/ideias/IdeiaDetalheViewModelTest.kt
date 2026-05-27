package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.usecase.ideia.AprovarIdeiaUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
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
class IdeiaDetalheViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: IdeiaDetalheViewModel
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var sessionManager: FakeSessionManager
    private val aprovarIdeiaUseCase = mockk<AprovarIdeiaUseCase>()

    private val ideia = TestDataFactory.createIdeia(
        id = "ideia-123",
        titulo = "Minha Ideia",
        status = StatusIdeia.PENDENTE
    )

    @Before
    fun setup() {
        ideiaRepository = FakeIdeiaRepository(listOf(ideia))
        orientacaoRepository = FakeOrientacaoRepository()
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel(ideiaId: String = "ideia-123") {
        val savedStateHandle = SavedStateHandle(mapOf(Destination.IdeiaDetalhe.ARG_IDEIA_ID to ideiaId))
        viewModel = IdeiaDetalheViewModel(
            ideiaRepository,
            orientacaoRepository,
            aprovarIdeiaUseCase,
            sessionManager,
            savedStateHandle
        )
    }

    @Test
    fun `dado ideia existente quando carrega então exibe dados`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.ideia)
        assertEquals("Minha Ideia", state.ideia?.titulo)
        assertEquals(PerfilUsuario.GESTOR, state.perfil)
    }

    @Test
    fun `dado ideia inexistente quando carrega então ideia é null`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel(ideiaId = "inexistente")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.ideia)
    }

    @Test
    fun `dado gestor quando aprova ideia com sucesso então actionSuccess é true`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)
        coEvery { aprovarIdeiaUseCase(any(), any(), any(), any(), any()) } returns Result.Success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.aprovar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.actionSuccess)
    }

    @Test
    fun `dado gestor quando reprova sem feedback então exibe erro`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)

        createViewModel()
        advanceUntilIdle()

        viewModel.reprovar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.error_feedback_required_reject, state.errorMessageRes)
    }

    @Test
    fun `dado operador quando tenta aprovar então não faz nada`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        createViewModel()
        advanceUntilIdle()

        viewModel.aprovar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.actionSuccess)
    }

    @Test
    fun `dado usuario quando altera feedback então atualiza state e limpa erro`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel()
        advanceUntilIdle()

        viewModel.onFeedbackChange("Ótima ideia!")

        val state = viewModel.uiState.value
        assertEquals("Ótima ideia!", state.feedback)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario quando vota então jaVotou é true`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel()
        advanceUntilIdle()

        viewModel.upvote()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.jaVotou)
    }

    @Test
    fun `dado usuario que ja votou quando vota novamente então não incrementa`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel()
        advanceUntilIdle()

        viewModel.upvote()
        advanceUntilIdle()
        val upvotesAposUm = viewModel.uiState.value.ideia?.upvotes

        viewModel.upvote()
        advanceUntilIdle()

        assertEquals(upvotesAposUm, viewModel.uiState.value.ideia?.upvotes)
    }
}
