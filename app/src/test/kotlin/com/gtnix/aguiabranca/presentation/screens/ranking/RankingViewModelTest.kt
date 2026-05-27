package com.gtnix.aguiabranca.presentation.screens.ranking

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
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
class RankingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RankingViewModel
    private lateinit var sessionManager: FakeSessionManager
    private val getRankingUseCase = mockk<GetRankingUseCase>()
    private val calcularPontuacao = mockk<CalcularPontuacaoUseCase>()

    private val operador = TestDataFactory.createUsuario(
        id = "user-ranking",
        perfil = PerfilUsuario.OPERADOR
    )

    private val rankingEntradas = listOf(
        RankingEntrada(
            usuarioId = "user-ranking",
            nome = "Usuário Teste",
            pontos = 100,
            nivel = NivelUsuario.EM_ASCENSAO,
            inicialNome = 'U',
            perfil = PerfilUsuario.OPERADOR,
            cargo = "Operador",
            divisao = DivisaoNegocio.LOGISTICA
        ),
        RankingEntrada(
            usuarioId = "user-2",
            nome = "Outro Usuário",
            pontos = 80,
            nivel = NivelUsuario.EM_ASCENSAO,
            inicialNome = 'O',
            perfil = PerfilUsuario.OPERADOR,
            cargo = "Operador",
            divisao = DivisaoNegocio.LOGISTICA
        )
    )

    private val resultadoRanking = ResultadoRanking(
        topRanking = rankingEntradas,
        posicaoUsuarioLogado = 1,
        entradaUsuarioLogado = rankingEntradas.first(),
        perfilFiltro = PerfilUsuario.OPERADOR,
        totalParticipantes = 2
    )

    private val pontuacaoMock = Pontuacao(
        total = 100,
        nivel = NivelUsuario.EM_ASCENSAO,
        progressoProximoNivel = 0.5f,
        pontosParaProximoNivel = 20,
        conquistas = emptyList(),
        estatisticas = Estatisticas()
    )

    @Before
    fun setup() {
        sessionManager = FakeSessionManager()

        coEvery {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = any(),
                divisaoFilter = any<DivisaoFilter>(),
                divisaoUsuario = any()
            )
        } returns resultadoRanking

        coEvery { calcularPontuacao.calcular(any(), any<PerfilUsuario>()) } returns pontuacaoMock
    }

    private fun createViewModel() {
        // Repositórios reais (fakes) acessam dados vazios — coerente com testes
        // que só checam navegação/estado de UI. Quando precisam de dados reais
        // (ex.: TeamInsight com valores), a expectativa do teste compensa.
        val fakeIdeia: IdeiaRepository = FakeIdeiaRepository(emptyList())
        val fakeOrientacao: OrientacaoRepository = FakeOrientacaoRepository(emptyList())
        val fakeProjeto: ProjetoRepository = FakeProjetoRepository(emptyList())
        val fakeUsuario: UsuarioRepository = FakeUsuarioRepository()
        viewModel = RankingViewModel(
            getRankingUseCase = getRankingUseCase,
            calcularPontuacao = calcularPontuacao,
            sessionManager = sessionManager,
            ideiaRepository = fakeIdeia,
            orientacaoRepository = fakeOrientacao,
            projetoRepository = fakeProjeto,
            usuarioRepository = fakeUsuario
        )
    }

    @Test
    fun `dado usuario logado quando iniciar então carrega ranking`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.topRanking.size)
        assertEquals(1, state.posicaoUsuarioLogado)
        assertEquals(PerfilUsuario.OPERADOR, state.tabSelecionada)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario nao logado quando carregar ranking então retorna erro`() = runTest {
        sessionManager.setUser(null)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.error_user_not_logged, state.errorMessageRes)
    }

    @Test
    fun `dado tab selecionada quando mudar tab então carrega novo ranking`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        val resultadoGestor = ResultadoRanking(
            topRanking = emptyList(),
            posicaoUsuarioLogado = null,
            entradaUsuarioLogado = null,
            perfilFiltro = PerfilUsuario.GESTOR,
            totalParticipantes = 0
        )
        coEvery {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = eq(PerfilUsuario.GESTOR),
                divisaoFilter = any<DivisaoFilter>(),
                divisaoUsuario = any()
            )
        } returns resultadoGestor

        viewModel.selecionarTab(PerfilUsuario.GESTOR)
        advanceUntilIdle()

        assertEquals(PerfilUsuario.GESTOR, viewModel.uiState.value.tabSelecionada)
    }

    @Test
    fun `dado mesma tab selecionada quando mudar tab então nao recarrega`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        val stateAntes = viewModel.uiState.value
        viewModel.selecionarTab(PerfilUsuario.OPERADOR)
        advanceUntilIdle()

        assertEquals(stateAntes.tabSelecionada, viewModel.uiState.value.tabSelecionada)
    }

    @Test
    fun `dado celebration quando dismiss então atualiza estado`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        viewModel.dismissPositionCelebration()

        assertFalse(viewModel.uiState.value.showPositionCelebration)
        assertTrue(viewModel.uiState.value.highlightUserCard)
    }

    @Test
    fun `dado highlight quando limpar então reseta estado`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        viewModel.dismissPositionCelebration()
        assertTrue(viewModel.uiState.value.highlightUserCard)

        viewModel.clearHighlightUserCard()
        assertFalse(viewModel.uiState.value.highlightUserCard)
    }

    @Test
    fun `dado erro no use case quando carregar ranking então retorna erro`() = runTest {
        coEvery {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = any(),
                divisaoFilter = any<DivisaoFilter>(),
                divisaoUsuario = any()
            )
        } throws RuntimeException("Erro")
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        assertEquals(R.string.error_load_ranking, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `dado divisao filter quando mudar então recarrega ranking`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        viewModel.selecionarDivisaoFilter(DivisaoFilter.GRUPO_COMPLETO)
        advanceUntilIdle()

        assertEquals(DivisaoFilter.GRUPO_COMPLETO, viewModel.uiState.value.selectedDivisaoFilter)
    }
}
