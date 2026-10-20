package com.gtnix.aguiabranca.presentation.screens.orientacoes

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
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
class NovaOrientacaoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NovaOrientacaoViewModel
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var sessionManager: FakeSessionManager

    @Before
    fun setup() {
        orientacaoRepository = FakeOrientacaoRepository()
        sessionManager = FakeSessionManager()
        viewModel = NovaOrientacaoViewModel(orientacaoRepository, sessionManager)
    }

    @Test
    fun `dado campos vazios quando salvar então retorna erro de validacao`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)

        viewModel.salvar {}
        advanceUntilIdle()

        assertEquals(R.string.novo_projeto_erro_campos, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado usuario nao logado quando salvar então retorna erro`() = runTest {
        sessionManager.setUser(null)

        viewModel.onTituloChange("Título")
        viewModel.onDescricaoChange("Descrição")
        viewModel.salvar {}

        assertEquals(R.string.error_user_not_logged, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado usuario nao lider quando salvar então retorna erro de permissao`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        viewModel.onTituloChange("Título")
        viewModel.onDescricaoChange("Descrição")
        viewModel.salvar {}

        assertEquals(R.string.error_only_leader_create_orientacao, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado dados validos quando lider salvar então salva com sucesso`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)
        var onSuccessCalled = false

        viewModel.onTituloChange("Nova Orientação")
        viewModel.onDescricaoChange("Descrição da orientação")
        viewModel.onCategoriaChange(CategoriaOrientacao.REDUCAO_CUSTOS)
        viewModel.onPrioridadeChange(1)
        viewModel.salvar { onSuccessCalled = true }
        advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `dado titulo quando alterar então limpa mensagem de erro`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)
        viewModel.salvar {}
        advanceUntilIdle()
        assertEquals(R.string.novo_projeto_erro_campos, viewModel.uiState.value.errorMessageRes)

        viewModel.onTituloChange("Novo título")

        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado descricao quando alterar então limpa mensagem de erro`() = runTest {
        val lider = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(lider)
        viewModel.salvar {}
        advanceUntilIdle()
        assertEquals(R.string.novo_projeto_erro_campos, viewModel.uiState.value.errorMessageRes)

        viewModel.onDescricaoChange("Nova descrição")

        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado categoria quando alterar então atualiza estado`() = runTest {
        viewModel.onCategoriaChange(CategoriaOrientacao.REDUCAO_CUSTOS)

        assertEquals(CategoriaOrientacao.REDUCAO_CUSTOS, viewModel.uiState.value.categoria)
    }

    @Test
    fun `dado prioridade quando alterar então atualiza estado`() = runTest {
        viewModel.onPrioridadeChange(1)

        assertEquals(1, viewModel.uiState.value.prioridade)
    }
}
