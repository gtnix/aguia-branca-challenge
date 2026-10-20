package com.gtnix.aguiabranca.presentation.screens.ideias

import android.util.Log
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any<String>(), any()) } returns 0
        ideiaRepository = FakeIdeiaRepository()
        orientacaoRepository = FakeOrientacaoRepository()
        sessionManager = FakeSessionManager()
        viewModel = NovaIdeiaViewModel(ideiaRepository, orientacaoRepository, sessionManager)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
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
        viewModel.onOrientacaoChange("orientacao-1")

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
    fun `dado dados sem orientacao quando salva então exibe erro`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        sessionManager.setUser(TestDataFactory.createUsuario())
        viewModel.onTituloChange("Título")
        viewModel.onDescricaoChange("Descrição")

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertEquals(R.string.error_ideia_orientacao_required, viewModel.uiState.value.errorMessageRes)
        assertTrue(!chamouSuccess)
    }

    @Test
    fun `dado gestor quando salva então bloqueia cadastro`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        viewModel.onTituloChange("Título")
        viewModel.onDescricaoChange("Descrição")
        viewModel.onOrientacaoChange("orientacao-1")

        viewModel.salvar { }
        advanceUntilIdle()

        assertEquals(R.string.error_only_operator_create_ideia, viewModel.uiState.value.errorMessageRes)
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
        viewModel.onOrientacaoChange("orientacao-1")

        viewModel.salvar { }
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessageRes == null)
    }

    @Test
    fun `contexto do colaborador vai junto e impacto e esforco ficam para o gestor`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        sessionManager.setUser(TestDataFactory.createUsuario(id = "user-test"))

        viewModel.onTituloChange("Manifesto digital")
        viewModel.onDescricaoChange("Trocar vias impressas por QR code")
        viewModel.onOrientacaoChange("orientacao-1")
        viewModel.onPublicoAfetadoChange("   ")
        viewModel.onFrequenciaChange(FrequenciaProblema.DIARIA)

        viewModel.salvar { }
        advanceUntilIdle()

        val salva = ideiaRepository.listarTodas().first().single()
        assertNull(salva.problema)
        assertNull(salva.publicoAfetado)
        assertEquals(FrequenciaProblema.DIARIA, salva.frequencia)
        assertNull(salva.beneficio)
        assertEquals(0, salva.impactoEstimado)
        assertEquals(0, salva.esforcoEstimado)
    }

    @Test
    fun `tocar na frequencia selecionada limpa a resposta`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }

        viewModel.onFrequenciaChange(FrequenciaProblema.SEMANAL)
        viewModel.onFrequenciaChange(FrequenciaProblema.SEMANAL)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.frequencia)
    }

    @Test
    fun `area parte da area do perfil do colaborador`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(area = AreaAtuacao.LOGISTICA))
        val vm = NovaIdeiaViewModel(ideiaRepository, orientacaoRepository, sessionManager)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.uiState.collect { }
        }
        advanceUntilIdle()

        assertEquals(AreaAtuacao.LOGISTICA, vm.uiState.value.area)
    }
}
