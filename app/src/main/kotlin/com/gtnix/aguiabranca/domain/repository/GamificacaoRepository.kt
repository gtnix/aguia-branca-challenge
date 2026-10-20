package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.Extrato
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.Resgate
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.util.Result

/** Economia de Inovação: tudo é calculado no servidor; o app só lê e dispara ações. */
interface GamificacaoRepository {
    suspend fun painel(perfil: PerfilUsuario): Result<Pontuacao>
    suspend fun extrato(): Result<Extrato>
    suspend fun marcarNovidadesVistas(): Result<Unit>
    suspend fun ranking(perfil: PerfilUsuario, escopo: DivisaoFilter): Result<ResultadoRanking>
    suspend fun rankingEquipes(escopo: DivisaoFilter): Result<List<EquipeRanking>>
    suspend fun indicacoesInovagab(temporada: String? = null): Result<IndicacoesInovaGab>

    suspend fun recompensas(incluirInativas: Boolean = false): Result<List<Recompensa>>
    suspend fun resgatar(recompensaId: String): Result<Resgate>
    suspend fun meusResgates(): Result<List<Resgate>>
}
