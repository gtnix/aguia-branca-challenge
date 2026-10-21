package com.gtnix.inovagab.config;

import com.gtnix.inovagab.document.ApoioDocument;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.PontoEventoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.document.RecompensaDocument;
import com.gtnix.inovagab.document.ResgateDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.NivelGamificacao;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.StatusResgate;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.repository.ApoioRepository;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.PontoEventoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.repository.RecompensaRepository;
import com.gtnix.inovagab.repository.ResgateRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.service.GamificacaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Popula a Economia de Inovação para a demo: colegas em outras divisões e equipes (ranking com gente de verdade),
 * eventos da temporada, catálogo da loja, campanha patrocinada, um apoio e um resgate aguardando entrega.
 * Idempotente: pode rodar a cada boot.
 */
@Component
@Order(2)
public class GamificacaoSeeder implements CommandLineRunner {

    static final String PREFIXO_SEED = "SEED:";
    static final String ID_OPERADOR = DataSeeder.ID_OPERADOR;
    static final String ID_GESTOR = DataSeeder.ID_GESTOR;
    static final String ID_LIDER = DataSeeder.ID_LIDER;
    static final String ID_RESGATE_PEDRO_TRILHA = "resgate-pedro-trilha";

    private record Colega(String id, String nome, String email, PerfilUsuario perfil, String divisao, String equipe,
                          List<Map.Entry<TipoEvento, Integer>> eventos) {
    }

    private record Item(String id, String titulo, String descricao, String categoria, String parceiro, int custo,
                        int estoque, NivelGamificacao nivel) {
    }

    private static Map.Entry<TipoEvento, Integer> ev(TipoEvento tipo, int diasAtras) {
        return Map.entry(tipo, diasAtras);
    }

    static final List<Colega> COLEGAS = List.of(
            new Colega("user-op-joana", "Joana Ribeiro", "joana.ribeiro@aguiabranca.com.br", PerfilUsuario.OPERADOR,
                    "LOGISTICA", "CD Serra", List.of(
                    ev(TipoEvento.IDEIA_ENVIADA, 15), ev(TipoEvento.IDEIA_APROVADA, 14),
                    ev(TipoEvento.IDEIA_ENVIADA, 9), ev(TipoEvento.IDEIA_APROVADA, 8),
                    ev(TipoEvento.IDEIA_ENVIADA, 2), ev(TipoEvento.IDEIA_APROVADA, 1),
                    ev(TipoEvento.IDEIA_ENVIADA, 16), ev(TipoEvento.FEEDBACK_RECEBIDO, 12))),
            new Colega("user-op-rafael", "Rafael Costa", "rafael.costa@aguiabranca.com.br", PerfilUsuario.OPERADOR,
                    "LOGISTICA", "VIX · Jardim Limoeiro", List.of(
                    ev(TipoEvento.IDEIA_ENVIADA, 12), ev(TipoEvento.IDEIA_ENVIADA, 5),
                    ev(TipoEvento.IDEIA_APROVADA, 4), ev(TipoEvento.APOIO_RECEBIDO, 4))),
            new Colega("user-op-bruna", "Bruna Lima", "bruna.lima@aguiabranca.com.br", PerfilUsuario.OPERADOR,
                    "LOGISTICA", "SERVICARGA · Grande Vitória", List.of(
                    ev(TipoEvento.IDEIA_ENVIADA, 8), ev(TipoEvento.FEEDBACK_RECEBIDO, 6),
                    ev(TipoEvento.IDEIA_ENVIADA, 3))),
            new Colega("user-op-carlos", "Carlos Mendes", "carlos.mendes@aguiabranca.com.br", PerfilUsuario.OPERADOR,
                    "PASSAGEIROS", "Terminal de Vitória", List.of(
                    ev(TipoEvento.IDEIA_ENVIADA, 10), ev(TipoEvento.IDEIA_APROVADA, 9),
                    ev(TipoEvento.IDEIA_CONVERTIDA, 6), ev(TipoEvento.IDEIA_ENVIADA, 6),
                    ev(TipoEvento.IDEIA_APROVADA, 3), ev(TipoEvento.IDEIA_ENVIADA, 1))),
            new Colega("user-op-lucia", "Lúcia Ferraz", "lucia.ferraz@aguiabranca.com.br", PerfilUsuario.OPERADOR,
                    "PASSAGEIROS", "Terminal de Vitória", List.of(
                    ev(TipoEvento.IDEIA_ENVIADA, 11), ev(TipoEvento.IDEIA_ENVIADA, 4),
                    ev(TipoEvento.IDEIA_APROVADA, 2))),
            new Colega("user-op-thiago", "Thiago Alves", "thiago.alves@aguiabranca.com.br", PerfilUsuario.OPERADOR,
                    "COMERCIO", "Vitória Motors", List.of(
                    ev(TipoEvento.IDEIA_ENVIADA, 7), ev(TipoEvento.IDEIA_APROVADA, 5))),
            new Colega("user-gestor-paulo", "Paulo Nunes", "paulo.nunes@aguiabranca.com.br", PerfilUsuario.GESTOR,
                    "PASSAGEIROS", "Terminal de Vitória", List.of(
                    ev(TipoEvento.AVALIACAO_NO_PRAZO, 13), ev(TipoEvento.AVALIACAO_NO_PRAZO, 9),
                    ev(TipoEvento.AVALIACAO_NO_PRAZO, 5), ev(TipoEvento.FEEDBACK_UTIL, 4),
                    ev(TipoEvento.AVALIACAO_NO_PRAZO, 2)))
    );

    static final List<Item> CATALOGO = List.of(
            new Item("rec-passagem", "Passagem Águia Branca",
                    "Ida e volta em linha da Viação Águia Branca para você ou um familiar.",
                    "VIAGEM", "Viação Águia Branca", 120, 30, NivelGamificacao.EM_ASCENSAO),
            new Item("rec-revisao", "30% na revisão do seu carro",
                    "Desconto em revisão nas concessionárias do grupo (Toyota, Mercedes-Benz, BYD, Jeep/RAM).",
                    "VEICULO", "Divisão Comércio", 80, 50, NivelGamificacao.INICIANTE),
            new Item("rec-pneus", "Jogo de pneus Michelin com desconto",
                    "Condição de colaborador em pneus Michelin na rede do grupo.",
                    "VEICULO", "Michelin / Divisão Comércio", 150, 15, NivelGamificacao.ENGAJADO),
            new Item("rec-folga", "Day-off de reconhecimento",
                    "Um dia de folga remunerada, agendado com o seu gestor.",
                    "TEMPO", "RH Águia Branca", 200, 20, NivelGamificacao.ENGAJADO),
            new Item("rec-reserva", "Visita à Reserva Águia Branca",
                    "Visita guiada à Reserva Ambiental Águia Branca, em Vargem Alta/ES, com a equipe de educação ambiental.",
                    "VIAGEM", "Reserva Águia Branca", 180, 20, NivelGamificacao.EM_ASCENSAO),
            new Item("rec-trilha", "Trilha na Universidade Corporativa GAB",
                    "Curso de liderança, segurança ou tecnologia na Universidade Corporativa.",
                    "CARREIRA", "Universidade Corporativa GAB", 40, 200, NivelGamificacao.INICIANTE),
            new Item("rec-almoco", "Almoço com a liderança do Grupo",
                    "Apresente sua ideia pessoalmente para a liderança da sua divisão.",
                    "CARREIRA", "Holding Águia Branca", 250, 6, NivelGamificacao.VISIONARIO),
            new Item("rec-demoday", "Vaga no Demoday do Prêmio Inovação",
                    "Pitch da sua ideia no Demoday do Prêmio Inovação Águia Branca, com mentoria antes do evento.",
                    "CARREIRA", "Prêmio Inovação Águia Branca", 400, 3, NivelGamificacao.VISIONARIO)
    );

    /**
     * Dias atrás em que cada ideia semeada "pontuou". Fica nas semanas anteriores para não consumir o limite diário
     * nem completar a missão semanal do Pedro antes da demo, e forma uma sequência de semanas ativas.
     */
    static final Map<String, Integer> HISTORICO_IDEIAS_DIAS = new LinkedHashMap<>();

    static {
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_PREDITIVA, 10);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_OFICINA, 13);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_INSPECAO_PAPEL, 8);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_PONTO_CEGO, 7);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_TELEMETRIA, 9);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_CHECKIN, 20);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_ENCOMENDA, 12);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_OFICINA_COMERCIO, 11);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_ROTAS, 15);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_RECARGA, 14);
        HISTORICO_IDEIAS_DIAS.put(DataSeeder.ID_IDEIA_TEST_DRIVE, 8);
    }

    private final UsuarioRepository usuarioRepository;
    private final PontoEventoRepository eventoRepository;
    private final RecompensaRepository recompensaRepository;
    private final OrientacaoRepository orientacaoRepository;
    private final IdeiaRepository ideiaRepository;
    private final ProjetoRepository projetoRepository;
    private final ResgateRepository resgateRepository;
    private final ApoioRepository apoioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public GamificacaoSeeder(
            UsuarioRepository usuarioRepository,
            PontoEventoRepository eventoRepository,
            RecompensaRepository recompensaRepository,
            OrientacaoRepository orientacaoRepository,
            IdeiaRepository ideiaRepository,
            ProjetoRepository projetoRepository,
            ResgateRepository resgateRepository,
            ApoioRepository apoioRepository,
            PasswordEncoder passwordEncoder,
            Clock clock
    ) {
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.recompensaRepository = recompensaRepository;
        this.orientacaoRepository = orientacaoRepository;
        this.ideiaRepository = ideiaRepository;
        this.projetoRepository = projetoRepository;
        this.resgateRepository = resgateRepository;
        this.apoioRepository = apoioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        definirEquipesPrincipais();
        upsertColegas();
        upsertCatalogo();
        ativarCampanhaSeguranca();
        creditarHistoricoDemo();
        creditarApoioJoana();
        creditarResgatePedro();
    }

    void definirEquipesPrincipais() {
        Map<String, String> equipes = Map.of(
                ID_OPERADOR, "CD Serra",
                ID_GESTOR, "CD Serra",
                ID_LIDER, "Holding");
        equipes.forEach((id, equipe) -> usuarioRepository.findById(id).ifPresent(u -> {
            u.setEquipe(equipe);
            usuarioRepository.save(u);
        }));
    }

    void upsertColegas() {
        String hash = null;
        Instant agora = clock.instant();
        Temporada temporada = Temporada.de(agora);
        for (Colega c : COLEGAS) {
            UsuarioDocument u = usuarioRepository.findById(c.id()).orElse(null);
            if (u == null) {
                if (hash == null) {
                    hash = passwordEncoder.encode("123456");
                }
                u = new UsuarioDocument();
                u.setId(c.id());
                u.setNome(c.nome());
                u.setEmail(c.email());
                u.setSenhaHash(hash);
                u.setPerfil(c.perfil());
                u.setArea("OPERACOES");
                u.setDivisao(c.divisao());
                u.setEquipe(c.equipe());
                usuarioRepository.save(u);
            } else {
                u.setDivisao(c.divisao());
                u.setEquipe(c.equipe());
                usuarioRepository.save(u);
            }
            for (int i = 0; i < c.eventos().size(); i++) {
                Map.Entry<TipoEvento, Integer> e = c.eventos().get(i);
                Instant quando = dentroDaTemporada(agora.minus(Duration.ofDays(e.getValue())), temporada);
                salvarEvento(PREFIXO_SEED + temporada.id() + ":" + c.id() + ":" + i, u, e.getKey(),
                        e.getKey().getXpBase(), e.getKey().getAsasBase(), "seed-" + c.id() + "-" + i, null, quando, true);
            }
        }
    }

    void upsertCatalogo() {
        Instant agora = clock.instant();
        for (Item item : CATALOGO) {
            RecompensaDocument doc = recompensaRepository.findById(item.id()).orElse(null);
            boolean novo = doc == null;
            if (novo) {
                doc = new RecompensaDocument();
                doc.setId(item.id());
                doc.setEstoque(item.estoque());
                doc.setDataCriacao(agora);
                doc.setAtivo(true);
            }
            doc.setTitulo(item.titulo());
            doc.setDescricao(item.descricao());
            doc.setCategoria(item.categoria());
            doc.setParceiro(item.parceiro());
            doc.setCustoAsas(item.custo());
            doc.setNivelMinimo(item.nivel());
            doc.setDataAtualizacao(agora);
            recompensaRepository.save(doc);
        }
    }

    void ativarCampanhaSeguranca() {
        orientacaoRepository.findById(DataSeeder.ID_ORI_SEGURANCA).ifPresent(ori -> {
            if (ori.getCampanhaFim() == null) {
                ori.setCampanha("Jornada Segura 2026");
                ori.setCampanhaFim(LocalDate.now(clock).plusDays(30));
                ori.setCampanhaBonusAsas(50);
                ori.setCampanhaTopN(3);
                orientacaoRepository.save(ori);
            }
        });
    }

    /**
     * Histórico das ideias semeadas com as mesmas chaves que os ganchos usariam, para não duplicar ao vivo.
     * Não conclui projeto do Pedro (senão ele ganha 100 Asas e a Loja deixa de mostrar bloqueio da passagem).
     */
    void creditarHistoricoDemo() {
        Instant agora = clock.instant();
        Temporada temporada = Temporada.de(agora);
        UsuarioDocument lider = usuarioRepository.findById(ID_LIDER).orElse(null);
        UsuarioDocument gestor = usuarioRepository.findById(ID_GESTOR).orElse(null);

        for (Map.Entry<String, Integer> entrada : HISTORICO_IDEIAS_DIAS.entrySet()) {
            IdeiaDocument ideia = ideiaRepository.findById(entrada.getKey()).orElse(null);
            if (ideia == null || !ideia.isAtivo()) {
                continue;
            }
            UsuarioDocument autor = usuarioRepository.findById(ideia.getAutorUserId()).orElse(null);
            if (autor == null) {
                continue;
            }
            Instant criada = dentroDaTemporada(agora.minus(Duration.ofDays(entrada.getValue())), temporada);
            OrientacaoDocument ori = orientacaoRepository.findById(ideia.getOrientacaoId()).orElse(null);
            String tag = ori != null ? ori.getCategoria() : null;
            padrao(autor, TipoEvento.IDEIA_ENVIADA, ideia.getId(), tag, criada, true);
            if (lider != null && ori != null && ID_LIDER.equals(ori.getCriadoPorUserId())) {
                padrao(lider, TipoEvento.IDEIA_ALINHADA, ideia.getId(), tag, criada, true);
            }

            if (ideia.getStatus() == StatusIdeia.PENDENTE || gestor == null) {
                continue;
            }
            Instant avaliada = ideia.getDataAvaliacao() != null
                    ? dentroDaTemporada(ideia.getDataAvaliacao(), temporada)
                    : dentroDaTemporada(criada.plus(Duration.ofDays(2)), temporada);
            salvarEvento("AVALIACAO:" + ideia.getId() + ":" + ID_GESTOR, gestor, TipoEvento.AVALIACAO_NO_PRAZO,
                    TipoEvento.AVALIACAO_NO_PRAZO.getXpBase(), TipoEvento.AVALIACAO_NO_PRAZO.getAsasBase(),
                    ideia.getId(), tag, avaliada, true);
            if (ideia.getStatus() == StatusIdeia.REPROVADA) {
                salvarEvento("FEEDBACK_RECEBIDO:" + temporada.id() + ":" + autor.getId(), autor,
                        TipoEvento.FEEDBACK_RECEBIDO, TipoEvento.FEEDBACK_RECEBIDO.getXpBase(), 0,
                        ideia.getId(), tag, avaliada, true);
            } else {
                padrao(autor, TipoEvento.IDEIA_APROVADA, ideia.getId(), tag, avaliada, true);
            }
            if (ideia.getStatus() == StatusIdeia.CONVERTIDA_PROJETO) {
                padrao(autor, TipoEvento.IDEIA_CONVERTIDA, ideia.getId(), tag, avaliada, true);
                padrao(gestor, TipoEvento.APROVACAO_CONVERTIDA, ideia.getId(), tag, avaliada, true);
            }
        }

        creditarProjetoConcluidoCheckin(gestor, lider, temporada, agora);
    }

    private void creditarProjetoConcluidoCheckin(
            UsuarioDocument gestor,
            UsuarioDocument lider,
            Temporada temporada,
            Instant agora
    ) {
        ProjetoDocument projeto = projetoRepository.findById(DataSeeder.ID_PROJ_CHECKIN).orElse(null);
        IdeiaDocument ideia = ideiaRepository.findById(DataSeeder.ID_IDEIA_CHECKIN).orElse(null);
        if (projeto == null || !projeto.isAtivo() || ideia == null) {
            return;
        }
        UsuarioDocument autor = usuarioRepository.findById(ideia.getAutorUserId()).orElse(null);
        if (autor == null) {
            return;
        }
        Instant quando = dentroDaTemporada(agora.minus(Duration.ofDays(5)), temporada);
        String tag = "EXPERIENCIA_CLIENTE";
        padrao(autor, TipoEvento.PROJETO_CONCLUIDO_AUTOR, projeto.getId(), tag, quando, true);
        int asasRetorno = GamificacaoService.asasPorRetorno(projeto.getRetorno());
        salvarEvento(TipoEvento.RETORNO_AUTOR.name() + ":" + projeto.getId() + ":" + autor.getId(), autor,
                TipoEvento.RETORNO_AUTOR, 0, asasRetorno, projeto.getId(), tag, quando, true);
        if (gestor != null) {
            padrao(gestor, TipoEvento.PROJETO_NO_PRAZO, projeto.getId(), tag, quando, true);
            BigDecimal roi = GamificacaoService.roiPercentual(projeto.getInvestimento(), projeto.getRetorno());
            int asasRoi = Math.max(5, Math.min(50, roi.divide(BigDecimal.TEN, 0, java.math.RoundingMode.DOWN).intValue()));
            salvarEvento(TipoEvento.ROI_POSITIVO.name() + ":" + projeto.getId() + ":" + gestor.getId(), gestor,
                    TipoEvento.ROI_POSITIVO, TipoEvento.ROI_POSITIVO.getXpBase(), asasRoi, projeto.getId(), tag,
                    quando, true);
        }
        if (lider != null) {
            padrao(lider, TipoEvento.PROJETO_CONCLUIDO_ESTRATEGIA, projeto.getId(), tag, quando, true);
        }
    }

    void creditarApoioJoana() {
        IdeiaDocument ideia = ideiaRepository.findById(DataSeeder.ID_IDEIA_PONTO_CEGO).orElse(null);
        UsuarioDocument joana = usuarioRepository.findById(DataSeeder.ID_OP_JOANA).orElse(null);
        if (ideia == null || joana == null) {
            return;
        }
        Instant quando = clock.instant().minus(Duration.ofDays(4));
        if (!apoioRepository.existsByIdeiaIdAndUserId(ideia.getId(), ID_GESTOR)) {
            ApoioDocument apoio = new ApoioDocument();
            apoio.setId("apoio-ana-ponto-cego");
            apoio.setIdeiaId(ideia.getId());
            apoio.setUserId(ID_GESTOR);
            apoio.setCriadoEm(quando);
            apoioRepository.save(apoio);
            salvarEvento("APOIO_RECEBIDO:" + ideia.getId() + ":" + ID_GESTOR, joana, TipoEvento.APOIO_RECEBIDO,
                    TipoEvento.APOIO_RECEBIDO.getXpBase(), 0, ideia.getId(), "SEGURANCA", quando, true);
        }
        if (ideia.getApoios() < 1) {
            ideia.setApoios(1);
            ideiaRepository.save(ideia);
        }
    }

    void creditarResgatePedro() {
        UsuarioDocument pedro = usuarioRepository.findById(ID_OPERADOR).orElse(null);
        RecompensaDocument trilha = recompensaRepository.findById("rec-trilha").orElse(null);
        if (pedro == null || trilha == null) {
            return;
        }
        Instant quando = clock.instant().minus(Duration.ofDays(2));
        if (resgateRepository.findById(ID_RESGATE_PEDRO_TRILHA).isPresent()) {
            return;
        }
        ResgateDocument resgate = new ResgateDocument();
        resgate.setId(ID_RESGATE_PEDRO_TRILHA);
        resgate.setRecompensaId(trilha.getId());
        resgate.setRecompensaTitulo(trilha.getTitulo());
        resgate.setUserId(pedro.getId());
        resgate.setUserNome(pedro.getNome());
        resgate.setCustoAsas(trilha.getCustoAsas());
        resgate.setStatus(StatusResgate.APROVADO);
        resgate.setSolicitadoEm(quando);
        resgate.setDecididoEm(quando);
        resgateRepository.save(resgate);

        trilha.setEstoque(Math.max(0, trilha.getEstoque() - 1));
        trilha.setDataAtualizacao(quando);
        recompensaRepository.save(trilha);

        salvarEvento("RESGATE:" + resgate.getId() + ":" + pedro.getId(), pedro, TipoEvento.RESGATE,
                0, -trilha.getCustoAsas(), resgate.getId(), trilha.getCategoria(), quando, false);
    }

    private void padrao(UsuarioDocument u, TipoEvento tipo, String refId, String tag, Instant quando, boolean visto) {
        salvarEvento(tipo.name() + ":" + refId + ":" + u.getId(), u, tipo, tipo.getXpBase(), tipo.getAsasBase(),
                refId, tag, quando, visto);
    }

    private void salvarEvento(String chave, UsuarioDocument u, TipoEvento tipo, int xp, int asas, String refId,
                              String tag, Instant quando, boolean visto) {
        if (eventoRepository.existsByChave(chave)) {
            return;
        }
        PontoEventoDocument e = new PontoEventoDocument();
        e.setChave(chave);
        e.setUserId(u.getId());
        e.setPerfil(u.getPerfil());
        e.setDivisao(u.getDivisao());
        e.setEquipe(u.getEquipe());
        e.setTipo(tipo);
        e.setXp(xp);
        e.setAsas(asas);
        e.setRefId(refId);
        e.setTag(tag);
        e.setDescricao(tipo == TipoEvento.RESGATE ? "Resgate: Trilha de capacitação" : tipo.getDescricao());
        e.setTemporadaId(Temporada.de(quando).id());
        e.setCriadoEm(quando);
        e.setVisto(visto);
        eventoRepository.save(e);
    }

    private static Instant dentroDaTemporada(Instant quando, Temporada temporada) {
        Instant inicio = temporada.inicio().atStartOfDay(Temporada.ZONA).toInstant().plus(Duration.ofHours(9));
        return quando.isBefore(inicio) ? inicio : quando;
    }
}
