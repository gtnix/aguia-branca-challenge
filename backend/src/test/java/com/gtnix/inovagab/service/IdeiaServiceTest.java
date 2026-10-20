package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.CriterioIa;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.OrigemEstimativa;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusAnaliseIa;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.ai.IdeaScoreItem;
import com.gtnix.inovagab.dto.ideia.AvaliacaoRequest;
import com.gtnix.inovagab.dto.ideia.EstimativaRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaRequest;
import com.gtnix.inovagab.dto.ideia.PrioridadeRequest;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.repository.ApoioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdeiaServiceTest {

    @Mock
    private IdeiaRepository ideiaRepository;
    @Mock
    private OrientacaoService orientacaoService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private GamificacaoService gamificacaoService;
    @Mock
    private ApoioRepository apoioRepository;
    @Mock
    private EmbeddingService embeddingService;

    private IdeiaService service;

    @BeforeEach
    void setUp() {
        service = new IdeiaService(ideiaRepository, orientacaoService, usuarioRepository, gamificacaoService, apoioRepository, embeddingService);
        lenient().when(embeddingService.gerar(any(), any())).thenReturn(Optional.empty());
        lenient().when(usuarioRepository.findById(any())).thenAnswer(invocation ->
                Optional.of(usuarioComNome(invocation.getArgument(0))));
        lenient().when(usuarioRepository.findAllById(any())).thenAnswer(invocation -> {
            Iterable<String> ids = invocation.getArgument(0);
            List<UsuarioDocument> users = new ArrayList<>();
            ids.forEach(id -> users.add(usuarioComNome(id)));
            return users;
        });
    }

    @Test
    void criarExigeOrientacaoAtivaEPersisteAutor() {
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(ideiaRepository.save(any())).thenAnswer(invocation -> {
            IdeiaDocument doc = invocation.getArgument(0);
            doc.setId("ideia-1");
            return doc;
        });

        var response = service.criar(request("ori-1"), principal(PerfilUsuario.OPERADOR));

        assertEquals("ideia-1", response.id());
        assertEquals("user-operador", response.autorUserId());
        assertEquals("Pedro Santos", response.autorNome());
        assertEquals("ori-1", response.orientacaoId());
        assertEquals(StatusIdeia.PENDENTE, response.status());
    }

    @Test
    void criarComOrientacaoInativaPropaga400() {
        when(orientacaoService.requireAtiva("ori-inativa"))
                .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "Orientação estratégica inválida ou inativa"));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.criar(request("ori-inativa"), principal(PerfilUsuario.OPERADOR)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void operadorListaApenasAsProprias() {
        when(ideiaRepository.findByAutorUserIdAndAtivoTrue("user-operador"))
                .thenReturn(List.of(ideia("ideia-1", "user-operador")));

        var lista = service.listar(principal(PerfilUsuario.OPERADOR), null, null, null);

        assertEquals(1, lista.size());
        assertEquals("ideia-1", lista.get(0).id());
        assertEquals("Pedro Santos", lista.get(0).autorNome());
    }

    @Test
    void gestorListaTodas() {
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of(
                ideia("ideia-1", "user-operador"),
                ideia("ideia-2", "user-outro")
        ));

        var lista = service.listar(principal(PerfilUsuario.GESTOR), null, null, null);

        assertEquals(2, lista.size());
    }

    @Test
    void operadorNaoEditaIdeiaDeTerceiro() {
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-2"))
                .thenReturn(Optional.of(ideia("ideia-2", "user-outro")));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.atualizar("ideia-2", request("ori-1"), principal(PerfilUsuario.OPERADOR)));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void excluirFazSoftDeleteDaPropria() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.excluir("ideia-1", principal(PerfilUsuario.OPERADOR));

        ArgumentCaptor<IdeiaDocument> captor = ArgumentCaptor.forClass(IdeiaDocument.class);
        verify(ideiaRepository).save(captor.capture());
        assertFalse(captor.getValue().isAtivo());
    }

    @Test
    void excluirIdeiaAprovadaRetorna400() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        doc.setStatus(StatusIdeia.APROVADA);
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.excluir("ideia-1", principal(PerfilUsuario.OPERADOR)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void obterInexistenteRetorna404() {
        when(ideiaRepository.findByIdAndAtivoTrue("sumiu")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.obter("sumiu", principal(PerfilUsuario.GESTOR)));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void operadorNaoObtemIdeiaDeTerceiro() {
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-2"))
                .thenReturn(Optional.of(ideia("ideia-2", "user-outro")));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.obter("ideia-2", principal(PerfilUsuario.OPERADOR)));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void gestorAprovaIdeiaPendente() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.avaliar("ideia-1", new AvaliacaoRequest(StatusIdeia.APROVADA, null), principal(PerfilUsuario.GESTOR));

        assertEquals(StatusIdeia.APROVADA, response.status());
        assertNull(response.feedbackReprovacao());
        assertNotNull(doc.getDataAvaliacao());
    }

    @Test
    void reprovarSemFeedbackRetorna400() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.avaliar("ideia-1", new AvaliacaoRequest(StatusIdeia.REPROVADA, "  "), principal(PerfilUsuario.GESTOR)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void naoReavaliaIdeiaJaAprovada() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        doc.setStatus(StatusIdeia.APROVADA);
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.avaliar("ideia-1", new AvaliacaoRequest(StatusIdeia.REPROVADA, "fora"), principal(PerfilUsuario.GESTOR)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void gestorPriorizaIdeiaPendente() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.priorizar("ideia-1", new PrioridadeRequest(80));

        assertEquals(80, response.prioridade());
    }

    @Test
    void aplicarAnaliseGravaRecomendacaoSemAprovar() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IdeaScoreItem item = service.aplicarAnalise(doc, analise(), "gemini-3.6-flash");

        assertEquals("ideia-1", item.ideiaId());
        assertEquals(StatusAnaliseIa.IA, item.status());
        assertNotNull(doc.getScoreIa());
        assertEquals("Resumo", doc.getJustificativaIa());
        assertFalse(doc.getScoreIaFallback());
        assertEquals("gemini-3.6-flash", doc.getScoreIaModelo());
        assertEquals(StatusIdeia.PENDENTE, doc.getStatus());
    }

    @Test
    void operadorNaoRecebeCamposDeIa() {
        IdeiaDocument doc = ideiaAnalisada("ideia-1", "user-operador");
        when(ideiaRepository.findByAutorUserIdAndAtivoTrue("user-operador")).thenReturn(List.of(doc));
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));

        var lista = service.listar(principal(PerfilUsuario.OPERADOR), null, null, null);
        var detalhe = service.obter("ideia-1", principal(PerfilUsuario.OPERADOR));

        for (var response : List.of(lista.get(0), detalhe)) {
            assertNull(response.scoreIa());
            assertNull(response.justificativaIa());
            assertNull(response.scoreIaFallback());
            assertNull(response.analiseIa());
        }
    }

    @Test
    void gestorRecebeAnaliseDetalhada() {
        IdeiaDocument doc = ideiaAnalisada("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));

        var response = service.obter("ideia-1", principal(PerfilUsuario.GESTOR));

        assertEquals(doc.getScoreIa(), response.scoreIa());
        assertNotNull(response.analiseIa());
        assertEquals(StatusAnaliseIa.IA, response.analiseIa().status());
        assertEquals(5, response.analiseIa().criterios().size());
        assertEquals(30, response.analiseIa().criterios().get(0).peso());
        assertEquals(List.of("Risco"), response.analiseIa().riscos());
        assertEquals(4, response.analiseIa().impactoSugerido());
    }

    @Test
    void gestorVeFilaPorOrdemDeChegadaENaoPorScore() {
        IdeiaDocument antiga = ideia("antiga", "user-operador");
        antiga.setDataCriacao(java.time.Instant.parse("2026-01-01T00:00:00Z"));
        antiga.setScoreIa(10);
        IdeiaDocument nova = ideia("nova", "user-operador");
        nova.setDataCriacao(java.time.Instant.parse("2026-03-01T00:00:00Z"));
        nova.setScoreIa(95);
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of(nova, antiga));

        var lista = service.listar(principal(PerfilUsuario.GESTOR), null, null, null);

        assertEquals(List.of("antiga", "nova"), lista.stream().map(r -> r.id()).toList());
    }

    @Test
    void editarConteudoLimpaAnaliseDaIa() {
        IdeiaDocument doc = ideiaAnalisada("ideia-1", "user-operador");
        doc.setDescricao("Descrição");
        doc.setArea("OPERACOES");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.atualizar("ideia-1",
                new IdeiaRequest("Título novo", "Descrição", "IDEIA", "OPERACOES", "ori-1", null, null, null, null),
                principal(PerfilUsuario.OPERADOR));

        assertNull(doc.getScoreIa());
        assertNull(doc.getScoreIaStatus());
        assertNull(doc.getCriteriosIa());
        assertNull(doc.getImpactoSugeridoIa());
        assertNull(doc.getJustificativaIa());
    }

    @Test
    void editarSemMudarConteudoPreservaAnalise() {
        IdeiaDocument doc = ideiaAnalisada("ideia-1", "user-operador");
        doc.setDescricao("Descrição");
        doc.setArea("OPERACOES");
        Integer score = doc.getScoreIa();
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.atualizar("ideia-1",
                new IdeiaRequest("Título", "Descrição", "IDEIA", "OPERACOES", "ori-1", null, null, null, null),
                principal(PerfilUsuario.OPERADOR));

        assertEquals(score, doc.getScoreIa());
    }

    @Test
    void cadastroGuardaContextoENaoDefineImpactoEsforco() {
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        ArgumentCaptor<IdeiaDocument> captor = ArgumentCaptor.forClass(IdeiaDocument.class);
        when(ideiaRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new IdeiaRequest("Título", "Descrição", "IDEIA", "OPERACOES", "ori-1",
                "Pneus furam", "Motoristas", "SEMANAL", "Menos atrasos"), principal(PerfilUsuario.OPERADOR));

        IdeiaDocument salvo = captor.getValue();
        assertEquals("Pneus furam", salvo.getProblema());
        assertEquals("Motoristas", salvo.getPublicoAfetado());
        assertEquals("SEMANAL", salvo.getFrequencia());
        assertEquals("Menos atrasos", salvo.getBeneficio());
        assertNull(salvo.getImpacto());
        assertNull(salvo.getEsforco());
        assertEquals("Pneus furam", response.problema());
    }

    @Test
    void cadastroMarcaDuplicadaPorSimilaridadeSemantica() {
        IdeiaDocument existente = ideia("ideia-0", "user-outro");
        existente.setTitulo("Sensor de pressão dos pneus");
        existente.setEmbedding(List.of(1.0, 0.0, 0.0));
        when(ideiaRepository.findByAtivoTrue()).thenReturn(List.of(existente));
        when(embeddingService.gerar(any(), any())).thenReturn(Optional.of(List.of(0.99, 0.05, 0.0)));
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new IdeiaRequest("Monitorar calibragem automaticamente", "Descrição", "IDEIA",
                "OPERACOES", "ori-1", null, null, null, null), principal(PerfilUsuario.OPERADOR));

        assertEquals("ideia-0", response.duplicadaDe());
    }

    @Test
    void estimativaDoGestorRecalculaScoreEPrevaleceSobreIa() {
        IdeiaDocument doc = ideiaAnalisada("ideia-1", "user-operador");
        Integer scoreIa = doc.getScoreIa();
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.salvarEstimativa("ideia-1",
                new EstimativaRequest(1, 5, "Exige integração com o ERP"), principal(PerfilUsuario.GESTOR));

        assertEquals(1, doc.getImpacto());
        assertEquals(5, doc.getEsforco());
        assertEquals(OrigemEstimativa.GESTOR, doc.getEstimativaOrigem());
        assertEquals("Exige integração com o ERP", doc.getMotivoAjusteEstimativa());
        assertEquals("user-gestor", doc.getEstimativaPorUserId());
        assertTrue(response.scoreIa() < scoreIa);
        assertEquals(4, response.analiseIa().impactoSugerido());
        assertEquals(OrigemEstimativa.GESTOR, response.analiseIa().estimativaOrigem());
    }

    @Test
    void estimativaSemAnaliseDaIaGeraEstimativaAutomatica() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.salvarEstimativa("ideia-1", new EstimativaRequest(5, 1, null), principal(PerfilUsuario.GESTOR));

        assertEquals(75, response.scoreIa());
        assertEquals(StatusAnaliseIa.ESTIMATIVA, response.analiseIa().status());
    }

    @Test
    void estimativaEmIdeiaJaAvaliadaRetorna400() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        doc.setStatus(StatusIdeia.APROVADA);
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class, () -> service.salvarEstimativa(
                "ideia-1", new EstimativaRequest(3, 3, null), principal(PerfilUsuario.GESTOR)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void avaliarRegistraDiscordanciaDaIa() {
        IdeiaDocument doc = ideiaAnalisada("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.avaliar("ideia-1",
                new AvaliacaoRequest(StatusIdeia.APROVADA, null, false, "IA subestimou o ganho de segurança"),
                principal(PerfilUsuario.GESTOR));

        assertFalse(doc.getConcordaIa());
        assertEquals("IA subestimou o ganho de segurança", doc.getMotivoDiscordanciaIa());
    }

    @Test
    void concordanciaIgnoradaSemRecomendacao() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.avaliar("ideia-1", new AvaliacaoRequest(StatusIdeia.APROVADA, null, true, null), principal(PerfilUsuario.GESTOR));

        assertNull(doc.getConcordaIa());
    }

    private static AnaliseIa analise() {
        return new AnaliseIa(
                List.of(
                        new CriterioIa(RubricaIdeia.ALINHAMENTO, 5, "a"),
                        new CriterioIa(RubricaIdeia.IMPACTO, 4, "b"),
                        new CriterioIa(RubricaIdeia.VIABILIDADE, 4, "c"),
                        new CriterioIa(RubricaIdeia.CLAREZA, 4, "d"),
                        new CriterioIa(RubricaIdeia.ORIGINALIDADE, 3, "e")),
                4, 2, "Resumo", List.of("Risco"), List.of("Pergunta?"));
    }

    private IdeiaDocument ideiaAnalisada(String id, String autor) {
        IdeiaDocument doc = ideia(id, autor);
        lenient().when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service.aplicarAnalise(doc, analise(), "gemini-3.6-flash");
        org.mockito.Mockito.clearInvocations(ideiaRepository);
        return doc;
    }

    private static IdeiaRequest request(String orientacaoId) {
        return new IdeiaRequest("Título", "Descrição", "IDEIA", "OPERACOES", orientacaoId, null, null, null, null);
    }

    @Test
    void gestorApoiaIdeiaUmaUnicaVez() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.apoiar("ideia-1", principal(PerfilUsuario.GESTOR));

        assertEquals(1, response.apoios());
        verify(gamificacaoService).onApoio(doc, "user-gestor");

        when(apoioRepository.existsByIdeiaIdAndUserId("ideia-1", "user-gestor")).thenReturn(true);
        ApiException ex = assertThrows(ApiException.class,
                () -> service.apoiar("ideia-1", principal(PerfilUsuario.GESTOR)));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void ninguemApoiaAPropriaIdeia() {
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(ideia("ideia-1", "user-gestor")));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.apoiar("ideia-1", principal(PerfilUsuario.GESTOR)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void autorMarcaFeedbackUtilUmaVezEPremiaAvaliador() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        doc.setStatus(StatusIdeia.REPROVADA);
        doc.setDataAvaliacao(java.time.Instant.parse("2026-01-02T00:00:00Z"));
        doc.setAvaliadorUserId("user-gestor");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.marcarFeedbackUtil("ideia-1", true, principal(PerfilUsuario.OPERADOR));

        assertTrue(response.feedbackUtil());
        verify(gamificacaoService).onFeedbackUtil(doc);
        ApiException ex = assertThrows(ApiException.class,
                () -> service.marcarFeedbackUtil("ideia-1", false, principal(PerfilUsuario.OPERADOR)));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void feedbackUtilAntesDaAvaliacaoRetorna400() {
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(ideia("ideia-1", "user-operador")));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.marcarFeedbackUtil("ideia-1", true, principal(PerfilUsuario.OPERADOR)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void avaliarRegistraAvaliadorEChamaGamificacao() {
        IdeiaDocument doc = ideia("ideia-1", "user-operador");
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(doc));
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.avaliar("ideia-1", new AvaliacaoRequest(StatusIdeia.APROVADA, null), principal(PerfilUsuario.GESTOR));

        assertEquals("user-gestor", doc.getAvaliadorUserId());
        verify(gamificacaoService).onIdeiaAvaliada(doc, "user-gestor");
    }

    private static IdeiaDocument ideia(String id, String autor) {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setId(id);
        doc.setAutorUserId(autor);
        doc.setTitulo("Título");
        doc.setOrientacaoId("ori-1");
        doc.setStatus(StatusIdeia.PENDENTE);
        doc.setDataCriacao(java.time.Instant.parse("2026-01-01T00:00:00Z"));
        return doc;
    }

    private static UsuarioDocument usuarioComNome(String id) {
        UsuarioDocument usuario = new UsuarioDocument();
        usuario.setId(id);
        usuario.setNome("user-operador".equals(id) ? "Pedro Santos" : "Usuário " + id);
        return usuario;
    }

    private static UsuarioPrincipal principal(PerfilUsuario perfil) {
        UsuarioDocument usuario = new UsuarioDocument();
        usuario.setId("user-" + perfil.name().toLowerCase());
        usuario.setEmail(perfil.name().toLowerCase() + "@aguiabranca.com.br");
        usuario.setSenhaHash("hash");
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        return new UsuarioPrincipal(usuario);
    }
}
