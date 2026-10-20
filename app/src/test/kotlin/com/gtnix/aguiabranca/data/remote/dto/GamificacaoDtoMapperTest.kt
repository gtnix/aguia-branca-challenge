package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusResgate
import com.gtnix.aguiabranca.domain.model.TipoConquista
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificacaoDtoMapperTest {

    @Test
    fun `mapeia painel do servidor sem recalcular nada`() {
        val dto = GamificacaoMeDto(
            userId = "pedro",
            xpTotal = 70,
            xpTemporada = 70,
            asas = 40,
            nivel = NivelDto(codigo = "EM_ASCENSAO", xpParaProximo = 80, progresso = 0.2),
            temporada = TemporadaDto(id = "2026-T4", label = "Temporada 4 · 2026", diasRestantes = 73),
            posicaoRanking = 2,
            totalRanking = 4,
            sequenciaSemanas = 3,
            conquistas = listOf(
                ConquistaDto(
                    tipo = "PRIMEIRA_IDEIA",
                    titulo = "Primeira ideia",
                    valor = 4,
                    tier = "PRATA",
                    desbloqueada = true,
                    proximaMeta = 10,
                    progresso = 0.4,
                    desbloqueadaEm = "2026-10-01T12:00:00Z"
                ),
                ConquistaDto(tipo = "CONQUISTA_DO_FUTURO", desbloqueada = true)
            ),
            missoes = listOf(
                MissaoDto(id = "seguranca-semana", titulo = "Segurança", progresso = 0, meta = 1, recompensaXp = 10, recompensaAsas = 5)
            ),
            impacto = ImpactoDto(ideiasEnviadas = 4, ideiasAprovadas = 2, projetos = 1, retornoMensal = null)
        )

        val painel = dto.toDomain(PerfilUsuario.OPERADOR)

        assertEquals(70, painel.total)
        assertEquals(40, painel.asas)
        assertEquals(NivelUsuario.EM_ASCENSAO, painel.nivel)
        assertEquals(0.2f, painel.progressoProximoNivel, 0.0001f)
        assertEquals(80, painel.pontosParaProximoNivel)
        assertEquals("Temporada 4 · 2026", painel.temporada?.label)
        assertEquals(3, painel.sequenciaSemanas)
        assertEquals(1, painel.conquistas.size)
        val conquista = painel.conquistas.single()
        assertEquals(TipoConquista.PRIMEIRA_IDEIA, conquista.tipo)
        assertEquals(ConquistaTier.PRATA, conquista.tier)
        assertEquals(10, conquista.proximaMeta)
        assertEquals(1_790_856_000_000L, conquista.desbloqueadaEm)
        assertEquals(0f, painel.missoes.single().fracao, 0f)
        assertEquals(4, painel.estatisticas.totalIdeias)
        assertEquals(0.0, painel.impacto.retornoMensal, 0.0)
    }

    @Test
    fun `nivel desconhecido cai para a faixa calculada pelo XP`() {
        val painel = GamificacaoMeDto(xpTotal = 160, nivel = NivelDto(codigo = "LENDARIO")).toDomain(PerfilUsuario.GESTOR)

        assertEquals(NivelUsuario.ENGAJADO, painel.nivel)
    }

    @Test
    fun `ranking usa XP da temporada e marca zona de premio`() {
        val dto = RankingDto(
            entradas = listOf(
                RankingEntradaDto(posicao = 1, usuarioId = "joana", nome = "Joana", divisao = "LOGISTICA", equipe = "CD Serra", xpTemporada = 95, nivel = "EM_ASCENSAO", zonaPremio = true),
                RankingEntradaDto(posicao = 2, usuarioId = "pedro", nome = "", divisao = "DESCONHECIDA", xpTemporada = 70)
            ),
            minhaPosicao = 2,
            minhaEntrada = RankingEntradaDto(posicao = 2, usuarioId = "pedro", xpTemporada = 70),
            totalParticipantes = 4,
            regraPremio = "Top 3 por divisão"
        )

        val ranking = dto.toDomain(PerfilUsuario.OPERADOR)

        val primeira = ranking.topRanking.first()
        assertEquals(95, primeira.pontos)
        assertTrue(primeira.zonaPremio)
        assertEquals("CD Serra", primeira.equipe)
        assertEquals("pedro", ranking.topRanking[1].nome)
        assertEquals(DivisaoNegocio.LOGISTICA, ranking.topRanking[1].divisao)
        assertEquals(2, ranking.posicaoUsuarioLogado)
        assertEquals("Top 3 por divisão", ranking.regraPremio)
    }

    @Test
    fun `indicacoes ignoram divisao desconhecida e preservam posicao`() {
        val dto = IndicacoesInovaGabDto(
            temporada = TemporadaDto(id = "2026-T4", label = "4º trimestre", diasRestantes = 72),
            criterio = "Top 3",
            indicados = listOf(
                IndicacaoInovaGabDto(
                    divisao = "LOGISTICA",
                    posicaoNaDivisao = 1,
                    usuarioId = "joana",
                    nome = "Joana",
                    equipe = "CD Serra",
                    xpTemporada = 74
                ),
                IndicacaoInovaGabDto(divisao = "ORBITA", usuarioId = "x", nome = "X", xpTemporada = 1)
            )
        )

        val indicacoes = dto.toDomain()

        assertEquals("4º trimestre", indicacoes.temporada?.label)
        assertEquals(1, indicacoes.indicados.size)
        val joana = indicacoes.indicados.single()
        assertEquals(DivisaoNegocio.LOGISTICA, joana.divisao)
        assertEquals(1, joana.posicaoNaDivisao)
        assertEquals(74, joana.xpTemporada)
    }

    @Test
    fun `recompensa bloqueada preserva motivo e resgate mapeia status`() {
        val recompensa = RecompensaDto(
            id = "rec-folga",
            titulo = "Folga",
            custoAsas = 120,
            nivelMinimo = "ENGAJADO",
            podeResgatar = false,
            motivoBloqueio = "Faltam 80 Asas"
        ).toDomain()
        val resgate = ResgateDto(id = "r1", status = "NEGADO", observacao = "Sem escala", solicitadoEm = "2026-10-01T12:00:00Z").toDomain()

        assertFalse(recompensa.podeResgatar)
        assertEquals("Faltam 80 Asas", recompensa.motivoBloqueio)
        assertEquals(NivelUsuario.ENGAJADO, recompensa.nivelMinimo)
        assertEquals(StatusResgate.NEGADO, resgate.status)
        assertEquals("Sem escala", resgate.observacao)
        assertNull(resgate.decididoEm)
    }
}
