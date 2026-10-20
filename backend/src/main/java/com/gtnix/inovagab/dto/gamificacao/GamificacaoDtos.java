package com.gtnix.inovagab.dto.gamificacao;

import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.TipoEvento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** Contratos de resposta da gamificação (GET /gamificacao/**). */
public final class GamificacaoDtos {

    private GamificacaoDtos() {
    }

    public record NivelResponse(
            String codigo,
            String label,
            int xpMinimo,
            String proximoCodigo,
            String proximoLabel,
            Integer xpProximo,
            int xpParaProximo,
            double progresso
    ) {
    }

    public record TemporadaResponse(String id, String label, LocalDate inicio, LocalDate fim, long diasRestantes) {
    }

    public record ConquistaResponse(
            String tipo,
            String titulo,
            String descricao,
            int valor,
            String tier,
            boolean desbloqueada,
            Integer proximaMeta,
            double progresso,
            Instant desbloqueadaEm
    ) {
    }

    public record MissaoResponse(
            String id,
            String titulo,
            String descricao,
            int progresso,
            int meta,
            boolean concluida,
            int recompensaXp,
            int recompensaAsas
    ) {
    }

    public record ImpactoResponse(
            int ideiasEnviadas,
            int ideiasAprovadas,
            int ideiasAvaliadas,
            int ideiasAlinhadas,
            int projetos,
            int projetosConcluidos,
            BigDecimal retornoMensal
    ) {
    }

    public record EventoResponse(
            String id,
            TipoEvento tipo,
            String descricao,
            int xp,
            int asas,
            Instant criadoEm,
            boolean estornado
    ) {
    }

    public record MeResponse(
            String userId,
            String nome,
            PerfilUsuario perfil,
            String divisao,
            String equipe,
            int xpTotal,
            int xpTemporada,
            int asas,
            NivelResponse nivel,
            TemporadaResponse temporada,
            Integer posicaoRanking,
            int totalRanking,
            int sequenciaSemanas,
            List<ConquistaResponse> conquistas,
            List<MissaoResponse> missoes,
            ImpactoResponse impacto,
            List<EventoResponse> novidades
    ) {
    }

    public record ExtratoResponse(int xpTotal, int asas, List<EventoResponse> eventos) {
    }

    public record RankingEntradaResponse(
            int posicao,
            String usuarioId,
            String nome,
            PerfilUsuario perfil,
            String divisao,
            String equipe,
            int xpTemporada,
            String nivel,
            String nivelLabel,
            boolean zonaPremio,
            boolean voce
    ) {
    }

    public record RankingResponse(
            TemporadaResponse temporada,
            PerfilUsuario perfil,
            String escopo,
            List<RankingEntradaResponse> entradas,
            Integer minhaPosicao,
            RankingEntradaResponse minhaEntrada,
            int totalParticipantes,
            String regraPremio,
            int pagina,
            int tamanho,
            boolean temProxima
    ) {
    }

    public record IndicacaoInovaGabResponse(
            String divisao,
            int posicaoNaDivisao,
            String usuarioId,
            String nome,
            String equipe,
            int xpTemporada
    ) {
    }

    public record IndicacoesInovaGabResponse(
            TemporadaResponse temporada,
            String criterio,
            List<IndicacaoInovaGabResponse> indicados
    ) {
    }

    public record EquipeRankingResponse(
            int posicao,
            String equipe,
            String divisao,
            int membros,
            int xpTotal,
            int xpMedio,
            boolean minhaEquipe
    ) {
    }
}
