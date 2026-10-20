package com.gtnix.aguiabranca.presentation.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.TemporadaInfo
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase,
    private val calcularPontuacao: CalcularPontuacaoUseCase,
    private val sessionManager: SessionManager,
    private val gamificacaoRepository: GamificacaoRepository
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
                divisaoUsuario = divisaoUsuario
            )
        }
        carregarRanking()
    }

    fun refresh() {
        carregarRanking(isRefresh = true)
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

    private fun carregarRanking(isRefresh: Boolean = false) {
        val userId = sessionManager.getCurrentUser()?.id
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_user_not_logged) }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh,
                usuarioLogadoId = userId
            )
        }

        viewModelScope.launch {
            try {
                val painelAsync = async {
                    runCatching { calcularPontuacao.calcular(userId, perfilUsuarioLogado) }.getOrNull()
                }
                val equipesAsync = async {
                    (gamificacaoRepository.rankingEquipes(DivisaoFilter.GRUPO_COMPLETO) as? Result.Success)?.data
                }
                val operadoresAsync = async {
                    if (perfilUsuarioLogado != PerfilUsuario.GESTOR) {
                        emptyList<RankingEntrada>()
                    } else {
                        runCatching {
                            getRankingUseCase.executar(
                                usuarioLogadoId = userId,
                                perfilFiltro = PerfilUsuario.OPERADOR,
                                divisaoFilter = DivisaoFilter.MINHA_DIVISAO,
                                divisaoUsuario = divisaoUsuario
                            ).topRanking.filter { it.perfil == PerfilUsuario.OPERADOR }.take(ZONA_PREMIO)
                        }.getOrDefault(emptyList())
                    }
                }
                val indicacoesAsync = async {
                    if (perfilUsuarioLogado != PerfilUsuario.LIDER) {
                        null
                    } else {
                        (gamificacaoRepository.indicacoesInovagab() as? Result.Success)?.data
                    }
                }

                val resultado = getRankingUseCase.executar(
                    usuarioLogadoId = userId,
                    perfilFiltro = perfilUsuarioLogado,
                    divisaoFilter = DivisaoFilter.MINHA_DIVISAO,
                    divisaoUsuario = divisaoUsuario
                )
                val rankingFiltrado = resultado.topRanking.filter { it.perfil == perfilUsuarioLogado }
                val entradaUsuario = resultado.entradaUsuarioLogado
                val posicaoUsuario = resultado.posicaoUsuarioLogado
                val painel = painelAsync.await()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        topRanking = rankingFiltrado,
                        posicaoUsuarioLogado = posicaoUsuario,
                        entradaUsuarioLogado = entradaUsuario,
                        painel = painel,
                        conquistasUsuario = painel?.conquistas.orEmpty(),
                        equipes = equipesAsync.await().orEmpty(),
                        podioOperadores = operadoresAsync.await(),
                        indicacoes = indicacoesAsync.await(),
                        regraPremio = resultado.regraPremio,
                        temporada = resultado.temporada,
                        xpParaZonaPremio = xpParaZonaPremio(rankingFiltrado, entradaUsuario),
                        proximaPosicao = proximaPosicao(rankingFiltrado, posicaoUsuario, entradaUsuario),
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

    companion object {
        /** Quantos de cada divisão entram na zona de prêmio (igual ao servidor). */
        const val ZONA_PREMIO = 3

        /**
         * XP de temporada que falta para entrar no top 3 da própria divisão. Nulo se o usuário já está
         * na zona ou não participa do ranking.
         */
        fun xpParaZonaPremio(ranking: List<RankingEntrada>, eu: RankingEntrada?): Int? {
            if (eu == null || eu.zonaPremio || eu.perfil != PerfilUsuario.OPERADOR) return null
            val daDivisao = ranking.filter { it.divisao == eu.divisao && it.usuarioId != eu.usuarioId }
            val corte = daDivisao.sortedByDescending { it.pontos }.getOrNull(ZONA_PREMIO - 1) ?: return 1
            return (corte.pontos - eu.pontos + 1).coerceAtLeast(1)
        }

        /** Quem está logo acima e quanto XP falta para passar. Empate conta como 1 XP. */
        fun proximaPosicao(ranking: List<RankingEntrada>, posicao: Int?, eu: RankingEntrada?): ProximaPosicao? {
            if (eu == null || posicao == null || posicao <= 1) return null
            val acima = ranking.getOrNull(posicao - 2) ?: return null
            val diff = acima.pontos - eu.pontos
            return ProximaPosicao(
                nome = acima.nome.split(" ").firstOrNull().orEmpty().ifBlank { acima.nome },
                posicao = posicao - 1,
                xpFaltando = diff.coerceAtLeast(0) + 1,
                xpAlvo = acima.pontos + 1,
                empatado = diff <= 0
            )
        }
    }
}

data class ProximaPosicao(
    val nome: String,
    val posicao: Int,
    val xpFaltando: Int,
    val xpAlvo: Int,
    val empatado: Boolean
)

data class RankingUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val topRanking: List<RankingEntrada> = emptyList(),
    val podioOperadores: List<RankingEntrada> = emptyList(),
    val indicacoes: IndicacoesInovaGab? = null,
    val posicaoUsuarioLogado: Int? = null,
    val entradaUsuarioLogado: RankingEntrada? = null,
    val usuarioLogadoId: String? = null,
    val perfilUsuarioLogado: PerfilUsuario = PerfilUsuario.OPERADOR,
    val divisaoUsuario: DivisaoNegocio = DivisaoNegocio.LOGISTICA,
    val painel: Pontuacao? = null,
    val conquistasUsuario: List<Conquista> = emptyList(),
    val equipes: List<EquipeRanking> = emptyList(),
    val regraPremio: String = "",
    val temporada: TemporadaInfo? = null,
    val xpParaZonaPremio: Int? = null,
    val proximaPosicao: ProximaPosicao? = null,
    val errorMessageRes: Int? = null,
    val showPositionCelebration: Boolean = false,
    val positionCelebrationMessage: String? = null,
    val highlightUserCard: Boolean = false
) {
    val temConteudo: Boolean
        get() = topRanking.isNotEmpty() ||
            podioOperadores.isNotEmpty() ||
            equipes.isNotEmpty() ||
            indicacoes?.indicados?.isNotEmpty() == true ||
            painel != null
}
