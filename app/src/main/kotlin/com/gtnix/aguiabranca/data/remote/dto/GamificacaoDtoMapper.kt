package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.IndicacaoInovaGab
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.model.EventoPontos
import com.gtnix.aguiabranca.domain.model.Extrato
import com.gtnix.aguiabranca.domain.model.Impacto
import com.gtnix.aguiabranca.domain.model.Missao
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.Resgate
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.model.StatusResgate
import com.gtnix.aguiabranca.domain.model.TemporadaInfo
import com.gtnix.aguiabranca.domain.model.TipoConquista
import com.gtnix.aguiabranca.domain.model.VencedorCampanha
import java.time.Instant

fun GamificacaoMeDto.toDomain(perfilUsuario: PerfilUsuario): Pontuacao {
    val nivelAtual = safeValueOf(nivel?.codigo.orEmpty(), NivelUsuario.fromPontuacao(xpTotal))
    val impactoDominio = impacto?.toDomain() ?: Impacto()
    return Pontuacao(
        total = xpTotal,
        nivel = nivelAtual,
        progressoProximoNivel = (nivel?.progresso ?: 0.0).toFloat().coerceIn(0f, 1f),
        pontosParaProximoNivel = nivel?.xpParaProximo ?: 0,
        conquistas = conquistas.mapNotNull { it.toDomain() },
        estatisticas = impactoDominio.toEstatisticas(perfilUsuario),
        asas = asas,
        xpTemporada = xpTemporada,
        temporada = temporada?.toDomain(),
        posicaoRanking = posicaoRanking,
        totalRanking = totalRanking,
        sequenciaSemanas = sequenciaSemanas,
        missoes = missoes.map { it.toDomain() },
        impacto = impactoDominio,
        novidades = novidades.map { it.toDomain() }
    )
}

fun TemporadaDto.toDomain(): TemporadaInfo = TemporadaInfo(id = id, label = label, diasRestantes = diasRestantes)

/** Conquistas que o app não conhece (servidor mais novo) são ignoradas em vez de quebrar a tela. */
fun ConquistaDto.toDomain(): Conquista? {
    val tipoDominio = runCatching { TipoConquista.valueOf(tipo) }.getOrNull() ?: return null
    val tierAtual = tier?.let { runCatching { ConquistaTier.valueOf(it) }.getOrNull() }
    return Conquista(
        id = tipo.lowercase(),
        tipo = tipoDominio,
        desbloqueada = desbloqueada,
        tier = tierAtual ?: ConquistaTier.BRONZE,
        titulo = titulo.orEmpty(),
        descricao = descricao.orEmpty(),
        valor = valor,
        proximaMeta = proximaMeta,
        progresso = progresso.toFloat().coerceIn(0f, 1f),
        desbloqueadaEm = parseIsoOrNull(desbloqueadaEm)
    )
}

fun MissaoDto.toDomain(): Missao = Missao(
    id = id,
    titulo = titulo,
    descricao = descricao,
    progresso = progresso,
    meta = meta,
    concluida = concluida,
    recompensaXp = recompensaXp,
    recompensaAsas = recompensaAsas
)

fun ImpactoDto.toDomain(): Impacto = Impacto(
    ideiasEnviadas = ideiasEnviadas,
    ideiasAprovadas = ideiasAprovadas,
    ideiasAvaliadas = ideiasAvaliadas,
    ideiasAlinhadas = ideiasAlinhadas,
    projetos = projetos,
    projetosConcluidos = projetosConcluidos,
    retornoMensal = retornoMensal ?: 0.0
)

private fun Impacto.toEstatisticas(perfil: PerfilUsuario): Estatisticas = when (perfil) {
    PerfilUsuario.OPERADOR -> Estatisticas(
        totalIdeias = ideiasEnviadas,
        ideiasAprovadas = ideiasAprovadas,
        projetosParticipando = projetos
    )
    PerfilUsuario.GESTOR -> Estatisticas(
        ideiasAvaliadas = ideiasAvaliadas,
        ideiasAprovadas = ideiasAprovadas,
        projetosParticipando = projetos
    )
    PerfilUsuario.LIDER -> Estatisticas(
        ideiasAlinhadas = ideiasAlinhadas,
        projetosDirecionados = projetos
    )
}

fun EventoPontosDto.toDomain(): EventoPontos = EventoPontos(
    id = id.orEmpty(),
    tipo = tipo.orEmpty(),
    descricao = descricao.orEmpty(),
    xp = xp,
    asas = asas,
    criadoEm = parseIsoOrNull(criadoEm) ?: 0L,
    estornado = estornado
)

fun ExtratoDto.toDomain(): Extrato = Extrato(xpTotal = xpTotal, asas = asas, eventos = eventos.map { it.toDomain() })

fun RankingDto.toDomain(perfilFiltro: PerfilUsuario): ResultadoRanking {
    val todas = entradas.map { it.toDomain(perfilFiltro) }
    val minha = minhaEntrada?.toDomain(perfilFiltro)
    return ResultadoRanking(
        topRanking = todas,
        posicaoUsuarioLogado = minhaPosicao,
        entradaUsuarioLogado = minha,
        perfilFiltro = perfilFiltro,
        totalParticipantes = totalParticipantes,
        regraPremio = regraPremio.orEmpty(),
        temporada = temporada?.toDomain()
    )
}

fun RankingEntradaDto.toDomain(perfilFiltro: PerfilUsuario): RankingEntrada {
    val nomeExibido = nome?.takeIf { it.isNotBlank() } ?: usuarioId
    return RankingEntrada(
        usuarioId = usuarioId,
        nome = nomeExibido,
        pontos = xpTemporada,
        nivel = safeValueOf(nivel.orEmpty(), NivelUsuario.INICIANTE),
        inicialNome = nomeExibido.firstOrNull()?.uppercaseChar() ?: 'U',
        perfil = perfilFiltro,
        cargo = when (perfilFiltro) {
            PerfilUsuario.OPERADOR -> "Operador"
            PerfilUsuario.GESTOR -> "Gestor"
            PerfilUsuario.LIDER -> "Líder"
        },
        divisao = safeValueOf(divisao.orEmpty(), DivisaoNegocio.LOGISTICA),
        equipe = equipe,
        zonaPremio = zonaPremio
    )
}

fun IndicacoesInovaGabDto.toDomain(): IndicacoesInovaGab = IndicacoesInovaGab(
    temporada = temporada?.toDomain(),
    criterio = criterio.orEmpty(),
    indicados = indicados.mapNotNull { it.toDomain() }
)

fun IndicacaoInovaGabDto.toDomain(): IndicacaoInovaGab? {
    val divisaoDominio = runCatching { DivisaoNegocio.valueOf(divisao.orEmpty()) }.getOrNull() ?: return null
    val nomeExibido = nome?.takeIf { it.isNotBlank() } ?: usuarioId
    return IndicacaoInovaGab(
        divisao = divisaoDominio,
        posicaoNaDivisao = posicaoNaDivisao,
        usuarioId = usuarioId,
        nome = nomeExibido,
        equipe = equipe,
        xpTemporada = xpTemporada
    )
}

fun EquipeRankingDto.toDomain(): EquipeRanking = EquipeRanking(
    posicao = posicao,
    equipe = equipe,
    divisao = divisao?.let { runCatching { DivisaoNegocio.valueOf(it) }.getOrNull() },
    membros = membros,
    xpTotal = xpTotal,
    xpMedio = xpMedio,
    minhaEquipe = minhaEquipe
)

fun RecompensaDto.toDomain(): Recompensa = Recompensa(
    id = id,
    titulo = titulo,
    descricao = descricao.orEmpty(),
    categoria = categoria.orEmpty(),
    parceiro = parceiro.orEmpty(),
    custoAsas = custoAsas,
    estoque = estoque,
    nivelMinimo = safeValueOf(nivelMinimo.orEmpty(), NivelUsuario.INICIANTE),
    ativo = ativo,
    podeResgatar = podeResgatar,
    motivoBloqueio = motivoBloqueio
)

fun ResgateDto.toDomain(): Resgate = Resgate(
    id = id,
    recompensaId = recompensaId,
    recompensaTitulo = recompensaTitulo.orEmpty(),
    userId = userId,
    userNome = userNome?.takeIf { it.isNotBlank() } ?: userId,
    custoAsas = custoAsas,
    status = safeValueOf(status.orEmpty(), StatusResgate.SOLICITADO),
    observacao = observacao,
    solicitadoEm = parseIsoOrNull(solicitadoEm) ?: 0L,
    decididoEm = parseIsoOrNull(decididoEm)
)

fun VencedorCampanhaDto.toDomain(): VencedorCampanha = VencedorCampanha(
    userId = userId,
    nome = nome?.takeIf { it.isNotBlank() } ?: userId,
    xpCampanha = xpCampanha,
    bonusAsas = bonusAsas
)

private fun parseIsoOrNull(value: String?): Long? {
    if (value.isNullOrBlank()) return null
    return runCatching { Instant.parse(value).toEpochMilli() }.getOrNull()
}
