package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrientacaoDetalheViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OrientacaoDetalheViewModel
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var projetoRepository: FakeProjetoRepository

    private val orientacao = TestDataFactory.createOrientacao(
        id = "orientacao-123",
        titulo = "Redução de Custos"
    )

    @Before
    fun setup() {
        orientacaoRepository = FakeOrientacaoRepository(listOf(orientacao))
        ideiaRepository = FakeIdeiaRepository()
        projetoRepository = FakeProjetoRepository()
    }

    private fun createViewModel(orientacaoId: String = "orientacao-123") {
        val savedStateHandle = SavedStateHandle(
            mapOf(Destination.OrientacaoDetalhe.ARG_ORIENTACAO_ID to orientacaoId)
        )
        viewModel = OrientacaoDetalheViewModel(
            orientacaoRepository,
            ideiaRepository,
            projetoRepository,
            savedStateHandle
        )
    }

    @Test
    fun `dado orientacao existente quando carrega então exibe dados`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.orientacao)
        assertEquals("Redução de Custos", state.orientacao?.titulo)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado orientacao inexistente quando carrega então exibe erro not found`() = runTest {
        createViewModel(orientacaoId = "inexistente")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.orientacao)
        assertEquals(R.string.orientacao_detalhe_not_found, state.errorMessageRes)
    }

    @Test
    fun `dado ideias alinhadas quando carrega então exibe lista de ideias`() = runTest {
        val ideia1 = TestDataFactory.createIdeia(id = "i1", titulo = "Ideia 1").copy(orientacaoId = "orientacao-123")
        val ideia2 = TestDataFactory.createIdeia(id = "i2", titulo = "Ideia 2").copy(orientacaoId = "orientacao-123")
        val ideiaOutra = TestDataFactory.createIdeia(id = "i3", titulo = "Outra").copy(orientacaoId = "outra-orientacao")
        ideiaRepository = FakeIdeiaRepository(listOf(ideia1, ideia2, ideiaOutra))

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.ideiasAlinhadas.size)
    }

    @Test
    fun `dado projetos vinculados quando carrega então exibe lista de projetos`() = runTest {
        val projeto1 = TestDataFactory.createProjeto(id = "p1", status = StatusProjeto.EM_ANDAMENTO)
            .copy(orientacaoId = "orientacao-123")
        val projeto2 = TestDataFactory.createProjeto(id = "p2", status = StatusProjeto.CONCLUIDO)
            .copy(orientacaoId = "orientacao-123")
        projetoRepository = FakeProjetoRepository(listOf(projeto1, projeto2))

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.projetosVinculados.size)
    }

    @Test
    fun `dado projetos vinculados quando calcula projetosEmAndamento então conta corretamente`() = runTest {
        val projetoAndamento = TestDataFactory.createProjeto(id = "p1", status = StatusProjeto.EM_ANDAMENTO)
            .copy(orientacaoId = "orientacao-123")
        val projetoPlanejado = TestDataFactory.createProjeto(id = "p2", status = StatusProjeto.PLANEJADO)
            .copy(orientacaoId = "orientacao-123")
        val projetoConcluido = TestDataFactory.createProjeto(id = "p3", status = StatusProjeto.CONCLUIDO)
            .copy(orientacaoId = "orientacao-123")
        projetoRepository = FakeProjetoRepository(listOf(projetoAndamento, projetoPlanejado, projetoConcluido))

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.projetosEmAndamento)
    }

    @Test
    fun `dado sem ideias alinhadas quando carrega então lista vazia`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.ideiasAlinhadas.size)
    }
}
