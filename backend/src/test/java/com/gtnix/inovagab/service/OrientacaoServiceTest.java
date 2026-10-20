package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.Temporada;
import com.gtnix.inovagab.dto.orientacao.OrientacaoRequest;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrientacaoServiceTest {

    @Mock
    private OrientacaoRepository orientacaoRepository;
    @Mock
    private GamificacaoService gamificacaoService;
    @Mock
    private UsuarioRepository usuarioRepository;

    private OrientacaoService service;

    @BeforeEach
    void setUp() {
        service = new OrientacaoService(orientacaoRepository, gamificacaoService, usuarioRepository,
                Clock.fixed(Instant.parse("2026-10-19T12:00:00Z"), Temporada.ZONA));
    }

    @Test
    void criarPersisteHistoricoECriador() {
        when(orientacaoRepository.save(any())).thenAnswer(invocation -> {
            OrientacaoDocument doc = invocation.getArgument(0);
            doc.setId("ori-1");
            return doc;
        });

        var response = service.criar(
                new OrientacaoRequest("Título", "Descrição", "EFICIENCIA_OPERACIONAL", "Águia 2026"),
                principal()
        );

        assertEquals("ori-1", response.id());
        assertEquals("user-lider", response.criadoPorUserId());
        assertEquals("Águia 2026", response.campanha());
        assertTrue(response.ativo());
        assertNotNull(response.dataCriacao());
        assertNull(response.divisao());
    }

    @Test
    void criarPersisteDivisao() {
        when(orientacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(
                new OrientacaoRequest("Título", "Descrição", "EFICIENCIA_OPERACIONAL", "Águia 2026",
                        null, null, null, "LOGISTICA"),
                principal()
        );

        assertEquals("LOGISTICA", response.divisao());
    }

    @Test
    void excluirFazSoftDelete() {
        OrientacaoDocument doc = ativa("ori-1");
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));
        when(orientacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.excluir("ori-1");

        ArgumentCaptor<OrientacaoDocument> captor = ArgumentCaptor.forClass(OrientacaoDocument.class);
        verify(orientacaoRepository).save(captor.capture());
        assertFalse(captor.getValue().isAtivo());
        assertNotNull(captor.getValue().getDeletedAt());
    }

    @Test
    void excluirInativaRemoveDaListagem() {
        OrientacaoDocument doc = ativa("ori-1");
        doc.setAtivo(false);
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));
        when(orientacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.excluir("ori-1");

        assertNotNull(doc.getDeletedAt());
        assertFalse(doc.isAtivo());
    }

    @Test
    void desativarNaoMarcaComoExcluidaPermanentemente() {
        OrientacaoDocument doc = ativa("ori-1");
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));
        when(orientacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.definirAtivo("ori-1", false);

        assertFalse(response.ativo());
        assertNull(doc.getDeletedAt());
    }

    @Test
    void listarHistoricoDoLiderIncluiExcluidas() {
        OrientacaoDocument ativa = ativa("ori-ativa");
        OrientacaoDocument inativa = ativa("ori-inativa");
        inativa.setAtivo(false);
        OrientacaoDocument excluida = ativa("ori-excluida");
        excluida.marcarExcluido();
        when(orientacaoRepository.findAll()).thenReturn(List.of(ativa, inativa, excluida));

        var lista = service.listar(false);

        assertEquals(3, lista.size());
        assertTrue(lista.stream().anyMatch(o -> o.id().equals("ori-ativa")));
        assertTrue(lista.stream().anyMatch(o -> o.id().equals("ori-inativa")));
        assertTrue(lista.stream().anyMatch(o -> o.id().equals("ori-excluida") && !o.ativo()));
    }

    @Test
    void definirAtivoRestauraSoftDelete() {
        OrientacaoDocument doc = ativa("ori-1");
        doc.marcarExcluido();
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));
        when(orientacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.definirAtivo("ori-1", true);

        assertTrue(response.ativo());
        assertNull(doc.getDeletedAt());
    }

    @Test
    void listarApenasAtivasNaoIncluiExcluidas() {
        when(orientacaoRepository.findByAtivoTrue()).thenReturn(List.of(ativa("ori-ativa")));

        var lista = service.listar(true);

        assertEquals(1, lista.size());
        assertEquals("ori-ativa", lista.get(0).id());
    }

    @Test
    void obterInexistenteRetorna404() {
        when(orientacaoRepository.findById("missing")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.obter("missing", principal()));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void obterInativaParaOperadorRetorna404() {
        OrientacaoDocument doc = ativa("ori-1");
        doc.marcarExcluido();
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class, () -> service.obter("ori-1", principal(PerfilUsuario.OPERADOR)));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void obterInativaParaLiderRetornaDocumento() {
        OrientacaoDocument doc = ativa("ori-1");
        doc.marcarExcluido();
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));

        var response = service.obter("ori-1", principal());

        assertEquals("ori-1", response.id());
        assertFalse(response.ativo());
    }

    private static OrientacaoDocument ativa(String id) {
        OrientacaoDocument doc = new OrientacaoDocument();
        doc.setId(id);
        doc.setTitulo("Título");
        doc.setCategoria("EFICIENCIA_OPERACIONAL");
        doc.setCampanha("Águia 2026");
        doc.setCriadoPorUserId("user-lider");
        return doc;
    }

    private static UsuarioPrincipal principal() {
        return principal(PerfilUsuario.LIDER);
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

    @Test
    void encerrarCampanhaSemCampanhaRetorna400() {
        OrientacaoDocument doc = new OrientacaoDocument();
        doc.setId("ori-1");
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class, () -> service.encerrarCampanha("ori-1"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void encerrarCampanhaDuasVezesRetorna409() {
        OrientacaoDocument doc = new OrientacaoDocument();
        doc.setId("ori-1");
        doc.setCampanhaFim(java.time.LocalDate.of(2026, 11, 30));
        doc.setCampanhaEncerradaEm(Instant.parse("2026-10-18T12:00:00Z"));
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class, () -> service.encerrarCampanha("ori-1"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void encerrarCampanhaGravaDataEMostraNomeDosVencedores() {
        OrientacaoDocument doc = new OrientacaoDocument();
        doc.setId("ori-1");
        doc.setCampanha("Jornada Segura");
        doc.setCampanhaFim(java.time.LocalDate.of(2026, 11, 30));
        when(orientacaoRepository.findById("ori-1")).thenReturn(Optional.of(doc));
        when(gamificacaoService.encerrarCampanha(doc))
                .thenReturn(List.of(new GamificacaoService.Vencedor("user-operador", 45, 50)));
        UsuarioDocument pedro = new UsuarioDocument();
        pedro.setNome("Pedro Santos");
        when(usuarioRepository.findById("user-operador")).thenReturn(Optional.of(pedro));

        var resposta = service.encerrarCampanha("ori-1");

        assertEquals("Pedro Santos", resposta.vencedores().get(0).nome());
        assertNotNull(doc.getCampanhaEncerradaEm());
        assertFalse(doc.isCampanhaAtiva(java.time.LocalDate.of(2026, 10, 19)));
        verify(orientacaoRepository).save(doc);
    }
}
