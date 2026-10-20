package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.PontoEventoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.PontoEventoRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamificacaoServiceTest {

    static final Instant AGORA = Instant.parse("2026-10-19T15:00:00Z");

    @Mock
    private PontoEventoRepository eventoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private OrientacaoRepository orientacaoRepository;
    @Mock
    private IdeiaRepository ideiaRepository;

    private LedgerEmMemoria ledger;
    private GamificacaoService service;

    @BeforeEach
    void setUp() {
        ledger = new LedgerEmMemoria(eventoRepository);
        lenient().when(usuarioRepository.findById(anyString())).thenAnswer(inv -> Optional.of(usuario(inv.getArgument(0))));
        service = new GamificacaoService(eventoRepository, usuarioRepository, orientacaoRepository, ideiaRepository,
                Clock.fixed(AGORA, Temporada.ZONA));
    }

    @Test
    void mesmaChaveCreditaUmaUnicaVez() {
        var l = GamificacaoService.Lancamento.padrao("op-1", TipoEvento.IDEIA_APROVADA, "ideia-1", 20, 10, null, null);

        assertTrue(service.registrar(l).isPresent());
        assertTrue(service.registrar(l).isEmpty());

        assertEquals(1, ledger.eventos.size());
        assertEquals(10, service.saldoAsas("op-1"));
        assertEquals("2026-T4", ledger.eventos.get(0).getTemporadaId());
    }

    @Test
    void lancamentoZeradoNaoGravaNada() {
        var l = GamificacaoService.Lancamento.padrao("op-1", TipoEvento.RETORNO_AUTOR, "proj-1", 0, 0, null, null);
        assertTrue(service.registrar(l).isEmpty());
        assertTrue(ledger.eventos.isEmpty());
    }

    @Test
    void estornoZeraSaldoEMantemHistoricoVisivel() {
        service.registrar(GamificacaoService.Lancamento.padrao("op-1", TipoEvento.IDEIA_APROVADA, "ideia-1", 20, 10, null, null));
        service.registrar(GamificacaoService.Lancamento.padrao("op-1", TipoEvento.IDEIA_CONVERTIDA, "ideia-1", 40, 30, null, null));

        int estornados = service.estornarPorRef("ideia-1", "ideia excluída");

        assertEquals(2, estornados);
        assertEquals(0, service.saldoAsas("op-1"));
        assertEquals(0, service.xpTotal("op-1"));
        assertEquals(2, ledger.contar("op-1", TipoEvento.ESTORNO));
        assertEquals(0, service.estornarPorRef("ideia-1", "de novo"), "estorno também é idempotente");
    }

    @Test
    void ideiaDuplicadaNaoPontua() {
        IdeiaDocument ideia = ideia("ideia-2", "op-1", "ori-1");
        ideia.setDuplicadaDe("ideia-1");

        service.onIdeiaCriada(ideia, orientacao("ori-1", "EFICIENCIA_OPERACIONAL", false));

        assertTrue(ledger.eventos.isEmpty());
    }

    @Test
    void soAsTresPrimeirasIdeiasDoDiaPontuam() {
        OrientacaoDocument ori = orientacao("ori-1", "EFICIENCIA_OPERACIONAL", false);
        for (int i = 1; i <= 5; i++) {
            service.onIdeiaCriada(ideia("ideia-" + i, "op-1", "ori-1"), ori);
        }

        assertEquals(GamificacaoService.LIMITE_IDEIAS_PONTUADAS_DIA, ledger.contar("op-1", TipoEvento.IDEIA_ENVIADA));
        assertEquals(GamificacaoService.LIMITE_IDEIAS_PONTUADAS_DIA, ledger.contar("lider-1", TipoEvento.IDEIA_ALINHADA),
                "o líder também só ganha pelas ideias que pontuaram");
    }

    @Test
    void liderNaoPontuaComIdeiaPropriaNaSuaOrientacao() {
        service.onIdeiaCriada(ideia("ideia-1", "lider-1", "ori-1"), orientacao("ori-1", "EFICIENCIA_OPERACIONAL", false));
        assertEquals(0, ledger.contar("lider-1", TipoEvento.IDEIA_ALINHADA));
    }

    @Test
    void aprovacaoEmCampanhaDeSegurancaAplicaOsDoisMultiplicadores() {
        OrientacaoDocument ori = orientacao("ori-seg", GamificacaoService.CATEGORIA_SEGURANCA, true);
        when(orientacaoRepository.findById("ori-seg")).thenReturn(Optional.of(ori));
        IdeiaDocument ideia = ideia("ideia-1", "op-1", "ori-seg");
        ideia.setStatus(StatusIdeia.APROVADA);
        ideia.setDataAvaliacao(AGORA);

        service.onIdeiaAvaliada(ideia, "gestor-1");

        PontoEventoDocument aprovada = unico("op-1", TipoEvento.IDEIA_APROVADA);
        assertEquals(45, aprovada.getXp());
        assertEquals(23, aprovada.getAsas());
        assertTrue(GamificacaoService.temTag(aprovada, GamificacaoService.TAG_CAMPANHA));
        assertTrue(GamificacaoService.temTag(aprovada, GamificacaoService.CATEGORIA_SEGURANCA));
        assertEquals(10, unico("gestor-1", TipoEvento.AVALIACAO_NO_PRAZO).getXp());
    }

    @Test
    void avaliacaoDepoisDoSlaValeMenos() {
        IdeiaDocument ideia = ideia("ideia-1", "op-1", null);
        ideia.setDataCriacao(AGORA.minus(Duration.ofDays(10)));
        ideia.setDataAvaliacao(AGORA);
        ideia.setStatus(StatusIdeia.APROVADA);

        service.onIdeiaAvaliada(ideia, "gestor-1");

        assertEquals(0, ledger.contar("gestor-1", TipoEvento.AVALIACAO_NO_PRAZO));
        assertEquals(TipoEvento.AVALIACAO_FORA_PRAZO.getXpBase(), unico("gestor-1", TipoEvento.AVALIACAO_FORA_PRAZO).getXp());
    }

    @Test
    void ideiaReprovadaNaoDaAsasEDaFeedbackSoUmaVezPorTemporada() {
        for (int i = 1; i <= 3; i++) {
            IdeiaDocument ideia = ideia("ideia-" + i, "op-1", null);
            ideia.setStatus(StatusIdeia.REPROVADA);
            ideia.setDataAvaliacao(AGORA);
            service.onIdeiaAvaliada(ideia, "gestor-1");
        }

        assertEquals(1, ledger.contar("op-1", TipoEvento.FEEDBACK_RECEBIDO));
        assertEquals(0, ledger.asas("op-1"));
        assertEquals(3, ledger.contar("gestor-1", TipoEvento.AVALIACAO_NO_PRAZO));
    }

    @Test
    void projetoConcluidoRemuneraGestorAutorELider() {
        OrientacaoDocument ori = orientacao("ori-1", "EFICIENCIA_OPERACIONAL", false);
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(ori));
        when(ideiaRepository.findById("ideia-1")).thenReturn(Optional.of(ideia("ideia-1", "op-1", "ori-1")));
        ProjetoDocument projeto = new ProjetoDocument();
        projeto.setId("proj-1");
        projeto.setTitulo("Preditiva");
        projeto.setOrientacaoId("ori-1");
        projeto.setIdeiaId("ideia-1");
        projeto.setResponsavelUserId("gestor-1");
        projeto.setPrazo(LocalDate.of(2026, 12, 31));
        projeto.setInvestimento(BigDecimal.valueOf(80_000));
        projeto.setRetorno(BigDecimal.valueOf(9_000));

        service.onProjetoConcluido(projeto);
        service.onProjetoConcluido(projeto);

        assertEquals(1, ledger.contar("gestor-1", TipoEvento.PROJETO_NO_PRAZO));
        assertEquals(1, ledger.contar("gestor-1", TipoEvento.ROI_POSITIVO));
        assertEquals(GamificacaoService.ASAS_ROI_MIN, unico("gestor-1", TipoEvento.ROI_POSITIVO).getAsas(),
                "ROI de 35% daria 3 Asas; vale o piso");
        assertEquals(100, unico("op-1", TipoEvento.PROJETO_CONCLUIDO_AUTOR).getAsas());
        assertEquals(9, unico("op-1", TipoEvento.RETORNO_AUTOR).getAsas());
        assertEquals(1, ledger.contar("lider-1", TipoEvento.PROJETO_CONCLUIDO_ESTRATEGIA));
    }

    @Test
    void projetoAtrasadoNaoDaBonusDePrazo() {
        ProjetoDocument projeto = new ProjetoDocument();
        projeto.setId("proj-1");
        projeto.setResponsavelUserId("gestor-1");
        projeto.setPrazo(LocalDate.of(2026, 10, 1));

        service.onProjetoConcluido(projeto);

        assertEquals(0, ledger.contar("gestor-1", TipoEvento.PROJETO_NO_PRAZO));
    }

    @Test
    void encerrarCampanhaPremiaOsTopNUmaUnicaVez() {
        OrientacaoDocument ori = orientacao("ori-seg", GamificacaoService.CATEGORIA_SEGURANCA, true);
        ori.setCampanhaBonusAsas(50);
        ori.setCampanhaTopN(2);
        when(ideiaRepository.findByAtivoTrueAndOrientacaoId("ori-seg")).thenReturn(List.of(
                ideia("i-a", "op-a", "ori-seg"), ideia("i-b", "op-b", "ori-seg"), ideia("i-c", "op-c", "ori-seg")));
        service.registrar(GamificacaoService.Lancamento.padrao("op-a", TipoEvento.IDEIA_APROVADA, "i-a", 45, 23, null, null));
        service.registrar(GamificacaoService.Lancamento.padrao("op-b", TipoEvento.IDEIA_ENVIADA, "i-b", 2, 0, null, null));
        service.registrar(GamificacaoService.Lancamento.padrao("op-c", TipoEvento.IDEIA_CONVERTIDA, "i-c", 90, 68, null, null));

        var vencedores = service.encerrarCampanha(ori);
        service.encerrarCampanha(ori);

        assertEquals(List.of("op-c", "op-a"), vencedores.stream().map(GamificacaoService.Vencedor::userId).toList());
        assertEquals(1, ledger.contar("op-c", TipoEvento.CAMPANHA_BONUS));
        assertEquals(0, ledger.contar("op-b", TipoEvento.CAMPANHA_BONUS));
    }

    @Test
    void asasPorRetornoTemTeto() {
        assertEquals(0, GamificacaoService.asasPorRetorno(null));
        assertEquals(9, GamificacaoService.asasPorRetorno(BigDecimal.valueOf(9_999)));
        assertEquals(GamificacaoService.TETO_ASAS_RETORNO, GamificacaoService.asasPorRetorno(BigDecimal.valueOf(1_000_000)));
    }

    private PontoEventoDocument unico(String userId, TipoEvento tipo) {
        List<PontoEventoDocument> achados = ledger.eventos.stream()
                .filter(e -> e.getUserId().equals(userId) && e.getTipo() == tipo)
                .toList();
        assertEquals(1, achados.size(), "esperava 1 evento " + tipo + " para " + userId);
        return achados.get(0);
    }

    static UsuarioDocument usuario(String id) {
        UsuarioDocument u = new UsuarioDocument();
        u.setId(id);
        u.setNome(id);
        u.setPerfil(id.startsWith("lider") ? PerfilUsuario.LIDER
                : id.startsWith("gestor") ? PerfilUsuario.GESTOR : PerfilUsuario.OPERADOR);
        u.setDivisao("LOGISTICA");
        return u;
    }

    static IdeiaDocument ideia(String id, String autor, String orientacaoId) {
        IdeiaDocument i = new IdeiaDocument();
        i.setId(id);
        i.setAutorUserId(autor);
        i.setTitulo("Ideia " + id);
        i.setOrientacaoId(orientacaoId);
        i.setStatus(StatusIdeia.PENDENTE);
        i.setDataCriacao(AGORA.minus(Duration.ofDays(1)));
        return i;
    }

    static OrientacaoDocument orientacao(String id, String categoria, boolean campanhaAtiva) {
        OrientacaoDocument o = new OrientacaoDocument();
        o.setId(id);
        o.setTitulo("Orientação " + id);
        o.setCategoria(categoria);
        o.setCampanha("Jornada Segura");
        o.setCriadoPorUserId("lider-1");
        o.setAtivo(true);
        if (campanhaAtiva) {
            o.setCampanhaFim(LocalDate.of(2026, 11, 30));
        }
        return o;
    }
}
