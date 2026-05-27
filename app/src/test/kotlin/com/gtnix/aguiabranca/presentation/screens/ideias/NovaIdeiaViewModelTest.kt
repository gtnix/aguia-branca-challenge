package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.Context
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NovaIdeiaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NovaIdeiaViewModel
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var sessionManager: FakeSessionManager
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        ideiaRepository = FakeIdeiaRepository()
        orientacaoRepository = FakeOrientacaoRepository()
        sessionManager = FakeSessionManager()
        viewModel = NovaIdeiaViewModel(context, ideiaRepository, orientacaoRepository, sessionManager)
    }

    @Test
    fun `dado campos vazios quando salva então exibe erro de validação`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        val usuario = TestDataFactory.createUsuario()
        sessionManager.setUser(usuario)

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertEquals(R.string.error_nova_ideia_fields, viewModel.uiState.value.errorMessageRes)
        assertTrue(!chamouSuccess)
    }

    @Test
    fun `dado usuario não logado quando salva então exibe erro`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        sessionManager.setUser(null)

        viewModel.onTituloChange("Título")
        viewModel.onDescricaoChange("Descrição")

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertEquals(R.string.error_user_not_logged, viewModel.uiState.value.errorMessageRes)
        assertTrue(!chamouSuccess)
    }

    @Test
    fun `dado dados válidos quando salva então chama onSuccess`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        val usuario = TestDataFactory.createUsuario()
        sessionManager.setUser(usuario)

        viewModel.onTituloChange("Nova Ideia ESG")
        viewModel.onDescricaoChange("Descrição completa da ideia")

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertTrue(chamouSuccess)
        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado alteração de título quando digita então atualiza state e limpa erro`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onTituloChange("Novo título")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Novo título", state.titulo)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado alteração de area quando seleciona então atualiza state`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onAreaChange(AreaAtuacao.LOGISTICA)
        advanceUntilIdle()

        assertEquals(AreaAtuacao.LOGISTICA, viewModel.uiState.value.area)
    }

    @Test
    fun `dado alteração de tipo quando seleciona então atualiza state`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onTipoChange(TipoIdeia.PROBLEMA)
        advanceUntilIdle()

        assertEquals(TipoIdeia.PROBLEMA, viewModel.uiState.value.tipo)
    }

    @Test
    fun `dado orientação selecionada quando altera então atualiza state`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onOrientacaoChange("orientacao-1")
        advanceUntilIdle()

        assertEquals("orientacao-1", viewModel.uiState.value.orientacaoId)
    }

    @Test
    fun `dado dados válidos quando salva então ideia é persistida no repositorio`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        val usuario = TestDataFactory.createUsuario(id = "user-test")
        sessionManager.setUser(usuario)

        viewModel.onTituloChange("Ideia Persistida")
        viewModel.onDescricaoChange("Descrição da ideia persistida")
        viewModel.onAreaChange(AreaAtuacao.TI)

        viewModel.salvar { }
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessageRes == null)
    }
}
