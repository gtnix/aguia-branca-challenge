package com.gtnix.aguiabranca.presentation.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase,
    private val calcularPontuacao: CalcularPontuacaoUseCase,
    private val sessionManager: SessionManager,
    private val ideiaRepository: IdeiaRepository,
    private val orientacaoRepository: OrientacaoRepository,
    private val projetoRepository: ProjetoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    private val usuarioLogado = sessionManager.getCurrentUser()
    private val perfilUsuarioLogado: PerfilUsuario =
        usuarioLogado?.perfil ?: PerfilUsuario.OPERADOR
    private val divisaoUsuario: DivisaoNegocio =
        usuarioLogado?.divisao ?: DivisaoNegocio.LOGISTICA

    init {
        _uiState.update {
            it.copy(
                perfilUsuarioLogado = perfilUsuarioLogado,
                tabSelecionada = perfilUsuarioLogado,
                divisaoUsuario = divisaoUsuario
            )
        }
        carregarRanking(perfilUsuarioLogado)
    }

    fun selecionarTab(perfil: PerfilUsuario) {
        if (perfil == _uiState.value.tabSelecionada) return
        _uiState.update { it.copy(tabSelecionada = perfil) }
        carregarRanking(perfil)
    }

    fun selecionarDivisaoFilter(filter: DivisaoFilter) {
        if (filter == _uiState.value.selectedDivisaoFilter) return
        _uiState.update { it.copy(selectedDivisaoFilter = filter) }
        carregarRanking(_uiState.value.tabSelecionada)
    }

    fun refresh() {
        carregarRanking(_uiState.value.tabSelecionada, isRefresh = true)
    }

    fun dismissPositionCelebration() {
        _uiState.update {
            it.copy(
                showPositionCelebration = false,
                highlightUserCard = true
            )
        }
    }

    fun clearHighlightUserCard() {
        _uiState.update { it.copy(highlightUserCard = false) }
    }

    private fun carregarRanking(perfilFiltro: PerfilUsuario, isRefresh: Boolean = false) {
        val userId = sessionManager.getCurrentUser()?.id
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_user_not_logged) }
            return
        }

        val state = _uiState.value
        val isAnalysisMode = perfilFiltro != perfilUsuarioLogado

        _uiState.update {
            it.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh,
                usuarioLogadoId = userId,
                isAnalysisMode = isAnalysisMode,
                analysisHeaderText = if (isAnalysisMode) analysisHeaderFor(perfilFiltro) else null
            )
        }

        viewModelScope.launch {
            try {
                val resultado = getRankingUseCase.executar(
                    usuarioLogadoId = userId,
                    perfilFiltro = perfilFiltro,
                    divisaoFilter = state.selectedDivisaoFilter,
                    divisaoUsuario = divisaoUsuario
                )

                val rankingFiltrado = resultado.topRanking.filter { it.perfil == perfilFiltro }
                val entradaUsuario = if (isAnalysisMode) null else resultado.entradaUsuarioLogado
                val posicaoUsuario = if (isAnalysisMode) null else resultado.posicaoUsuarioLogado

                val gamificationStats = if (isAnalysisMode) {
                    GamificationStats()
                } else {
                    buildGamificationStats(
                        entrada = entradaUsuario,
                        perfil = perfilUsuarioLogado,
                        posicao = posicaoUsuario,
                        userId = userId
                    )
                }

                val conquistasUsuario = if (isAnalysisMode) {
                    emptyList()
                } else {
                    calcularPontuacao.calcular(userId, perfilUsuarioLogado).conquistas
                }

                val teamInsight = if (isAnalysisMode) {
                    buildTeamInsight(rankingFiltrado, perfilFiltro)
                } else {
                    null
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        topRanking = rankingFiltrado,
                        posicaoUsuarioLogado = posicaoUsuario,
                        entradaUsuarioLogado = entradaUsuario,
                        tabSelecionada = perfilFiltro,
                        gamificationStats = gamificationStats,
                        conquistasUsuario = conquistasUsuario,
                        teamInsight = teamInsight,
                        isAnalysisMode = isAnalysisMode,
                        analysisHeaderText = if (isAnalysisMode) analysisHeaderFor(perfilFiltro) else null,
                        errorMessageRes = null,
                        showPositionCelebration = false,
                        positionCelebrationMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, errorMessageRes = R.string.error_load_ranking)
                }
            }
        }
    }

    private suspend fun buildGamificationStats(
        entrada: RankingEntrada?,
        perfil: PerfilUsuario,
        posicao: Int?,
        userId: String
    ): GamificationStats {
        val pontos = entrada?.pontos ?: 0
        return when (perfil) {
            PerfilUsuario.OPERADOR -> GamificationStats(
                ideiasEnviadas = (pontos / CalcularPontuacaoUseCase.PONTOS_OPERADOR_POR_IDEIA)
                    .coerceAtLeast(1),
                streakDias = if (pontos >= 50) 3 else if (pontos >= 20) 2 else 1,
                desafioSemanalProgresso = ((pontos / CalcularPontuacaoUseCase.PONTOS_OPERADOR_POR_IDEIA) % 3)
                    .coerceAtMost(2),
                desafioSemanalMeta = 2,
                isTopThree = posicao != null && posicao <= 3
            )
            PerfilUsuario.GESTOR -> GamificationStats(
                avaliacoesFeitas = (pontos / CalcularPontuacaoUseCase.PONTOS_GESTOR_POR_AVALIACAO)
                    .coerceAtLeast(1),
                desafioSemanalProgresso = ((pontos / CalcularPontuacaoUseCase.PONTOS_GESTOR_POR_AVALIACAO) % 4)
                    .coerceAtMost(3),
                desafioSemanalMeta = 3,
                isTopThree = posicao != null && posicao <= 3
            )
            PerfilUsuario.LIDER -> {
                val weekStart = startOfCurrentWeekMillis()
                val orientacoesSemana = orientacaoRepository.listarTodas().first()
                    .count { it.criadoPor == userId && it.dataCriacao >= weekStart }
                val totalOrientacoes = orientacaoRepository.listarTodas().first()
                    .count { it.criadoPor == userId }
                GamificationStats(
                    orientacoesPublicadas = totalOrientacoes,
                    desafioSemanalProgresso = orientacoesSemana.coerceAtMost(LIDER_DESAFIO_META),
                    desafioSemanalMeta = LIDER_DESAFIO_META,
                    isTopThree = posicao != null && posicao <= 3
                )
            }
        }
    }

    private suspend fun buildTeamInsight(
        ranking: List<RankingEntrada>,
        tab: PerfilUsuario
    ): TeamInsight {
        if (ranking.isEmpty()) {
            return TeamInsight(mediaPontos = 0, topContribuidorNome = "—", totalIdeias = 0)
        }
        val mediaPontos = ranking.map { it.pontos }.average().toInt()
        val topContribuidorNome = ranking
            .maxByOrNull { it.pontos }
            ?.nome
            ?.substringBefore("(")
            ?.trim()
            .orEmpty()
            .ifBlank { "—" }
        val totalIdeias = totalRealForTab(ranking, tab)
        return TeamInsight(
            mediaPontos = mediaPontos,
            topContribuidorNome = topContribuidorNome,
            totalIdeias = totalIdeias
        )
    }

    private suspend fun totalRealForTab(
        ranking: List<RankingEntrada>,
        tab: PerfilUsuario
    ): Int {
        if (ranking.isEmpty()) return 0
        val divisaoEquipe = ranking.first().divisao
        val ids = ranking.map { it.usuarioId }.toSet()
        return when (tab) {
            // Operadores: ideias submetidas pelos membros da divisão filtrada.
            PerfilUsuario.OPERADOR -> ideiaRepository.listarTodas().first()
                .count { it.autorId in ids }
            // Gestores: ideias avaliadas (não pendentes) das áreas da equipe filtrada.
            PerfilUsuario.GESTOR -> {
                val areas = usuarioRepository.listarTodos().first()
                    .filter { it.id in ids }
                    .map { it.area }
                    .toSet()
                ideiaRepository.listarTodas().first()
                    .count { ideia ->
                        ideia.area in areas &&
                            ideia.dataAvaliacao != null &&
                            ideia.status != com.gtnix.aguiabranca.domain.model.StatusIdeia.PENDENTE
                    }
            }
            // Líderes: orientações criadas pelos líderes filtrados (mesma divisão).
            PerfilUsuario.LIDER -> orientacaoRepository.listarTodas().first()
                .count { it.criadoPor in ids }
        }.also { _ -> divisaoEquipe.let { /* mantido para futuras filtragens cross-divisão */ } }
    }

    private fun startOfCurrentWeekMillis(): Long {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun analysisHeaderFor(perfilTab: PerfilUsuario): String = when (perfilTab) {
        PerfilUsuario.OPERADOR ->
            "Você está acompanhando o ranking de Operadores da sua equipe"
        PerfilUsuario.GESTOR ->
            "Você está acompanhando o ranking de Gestores da sua equipe"
        PerfilUsuario.LIDER ->
            "Você está acompanhando o ranking de Estrategistas da sua equipe"
    }

    companion object {
        private const val LIDER_DESAFIO_META = 1
    }
}

data class GamificationStats(
    val ideiasEnviadas: Int = 0,
    val avaliacoesFeitas: Int = 0,
    val orientacoesPublicadas: Int = 0,
    val streakDias: Int = 0,
    val desafioSemanalProgresso: Int = 0,
    val desafioSemanalMeta: Int = 2,
    val isTopThree: Boolean = false
)

data class TeamInsight(
    val mediaPontos: Int,
    val topContribuidorNome: String,
    val totalIdeias: Int
)

data class RankingUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val topRanking: List<RankingEntrada> = emptyList(),
    val posicaoUsuarioLogado: Int? = null,
    val entradaUsuarioLogado: RankingEntrada? = null,
    val usuarioLogadoId: String? = null,
    val tabSelecionada: PerfilUsuario = PerfilUsuario.OPERADOR,
    val perfilUsuarioLogado: PerfilUsuario = PerfilUsuario.OPERADOR,
    val divisaoUsuario: DivisaoNegocio = DivisaoNegocio.LOGISTICA,
    val selectedDivisaoFilter: DivisaoFilter = DivisaoFilter.MINHA_DIVISAO,
    val isAnalysisMode: Boolean = false,
    val analysisHeaderText: String? = null,
    val teamInsight: TeamInsight? = null,
    val gamificationStats: GamificationStats = GamificationStats(),
    val conquistasUsuario: List<Conquista> = emptyList(),
    val errorMessageRes: Int? = null,
    val showPositionCelebration: Boolean = false,
    val positionCelebrationMessage: String? = null,
    val highlightUserCard: Boolean = false
) {
    val tabsDisponiveis: List<PerfilUsuario>
        get() = when (perfilUsuarioLogado) {
            PerfilUsuario.OPERADOR -> listOf(PerfilUsuario.OPERADOR)
            PerfilUsuario.GESTOR -> listOf(PerfilUsuario.GESTOR, PerfilUsuario.OPERADOR)
            PerfilUsuario.LIDER -> listOf(
                PerfilUsuario.LIDER,
                PerfilUsuario.OPERADOR,
                PerfilUsuario.GESTOR
            )
        }
}
