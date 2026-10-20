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
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.IndicacaoInovaGab
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.fake.FakeGamificacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
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
    private lateinit var gamificacao: FakeGamificacaoRepository
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
        gamificacao = FakeGamificacaoRepository()

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
        viewModel = RankingViewModel(
            getRankingUseCase = getRankingUseCase,
            calcularPontuacao = calcularPontuacao,
            sessionManager = sessionManager,
            gamificacaoRepository = gamificacao
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
        assertEquals(PerfilUsuario.OPERADOR, state.perfilUsuarioLogado)
        assertTrue(state.podioOperadores.isEmpty())
        assertNull(state.indicacoes)
        assertEquals(0, gamificacao.indicacoesChamadas)
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
    fun `dado operador quando carregar então usa só o pódio da divisão`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(rankingEntradas, state.topRanking)
        coVerify(exactly = 0) {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = any(),
                divisaoFilter = eq(DivisaoFilter.GRUPO_COMPLETO),
                divisaoUsuario = any()
            )
        }
    }

    @Test
    fun `dado operador quando carregar então gincana é do grupo inteiro`() = runTest {
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(DivisaoFilter.GRUPO_COMPLETO), gamificacao.equipesEscopos)
    }

    @Test
    fun `dado empate com quem está acima quando calcular próxima posição então falta 1 XP`() {
        val acima = rankingEntradas.first().copy(usuarioId = "joana", nome = "Joana Souza", pontos = 71)
        val eu = rankingEntradas.first().copy(pontos = 71)

        val proxima = RankingViewModel.proximaPosicao(listOf(acima, eu), posicao = 2, eu = eu)

        assertEquals("Joana", proxima?.nome)
        assertEquals(1, proxima?.xpFaltando)
        assertTrue(proxima?.empatado == true)
    }

    @Test
    fun `dado primeiro lugar quando calcular próxima posição então não há meta`() {
        assertNull(RankingViewModel.proximaPosicao(rankingEntradas, posicao = 1, eu = rankingEntradas.first()))
    }

    @Test
    fun `dado painel do servidor quando carregar então expõe missões e equipes`() = runTest {
        gamificacao.equipesResult = Result.Success(
            listOf(EquipeRanking(1, "CD Serra", DivisaoNegocio.LOGISTICA, 3, 150, 50, minhaEquipe = true))
        )
        sessionManager.setUser(operador)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(pontuacaoMock, state.painel)
        assertEquals("CD Serra", state.equipes.single().equipe)
    }

    @Test
    fun `dado gestor quando carregar então traz gincana e pódio dos operadores`() = runTest {
        val gestores = listOf(
            rankingEntradas.first().copy(
                usuarioId = "gestor-x",
                nome = "Ana",
                perfil = PerfilUsuario.GESTOR,
                cargo = "Gestor"
            )
        )
        coEvery {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = eq(PerfilUsuario.GESTOR),
                divisaoFilter = any<DivisaoFilter>(),
                divisaoUsuario = any()
            )
        } returns ResultadoRanking(
            topRanking = gestores,
            posicaoUsuarioLogado = 1,
            entradaUsuarioLogado = gestores.first(),
            perfilFiltro = PerfilUsuario.GESTOR,
            totalParticipantes = 1
        )
        gamificacao.equipesResult = Result.Success(
            listOf(EquipeRanking(1, "CD Serra", DivisaoNegocio.LOGISTICA, 3, 150, 50, minhaEquipe = false))
        )
        sessionManager.setUser(TestDataFactory.createUsuario(id = "gestor-x", perfil = PerfilUsuario.GESTOR))
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(PerfilUsuario.GESTOR, state.perfilUsuarioLogado)
        assertEquals(gestores, state.topRanking)
        assertEquals(rankingEntradas.take(3), state.podioOperadores)
        assertEquals("CD Serra", state.equipes.single().equipe)
        assertEquals(0, gamificacao.indicacoesChamadas)
    }

    @Test
    fun `dado lider quando carregar então traz indicações Imova-se e gincana`() = runTest {
        val lideres = listOf(
            rankingEntradas.first().copy(
                usuarioId = "lider-x",
                nome = "Marcos",
                perfil = PerfilUsuario.LIDER,
                cargo = "Líder"
            )
        )
        coEvery {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = eq(PerfilUsuario.LIDER),
                divisaoFilter = any<DivisaoFilter>(),
                divisaoUsuario = any()
            )
        } returns ResultadoRanking(
            topRanking = lideres,
            posicaoUsuarioLogado = 1,
            entradaUsuarioLogado = lideres.first(),
            perfilFiltro = PerfilUsuario.LIDER,
            totalParticipantes = 1
        )
        gamificacao.equipesResult = Result.Success(
            listOf(EquipeRanking(1, "CD Serra", DivisaoNegocio.LOGISTICA, 3, 150, 50, minhaEquipe = false))
        )
        gamificacao.indicacoesResult = Result.Success(
            IndicacoesInovaGab(
                temporada = null,
                criterio = "Top 3 por divisão",
                indicados = listOf(
                    IndicacaoInovaGab(
                        divisao = DivisaoNegocio.LOGISTICA,
                        posicaoNaDivisao = 1,
                        usuarioId = "joana",
                        nome = "Joana",
                        equipe = "CD Serra",
                        xpTemporada = 74
                    )
                )
            )
        )
        sessionManager.setUser(TestDataFactory.createUsuario(id = "lider-x", perfil = PerfilUsuario.LIDER))
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(PerfilUsuario.LIDER, state.perfilUsuarioLogado)
        assertEquals(lideres, state.topRanking)
        assertTrue(state.podioOperadores.isEmpty())
        assertEquals("Joana", state.indicacoes?.indicados?.single()?.nome)
        assertEquals("CD Serra", state.equipes.single().equipe)
        assertEquals(1, gamificacao.indicacoesChamadas)
    }

    @Test
    fun `dado operador fora do top 3 quando calcular zona então devolve XP que falta`() {
        val ranking = listOf(150, 120, 90, 60).mapIndexed { i, xp ->
            rankingEntradas.first().copy(usuarioId = "op-$i", pontos = xp, zonaPremio = i < 3)
        }
        val eu = ranking.last()

        assertEquals(31, RankingViewModel.xpParaZonaPremio(ranking, eu))
    }

    @Test
    fun `dado operador na zona de prêmio quando calcular zona então não mostra meta`() {
        val eu = rankingEntradas.first().copy(zonaPremio = true)

        assertNull(RankingViewModel.xpParaZonaPremio(rankingEntradas, eu))
    }

}
