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
    static final String ID_IDEIA_RECARGA = "ideia-recarga";
    static final String ID_IDEIA_TEST_DRIVE = "ideia-test-drive";

    static final String ID_PROJ_PREDITIVA = "proj-preditiva";
    static final String ID_PROJ_CHECKIN = "proj-checkin";
    static final String ID_PROJ_ROTAS = "proj-rotas";
    static final String ID_PROJ_MAIS_CARGA = "proj-mais-carga";

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
                "LOGISTICA", "LOGISTICA", "CD Serra"));
        usuarioRepository.save(usuario(ID_OP_JOANA, "Joana Ribeiro",
                "joana.ribeiro@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "CD Serra"));
        usuarioRepository.save(usuario(ID_OP_RAFAEL, "Rafael Costa",
                "rafael.costa@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "VIX · Jardim Limoeiro"));
        usuarioRepository.save(usuario(ID_OP_BRUNA, "Bruna Lima",
                "bruna.lima@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "LOGISTICA", "LOGISTICA", "SERVICARGA · Grande Vitória"));
        usuarioRepository.save(usuario(ID_OP_CARLOS, "Carlos Mendes",
                "carlos.mendes@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "OPERACOES", "PASSAGEIROS", "Terminal de Vitória"));
        usuarioRepository.save(usuario(ID_OP_LUCIA, "Lúcia Ferraz",
                "lucia.ferraz@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "OPERACOES", "PASSAGEIROS", "Terminal de Vitória"));
        usuarioRepository.save(usuario(ID_OP_THIAGO, "Thiago Alves",
                "thiago.alves@aguiabranca.com.br", hash, PerfilUsuario.OPERADOR,
                "COMERCIAL", "COMERCIO", "Vitória Motors"));
        usuarioRepository.save(usuario(ID_GESTOR_PAULO, "Paulo Nunes",
                "paulo.nunes@aguiabranca.com.br", hash, PerfilUsuario.GESTOR,
                "OPERACOES", "PASSAGEIROS", "Terminal de Vitória"));
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

        salvarOrientacao(
                ID_ORI_EFICIENCIA,
                "Disponibilidade da frota e eficiência do pátio",
                "Reduzir tempo parado, espera em doca e custo por km nas operações da Logística, com manutenção, telemetria e pátio conectado.",
                CATEGORIA_DEMO,
                "Circuito Inovação 2026",
                "LOGISTICA",
                now
        );
        salvarOrientacao(
                ID_ORI_PASSAGEIRO,
                "Experiência do cliente: do guichê à entrega",
                "Melhorar embarque, encomendas, atendimento e pós-venda nas operações de Passageiros e Comércio.",
                "EXPERIENCIA_CLIENTE",
                "Circuito Inovação 2026",
                "PASSAGEIROS",
                now
        );
        salvarOrientacao(
                ID_ORI_SEGURANCA,
                "Segurança inegociável: zero incidentes na operação",
                "Reduzir incidentes com comportamento seguro, telemetria, inspeções e cultura de segurança em todas as bases.",
                "SEGURANCA",
                "Jornada Segura 2026",
                null,
                now
        );
        salvarOrientacao(
                ID_ORI_SUSTENTABILIDADE,
                "Transição energética e logística verde",
                "Reduzir emissões, km ocioso e consumo com eletrificação, roteirização e energia limpa.",
                "SUSTENTABILIDADE",
                "Circuito Inovação 2026",
                "LOGISTICA",
                now
        );

        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_OFICINA, ID_OPERADOR, ID_ORI_EFICIENCIA,
                "Docas inteligentes no CD Serra",
                "Carreta espera até 3h por uma doca porque o pátio não tem visão das janelas. Com agendamento por janela e alerta no celular do motorista, o CD Serra reduz a espera, libera o pátio e aumenta as viagens por dia.",
                "LOGISTICA", StatusIdeia.PENDENTE, 4, 3, null,
                now.minus(Duration.ofDays(13)), null, null, null, 12));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_PONTO_CEGO, ID_OP_JOANA, ID_ORI_SEGURANCA,
                "Ponto cego em manobras de carreta",
                "Na manobra da carreta no pátio do CD Serra, o ponto cego na lateral esquerda já provocou quase-acidente. Sensor de aproximação no espelho e alerta sonoro no rádio da equipe de pátio.",
                "LOGISTICA", StatusIdeia.PENDENTE, 5, 3, null,
                now.minus(Duration.ofDays(7)), null, null, null, 9));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_TELEMETRIA, ID_OP_RAFAEL, ID_ORI_EFICIENCIA,
                "Estoque inteligente de peças críticas",
                "Quando falta uma peça crítica, a carreta fica parada na oficina de Jardim Limoeiro esperando reposição. Previsão de consumo por telemetria e estoque mínimo dinâmico por base.",
                "LOGISTICA", StatusIdeia.PENDENTE, 4, 4, null,
                now.minus(Duration.ofDays(9)), null, null, null, 7));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_OFICINA_COMERCIO, ID_OP_THIAGO, ID_ORI_PASSAGEIRO,
                "Diagnóstico remoto na oficina",
                "Na Vitória Diesel, o veículo chega sem diagnóstico e o box fica ocupado esperando peça. Diagnóstico remoto e pré-ordem de serviço antes da chegada do cliente.",
                "COMERCIAL", StatusIdeia.PENDENTE, 4, 3, null,
                now.minus(Duration.ofDays(11)), null, null, null, 6));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_CHECKIN, ID_OP_CARLOS, ID_ORI_PASSAGEIRO,
                "Embarque sem fila no Terminal de Vitória",
                "No pico, a fila de conferência de passagem faz o passageiro perder o horário. Check-in digital com QR Code e aviso de embarque no app da Viação Águia Branca.",
                "OPERACOES", StatusIdeia.CONVERTIDA_PROJETO, 5, 2, 1,
                now.minus(Duration.ofDays(20)), now.minus(Duration.ofDays(14)), ID_GESTOR, null, 15));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_ENCOMENDA, ID_OP_LUCIA, ID_ORI_PASSAGEIRO,
                "Rastreio de encomenda no guichê",
                "O atendente de encomendas consulta caderno e protocolo, e o cliente liga para saber onde está a carga. Rastreio por código e aviso por WhatsApp reduzem a ligação e liberam o guichê.",
                "OPERACOES", StatusIdeia.APROVADA, 4, 2, 2,
                now.minus(Duration.ofDays(12)), now.minus(Duration.ofDays(8)), ID_GESTOR, null, 11));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_PREDITIVA, ID_OPERADOR, ID_ORI_EFICIENCIA,
                "Preditiva de cubo e suspensão",
                "Falha de cubo ou suspensão só aparece na estrada, e a carreta perde a janela do cliente. Sensores, modelo preditivo e agenda de manutenção por base para as carretas de minério e celulose da VIX.",
                "LOGISTICA", StatusIdeia.CONVERTIDA_PROJETO, 5, 3, 1,
                now.minus(Duration.ofDays(10)), now.minus(Duration.ofDays(3)), ID_GESTOR, null, 10));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_ROTAS, ID_OP_BRUNA, ID_ORI_SUSTENTABILIDADE,
                "Roteirização verde da última milha",
                "Km ocioso e emissões na entrega urbana da SERVICARGA na Grande Vitória. Roteirização por janela de coleta, consolidação de carga e prioridade para veículos elétricos.",
                "LOGISTICA", StatusIdeia.CONVERTIDA_PROJETO, 4, 3, 3,
                now.minus(Duration.ofDays(15)), now.minus(Duration.ofDays(9)), ID_GESTOR, null, 8));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_RECARGA, ID_GESTOR_PAULO, ID_ORI_SUSTENTABILIDADE,
                "Recarga inteligente da frota elétrica",
                "A recarga no horário de pico encarece a operação e reduz a disponibilidade dos caminhões elétricos em teste na VIX. Janela por tarifa e prioridade por viagem.",
                "LOGISTICA", StatusIdeia.APROVADA, 4, 4, 3,
                now.minus(Duration.ofDays(14)), now.minus(Duration.ofDays(10)), ID_GESTOR, null, 5));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_TEST_DRIVE, ID_OP_THIAGO, ID_ORI_PASSAGEIRO,
                "Test-drive digital e trade-in",
                "O lead esfria entre o site e a concessionária, e a avaliação do usado demora. Agendamento digital do test-drive e avaliação preliminar do trade-in na Vitória Motors.",
                "COMERCIAL", StatusIdeia.APROVADA, 4, 2, 3,
                now.minus(Duration.ofDays(8)), now.minus(Duration.ofDays(5)), ID_GESTOR, null, 8));
        salvarIdeia(new IdeiaSeed(
                ID_IDEIA_INSPECAO_PAPEL, ID_OPERADOR, ID_ORI_EFICIENCIA,
                "Grupo de WhatsApp para avisos do pátio",
                "Criar mais um grupo de WhatsApp para avisar as equipes do pátio sobre janelas e prioridades.",
                "LOGISTICA", StatusIdeia.REPROVADA, 1, 1, null,
                now.minus(Duration.ofDays(8)), now.minus(Duration.ofDays(6)), ID_GESTOR,
                "O canal já existe; o problema não é falta de aviso, é falta de priorização e dono. Traga de volta com uma fila de prioridades por turno.",
                0));

        salvarProjeto(
                ID_PROJ_PREDITIVA,
                ID_ORI_EFICIENCIA,
                ID_IDEIA_PREDITIVA,
                "Preditiva de cubo e suspensão nas carretas da VIX",
                "Reduzir paradas não planejadas e aumentar a disponibilidade da frota de carretas.",
                "Sensores de vibração e temperatura em cubo e suspensão, com modelo preditivo e agenda de manutenção por base.",
                "LOGISTICA",
                "EM_ANDAMENTO",
                "EM_ANDAMENTO",
                BigDecimal.valueOf(1_800_000),
                BigDecimal.valueOf(420_000),
                35,
                LocalDate.now().plusMonths(6),
                null,
                now.minus(Duration.ofDays(3))
        );
        salvarProjeto(
                ID_PROJ_CHECKIN,
                ID_ORI_PASSAGEIRO,
                ID_IDEIA_CHECKIN,
                "Embarque sem fila no Terminal de Vitória",
                "Reduzir a fila de conferência e o tempo de embarque no pico.",
                "Check-in digital com QR Code no app da Viação Águia Branca e aviso de embarque no celular do passageiro.",
                "OPERACOES",
                "CONCLUIDO",
                "CONCLUIDO",
                BigDecimal.valueOf(650_000),
                BigDecimal.valueOf(180_000),
                100,
                LocalDate.now().plusDays(10),
                "Fila de embarque caiu 18 minutos no pico; NPS +12; guichê liberado para encomendas.",
                now.minus(Duration.ofDays(5))
        );
        salvarProjeto(
                ID_PROJ_MAIS_CARGA,
                ID_ORI_EFICIENCIA,
                null,
                "Mais Carga, Mais Valor: 5 veículos por carreta",
                "Aumentar a capacidade por viagem na operação de transporte de veículos em Juiz de Fora/MG.",
                "Estrutura metálica móvel que permite carregar cinco veículos por carreta, com ganho de frete por unidade e menos viagens.",
                "LOGISTICA",
                "CONCLUIDO",
                "CONCLUIDO",
                BigDecimal.valueOf(1_200_000),
                BigDecimal.valueOf(300_000),
                100,
                LocalDate.now().plusDays(20),
                "Cinco veículos por viagem; redução de 20% no custo logístico por unidade.",
                now.minus(Duration.ofDays(8))
        );
        salvarProjeto(
                ID_PROJ_ROTAS,
                ID_ORI_SUSTENTABILIDADE,
                ID_IDEIA_ROTAS,
                "Roteirização verde da última milha",
                "Reduzir km ocioso e emissões nas entregas urbanas da SERVICARGA na Grande Vitória.",
                "Roteirização por janela de coleta, consolidação de carga e priorização de veículos elétricos.",
                "LOGISTICA",
                "PLANEJADO",
                "PLANEJADO",
                BigDecimal.valueOf(900_000),
                BigDecimal.valueOf(210_000),
                0,
                LocalDate.now().minusDays(15),
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
