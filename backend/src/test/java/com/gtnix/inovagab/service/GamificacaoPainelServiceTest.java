package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.dto.gamificacao.GamificacaoDtos.RankingEntradaResponse;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.PontoEventoRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.repository.RecompensaRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamificacaoPainelServiceTest {

    @Mock
    private PontoEventoRepository eventoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private IdeiaRepository ideiaRepository;
    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private OrientacaoRepository orientacaoRepository;
    @Mock
    private RecompensaRepository recompensaRepository;

    private final Map<String, UsuarioDocument> usuarios = new HashMap<>();
    private LedgerEmMemoria ledger;
    private GamificacaoService gamificacao;
    private GamificacaoPainelService painel;

    @BeforeEach
    void setUp() {
        ledger = new LedgerEmMemoria(eventoRepository);
        lenient().when(usuarioRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(usuarios.get(inv.getArgument(0))));
        lenient().when(usuarioRepository.findByPerfilAndAtivoTrue(any())).thenAnswer(inv -> usuarios.values().stream()
                .filter(u -> u.getPerfil() == inv.getArgument(0))
                .toList());
        Clock clock = Clock.fixed(GamificacaoServiceTest.AGORA, Temporada.ZONA);
        gamificacao = new GamificacaoService(eventoRepository, usuarioRepository, orientacaoRepository, ideiaRepository, clock);
        lenient().when(recompensaRepository.findByAtivoTrueOrderByCustoAsasAsc()).thenReturn(List.of());
        painel = new GamificacaoPainelService(eventoRepository, usuarioRepository, ideiaRepository, projetoRepository,
                orientacaoRepository, recompensaRepository, gamificacao, clock);

        operador("op-a", "LOGISTICA", "CD Serra");
        operador("op-b", "LOGISTICA", "CD Serra");
        operador("op-c", "LOGISTICA", "Cariacica");
        operador("op-d", "LOGISTICA", "Cariacica");
        operador("op-zero", "LOGISTICA", "Cariacica");
        operador("op-pass", "PASSAGEIROS", "Garagem Vitória");
        xp("op-a", 50);
        xp("op-b", 40);
        xp("op-c", 30);
        xp("op-d", 20);
        xp("op-pass", 10);
    }

    @Test
    void zonaDePremioSaoOsTresPrimeirosDeCadaDivisao() {
        var ranking = painel.ranking(RecompensaServiceTest.principal("op-d", PerfilUsuario.OPERADOR),
                PerfilUsuario.OPERADOR, "GRUPO", null);

        Map<String, Boolean> zona = new HashMap<>();
        ranking.entradas().forEach(e -> zona.put(e.usuarioId(), e.zonaPremio()));
        assertTrue(zona.get("op-a") && zona.get("op-b") && zona.get("op-c"));
        assertFalse(zona.get("op-d"));
        assertTrue(zona.get("op-pass"), "líder da sua divisão, mesmo com pouco XP no grupo");
        assertFalse(zona.get("op-zero"), "sem XP não entra na zona");
        assertEquals(4, ranking.minhaPosicao());
        assertEquals("2026-T4", ranking.temporada().id());
    }

    @Test
    void escopoDivisaoMostraSoAMinhaDivisao() {
        var ranking = painel.ranking(RecompensaServiceTest.principal("op-a", PerfilUsuario.OPERADOR),
                PerfilUsuario.OPERADOR, "DIVISAO", null);

        assertEquals(5, ranking.totalParticipantes());
        assertTrue(ranking.entradas().stream().map(RankingEntradaResponse::divisao).allMatch("LOGISTICA"::equals));
    }

    @Test
    void operadorNaoVeRankingDeGestorNemLiderDeOutros() {
        ApiException ex = assertThrows(ApiException.class,
                () -> GamificacaoPainelService.exigirAba(PerfilUsuario.OPERADOR, PerfilUsuario.GESTOR));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertThrows(ApiException.class, () -> GamificacaoPainelService.exigirAba(PerfilUsuario.GESTOR, PerfilUsuario.LIDER));
        assertDoesNotThrow(() -> GamificacaoPainelService.exigirAba(PerfilUsuario.GESTOR, PerfilUsuario.OPERADOR));
        assertDoesNotThrow(() -> GamificacaoPainelService.exigirAba(PerfilUsuario.LIDER, PerfilUsuario.GESTOR));
    }

    @Test
    void temporadaInvalidaRetorna400() {
        ApiException ex = assertThrows(ApiException.class, () -> painel.ranking(
                RecompensaServiceTest.principal("op-a", PerfilUsuario.OPERADOR), PerfilUsuario.OPERADOR, "GRUPO", "ontem"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void rankingDeEquipesUsaMediaPorPessoa() {
        var equipes = painel.rankingEquipes(RecompensaServiceTest.principal("op-a", PerfilUsuario.OPERADOR), "DIVISAO");

        assertEquals("CD Serra", equipes.get(0).equipe());
        assertEquals(45, equipes.get(0).xpMedio());
        assertTrue(equipes.get(0).minhaEquipe());
        assertEquals(16, equipes.get(1).xpMedio(), "50 XP em 3 pessoas");
    }

    @Test
    void conquistaSobeDeTierNasMetas() {
        var bronze = GamificacaoPainelService.conquista("CURADOR", "Curador", "d", 1, GamificacaoServiceTest.AGORA);
        var quase = GamificacaoPainelService.conquista("CURADOR", "Curador", "d", 9, GamificacaoServiceTest.AGORA);
        var platina = GamificacaoPainelService.conquista("CURADOR", "Curador", "d", 30, GamificacaoServiceTest.AGORA);
        var nada = GamificacaoPainelService.conquista("CURADOR", "Curador", "d", 0, null);

        assertEquals("BRONZE", bronze.tier());
        assertEquals(3, bronze.proximaMeta());
        assertEquals("PRATA", quase.tier());
        assertEquals(0.9, quase.progresso(), 1e-9);
        assertEquals("PLATINA", platina.tier());
        assertNull(platina.proximaMeta());
        assertFalse(nada.desbloqueada());
    }

    @Test
    void meCreditaMissaoUmaVezPorSemana() {
        gamificacao.registrar(GamificacaoService.Lancamento.padrao("op-zero", TipoEvento.IDEIA_ENVIADA, "ideia-x",
                2, 0, null, null));

        var primeiro = painel.me(RecompensaServiceTest.principal("op-zero", PerfilUsuario.OPERADOR));
        var segundo = painel.me(RecompensaServiceTest.principal("op-zero", PerfilUsuario.OPERADOR));

        assertTrue(primeiro.missoes().stream().anyMatch(m -> m.id().equals("ideia-semana") && m.concluida()));
        assertEquals(1, ledger.contar("op-zero", TipoEvento.MISSAO_CONCLUIDA));
        assertEquals(7, segundo.xpTotal());
        assertEquals(1, segundo.sequenciaSemanas());
        assertEquals("INICIANTE", segundo.nivel().codigo());
    }

    @Test
    void rankingPaginaSemPerderTotalNemPosicao() {
        var ranking = painel.ranking(RecompensaServiceTest.principal("op-d", PerfilUsuario.OPERADOR),
                PerfilUsuario.OPERADOR, "DIVISAO", null, 0, 2);

        assertEquals(2, ranking.entradas().size());
        assertEquals(5, ranking.totalParticipantes());
        assertTrue(ranking.temProxima());
        assertEquals(4, ranking.minhaPosicao());
        assertEquals("op-d", ranking.minhaEntrada().usuarioId());
    }

    @Test
    void indicacoesListamTop3ComXpDeCadaDivisao() {
        UsuarioDocument lider = GamificacaoServiceTest.usuario("lider-1");
        lider.setPerfil(PerfilUsuario.LIDER);
        usuarios.put(lider.getId(), lider);

        var resposta = painel.indicacoes(RecompensaServiceTest.principal("lider-1", PerfilUsuario.LIDER), null);

        assertEquals("2026-T4", resposta.temporada().id());
        assertEquals(4, resposta.indicados().size(), "3 da Logística + 1 de Passageiros");
        assertTrue(resposta.indicados().stream().noneMatch(i -> i.usuarioId().equals("op-zero")));
        assertTrue(resposta.indicados().stream().noneMatch(i -> i.usuarioId().equals("op-d")));
    }

    @Test
    void filaEmDiaIgnoraPendenciaDeOutraArea() {
        UsuarioDocument gestor = GamificacaoServiceTest.usuario("gestor-1");
        gestor.setArea("OPERACOES");
        usuarios.put(gestor.getId(), gestor);
        gamificacao.registrar(GamificacaoService.Lancamento.padrao("gestor-1", TipoEvento.AVALIACAO_NO_PRAZO,
                "ideia-avaliada", 10, 0, null, null));

        IdeiaDocument outraArea = GamificacaoServiceTest.ideia("ideia-outra", "op-a", "ori-1");
        outraArea.setArea("COMERCIAL");
        outraArea.setAtivo(true);
        outraArea.setDataCriacao(GamificacaoServiceTest.AGORA.minus(Duration.ofDays(20)));
        when(ideiaRepository.findByAtivoTrueAndStatus(StatusIdeia.PENDENTE)).thenReturn(List.of(outraArea));
        when(projetoRepository.findByResponsavelUserIdAndAtivoTrue("gestor-1")).thenReturn(List.of());

        var painelGestor = painel.me(RecompensaServiceTest.principal("gestor-1", PerfilUsuario.GESTOR));
        var fila = painelGestor.missoes().stream().filter(m -> m.id().equals("fila-em-dia")).findFirst().orElseThrow();
        assertTrue(fila.concluida());
    }

    @Test
    void novidadesSomemDepoisDeVistas() {
        var principal = RecompensaServiceTest.principal("op-a", PerfilUsuario.OPERADOR);
        assertFalse(painel.me(principal).novidades().isEmpty());

        painel.marcarNovidadesVistas(principal);

        assertTrue(painel.me(principal).novidades().isEmpty());
    }

    private void operador(String id, String divisao, String equipe) {
        UsuarioDocument u = GamificacaoServiceTest.usuario(id);
        u.setDivisao(divisao);
        u.setEquipe(equipe);
        u.setAtivo(true);
        usuarios.put(id, u);
    }

    private void xp(String userId, int xp) {
        gamificacao.registrar(GamificacaoService.Lancamento.padrao(userId, TipoEvento.IDEIA_APROVADA, "seed-" + userId,
                xp, 0, null, null));
    }
}
