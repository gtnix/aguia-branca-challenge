package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.PontoEventoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.PontoEventoRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Motor da Economia de Inovação GAB: grava lançamentos idempotentes no extrato
 * ({@code pontos_eventos}) a partir dos fatos de domínio. Regras em docs/GAMIFICACAO.md §4.
 */
@Service
public class GamificacaoService {

    private static final Logger log = LoggerFactory.getLogger(GamificacaoService.class);

    static final int LIMITE_IDEIAS_PONTUADAS_DIA = 3;
    static final Duration SLA_AVALIACAO = Duration.ofDays(7);
    static final double MULTIPLICADOR_CAMPANHA = 1.5;
    static final double MULTIPLICADOR_SEGURANCA = 1.5;
    static final int REAIS_POR_ASA_RETORNO = 1000;
    static final int TETO_ASAS_RETORNO = 150;
    static final int ASAS_ROI_MIN = 5;
    static final int ASAS_ROI_MAX = 50;
    static final String CATEGORIA_SEGURANCA = "SEGURANCA";
    static final String TAG_CAMPANHA = "CAMPANHA";

    private static final Set<TipoEvento> EVENTOS_AUTOR_CAMPANHA = Set.of(
            TipoEvento.IDEIA_ENVIADA,
            TipoEvento.IDEIA_APROVADA,
            TipoEvento.IDEIA_CONVERTIDA,
            TipoEvento.PROJETO_CONCLUIDO_AUTOR
    );

    private final PontoEventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrientacaoRepository orientacaoRepository;
    private final IdeiaRepository ideiaRepository;
    private final Clock clock;

    public GamificacaoService(
            PontoEventoRepository eventoRepository,
            UsuarioRepository usuarioRepository,
            OrientacaoRepository orientacaoRepository,
            IdeiaRepository ideiaRepository,
            Clock clock
    ) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.orientacaoRepository = orientacaoRepository;
        this.ideiaRepository = ideiaRepository;
        this.clock = clock;
    }

    public record Lancamento(
            String chave,
            String userId,
            TipoEvento tipo,
            int xp,
            int asas,
            String refId,
            String tag,
            String descricao
    ) {
        public static Lancamento padrao(String userId, TipoEvento tipo, String refId, int xp, int asas,
                                        String tag, String descricao) {
            return new Lancamento(tipo.name() + ":" + refId + ":" + userId,
                    userId, tipo, xp, asas, refId, tag, descricao);
        }
    }

    public record Vencedor(String userId, int xpCampanha, int bonusAsas) {
    }

    // ---------------------------------------------------------------------
    // Extrato
    // ---------------------------------------------------------------------

    /** Grava o lançamento uma única vez por chave. Retorna vazio se já existia ou se vale zero. */
    public Optional<PontoEventoDocument> registrar(Lancamento l) {
        if (l.userId() == null || (l.xp() == 0 && l.asas() == 0)) {
            return Optional.empty();
        }
        if (eventoRepository.existsByChave(l.chave())) {
            return Optional.empty();
        }
        UsuarioDocument usuario = usuarioRepository.findById(l.userId()).orElse(null);
        if (usuario == null) {
            return Optional.empty();
        }
        Instant agora = clock.instant();
        PontoEventoDocument doc = new PontoEventoDocument();
        doc.setChave(l.chave());
        doc.setUserId(l.userId());
        doc.setPerfil(usuario.getPerfil());
        doc.setDivisao(usuario.getDivisao());
        doc.setEquipe(usuario.getEquipe());
        doc.setTipo(l.tipo());
        doc.setXp(l.xp());
        doc.setAsas(l.asas());
        doc.setRefId(l.refId());
        doc.setTag(l.tag());
        doc.setDescricao(l.descricao() != null ? l.descricao() : l.tipo().getDescricao());
        doc.setTemporadaId(Temporada.de(agora).id());
        doc.setCriadoEm(agora);
        try {
            return Optional.of(eventoRepository.save(doc));
        } catch (DuplicateKeyException ex) {
            return Optional.empty();
        }
    }

    /** Anula todos os lançamentos ligados a uma referência, gerando um ESTORNO visível no extrato. */
    public int estornarPorRef(String refId, String motivo) {
        int estornados = 0;
        for (PontoEventoDocument original : eventoRepository.findByRefIdAndEstornadoFalse(refId)) {
            if (original.getTipo() == TipoEvento.ESTORNO) {
                continue;
            }
            original.setEstornado(true);
            eventoRepository.save(original);
            registrar(new Lancamento(
                    "ESTORNO:" + original.getChave(),
                    original.getUserId(),
                    TipoEvento.ESTORNO,
                    -original.getXp(),
                    -original.getAsas(),
                    refId,
                    original.getTipo().name(),
                    "Estorno: " + original.getDescricao() + " (" + motivo + ")"
            ));
            estornados++;
        }
        return estornados;
    }

    public int saldoAsas(String userId) {
        return eventoRepository.findByUserIdOrderByCriadoEmDesc(userId).stream()
                .mapToInt(PontoEventoDocument::getAsas)
                .sum();
    }

    public int xpTotal(String userId) {
        return eventoRepository.findByUserIdOrderByCriadoEmDesc(userId).stream()
                .mapToInt(PontoEventoDocument::getXp)
                .sum();
    }

    // ---------------------------------------------------------------------
    // Ganchos de domínio (falha de gamificação não derruba a operação principal)
    // ---------------------------------------------------------------------

    public void onIdeiaCriada(IdeiaDocument ideia, OrientacaoDocument orientacao) {
        seguro("ideia criada", () -> {
            if (ideia.getDuplicadaDe() != null) {
                return;
            }
            Instant inicioDia = LocalDate.now(clock).atStartOfDay(Temporada.ZONA).toInstant();
            long hoje = eventoRepository.countByUserIdAndTipoAndCriadoEmAfter(
                    ideia.getAutorUserId(), TipoEvento.IDEIA_ENVIADA, inicioDia);
            if (hoje >= LIMITE_IDEIAS_PONTUADAS_DIA) {
                return;
            }
            String tag = tag(orientacao);
            registrar(Lancamento.padrao(ideia.getAutorUserId(), TipoEvento.IDEIA_ENVIADA, ideia.getId(),
                    TipoEvento.IDEIA_ENVIADA.getXpBase(), 0, tag,
                    "Ideia enviada: " + ideia.getTitulo()));
            String lider = orientacao != null ? orientacao.getCriadoPorUserId() : null;
            if (lider != null && !lider.equals(ideia.getAutorUserId())) {
                registrar(Lancamento.padrao(lider, TipoEvento.IDEIA_ALINHADA, ideia.getId(),
                        TipoEvento.IDEIA_ALINHADA.getXpBase(), 0, tag,
                        "Ideia alinhada a \"" + orientacao.getTitulo() + "\""));
            }
        });
    }

    public void onIdeiaAvaliada(IdeiaDocument ideia, String avaliadorUserId) {
        seguro("ideia avaliada", () -> {
            OrientacaoDocument orientacao = orientacao(ideia.getOrientacaoId());
            String tag = tag(orientacao);
            boolean noPrazo = ideia.getDataCriacao() == null || ideia.getDataAvaliacao() == null
                    || Duration.between(ideia.getDataCriacao(), ideia.getDataAvaliacao()).compareTo(SLA_AVALIACAO) <= 0;
            TipoEvento sla = noPrazo ? TipoEvento.AVALIACAO_NO_PRAZO : TipoEvento.AVALIACAO_FORA_PRAZO;
            registrar(new Lancamento("AVALIACAO:" + ideia.getId() + ":" + avaliadorUserId,
                    avaliadorUserId, sla, sla.getXpBase(), 0, ideia.getId(), tag,
                    sla.getDescricao() + ": " + ideia.getTitulo()));

            if (ideia.getStatus() == StatusIdeia.APROVADA) {
                double m = multiplicador(orientacao);
                registrar(Lancamento.padrao(ideia.getAutorUserId(), TipoEvento.IDEIA_APROVADA, ideia.getId(),
                        aplicar(TipoEvento.IDEIA_APROVADA.getXpBase(), m),
                        aplicar(TipoEvento.IDEIA_APROVADA.getAsasBase(), m),
                        tag, "Ideia aprovada: " + ideia.getTitulo() + sufixoMultiplicador(m)));
            } else if (ideia.getStatus() == StatusIdeia.REPROVADA) {
                String temporada = Temporada.de(clock.instant()).id();
                registrar(new Lancamento(
                        "FEEDBACK_RECEBIDO:" + temporada + ":" + ideia.getAutorUserId(),
                        ideia.getAutorUserId(), TipoEvento.FEEDBACK_RECEBIDO,
                        TipoEvento.FEEDBACK_RECEBIDO.getXpBase(), 0, ideia.getId(), tag,
                        "Aprendiz: feedback recebido em \"" + ideia.getTitulo() + "\""));
            }
        });
    }

    public void onIdeiaConvertida(IdeiaDocument ideia, ProjetoDocument projeto) {
        seguro("ideia convertida", () -> {
            OrientacaoDocument orientacao = orientacao(ideia.getOrientacaoId());
            double m = multiplicador(orientacao);
            String tag = tag(orientacao);
            registrar(Lancamento.padrao(ideia.getAutorUserId(), TipoEvento.IDEIA_CONVERTIDA, ideia.getId(),
                    aplicar(TipoEvento.IDEIA_CONVERTIDA.getXpBase(), m),
                    aplicar(TipoEvento.IDEIA_CONVERTIDA.getAsasBase(), m),
                    tag, "Ideia virou o projeto \"" + projeto.getTitulo() + "\"" + sufixoMultiplicador(m)));
            if (ideia.getAvaliadorUserId() != null) {
                registrar(Lancamento.padrao(ideia.getAvaliadorUserId(), TipoEvento.APROVACAO_CONVERTIDA,
                        ideia.getId(), TipoEvento.APROVACAO_CONVERTIDA.getXpBase(), 0, tag,
                        "Sua aprovação virou projeto: " + ideia.getTitulo()));
            }
        });
    }

    public void onProjetoConcluido(ProjetoDocument projeto) {
        seguro("projeto concluído", () -> {
            LocalDate hoje = LocalDate.now(clock);
            OrientacaoDocument orientacao = orientacao(projeto.getOrientacaoId());
            String tag = tag(orientacao);
            String responsavel = projeto.getResponsavelUserId();

            if (responsavel != null && (projeto.getPrazo() == null || !hoje.isAfter(projeto.getPrazo()))) {
                registrar(Lancamento.padrao(responsavel, TipoEvento.PROJETO_NO_PRAZO, projeto.getId(),
                        TipoEvento.PROJETO_NO_PRAZO.getXpBase(), TipoEvento.PROJETO_NO_PRAZO.getAsasBase(), tag,
                        "Projeto entregue no prazo: " + projeto.getTitulo()));
            }
            BigDecimal roi = roiPercentual(projeto.getInvestimento(), projeto.getRetorno());
            if (responsavel != null && roi.signum() > 0) {
                int asas = Math.max(ASAS_ROI_MIN, Math.min(ASAS_ROI_MAX, roi.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue()));
                registrar(Lancamento.padrao(responsavel, TipoEvento.ROI_POSITIVO, projeto.getId(),
                        TipoEvento.ROI_POSITIVO.getXpBase(), asas, tag,
                        "ROI de " + roi.setScale(0, RoundingMode.HALF_UP) + "% em " + projeto.getTitulo()));
            }

            IdeiaDocument ideia = projeto.getIdeiaId() != null
                    ? ideiaRepository.findById(projeto.getIdeiaId()).orElse(null)
                    : null;
            if (ideia != null) {
                double m = multiplicador(orientacao);
                registrar(Lancamento.padrao(ideia.getAutorUserId(), TipoEvento.PROJETO_CONCLUIDO_AUTOR, projeto.getId(),
                        aplicar(TipoEvento.PROJETO_CONCLUIDO_AUTOR.getXpBase(), m),
                        aplicar(TipoEvento.PROJETO_CONCLUIDO_AUTOR.getAsasBase(), m),
                        tag, "Projeto da sua ideia concluído: " + projeto.getTitulo() + sufixoMultiplicador(m)));
                int asasRetorno = asasPorRetorno(projeto.getRetorno());
                registrar(Lancamento.padrao(ideia.getAutorUserId(), TipoEvento.RETORNO_AUTOR, projeto.getId(),
                        0, asasRetorno, tag,
                        "Participação no retorno de " + projeto.getTitulo()));
            }

            String lider = orientacao != null ? orientacao.getCriadoPorUserId() : null;
            if (lider != null) {
                registrar(Lancamento.padrao(lider, TipoEvento.PROJETO_CONCLUIDO_ESTRATEGIA, projeto.getId(),
                        TipoEvento.PROJETO_CONCLUIDO_ESTRATEGIA.getXpBase(), 0, tag,
                        "Projeto concluído em \"" + orientacao.getTitulo() + "\""));
            }
        });
    }

    public void onIdeiaExcluida(IdeiaDocument ideia) {
        seguro("ideia excluída", () -> estornarPorRef(ideia.getId(), "ideia excluída"));
    }

    public void onProjetoExcluido(ProjetoDocument projeto) {
        seguro("projeto excluído", () -> estornarPorRef(projeto.getId(), "projeto excluído"));
    }

    public void onApoio(IdeiaDocument ideia, String apoiadorUserId) {
        seguro("apoio", () -> registrar(new Lancamento(
                "APOIO_RECEBIDO:" + ideia.getId() + ":" + apoiadorUserId,
                ideia.getAutorUserId(), TipoEvento.APOIO_RECEBIDO,
                TipoEvento.APOIO_RECEBIDO.getXpBase(), 0, ideia.getId(), null,
                "Apoio recebido em \"" + ideia.getTitulo() + "\"")));
    }

    public void onFeedbackUtil(IdeiaDocument ideia) {
        if (ideia.getAvaliadorUserId() == null) {
            return;
        }
        seguro("feedback útil", () -> registrar(Lancamento.padrao(
                ideia.getAvaliadorUserId(), TipoEvento.FEEDBACK_UTIL, ideia.getId(),
                TipoEvento.FEEDBACK_UTIL.getXpBase(), TipoEvento.FEEDBACK_UTIL.getAsasBase(), null,
                "Avaliação útil em \"" + ideia.getTitulo() + "\"")));
    }

    /**
     * Distribui o bônus da campanha aos N autores que mais ganharam XP com ideias da orientação.
     * Idempotente: o mesmo vencedor não recebe duas vezes.
     */
    public List<Vencedor> encerrarCampanha(OrientacaoDocument orientacao) {
        int bonus = orientacao.getCampanhaBonusAsas() != null ? orientacao.getCampanhaBonusAsas() : 0;
        int topN = orientacao.getCampanhaTopN() != null ? orientacao.getCampanhaTopN() : 0;
        if (bonus <= 0 || topN <= 0) {
            return List.of();
        }
        Map<String, Integer> xpPorAutor = new HashMap<>();
        for (IdeiaDocument ideia : ideiaRepository.findByAtivoTrueAndOrientacaoId(orientacao.getId())) {
            for (PontoEventoDocument ev : eventoRepository.findByRefIdAndEstornadoFalse(ideia.getId())) {
                if (EVENTOS_AUTOR_CAMPANHA.contains(ev.getTipo()) && ev.getUserId().equals(ideia.getAutorUserId())) {
                    xpPorAutor.merge(ev.getUserId(), ev.getXp(), Integer::sum);
                }
            }
        }
        List<Map.Entry<String, Integer>> ordenado = new ArrayList<>(xpPorAutor.entrySet());
        ordenado.removeIf(e -> e.getValue() <= 0);
        ordenado.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey()));
        List<Vencedor> vencedores = new ArrayList<>();
        for (Map.Entry<String, Integer> e : ordenado.subList(0, Math.min(topN, ordenado.size()))) {
            registrar(new Lancamento(
                    "CAMPANHA_BONUS:" + orientacao.getId() + ":" + e.getKey(),
                    e.getKey(), TipoEvento.CAMPANHA_BONUS, 0, bonus, orientacao.getId(), TAG_CAMPANHA,
                    "Top " + topN + " da campanha \"" + orientacao.getCampanha() + "\""));
            vencedores.add(new Vencedor(e.getKey(), e.getValue(), bonus));
        }
        return vencedores;
    }

    // ---------------------------------------------------------------------
    // Regras auxiliares
    // ---------------------------------------------------------------------

    double multiplicador(OrientacaoDocument orientacao) {
        if (orientacao == null) {
            return 1.0;
        }
        double m = 1.0;
        if (orientacao.isCampanhaAtiva(LocalDate.now(clock))) {
            m *= MULTIPLICADOR_CAMPANHA;
        }
        if (CATEGORIA_SEGURANCA.equalsIgnoreCase(orientacao.getCategoria())) {
            m *= MULTIPLICADOR_SEGURANCA;
        }
        return m;
    }

    /** Tag do evento: categoria da orientação e, se houver campanha ativa, o marcador de campanha. */
    String tag(OrientacaoDocument orientacao) {
        if (orientacao == null) {
            return null;
        }
        String categoria = orientacao.getCategoria() != null ? orientacao.getCategoria().toUpperCase() : "";
        return orientacao.isCampanhaAtiva(LocalDate.now(clock)) ? categoria + "," + TAG_CAMPANHA : categoria;
    }

    static int aplicar(int base, double multiplicador) {
        return (int) Math.round(base * multiplicador);
    }

    public static int asasPorRetorno(BigDecimal retornoMensal) {
        if (retornoMensal == null || retornoMensal.signum() <= 0) {
            return 0;
        }
        int asas = retornoMensal.divide(BigDecimal.valueOf(REAIS_POR_ASA_RETORNO), 0, RoundingMode.DOWN).intValue();
        return Math.min(TETO_ASAS_RETORNO, asas);
    }

    /** Mesmo ROI anualizado do ProjetoService: ((retorno mensal × 12) − investimento) / investimento × 100. */
    public static BigDecimal roiPercentual(BigDecimal investimento, BigDecimal retorno) {
        if (investimento == null || retorno == null || investimento.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return retorno.multiply(BigDecimal.valueOf(12))
                .subtract(investimento)
                .divide(investimento, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private static String sufixoMultiplicador(double m) {
        return m > 1.0 ? String.format(" (x%.2f)", m).replace('.', ',') : "";
    }

    private OrientacaoDocument orientacao(String id) {
        return id == null ? null : orientacaoRepository.findById(id).orElse(null);
    }

    private void seguro(String contexto, Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException ex) {
            log.warn("Gamificação falhou ({}); operação principal mantida: {}", contexto, ex.getMessage());
        }
    }

    static boolean temTag(PontoEventoDocument ev, String tag) {
        return ev.getTag() != null && List.of(ev.getTag().split(",")).contains(tag);
    }

    static boolean conta(PontoEventoDocument ev) {
        return !ev.isEstornado() && ev.getTipo() != TipoEvento.ESTORNO && Objects.nonNull(ev.getTipo());
    }
}
