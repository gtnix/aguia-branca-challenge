package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Painel de pontuação do usuário logado. O cálculo (XP, Asas, conquistas, missões) acontece no
 * servidor a partir do extrato de eventos; o app não recalcula nada localmente.
 */
@Singleton
class CalcularPontuacaoUseCase @Inject constructor(
    private val gamificacaoRepository: GamificacaoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    suspend fun calcular(userId: String): Pontuacao {
        val perfil = usuarioRepository.buscarPorId(userId)?.perfil ?: PerfilUsuario.OPERADOR
        return calcular(userId, perfil)
    }

    /** [userId] é sempre o usuário da sessão: o servidor só expõe o painel do próprio token. */
    @Suppress("UNUSED_PARAMETER")
    suspend fun calcular(userId: String, perfil: PerfilUsuario): Pontuacao =
        when (val result = gamificacaoRepository.painel(perfil)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            Result.Loading -> error("estado inesperado")
        }
}
