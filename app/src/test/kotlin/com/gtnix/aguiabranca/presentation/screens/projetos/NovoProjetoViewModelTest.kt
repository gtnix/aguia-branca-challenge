package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.util.MainDispatcherRule
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
class NovoProjetoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NovoProjetoViewModel
    private lateinit var projetoRepository: FakeProjetoRepository
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var usuarioRepository: FakeUsuarioRepository
    private lateinit var sessionManager: FakeSessionManager

    @Before
    fun setup() {
        projetoRepository = FakeProjetoRepository()
        ideiaRepository = FakeIdeiaRepository()
        orientacaoRepository = FakeOrientacaoRepository()
        usuarioRepository = FakeUsuarioRepository()
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel(ideiaId: String? = null) {
        val args = mutableMapOf<String, Any>()
        if (ideiaId != null) {
            args[Destination.NovoProjeto.ARG_IDEIA_ID] = ideiaId
        }
        val savedStateHandle = SavedStateHandle(args)
        viewModel = NovoProjetoViewModel(
            projetoRepository,
            ideiaRepository,
            orientacaoRepository,
            usuarioRepository,
            sessionManager,
            savedStateHandle
        )
    }

    @Test
    fun `dado campos vazios quando salva então exibe erro de validação`() = runTest {
        val usuario = TestDataFactory.createUsuario()
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertEquals(R.string.novo_projeto_erro_campos, viewModel.uiState.value.errorMessageRes)
        assertTrue(!chamouSuccess)
    }

    @Test
    fun `dado usuario não logado quando salva então exibe erro`() = runTest {
        sessionManager.setUser(null)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onNomeChange("Projeto Teste")
        viewModel.onObjetivoChange("Objetivo do projeto")

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertEquals(R.string.error_user_not_logged, viewModel.uiState.value.errorMessageRes)
        assertTrue(!chamouSuccess)
    }

    @Test
    fun `dado dados válidos quando salva então chama onSuccess`() = runTest {
        val usuario = TestDataFactory.createUsuario()
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onNomeChange("Projeto ESG")
        viewModel.onObjetivoChange("Reduzir emissões")

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertTrue(chamouSuccess)
        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado ideia origem quando cria viewmodel então preenche form com dados da ideia`() = runTest {
        val ideia = TestDataFactory.createIdeia(
            id = "ideia-origem",
            titulo = "Ideia Original",
            area = AreaAtuacao.LOGISTICA
        )
        ideiaRepository.salvar(ideia)

        val usuario = TestDataFactory.createUsuario()
        sessionManager.setUser(usuario)

        createViewModel(ideiaId = "ideia-origem")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Ideia Original", state.nome)
        assertEquals(AreaAtuacao.LOGISTICA, state.area)
        assertEquals("ideia-origem", state.ideiaOrigemId)
    }

    @Test
    fun `dado projeto salvo com ideia origem quando salva então vincula ideia ao projeto`() = runTest {
        val ideia = TestDataFactory.createIdeia(id = "ideia-vincular", status = StatusIdeia.APROVADA)
        ideiaRepository.salvar(ideia)

        val usuario = TestDataFactory.createUsuario()
        sessionManager.setUser(usuario)

        createViewModel(ideiaId = "ideia-vincular")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onNomeChange("Projeto da Ideia")
        viewModel.onObjetivoChange("Converter ideia em projeto")

        var chamouSuccess = false
        viewModel.salvar { chamouSuccess = true }
        advanceUntilIdle()

        assertTrue(chamouSuccess)
        val ideiaAtualizada = ideiaRepository.getById("ideia-vincular")
        assertEquals(StatusIdeia.CONVERTIDA_PROJETO, ideiaAtualizada?.status)
    }

    @Test
    fun `dado alteração de area quando seleciona então atualiza state`() = runTest {
        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.onAreaChange(AreaAtuacao.TI)
        advanceUntilIdle()

        assertEquals(AreaAtuacao.TI, viewModel.uiState.value.area)
    }

    @Test
    fun `dado membro quando toggle então adiciona e remove`() = runTest {
        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.toggleMembro("user-membro-1")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.membrosIds.contains("user-membro-1"))

        viewModel.toggleMembro("user-membro-1")
        advanceUntilIdle()
        assertTrue(!viewModel.uiState.value.membrosIds.contains("user-membro-1"))
    }
}
