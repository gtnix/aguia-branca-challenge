package com.gtnix.inovagab.config;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    static final String CATEGORIA_DEMO = "EFICIENCIA_OPERACIONAL";
    static final Set<String> CATEGORIAS_LEGADAS = Set.of("Operações", "OPERACOES", "Operacoes");

    private static final Pattern CONTEUDO_TESTE = Pattern.compile(
            "(?i).*(crud|smoke|fatia|alheia|ownership|auditoria|audit).*"
    );

    static final String ID_ORI_EFICIENCIA = "ori-eficiencia";
    static final String ID_ORI_PASSAGEIRO = "ori-passageiro";
    static final String ID_ORI_SEGURANCA = "ori-seguranca";
    static final String ID_ORI_SUSTENTABILIDADE = "ori-sustentabilidade";

    static final String ID_IDEIA_PREDITIVA = "ideia-preditiva";
    static final String ID_IDEIA_OFICINA = "ideia-oficina";
    static final String ID_IDEIA_INSPECAO_PAPEL = "ideia-checklist-paralelo";
    static final String ID_IDEIA_PONTO_CEGO = "ideia-ponto-cego";
    static final String ID_IDEIA_TELEMETRIA = "ideia-telemetria";
    static final String ID_IDEIA_CHECKIN = "ideia-checkin";
    static final String ID_IDEIA_ENCOMENDA = "ideia-encomenda";
    static final String ID_IDEIA_OFICINA_COMERCIO = "ideia-oficina-comercio";
    static final String ID_IDEIA_ROTAS = "ideia-rotas";

    static final String ID_PROJ_PREDITIVA = "proj-preditiva";
    static final String ID_PROJ_CHECKIN = "proj-checkin";
    static final String ID_PROJ_ROTAS = "proj-rotas";

    static final String ID_LIDER = "user-lider";
    static final String ID_GESTOR = "user-gestor";
    static final String ID_OPERADOR = "user-operador";
    static final String ID_OP_JOANA = "user-op-joana";
    static final String ID_OP_RAFAEL = "user-op-rafael";
    static final String ID_OP_BRUNA = "user-op-bruna";
    static final String ID_OP_CARLOS = "user-op-carlos";
    static final String ID_OP_LUCIA = "user-op-lucia";
    static final String ID_OP_THIAGO = "user-op-thiago";
    static final String ID_GESTOR_PAULO = "user-gestor-paulo";

    private final UsuarioRepository usuarioRepository;
    private final OrientacaoRepository orientacaoRepository;
    private final IdeiaRepository ideiaRepository;
    private final ProjetoRepository projetoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UsuarioRepository usuarioRepository,
            OrientacaoRepository orientacaoRepository,
            IdeiaRepository ideiaRepository,
            ProjetoRepository projetoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.orientacaoRepository = orientacaoRepository;
        this.ideiaRepository = ideiaRepository;
        this.projetoRepository = projetoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            seedDemo();
        }
        normalizarCategoriasLegadas();
        removerConteudoTeste();
        upsertCatalogoSetorial();
    }

    private void seedDemo() {
        String hash = passwordEncoder.encode("123456");
        usuarioRepository.save(usuario(ID_LIDER, "Marcos Silva",
                "marcos.silva@aguiabranca.com.br", hash, PerfilUsuario.LIDER,
                "OPERACOES", "LOGISTICA", "Holding"));
        usuarioRepository.save(usuario(ID_GESTOR, "Ana Oliveira",
                "ana.oliveira@aguiabranca.com.br", hash, PerfilUsuario.GESTOR,
                "LOGISTICA", "LOGISTICA", "CD Serra"));
        usuarioRepository.save(usuario(ID_OPERADOR, "Pedro Santos",
                "pedro.santos@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "Transportadora Cariacica"));
        usuarioRepository.save(usuario(ID_OP_JOANA, "Joana Ribeiro",
                "joana.ribeiro@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "CD Serra"));
        usuarioRepository.save(usuario(ID_OP_RAFAEL, "Rafael Costa",
                "rafael.costa@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "CD Serra"));
        usuarioRepository.save(usuario(ID_OP_BRUNA, "Bruna Lima",
                "bruna.lima@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "Transportadora Cariacica"));
        usuarioRepository.save(usuario(ID_OP_CARLOS, "Carlos Mendes",
                "carlos.mendes@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "OPERACOES", "PASSAGEIROS", "Garagem Vitória"));
        usuarioRepository.save(usuario(ID_OP_LUCIA, "Lúcia Ferraz",
                "lucia.ferraz@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "OPERACOES", "PASSAGEIROS", "Garagem Vitória"));
        usuarioRepository.save(usuario(ID_OP_THIAGO, "Thiago Alves",
                "thiago.alves@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "COMERCIAL", "COMERCIO", "Concessionária Vila Velha"));
        usuarioRepository.save(usuario(ID_GESTOR_PAULO, "Paulo Nunes",
                "paulo.nunes@aguiabranca.com.br", hash, PerfilUsuario.GESTOR,
                "OPERACOES", "PASSAGEIROS", "Garagem Vitória"));
    }

    void normalizarCategoriasLegadas() {
        for (OrientacaoDocument doc : orientacaoRepository.findAll()) {
            if (doc.getCategoria() != null && CATEGORIAS_LEGADAS.contains(doc.getCategoria())) {
                doc.setCategoria(CATEGORIA_DEMO);
                orientacaoRepository.save(doc);
            }
        }
    }

    void removerConteudoTeste() {
        for (IdeiaDocument doc : ideiaRepository.findAll()) {
            if (doc.isAtivo() && isConteudoTeste(doc.getTitulo(), doc.getDescricao())) {
                doc.marcarExcluido();
                ideiaRepository.save(doc);
            }
        }
        for (ProjetoDocument doc : projetoRepository.findAll()) {
            if (doc.isAtivo() && isConteudoTeste(doc.getTitulo(), doc.getDescricao(), doc.getObjetivo())) {
                doc.marcarExcluido();
                projetoRepository.save(doc);
            }
        }
        for (OrientacaoDocument doc : orientacaoRepository.findAll()) {
            if (doc.isAtivo() && isConteudoTeste(doc.getTitulo(), doc.getDescricao())) {
                doc.marcarExcluido();
                orientacaoRepository.save(doc);
            }
        }
    }

    void upsertCatalogoSetorial() {
        Instant now = Instant.now();
        String campanha = "Águia 2026 — Inovação em Campo";

        salvarOrientacao(
                ID_ORI_EFICIENCIA,
                "Eficiência operacional nas 690 bases",
                "Priorizar iniciativas que reduzam tempo de pátio, retrabalho de oficina e custo por km em todas as bases do grupo.",
                CATEGORIA_DEMO,
                campanha,
                "LOGISTICA",
                now
        );
        salvarOrientacao(
                ID_ORI_PASSAGEIRO,
                "Experiência digital do passageiro",
                "Modernizar embarque, bilhetagem, encomendas rápidas e comunicação com o passageiro nos terminais e no aplicativo.",
                "EXPERIENCIA_CLIENTE",
                campanha,
                "PASSAGEIROS",
                now
        );
        salvarOrientacao(
                ID_ORI_SEGURANCA,
                "Segurança operacional e jornada segura",
                "Reduzir incidentes com treinamento, telemetria de condução e checagens digitais na frota, oficinas e pátios.",
                "SEGURANCA",
                campanha,
                null,
                now
        );
        salvarOrientacao(
                ID_ORI_SUSTENTABILIDADE,
                "Redução de emissões da frota",
                "Monitorar consumo de combustível, quilometragem ociosa e transição energética da frota até 2027.",
                "SUSTENTABILIDADE",
                campanha,
                "LOGISTICA",
                now
        );

        Instant d10 = now.minus(Duration.ofDays(10));
        Instant d13 = now.minus(Duration.ofDays(13));
        Instant d8 = now.minus(Duration.ofDays(8));
        Instant d7 = now.minus(Duration.ofDays(7));
        Instant d9 = now.minus(Duration.ofDays(9));
        Instant d20 = now.minus(Duration.ofDays(20));
        Instant d12 = now.minus(Duration.ofDays(12));
        Instant d11 = now.minus(Duration.ofDays(11));
        Instant d15 = now.minus(Duration.ofDays(15));

        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_PREDITIVA, ID_OPERADOR, ID_ORI_EFICIENCIA,
                "Manutenção preditiva da frota",
                "Sensores IoT antecipam falhas e reduzem tempo parado na oficina da Transportadora Cariacica.",
                "LOGISTICA", StatusIdeia.CONVERTIDA_PROJETO, 5, 3, 1, d10, now.minus(Duration.ofDays(3)),
                ID_GESTOR, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_OFICINA, ID_OPERADOR, ID_ORI_EFICIENCIA,
                "Oficina sem papel",
                "Ordens de serviço digitais na manutenção da frota, com histórico por veículo e foto da peça.",
                "LOGISTICA", StatusIdeia.PENDENTE, 3, 4, null, d13, null, null, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_INSPECAO_PAPEL, ID_OPERADOR, ID_ORI_SEGURANCA,
                "Inspeção pré-viagem em papel na tesouraria",
                "Motorista ainda assina folha impressa na tesouraria antes de sair. A folha some, o horário atrasa e o item crítico não trava a saída.",
                "LOGISTICA", StatusIdeia.REPROVADA, 4, 2, null, d8, now.minus(Duration.ofDays(6)),
                ID_GESTOR,
                "Papel não escala nas 690 bases e some no chão da garagem. Traga de novo com fluxo no celular, item crítico bloqueando a saída.",
                0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_PONTO_CEGO, ID_OP_JOANA, ID_ORI_SEGURANCA,
                "Sensor de ponto cego no pátio do CD Serra",
                "Carreta em manobra no CD Serra tem ponto cego na lateral esquerda. Sensor de aproximação no espelho e alarme no rádio da equipe de pátio.",
                "LOGISTICA", StatusIdeia.PENDENTE, 5, 2, 2, d7, null, null, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_TELEMETRIA, ID_OP_RAFAEL, ID_ORI_EFICIENCIA,
                "Rastreamento de frota em tempo real",
                "Usar telemetria para reduzir combustível e melhorar pontualidade das linhas da Logística.",
                "LOGISTICA", StatusIdeia.PENDENTE, 4, 3, 3, d9, null, null, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_CHECKIN, ID_OP_CARLOS, ID_ORI_PASSAGEIRO,
                "Check-in digital no embarque",
                "QR Code no aplicativo reduz filas e agiliza a conferência de passageiros nos terminais da Garagem Vitória.",
                "OPERACOES", StatusIdeia.CONVERTIDA_PROJETO, 5, 2, 1, d20, now.minus(Duration.ofDays(14)),
                ID_GESTOR, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_ENCOMENDA, ID_OP_LUCIA, ID_ORI_PASSAGEIRO,
                "Rastreio de encomenda no guichê",
                "Atendente de encomendas consulta o volume pelo código de barras e avisa o destinatário por SMS, sem caderno de protocolo.",
                "OPERACOES", StatusIdeia.APROVADA, 4, 2, 2, d12, now.minus(Duration.ofDays(8)),
                ID_GESTOR, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_OFICINA_COMERCIO, ID_OP_THIAGO, ID_ORI_EFICIENCIA,
                "Peça certa na primeira visita da oficina",
                "Técnico da concessionária Vila Velha aponta a peça pelo VIN no tablet e o estoque confirma antes de abrir o capô. Menos retrabalho e menos cliente esperando.",
                "COMERCIAL", StatusIdeia.PENDENTE, 4, 3, null, d11, null, null, null, 0));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_ROTAS, ID_OP_BRUNA, ID_ORI_SUSTENTABILIDADE,
                "Roteirização sustentável da última milha",
                "Algoritmo de rotas reduz km ocioso e emissões na operação logística da Transportadora Cariacica.",
                "LOGISTICA", StatusIdeia.APROVADA, 4, 3, 3, d15, now.minus(Duration.ofDays(9)),
                ID_GESTOR, null, 0));

        salvarProjeto(
                ID_PROJ_PREDITIVA,
                ID_ORI_EFICIENCIA,
                ID_IDEIA_PREDITIVA,
                "Manutenção preditiva da frota",
                "Reduzir tempo parado da frota com sensores IoT na Transportadora Cariacica.",
                "Sensores nas carretas da Logística antecipam falha de cubo e suspensão.",
                "LOGISTICA",
                "EM_ANDAMENTO",
                "EM_ANDAMENTO",
                BigDecimal.valueOf(80_000),
                BigDecimal.valueOf(9_000),
                35,
                LocalDate.now().plusMonths(6),
                null,
                now.minus(Duration.ofDays(3))
        );
        salvarProjeto(
                ID_PROJ_CHECKIN,
                ID_ORI_PASSAGEIRO,
                ID_IDEIA_CHECKIN,
                "Check-in digital no embarque",
                "Eliminar a fila de conferência de passagem nos terminais da Divisão Passageiros.",
                "QR Code no aplicativo da Viação, validado no embarque da Garagem Vitória. Candidato natural ao Demoday.",
                "OPERACOES",
                "CONCLUIDO",
                "CONCLUIDO",
                BigDecimal.valueOf(45_000),
                BigDecimal.valueOf(22_000),
                100,
                LocalDate.now().plusDays(10),
                "Fila de embarque caiu 18 minutos no pico. Retorno mensal de R$ 22 mil em produtividade de guichê e bordo.",
                now.minus(Duration.ofDays(5))
        );
        salvarProjeto(
                ID_PROJ_ROTAS,
                ID_ORI_SUSTENTABILIDADE,
                null,
                "Roteirização da última milha",
                "Reduzir km ocioso e emissões na distribuição da Logística.",
                "Iniciativa da gestora a partir da orientação de sustentabilidade, ainda em planejamento.",
                "LOGISTICA",
                "PLANEJADO",
                "PLANEJADO",
                BigDecimal.valueOf(60_000),
                BigDecimal.valueOf(12_000),
                0,
                LocalDate.now().plusMonths(4),
                null,
                now
        );
    }

    private OrientacaoDocument salvarOrientacao(
            String id,
            String titulo,
            String descricao,
            String categoria,
            String campanha,
            String divisao,
            Instant now
    ) {
        OrientacaoDocument doc = orientacaoRepository.findById(id).orElseGet(OrientacaoDocument::new);
        doc.setId(id);
        doc.setTitulo(titulo);
        doc.setDescricao(descricao);
        doc.setCategoria(categoria);
        doc.setCampanha(campanha);
        doc.setDivisao(divisao);
        doc.setAtivo(true);
        doc.setDeletedAt(null);
        if (doc.getDataCriacao() == null) {
            doc.setDataCriacao(now);
        }
        doc.setDataAtualizacao(now);
        if (doc.getCriadoPorUserId() == null) {
            doc.setCriadoPorUserId(ID_LIDER);
        }
        return orientacaoRepository.save(doc);
    }

    private IdeiaDocument salvarIdeia(IdeiaSeed seed) {
        IdeiaDocument doc = ideiaRepository.findById(seed.id()).orElseGet(IdeiaDocument::new);
        doc.setId(seed.id());
        doc.setAutorUserId(seed.autorUserId());
        doc.setTitulo(seed.titulo());
        doc.setDescricao(seed.descricao());
        doc.setTipo("IDEIA");
        doc.setArea(seed.area());
        doc.setOrientacaoId(seed.orientacaoId());
        doc.setStatus(seed.status());
        doc.setImpacto(seed.impacto());
        doc.setEsforco(seed.esforco());
        doc.setPrioridade(seed.prioridade());
        doc.setAvaliadorUserId(seed.avaliadorUserId());
        doc.setFeedbackReprovacao(seed.feedbackReprovacao());
        doc.setApoios(seed.apoios());
        doc.setAtivo(true);
        doc.setDeletedAt(null);
        doc.setDataCriacao(seed.dataCriacao());
        doc.setDataAvaliacao(seed.dataAvaliacao());
        doc.setDataAtualizacao(seed.dataAvaliacao() != null ? seed.dataAvaliacao() : seed.dataCriacao());
        return ideiaRepository.save(doc);
    }

    private void salvarProjeto(
            String id,
            String orientacaoId,
            String ideiaId,
            String titulo,
            String objetivo,
            String descricao,
            String area,
            String status,
            String etapa,
            BigDecimal investimento,
            BigDecimal retorno,
            int progresso,
            LocalDate prazo,
            String resultadosObtidos,
            Instant now
    ) {
        ProjetoDocument doc = projetoRepository.findById(id).orElseGet(ProjetoDocument::new);
        doc.setId(id);
        doc.setTitulo(titulo);
        doc.setObjetivo(objetivo);
        doc.setDescricao(descricao);
        doc.setArea(area);
        doc.setOrientacaoId(orientacaoId);
        doc.setIdeiaId(ideiaId);
        doc.setEtapa(etapa);
        doc.setStatus(status);
        doc.setInvestimento(investimento);
        doc.setRetorno(retorno);
        doc.setPrazo(prazo);
        doc.setProgressoPercentual(progresso);
        doc.setResultadosObtidos(resultadosObtidos);
        doc.setResponsavelUserId(ID_GESTOR);
        doc.setAtivo(true);
        doc.setDeletedAt(null);
        if (doc.getDataCriacao() == null) {
            doc.setDataCriacao(now);
        }
        doc.setDataAtualizacao(now);
        projetoRepository.save(doc);
    }

    static boolean isConteudoTeste(String... textos) {
        for (String texto : textos) {
            if (texto != null && CONTEUDO_TESTE.matcher(texto).matches()) {
                return true;
            }
        }
        return false;
    }

    private UsuarioDocument usuario(
            String id,
            String nome,
            String email,
            String hash,
            PerfilUsuario perfil,
            String area,
            String divisao,
            String equipe
    ) {
        UsuarioDocument u = new UsuarioDocument();
        u.setId(id);
        u.setNome(nome);
        u.setEmail(email);
        u.setSenhaHash(hash);
        u.setPerfil(perfil);
        u.setArea(area);
        u.setDivisao(divisao);
        u.setEquipe(equipe);
        return u;
    }

    private record IdeiaSeed(
            String id,
            String autorUserId,
            String orientacaoId,
            String titulo,
            String descricao,
            String area,
            StatusIdeia status,
            int impacto,
            int esforco,
            Integer prioridade,
            Instant dataCriacao,
            Instant dataAvaliacao,
            String avaliadorUserId,
            String feedbackReprovacao,
            int apoios
    ) {
    }
}
