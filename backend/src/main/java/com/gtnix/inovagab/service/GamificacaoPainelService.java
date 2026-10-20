package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.PontoEventoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.document.RecompensaDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.NivelGamificacao;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.StatusProjeto;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.ConquistaResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.EquipeRankingResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.EventoResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.ExtratoResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.ImpactoResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.IndicacaoInovaGabResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.IndicacoesInovaGabResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.MeResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.MissaoResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.NivelResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.RankingEntradaResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.RankingResponse;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.TemporadaResponse;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.PontoEventoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.repository.RecompensaRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Leitura da gamificação: painel do usuário, ranking, extrato. Regras em docs/GAMIFICACAO.md. */
@Service
public class GamificacaoPainelService {

    static final int[] METAS_TIER = {1, 3, 10, 25};
    static final String[] NOMES_TIER = {"BRONZE", "PRATA", "OURO", "PLATINA"};
    static final int LIMITE_RANKING = 50;
    static final int LIMITE_RANKING_MAX = 200;
    static final int ZONA_PREMIO = 3;
    static final int LIMITE_NOVIDADES = 10;
    static final int LIMITE_EXTRATO = 100;

    private final PontoEventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IdeiaRepository ideiaRepository;
    private final ProjetoRepository projetoRepository;
    private final OrientacaoRepository orientacaoRepository;
    private final RecompensaRepository recompensaRepository;
    private final GamificacaoService gamificacaoService;
    private final Clock clock;

    public GamificacaoPainelService(
            PontoEventoRepository eventoRepository,
            UsuarioRepository usuarioRepository,
            IdeiaRepository ideiaRepository,
            ProjetoRepository projetoRepository,
            OrientacaoRepository orientacaoRepository,
            RecompensaRepository recompensaRepository,
            GamificacaoService gamificacaoService,
            Clock clock
    ) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ideiaRepository = ideiaRepository;
        this.projetoRepository = projetoRepository;
        this.orientacaoRepository = orientacaoRepository;
        this.recompensaRepository = recompensaRepository;
        this.gamificacaoService = gamificacaoService;
        this.clock = clock;
    }

    // ---------------------------------------------------------------------
    // Painel
    // ---------------------------------------------------------------------

    /**
     * Painel do usuário. Missões concluídas são creditadas aqui (idempotente por semana),
     * então o prêmio aparece no extrato na mesma resposta.
     */
    public MeResponse me(UsuarioPrincipal principal) {
        UsuarioDocument usuario = usuario(principal);
        List<PontoEventoDocument> eventos = eventoRepository.findByUserIdOrderByCriadoEmDesc(usuario.getId());
        List<MissaoAvaliada> missoes = avaliarMissoes(usuario, eventos);
        boolean creditou = false;
        for (MissaoAvaliada m : missoes) {
            if (m.resposta().concluida()) {
                creditou |= gamificacaoService.registrar(m.lancamento()).isPresent();
            }
        }
        if (creditou) {
            eventos = eventoRepository.findByUserIdOrderByCriadoEmDesc(usuario.getId());
        }

        Temporada temporada = Temporada.de(LocalDate.now(clock));
        int xpTotal = eventos.stream().mapToInt(PontoEventoDocument::getXp).sum();
        int asas = eventos.stream().mapToInt(PontoEventoDocument::getAsas).sum();
        int xpTemporada = eventos.stream()
                .filter(e -> temporada.id().equals(e.getTemporadaId()))
                .mapToInt(PontoEventoDocument::getXp)
                .sum();

        RankingResponse ranking = ranking(principal, usuario.getPerfil(), "DIVISAO", null);
        List<EventoResponse> novidades = eventoRepository.findByUserIdAndVistoFalseOrderByCriadoEmDesc(usuario.getId())
                .stream()
                .limit(LIMITE_NOVIDADES)
                .map(GamificacaoPainelService::toEvento)
                .toList();

        return new MeResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getDivisao(),
                usuario.getEquipe(),
                xpTotal,
                xpTemporada,
                asas,
                nivel(xpTotal),
                temporada(temporada),
                ranking.minhaPosicao(),
                ranking.totalParticipantes(),
                sequenciaSemanas(eventos),
                conquistas(usuario, eventos),
                missoes.stream().map(MissaoAvaliada::resposta).toList(),
                impacto(usuario, eventos),
                novidades
        );
    }

    public ExtratoResponse extrato(UsuarioPrincipal principal) {
        List<PontoEventoDocument> eventos = eventoRepository.findByUserIdOrderByCriadoEmDesc(principal.getUserId());
        return new ExtratoResponse(
                eventos.stream().mapToInt(PontoEventoDocument::getXp).sum(),
                eventos.stream().mapToInt(PontoEventoDocument::getAsas).sum(),
                eventos.stream().limit(LIMITE_EXTRATO).map(GamificacaoPainelService::toEvento).toList()
        );
    }

    public void marcarNovidadesVistas(UsuarioPrincipal principal) {
        List<PontoEventoDocument> novos = eventoRepository.findByUserIdAndVistoFalseOrderByCriadoEmDesc(principal.getUserId());
        novos.forEach(e -> e.setVisto(true));
        eventoRepository.saveAll(novos);
    }

    // ---------------------------------------------------------------------
    // Ranking
    // ---------------------------------------------------------------------

    public RankingResponse ranking(UsuarioPrincipal principal, PerfilUsuario perfil, String escopo, String temporadaId) {
        return ranking(principal, perfil, escopo, temporadaId, 0, LIMITE_RANKING);
    }

    public RankingResponse ranking(
            UsuarioPrincipal principal,
            PerfilUsuario perfil,
            String escopo,
            String temporadaId,
            int pagina,
            int tamanho
    ) {
        PerfilUsuario perfilAlvo = perfil != null ? perfil : principal.getPerfil();
        exigirAba(principal.getPerfil(), perfilAlvo);
        Temporada temporada = temporada(temporadaId);
        UsuarioDocument eu = usuario(principal);
        boolean soDivisao = !"GRUPO".equalsIgnoreCase(escopo);

        List<UsuarioDocument> participantes = usuarioRepository.findByPerfilAndAtivoTrue(perfilAlvo).stream()
                .filter(u -> !soDivisao || Objects.equals(u.getDivisao(), eu.getDivisao()))
                .toList();
        Map<String, Integer> xpTemporada = somarXp(eventoRepository.findByTemporadaIdAndPerfil(temporada.id(), perfilAlvo));
        Map<String, Integer> xpVida = somarXp(eventoRepository.findByPerfil(perfilAlvo));

        List<UsuarioDocument> ordenados = participantes.stream()
                .sorted(Comparator.<UsuarioDocument>comparingInt(u -> -xpTemporada.getOrDefault(u.getId(), 0))
                        .thenComparing(u -> u.getNome() != null ? u.getNome() : ""))
                .toList();

        Map<String, Integer> posicaoNaDivisao = new HashMap<>();
        List<RankingEntradaResponse> entradas = new ArrayList<>();
        for (int i = 0; i < ordenados.size(); i++) {
            UsuarioDocument u = ordenados.get(i);
            int xp = xpTemporada.getOrDefault(u.getId(), 0);
            int naDivisao = posicaoNaDivisao.merge(String.valueOf(u.getDivisao()), 1, Integer::sum);
            NivelGamificacao nivel = NivelGamificacao.fromXp(xpVida.getOrDefault(u.getId(), 0));
            entradas.add(new RankingEntradaResponse(
                    i + 1,
                    u.getId(),
                    u.getNome(),
                    perfilAlvo,
                    u.getDivisao(),
                    u.getEquipe(),
                    xp,
                    nivel.name(),
                    nivel.getLabel(),
                    perfilAlvo == PerfilUsuario.OPERADOR && xp > 0 && naDivisao <= ZONA_PREMIO,
                    u.getId().equals(eu.getId())
            ));
        }
        RankingEntradaResponse minha = entradas.stream().filter(RankingEntradaResponse::voce).findFirst().orElse(null);
        int page = Math.max(0, pagina);
        boolean listaCompleta = tamanho < 0;
        int size = listaCompleta
                ? Math.max(entradas.size(), 1)
                : (tamanho <= 0 ? LIMITE_RANKING : Math.min(tamanho, LIMITE_RANKING_MAX));
        int from = Math.min(page * size, entradas.size());
        int to = Math.min(from + size, entradas.size());
        return new RankingResponse(
                temporada(temporada),
                perfilAlvo,
                soDivisao ? "DIVISAO" : "GRUPO",
                entradas.subList(from, to),
                minha != null ? minha.posicao() : null,
                minha,
                entradas.size(),
                regraPremio(perfilAlvo),
                page,
                size,
                to < entradas.size()
        );
    }

    /**
     * Retrato da zona de prêmio para o comitê do Prêmio InovaGAB.
     * Não envia e-mail nem muda status de ideia: é a lista que o Líder exporta ao fim da temporada.
     */
    public IndicacoesInovaGabResponse indicacoes(UsuarioPrincipal principal, String temporadaId) {
        if (principal.getPerfil() != PerfilUsuario.LIDER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Somente o Líder consulta as indicações ao Prêmio InovaGAB");
        }
        RankingResponse ranking = ranking(principal, PerfilUsuario.OPERADOR, "GRUPO", temporadaId, 0, -1);
        List<IndicacaoInovaGabResponse> indicados = new ArrayList<>();
        Map<String, Integer> ordem = new HashMap<>();
        for (RankingEntradaResponse entrada : ranking.entradas()) {
            if (!entrada.zonaPremio()) {
                continue;
            }
            int posicao = ordem.merge(entrada.divisao(), 1, Integer::sum);
            indicados.add(new IndicacaoInovaGabResponse(
                    entrada.divisao(),
                    posicao,
                    entrada.usuarioId(),
                    entrada.nome(),
                    entrada.equipe(),
                    entrada.xpTemporada()
            ));
        }
        return new IndicacoesInovaGabResponse(
                ranking.temporada(),
                "Top 3 operadores de cada divisão com XP maior que zero nesta temporada. "
                        + "O comitê de inovação usa esta lista para a categoria Imova-se; o app não inscreve ninguém sozinho.",
                indicados
        );
    }

    /** Gincana por equipe/base: média de XP da temporada por operador, para equipes pequenas competirem. */
    public List<EquipeRankingResponse> rankingEquipes(UsuarioPrincipal principal, String escopo) {
        UsuarioDocument eu = usuario(principal);
        boolean soDivisao = !"GRUPO".equalsIgnoreCase(escopo);
        Temporada temporada = Temporada.de(LocalDate.now(clock));
        Map<String, Integer> xp = somarXp(eventoRepository.findByTemporadaIdAndPerfil(temporada.id(), PerfilUsuario.OPERADOR));

        Map<String, List<UsuarioDocument>> porEquipe = usuarioRepository.findByPerfilAndAtivoTrue(PerfilUsuario.OPERADOR).stream()
                .filter(u -> u.getEquipe() != null && !u.getEquipe().isBlank())
                .filter(u -> !soDivisao || Objects.equals(u.getDivisao(), eu.getDivisao()))
                .collect(Collectors.groupingBy(UsuarioDocument::getEquipe));

        record Linha(String equipe, String divisao, int membros, int total) {
            int media() {
                return membros == 0 ? 0 : total / membros;
            }
        }
        List<Linha> linhas = porEquipe.entrySet().stream()
                .map(e -> new Linha(
                        e.getKey(),
                        e.getValue().get(0).getDivisao(),
                        e.getValue().size(),
                        e.getValue().stream().mapToInt(u -> xp.getOrDefault(u.getId(), 0)).sum()))
                .sorted(Comparator.comparingInt(Linha::media).reversed()
                        .thenComparing(Comparator.comparingInt(Linha::total).reversed())
                        .thenComparing(Linha::equipe))
                .toList();

        List<EquipeRankingResponse> resposta = new ArrayList<>();
        for (int i = 0; i < linhas.size(); i++) {
            Linha l = linhas.get(i);
            resposta.add(new EquipeRankingResponse(i + 1, l.equipe(), l.divisao(), l.membros(), l.total(), l.media(),
                    l.equipe().equals(eu.getEquipe())));
        }
        return resposta;
    }

    static void exigirAba(PerfilUsuario quemConsulta, PerfilUsuario aba) {
        boolean permitido = switch (quemConsulta) {
            case OPERADOR -> aba == PerfilUsuario.OPERADOR;
            case GESTOR -> aba == PerfilUsuario.GESTOR || aba == PerfilUsuario.OPERADOR;
            case LIDER -> true;
        };
        if (!permitido) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Ranking não disponível para o seu perfil");
        }
    }

    private static String regraPremio(PerfilUsuario perfil) {
        return switch (perfil) {
            case OPERADOR -> "Os 3 primeiros de cada divisão ao fim da temporada são indicados ao Prêmio InovaGAB (categoria Imova-se).";
            case GESTOR -> "Sobe quem avalia no prazo, dá feedback útil e entrega projetos no prazo.";
            case LIDER -> "Sobe quem recebe ideias nas suas orientações e conclui projetos nelas.";
        };
    }

    // ---------------------------------------------------------------------
    // Conquistas
    // ---------------------------------------------------------------------

    private record ConquistaDef(String tipo, String titulo, String descricao, Predicate<PontoEventoDocument> filtro) {
    }

    private static Predicate<PontoEventoDocument> tipo(TipoEvento t) {
        return e -> e.getTipo() == t;
    }

    private static final List<ConquistaDef> CONQUISTAS_OPERADOR = List.of(
            new ConquistaDef("PRIMEIRA_IDEIA", "Voz do campo", "Ideias enviadas que pontuaram", tipo(TipoEvento.IDEIA_ENVIADA)),
            new ConquistaDef("PRIMEIRA_APROVACAO", "Ideia validada", "Ideias aprovadas pelo gestor", tipo(TipoEvento.IDEIA_APROVADA)),
            new ConquistaDef("EM_PROJETO", "Do papel ao projeto", "Ideias que viraram projeto", tipo(TipoEvento.IDEIA_CONVERTIDA)),
            new ConquistaDef("TRANSFORMADOR", "Resultado entregue", "Projetos concluídos a partir das suas ideias",
                    tipo(TipoEvento.PROJETO_CONCLUIDO_AUTOR)),
            new ConquistaDef("INOVADOR", "Alinhado à estratégia", "Ideias aprovadas em campanha patrocinada",
                    tipo(TipoEvento.IDEIA_APROVADA).and(e -> GamificacaoService.temTag(e, GamificacaoService.TAG_CAMPANHA))),
            new ConquistaDef("GUARDIAO_SEGURANCA", "Guardião da segurança", "Ideias de segurança aprovadas",
                    tipo(TipoEvento.IDEIA_APROVADA).and(e -> GamificacaoService.temTag(e, GamificacaoService.CATEGORIA_SEGURANCA))),
            new ConquistaDef("INFLUENCIADOR", "Apoio da equipe", "Apoios recebidos de gestores e líderes",
                    tipo(TipoEvento.APOIO_RECEBIDO))
    );

    private static final List<ConquistaDef> CONQUISTAS_GESTOR = List.of(
            new ConquistaDef("CURADOR", "Curador ágil", "Ideias avaliadas em até 7 dias", tipo(TipoEvento.AVALIACAO_NO_PRAZO)),
            new ConquistaDef("MENTOR", "Mentor", "Avaliações marcadas como úteis pelo autor", tipo(TipoEvento.FEEDBACK_UTIL)),
            new ConquistaDef("LIDER_RESULTADOS", "Ideias que viram projeto", "Ideias aprovadas por você que viraram projeto",
                    tipo(TipoEvento.APROVACAO_CONVERTIDA)),
            new ConquistaDef("EXECUTOR", "Entrega no prazo", "Projetos concluídos dentro do prazo", tipo(TipoEvento.PROJETO_NO_PRAZO))
    );

    private static final List<ConquistaDef> CONQUISTAS_LIDER = List.of(
            new ConquistaDef("ESTRATEGISTA", "Estratégia que engaja", "Ideias recebidas nas suas orientações",
                    tipo(TipoEvento.IDEIA_ALINHADA)),
            new ConquistaDef("TRANSFORMADOR_ESTRATEGICO", "Portfólio que entrega", "Projetos concluídos nas suas orientações",
                    tipo(TipoEvento.PROJETO_CONCLUIDO_ESTRATEGIA))
    );

    List<ConquistaResponse> conquistas(UsuarioDocument usuario, List<PontoEventoDocument> eventos) {
        List<PontoEventoDocument> validos = eventos.stream()
                .filter(GamificacaoService::conta)
                .sorted(Comparator.comparing(PontoEventoDocument::getCriadoEm))
                .toList();
        List<ConquistaResponse> lista = new ArrayList<>();
        lista.add(new ConquistaResponse("BEM_VINDO", "Bem-vindo", "Entrou na Economia de Inovação GAB",
                1, NOMES_TIER[0], true, null, 1.0, validos.isEmpty() ? null : validos.get(0).getCriadoEm()));

        List<ConquistaDef> defs = switch (usuario.getPerfil()) {
            case OPERADOR -> CONQUISTAS_OPERADOR;
            case GESTOR -> CONQUISTAS_GESTOR;
            case LIDER -> CONQUISTAS_LIDER;
        };
        for (ConquistaDef def : defs) {
            List<PontoEventoDocument> casos = validos.stream().filter(def.filtro()).toList();
            lista.add(conquista(def.tipo(), def.titulo(), def.descricao(), casos.size(),
                    casos.isEmpty() ? null : casos.get(0).getCriadoEm()));
        }
        if (usuario.getPerfil() == PerfilUsuario.LIDER) {
            List<RecompensaDocument> ativos = recompensaRepository.findByAtivoTrueOrderByCustoAsasAsc();
            Instant primeira = ativos.stream()
                    .map(RecompensaDocument::getDataCriacao)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(null);
            lista.add(conquista("VISIONARIO_ESTRATEGICO", "Reconhecedor", "Prêmios ativos no catálogo",
                    ativos.size(), primeira));
        }
        return lista;
    }

    static ConquistaResponse conquista(String tipo, String titulo, String descricao, int valor, Instant primeiraVez) {
        String tier = null;
        Integer proxima = null;
        for (int i = 0; i < METAS_TIER.length; i++) {
            if (valor >= METAS_TIER[i]) {
                tier = NOMES_TIER[i];
            } else {
                proxima = METAS_TIER[i];
                break;
            }
        }
        double progresso = proxima == null ? 1.0 : Math.min(1.0, (double) valor / proxima);
        return new ConquistaResponse(tipo, titulo, descricao, valor, tier, tier != null, proxima, progresso,
                tier != null ? primeiraVez : null);
    }

    // ---------------------------------------------------------------------
    // Missões semanais
    // ---------------------------------------------------------------------

    private record MissaoAvaliada(MissaoResponse resposta, GamificacaoService.Lancamento lancamento) {
    }

    List<MissaoAvaliada> avaliarMissoes(UsuarioDocument usuario, List<PontoEventoDocument> eventos) {
        LocalDate semana = Temporada.inicioSemana(LocalDate.now(clock));
        Instant inicioSemana = semana.atStartOfDay(Temporada.ZONA).toInstant();
        List<PontoEventoDocument> daSemana = eventos.stream()
                .filter(GamificacaoService::conta)
                .filter(e -> !e.getCriadoEm().isBefore(inicioSemana))
                .toList();
        Instant agora = clock.instant();

        List<MissaoResponse> missoes = switch (usuario.getPerfil()) {
            case OPERADOR -> List.of(
                    missao("ideia-semana", "Ideia da semana", "Envie 1 ideia nesta semana",
                            contar(daSemana, tipo(TipoEvento.IDEIA_ENVIADA)), 1, 5, 0),
                    missao("seguranca-semana", "Olhar de segurança", "Envie 1 ideia para uma orientação de Segurança",
                            contar(daSemana, tipo(TipoEvento.IDEIA_ENVIADA)
                                    .and(e -> GamificacaoService.temTag(e, GamificacaoService.CATEGORIA_SEGURANCA))), 1, 10, 5)
            );
            case GESTOR -> {
                int noPrazo = contar(daSemana, tipo(TipoEvento.AVALIACAO_NO_PRAZO));
                int avaliacoes = noPrazo + contar(daSemana, tipo(TipoEvento.AVALIACAO_FORA_PRAZO));
                long antigas = ideiaRepository.findByAtivoTrueAndStatus(StatusIdeia.PENDENTE).stream()
                        .filter(i -> naFilaDoGestor(usuario, i))
                        .filter(i -> i.getDataCriacao() != null
                                && Duration.between(i.getDataCriacao(), agora).compareTo(GamificacaoService.SLA_AVALIACAO) > 0)
                        .count();
                yield List.of(
                        missao("avaliacoes-prazo", "Fila ágil", "Avalie 3 ideias dentro do prazo de 7 dias",
                                noPrazo, 3, 10, 5),
                        missao("fila-em-dia", "Ninguém esperando",
                                "Avalie ao menos 1 ideia e não deixe nenhuma da sua área pendente há mais de 7 dias",
                                avaliacoes >= 1 && antigas == 0 ? 1 : 0, 1, 10, 0)
                );
            }
            case LIDER -> {
                boolean catalogoEmDia = recompensaRepository.findByAtivoTrueOrderByCustoAsasAsc().stream()
                        .anyMatch(r -> r.getEstoque() > 0);
                yield List.of(
                        missao("ideias-alinhadas", "Estratégia viva", "Receba 3 ideias nas suas orientações nesta semana",
                                contar(daSemana, tipo(TipoEvento.IDEIA_ALINHADA)), 3, 10, 0),
                        missao("catalogo-em-dia", "Catálogo em dia",
                                "Mantenha ao menos 1 prêmio ativo com estoque",
                                catalogoEmDia ? 1 : 0, 1, 10, 0)
                );
            }
        };
        return missoes.stream()
                .map(m -> new MissaoAvaliada(m, new GamificacaoService.Lancamento(
                        "MISSAO_CONCLUIDA:" + m.id() + ":" + semana + ":" + usuario.getId(),
                        usuario.getId(),
                        TipoEvento.MISSAO_CONCLUIDA,
                        m.recompensaXp(),
                        m.recompensaAsas(),
                        "missao-" + m.id() + "-" + semana,
                        null,
                        "Missão concluída: " + m.titulo())))
                .toList();
    }

    /** Pendência conta só na área do gestor. Sem área cadastrada, a fila inteira continua valendo. */
    static boolean naFilaDoGestor(UsuarioDocument gestor, IdeiaDocument ideia) {
        String area = gestor.getArea();
        if (area == null || area.isBlank()) {
            return true;
        }
        return area.equalsIgnoreCase(ideia.getArea());
    }

    private static MissaoResponse missao(String id, String titulo, String descricao, int progresso, int meta, int xp, int asas) {
        return new MissaoResponse(id, titulo, descricao, Math.min(progresso, meta), meta, progresso >= meta, xp, asas);
    }

    private static int contar(List<PontoEventoDocument> eventos, Predicate<PontoEventoDocument> filtro) {
        return (int) eventos.stream().filter(filtro).count();
    }

    // ---------------------------------------------------------------------
    // Sequência, impacto, nível
    // ---------------------------------------------------------------------

    int sequenciaSemanas(List<PontoEventoDocument> eventos) {
        Set<LocalDate> semanas = new HashSet<>();
        for (PontoEventoDocument e : eventos) {
            if (GamificacaoService.conta(e) && e.getTipo().isAtividade() && e.getXp() > 0) {
                semanas.add(Temporada.inicioSemana(LocalDate.ofInstant(e.getCriadoEm(), Temporada.ZONA)));
            }
        }
        LocalDate semana = Temporada.inicioSemana(LocalDate.now(clock));
        if (!semanas.contains(semana)) {
            semana = semana.minusWeeks(1);
        }
        int sequencia = 0;
        while (semanas.contains(semana)) {
            sequencia++;
            semana = semana.minusWeeks(1);
        }
        return sequencia;
    }

    ImpactoResponse impacto(UsuarioDocument usuario, List<PontoEventoDocument> eventos) {
        List<PontoEventoDocument> validos = eventos.stream().filter(GamificacaoService::conta).toList();
        int avaliadas = contar(validos, tipo(TipoEvento.AVALIACAO_NO_PRAZO).or(tipo(TipoEvento.AVALIACAO_FORA_PRAZO)));
        int alinhadas = contar(validos, tipo(TipoEvento.IDEIA_ALINHADA));
        return switch (usuario.getPerfil()) {
            case OPERADOR -> {
                List<IdeiaDocument> ideias = ideiaRepository.findByAutorUserIdAndAtivoTrue(usuario.getId());
                List<ProjetoDocument> projetos = ideias.stream()
                        .map(i -> projetoRepository.findByIdeiaIdAndAtivoTrue(i.getId()).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();
                int aprovadas = (int) ideias.stream()
                        .filter(i -> i.getStatus() == StatusIdeia.APROVADA || i.getStatus() == StatusIdeia.CONVERTIDA_PROJETO)
                        .count();
                yield impactoDe(ideias.size(), aprovadas, 0, 0, projetos);
            }
            case GESTOR -> impactoDe(0, 0, avaliadas, 0,
                    projetoRepository.findByResponsavelUserIdAndAtivoTrue(usuario.getId()));
            case LIDER -> {
                List<ProjetoDocument> projetos = orientacaoRepository.findByCriadoPorUserId(usuario.getId()).stream()
                        .map(OrientacaoDocument::getId)
                        .flatMap(id -> projetoRepository.findByOrientacaoIdAndAtivoTrue(id).stream())
                        .toList();
                yield impactoDe(0, 0, 0, alinhadas, projetos);
            }
        };
    }

    private static ImpactoResponse impactoDe(int enviadas, int aprovadas, int avaliadas, int alinhadas,
                                             List<ProjetoDocument> projetos) {
        List<ProjetoDocument> validos = projetos.stream()
                .filter(p -> !StatusProjeto.CANCELADO.name().equals(p.getStatus()))
                .toList();
        int concluidos = (int) validos.stream().filter(p -> StatusProjeto.CONCLUIDO.name().equals(p.getStatus())).count();
        BigDecimal retorno = validos.stream()
                .map(ProjetoDocument::getRetorno)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ImpactoResponse(enviadas, aprovadas, avaliadas, alinhadas, validos.size(), concluidos, retorno);
    }

    static NivelResponse nivel(int xp) {
        NivelGamificacao atual = NivelGamificacao.fromXp(xp);
        NivelGamificacao proximo = atual.proximo();
        if (proximo == null) {
            return new NivelResponse(atual.name(), atual.getLabel(), atual.getXpMinimo(), null, null, null, 0, 1.0);
        }
        int faixa = proximo.getXpMinimo() - atual.getXpMinimo();
        double progresso = Math.max(0, Math.min(1.0, (double) (xp - atual.getXpMinimo()) / faixa));
        return new NivelResponse(atual.name(), atual.getLabel(), atual.getXpMinimo(), proximo.name(),
                proximo.getLabel(), proximo.getXpMinimo(), Math.max(0, proximo.getXpMinimo() - xp), progresso);
    }

    // ---------------------------------------------------------------------
    // Utilitários
    // ---------------------------------------------------------------------

    private UsuarioDocument usuario(UsuarioPrincipal principal) {
        return usuarioRepository.findById(principal.getUserId()).orElse(principal.getUsuario());
    }

    private Temporada temporada(String id) {
        if (id == null || id.isBlank()) {
            return Temporada.de(LocalDate.now(clock));
        }
        try {
            return Temporada.parse(id);
        } catch (RuntimeException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Temporada inválida. Use o formato 2026-T4");
        }
    }

    private TemporadaResponse temporada(Temporada t) {
        return new TemporadaResponse(t.id(), t.label(), t.inicio(), t.fim(), t.diasRestantes(LocalDate.now(clock)));
    }

    private static Map<String, Integer> somarXp(List<PontoEventoDocument> eventos) {
        Map<String, Integer> soma = new HashMap<>();
        for (PontoEventoDocument e : eventos) {
            soma.merge(e.getUserId(), e.getXp(), Integer::sum);
        }
        return soma;
    }

    static EventoResponse toEvento(PontoEventoDocument e) {
        return new EventoResponse(e.getId(), e.getTipo(), e.getDescricao(), e.getXp(), e.getAsas(), e.getCriadoEm(),
                e.isEstornado());
    }
}
