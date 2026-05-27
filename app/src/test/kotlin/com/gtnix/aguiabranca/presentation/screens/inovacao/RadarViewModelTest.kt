package com.gtnix.aguiabranca.presentation.screens.inovacao

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.fake.FakeInovacaoAbertaRepository
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RadarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RadarViewModel
    private lateinit var repository: FakeInovacaoAbertaRepository

    private val startups = listOf(
        StartupPartner(
            id = "startup-1",
            nome = "GreenTech",
            setor = "Sustentabilidade",
            descricao = "Soluções verdes",
            matchScore = 95
        ),
        StartupPartner(
            id = "startup-2",
            nome = "LogiAI",
            setor = "Logística",
            descricao = "IA para logística",
            matchScore = 88
        )
    )

    @Before
    fun setup() {
        repository = FakeInovacaoAbertaRepository(startups)
    }

    @Test
    fun `dado startups disponiveis quando iniciar então carrega lista`() = runTest {
        viewModel = RadarViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.startups.size)
        assertEquals("GreenTech", state.startups.first().nome)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado nenhuma startup quando iniciar então retorna lista vazia`() = runTest {
        repository = FakeInovacaoAbertaRepository(emptyList())
        viewModel = RadarViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(0, state.startups.size)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado erro quando iniciar então retorna erro`() = runTest {
        repository = FakeInovacaoAbertaRepository(shouldThrow = true)
        viewModel = RadarViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.error_radar_startups, state.errorMessageRes)
    }
}
