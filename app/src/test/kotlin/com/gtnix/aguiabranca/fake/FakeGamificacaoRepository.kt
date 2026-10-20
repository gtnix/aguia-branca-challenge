package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.Extrato
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.Resgate
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.model.StatusResgate
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.util.Result

class FakeGamificacaoRepository : GamificacaoRepository {

    var painelResult: Result<Pontuacao> = Result.Success(
        Pontuacao(
            total = 0,
            nivel = NivelUsuario.INICIANTE,
            progressoProximoNivel = 0f,
            pontosParaProximoNivel = 50
        )
    )
    var extratoResult: Result<Extrato> = Result.Success(Extrato(0, 0, emptyList()))
    var rankingResults: MutableMap<PerfilUsuario, Result<ResultadoRanking>> = mutableMapOf()
    var equipesResult: Result<List<EquipeRanking>> = Result.Success(emptyList())
    var indicacoesResult: Result<IndicacoesInovaGab> = Result.Success(
        IndicacoesInovaGab(temporada = null, criterio = "", indicados = emptyList())
    )
    var recompensasResult: Result<List<Recompensa>> = Result.Success(emptyList())
    var resgatesResult: Result<List<Resgate>> = Result.Success(emptyList())
    var resgatarResult: Result<Resgate>? = null

    val painelChamadas = mutableListOf<PerfilUsuario>()
    val rankingChamadas = mutableListOf<Pair<PerfilUsuario, DivisaoFilter>>()
    val resgatesSolicitados = mutableListOf<String>()
    val recompensasChamadas = mutableListOf<Boolean>()
    var novidadesVistas = 0
        private set

    override suspend fun painel(perfil: PerfilUsuario): Result<Pontuacao> {
        painelChamadas += perfil
        return painelResult
    }

    override suspend fun extrato(): Result<Extrato> = extratoResult

    override suspend fun marcarNovidadesVistas(): Result<Unit> {
        novidadesVistas++
        return Result.Success(Unit)
    }

    override suspend fun ranking(perfil: PerfilUsuario, escopo: DivisaoFilter): Result<ResultadoRanking> {
        rankingChamadas += perfil to escopo
        return rankingResults[perfil] ?: Result.Success(
            ResultadoRanking(
                topRanking = emptyList(),
                posicaoUsuarioLogado = null,
                entradaUsuarioLogado = null,
                perfilFiltro = perfil
            )
        )
    }

    val equipesEscopos = mutableListOf<DivisaoFilter>()
    var indicacoesChamadas = 0
        private set

    override suspend fun rankingEquipes(escopo: DivisaoFilter): Result<List<EquipeRanking>> {
        equipesEscopos += escopo
        return equipesResult
    }

    override suspend fun indicacoesInovagab(temporada: String?): Result<IndicacoesInovaGab> {
        indicacoesChamadas++
        return indicacoesResult
    }

    override suspend fun recompensas(incluirInativas: Boolean): Result<List<Recompensa>> {
        recompensasChamadas += incluirInativas
        return recompensasResult
    }

    override suspend fun resgatar(recompensaId: String): Result<Resgate> {
        resgatesSolicitados += recompensaId
        return resgatarResult ?: Result.Success(resgate(id = "res-$recompensaId", recompensaId = recompensaId))
    }

    override suspend fun meusResgates(): Result<List<Resgate>> = resgatesResult

    companion object {
        fun resgate(
            id: String = "res-1",
            recompensaId: String = "rec-folga",
            status: StatusResgate = StatusResgate.SOLICITADO,
            observacao: String? = null,
            custoAsas: Int = 40
        ) = Resgate(
            id = id,
            recompensaId = recompensaId,
            recompensaTitulo = "Folga de meio período",
            userId = "user-1",
            userNome = "Pedro",
            custoAsas = custoAsas,
            status = status,
            observacao = observacao,
            solicitadoEm = 0L,
            decididoEm = null
        )
    }
}
