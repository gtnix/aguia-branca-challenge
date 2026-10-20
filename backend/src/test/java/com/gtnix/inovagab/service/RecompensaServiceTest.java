package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.RecompensaDocument;
import com.gtnix.inovagab.document.ResgateDocument;
import com.gtnix.inovagab.domain.NivelGamificacao;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusResgate;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.DecisaoResgateRequest;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.PontoEventoRepository;
import com.gtnix.inovagab.repository.RecompensaRepository;
import com.gtnix.inovagab.repository.ResgateRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RecompensaServiceTest {

    @Mock
    private PontoEventoRepository eventoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private OrientacaoRepository orientacaoRepository;
    @Mock
    private IdeiaRepository ideiaRepository;
    @Mock
    private RecompensaRepository recompensaRepository;
    @Mock
    private ResgateRepository resgateRepository;

    private final Map<String, ResgateDocument> resgates = new HashMap<>();
    private LedgerEmMemoria ledger;
    private GamificacaoService gamificacao;
    private RecompensaService service;
    private RecompensaDocument vale;

    @BeforeEach
    void setUp() {
        ledger = new LedgerEmMemoria(eventoRepository);
        lenient().when(usuarioRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.of(GamificacaoServiceTest.usuario(inv.getArgument(0))));
        Clock clock = Clock.fixed(GamificacaoServiceTest.AGORA, Temporada.ZONA);
        gamificacao = new GamificacaoService(eventoRepository, usuarioRepository, orientacaoRepository, ideiaRepository, clock);
        service = new RecompensaService(recompensaRepository, resgateRepository, usuarioRepository, gamificacao, clock);

        vale = new RecompensaDocument();
        vale.setId("rec-vale");
        vale.setTitulo("Vale R$ 100");
        vale.setCategoria("VALE");
        vale.setCustoAsas(60);
        vale.setEstoque(1);
        vale.setNivelMinimo(NivelGamificacao.INICIANTE);
        vale.setAtivo(true);
        lenient().when(recompensaRepository.findByIdAndAtivoTrue("rec-vale")).thenReturn(Optional.of(vale));
        lenient().when(recompensaRepository.findById("rec-vale")).thenReturn(Optional.of(vale));
        lenient().when(recompensaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(resgateRepository.save(any())).thenAnswer(inv -> {
            ResgateDocument r = inv.getArgument(0);
            if (r.getId() == null) {
                r.setId("resgate-" + (resgates.size() + 1));
            }
            resgates.put(r.getId(), r);
            return r;
        });
        lenient().when(resgateRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(resgates.get(inv.getArgument(0))));
    }

    @Test
    void resgateDebitaAsasEReservaEstoque() {
        creditar("op-1", 70);

        var resgate = service.solicitar("rec-vale", principal("op-1", PerfilUsuario.OPERADOR));

        assertEquals(StatusResgate.APROVADO, resgate.status());
        assertFalse(resgate.decididoEm() == null);
        assertEquals(10, gamificacao.saldoAsas("op-1"));
        assertEquals(0, vale.getEstoque());
        assertEquals(1, ledger.contar("op-1", TipoEvento.RESGATE));
    }

    @Test
    void saldoInsuficienteRetorna409SemDebitar() {
        creditar("op-1", 59);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.solicitar("rec-vale", principal("op-1", PerfilUsuario.OPERADOR)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("Faltam 1 Asas", ex.getMessage());
        assertEquals(59, gamificacao.saldoAsas("op-1"));
        assertEquals(1, vale.getEstoque());
    }

    @Test
    void semEstoqueRetorna409() {
        creditar("op-1", 500);
        creditar("op-2", 500);
        service.solicitar("rec-vale", principal("op-1", PerfilUsuario.OPERADOR));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.solicitar("rec-vale", principal("op-2", PerfilUsuario.OPERADOR)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("Esgotado", ex.getMessage());
    }

    @Test
    void nivelMinimoBloqueiaMesmoComSaldo() {
        vale.setNivelMinimo(NivelGamificacao.VISIONARIO);
        creditar("op-1", 500);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.solicitar("rec-vale", principal("op-1", PerfilUsuario.OPERADOR)));

        assertTrue(ex.getMessage().contains("Visionário"));
    }

    @Test
    void negarResgateERecusado() {
        creditar("op-1", 60);
        var resgate = service.solicitar("rec-vale", principal("op-1", PerfilUsuario.OPERADOR));
        UsuarioPrincipal lider = principal("lider-1", PerfilUsuario.LIDER);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.decidir(resgate.id(), new DecisaoResgateRequest(StatusResgate.NEGADO, "Fora da política"), lider));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals(0, gamificacao.saldoAsas("op-1"));
        assertEquals(0, vale.getEstoque());
        assertEquals(StatusResgate.APROVADO, resgates.get(resgate.id()).getStatus());
    }

    @Test
    void fluxoAprovadoEntregueENaoVoltaAtras() {
        creditar("op-1", 60);
        var resgate = service.solicitar("rec-vale", principal("op-1", PerfilUsuario.OPERADOR));
        UsuarioPrincipal lider = principal("lider-1", PerfilUsuario.LIDER);

        assertEquals(StatusResgate.APROVADO, resgate.status());
        var entregue = service.decidir(resgate.id(), new DecisaoResgateRequest(StatusResgate.ENTREGUE, null), lider);

        assertEquals(StatusResgate.ENTREGUE, entregue.status());
        assertFalse(entregue.entregueEm() == null);
        ApiException ex = assertThrows(ApiException.class,
                () -> service.decidir(resgate.id(), new DecisaoResgateRequest(StatusResgate.NEGADO, "tarde"), lider));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals(0, gamificacao.saldoAsas("op-1"));
    }

    @Test
    void ninguemDecideOProprioResgate() {
        creditar("gestor-1", 60);
        var resgate = service.solicitar("rec-vale", principal("gestor-1", PerfilUsuario.GESTOR));

        ApiException ex = assertThrows(ApiException.class, () -> service.decidir(resgate.id(),
                new DecisaoResgateRequest(StatusResgate.ENTREGUE, null), principal("gestor-1", PerfilUsuario.LIDER)));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    private void creditar(String userId, int asas) {
        gamificacao.registrar(GamificacaoService.Lancamento.padrao(userId, TipoEvento.IDEIA_CONVERTIDA,
                "credito-" + userId, 0, asas, null, null));
    }

    static UsuarioPrincipal principal(String userId, PerfilUsuario perfil) {
        var u = GamificacaoServiceTest.usuario(userId);
        u.setPerfil(perfil);
        u.setEmail(userId + "@gab.com.br");
        u.setSenhaHash("x");
        u.setAtivo(true);
        return new UsuarioPrincipal(u);
    }
}
