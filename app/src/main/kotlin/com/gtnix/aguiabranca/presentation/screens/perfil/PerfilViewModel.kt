package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val calcularPontuacao: CalcularPontuacaoUseCase,
    private val getRankingUseCase: GetRankingUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val seenUnlockedIds = mutableSetOf<String>()
    private var hasInitializedSeenConquistas = false

    init {
        viewModelScope.launch {
            sessionManager.currentUserFlow.collect { user ->
                if (user == null) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@collect
                }

                _uiState.update {
                    it.copy(
                        isLoading = true,
                        nome = user.nome,
                        area = user.area.name,
                        perfil = user.perfil.name,
                        fotoPerfil = user.fotoPerfil,
                        inicialNome = user.nome.firstOrNull()?.uppercaseChar() ?: 'U',
                        errorMessageRes = null
                    )
                }

                try {
                    val usuario = usuarioRepository.buscarPorId(user.id)
                    if (usuario != null) {
                        _uiState.update {
                            it.copy(
                                nome = usuario.nome,
                                area = usuario.area.name,
                                perfil = usuario.perfil.name,
                                fotoPerfil = usuario.fotoPerfil,
                                inicialNome = usuario.nome.firstOrNull()?.uppercaseChar() ?: 'U'
                            )
                        }
                    }

                    val perfilPontuacao = usuario?.perfil ?: user.perfil
                    val divisaoUsuario = usuario?.divisao ?: user.divisao
                    val pontuacao = calcularPontuacao.calcular(user.id, perfilPontuacao)
                    val (newlyUnlockedIds, showCelebration) = detectNewlyUnlocked(pontuacao)

                    val rankingResult = getRankingUseCase.executar(
                        usuarioLogadoId = user.id,
                        perfilFiltro = perfilPontuacao,
                        divisaoFilter = DivisaoFilter.MINHA_DIVISAO,
                        divisaoUsuario = divisaoUsuario
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pontuacao = pontuacao,
                            newlyUnlockedIds = newlyUnlockedIds,
                            showCelebration = showCelebration,
                            posicaoRanking = rankingResult.posicaoUsuarioLogado ?: 0,
                            totalParticipantesRanking = rankingResult.totalParticipantes,
                            perfilRankingLabel = perfilRankingLabel(perfilPontuacao),
                            divisaoRankingLabel = divisaoUsuario.label,
                            deltaSemanaPosicao = DEMO_DELTA_SEMANA_POSICAO
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_load_profile) }
                }
            }
        }
    }

    private fun detectNewlyUnlocked(pontuacao: Pontuacao): Pair<Set<String>, Boolean> {
        val unlockedIds = pontuacao.conquistas
            .filter { it.desbloqueada }
            .map { it.id }
            .toSet()

        if (!hasInitializedSeenConquistas) {
            hasInitializedSeenConquistas = true
            seenUnlockedIds.addAll(unlockedIds)
            return emptySet<String>() to false
        }

        val newlyUnlocked = unlockedIds - seenUnlockedIds
        seenUnlockedIds.addAll(newlyUnlocked)
        return newlyUnlocked to newlyUnlocked.isNotEmpty()
    }

    fun clearNewlyUnlocked() {
        _uiState.update { it.copy(newlyUnlockedIds = emptySet(), showCelebration = false) }
    }

    fun onLogout() {
        viewModelScope.launch {
            sessionManager.logout()
        }
        _uiState.update { it.copy(logoutSuccess = true) }
    }

    private fun perfilRankingLabel(perfil: PerfilUsuario): String = when (perfil) {
        PerfilUsuario.OPERADOR -> "Inovadores"
        PerfilUsuario.GESTOR -> "Curadores"
        PerfilUsuario.LIDER -> "Estrategistas"
    }

    companion object {
        private const val DEMO_DELTA_SEMANA_POSICAO = 2
    }
}

data class PerfilUiState(
    val isLoading: Boolean = true,
    val nome: String = "",
    val area: String = "",
    val perfil: String = "",
    val fotoPerfil: String? = null,
    val inicialNome: Char = 'U',
    val pontuacao: Pontuacao? = null,
    val newlyUnlockedIds: Set<String> = emptySet(),
    val showCelebration: Boolean = false,
    val errorMessageRes: Int? = null,
    val logoutSuccess: Boolean = false,
    val posicaoRanking: Int = 0,
    val totalParticipantesRanking: Int = 0,
    val perfilRankingLabel: String = "",
    val divisaoRankingLabel: String = "",
    val deltaSemanaPosicao: Int? = null
)
