package com.gtnix.aguiabranca.presentation.screens.ideias

import app.cash.turbine.test
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.usecase.ideia.AprovarIdeiaUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.GetIdeiasUseCase
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.util.UiState
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IdeiasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: IdeiasViewModel
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository
    private lateinit var fakeSessionManager: FakeSessionManager

    private val gestorUser = TestDataFactory.createUsuario(
        id = "user-gestor",
        perfil = PerfilUsuario.GESTOR,
        area = AreaAtuacao.OPERACOES
    )

    private val ideias = listOf(
        TestDataFactory.createIdeia(
            id = "ideia-1",
            titulo = "Pendente",
            area = AreaAtuacao.OPERACOES,
            status = StatusIdeia.PENDENTE
        ),
        TestDataFactory.createIdeia(
            id = "ideia-2",
            titulo = "Aprovada",
            area = AreaAtuacao.OPERACOES,
            status = StatusIdeia.APROVADA
        ),
        TestDataFactory.createIdeia(
            id = "ideia-3",
            titulo = "Logistica",
            area = AreaAtuacao.LOGISTICA,
            status = StatusIdeia.PENDENTE
        )
    )

    @Before
    fun setup() {
        fakeIdeiaRepository = FakeIdeiaRepository(ideias)
        fakeSessionManager = FakeSessionManager()
        fakeSessionManager.setUser(gestorUser)

        val getIdeiasUseCase = GetIdeiasUseCase(fakeIdeiaRepository, fakeSessionManager)
        val aprovarIdeiaUseCase = AprovarIdeiaUseCase(fakeIdeiaRepository, fakeSessionManager)
        viewModel = IdeiasViewModel(getIdeiasUseCase, aprovarIdeiaUseCase, fakeSessionManager)
    }

    @Test
    fun `carrega ideias filtradas por perfil do gestor`() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(2, (state as UiState.Success).data.size)
    }

    @Test
    fun `filtro pendentes retorna apenas ideias pendentes`() = runTest {
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroIdeia.PENDENTES)
        advanceUntilIdle()

        viewModel.ideiasFiltradas.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            val filtered = (state as UiState.Success).data
            assertEquals(1, filtered.size)
            assertEquals(StatusIdeia.PENDENTE, filtered.first().status)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `aprovar ideia atualiza status no repositorio`() = runTest {
        advanceUntilIdle()

        val ideiaPendente = ideias.first { it.status == StatusIdeia.PENDENTE && it.area == AreaAtuacao.OPERACOES }
        viewModel.aprovarIdeia(ideiaPendente)
        advanceUntilIdle()

        assertEquals(StatusIdeia.APROVADA, fakeIdeiaRepository.getById(ideiaPendente.id)?.status)
    }

    @Test
    fun `filtro aprovadas retorna apenas ideias aprovadas`() = runTest {
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroIdeia.APROVADAS)
        advanceUntilIdle()

        viewModel.ideiasFiltradas.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            val filtered = (state as UiState.Success).data
            assertEquals(1, filtered.size)
            assertEquals(StatusIdeia.APROVADA, filtered.first().status)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
