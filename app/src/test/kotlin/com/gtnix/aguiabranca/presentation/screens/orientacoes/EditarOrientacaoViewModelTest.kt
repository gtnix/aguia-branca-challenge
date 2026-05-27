package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
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
class EditarOrientacaoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EditarOrientacaoViewModel
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var sessionManager: FakeSessionManager

    private val orientacaoExistente = TestDataFactory.createOrientacao(
        id = "orientacao-edit-1",
        titulo = "Orientação Original"
    )

    @Before
    fun setup() {
        orientacaoRepository = FakeOrientacaoRepository(listOf(orientacaoExistente))
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel(orientacaoId: String = orientacaoExistente.id) {
        val savedStateHandle = SavedStateHandle(
            mapOf(Destination.EditarOrientacao.ARG_ORIENTACAO_ID to orientacaoId)
        )
        viewModel = EditarOrientacaoViewModel(orientacaoRepository, sessionManager, savedStateHandle)
    }

    @Test
    fun `dado orientacao existente quando carregar então preenche campos`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(orientacaoExistente.titulo, state.titulo)
        assertEquals(orientacaoExistente.descricao, state.descricao)
        assertEquals(orientacaoExistente.categoria, state.categoria)
        assertNotNull(state.orientacaoOriginal)
    }

    @Test
    fun `dado orientacao inexistente quando carregar então retorna erro`() = runTest {
        createViewModel(orientacaoId = "id-inexistente")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.error_orientacao_not_found, state.errorMessageRes)
    }

    @Test
    fun `dado campos vazios quando salvar então retorna erro de validacao`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)
        createViewModel()
        advanceUntilIdle()

        viewModel.onTituloChange("")
        viewModel.onDescricaoChange("")
        viewModel.salvar {}

        assertEquals(R.string.novo_projeto_erro_campos, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado usuario nao logado quando salvar então retorna erro`() = runTest {
        sessionManager.setUser(null)
        createViewModel()
        advanceUntilIdle()

        viewModel.salvar {}

        assertEquals(R.string.error_user_not_logged, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado usuario nao lider quando salvar então retorna erro de permissao`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        viewModel.salvar {}

        assertEquals(R.string.error_only_leader_create_orientacao, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado dados validos quando lider salvar então atualiza com sucesso`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)
        createViewModel()
        advanceUntilIdle()
        var onSuccessCalled = false

        viewModel.onTituloChange("Título Atualizado")
        viewModel.onDescricaoChange("Descrição Atualizada")
        viewModel.salvar { onSuccessCalled = true }
        advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `dado titulo quando alterar então limpa mensagem de erro`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)
        createViewModel()
        advanceUntilIdle()
        viewModel.onTituloChange("")
        viewModel.onDescricaoChange("")
        viewModel.salvar {}
        assertEquals(R.string.novo_projeto_erro_campos, viewModel.uiState.value.errorMessageRes)

        viewModel.onTituloChange("Novo título")

        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado categoria quando alterar então atualiza estado`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.onCategoriaChange(CategoriaOrientacao.REDUCAO_CUSTOS)

        assertEquals(CategoriaOrientacao.REDUCAO_CUSTOS, viewModel.uiState.value.categoria)
    }

    @Test
    fun `dado prioridade quando alterar então atualiza estado`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.onPrioridadeChange(5)

        assertEquals(5, viewModel.uiState.value.prioridade)
    }
}
