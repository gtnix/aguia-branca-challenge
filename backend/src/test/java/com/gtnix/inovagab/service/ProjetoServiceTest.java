package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.projeto.ProgressoRequest;
import com.gtnix.inovagab.dto.projeto.ProjetoRequest;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private OrientacaoService orientacaoService;
    @Mock
    private GamificacaoService gamificacaoService;
    @Mock
    private IdeiaRepository ideiaRepository;

    private ProjetoService service;

    @BeforeEach
    void setUp() {
        service = new ProjetoService(projetoRepository, orientacaoService, ideiaRepository, gamificacaoService);
    }

    @Test
    void criarManualPersisteGestorComoResponsavel() {
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(projetoRepository.save(any())).thenAnswer(invocation -> {
            ProjetoDocument doc = invocation.getArgument(0);
            doc.setId("proj-1");
            return doc;
        });

        var response = service.criar(request(null), principal());

        assertEquals("proj-1", response.id());
        assertEquals("user-gestor", response.responsavelUserId());
        assertEquals("ori-1", response.orientacaoId());
        assertEquals("PLANEJADO", response.status());
        assertEquals(0, response.progressoPercentual());
        assertNull(response.ideiaId());
        verify(ideiaRepository, never()).save(any());
    }

    @Test
    void converterIdeiaAprovadaMarcaConvertida() {
        when(projetoRepository.findByIdeiaIdAndAtivoTrue("ideia-1")).thenReturn(Optional.empty());
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        IdeiaDocument ideia = ideia("ideia-1", StatusIdeia.APROVADA);
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(ideia));
        when(projetoRepository.save(any())).thenAnswer(invocation -> {
            ProjetoDocument doc = invocation.getArgument(0);
            doc.setId("proj-1");
            return doc;
        });
        when(ideiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(request("ideia-1"), principal());

        assertEquals("ideia-1", response.ideiaId());
        assertEquals(StatusIdeia.CONVERTIDA_PROJETO, ideia.getStatus());
        verify(ideiaRepository).save(ideia);
    }

    @Test
    void converterDuasVezesRetornaOMesmoProjeto() {
        ProjetoDocument existente = projeto("proj-1", "ideia-1");
        when(projetoRepository.findByIdeiaIdAndAtivoTrue("ideia-1")).thenReturn(Optional.of(existente));

        var primeira = service.criar(request("ideia-1"), principal());
        var segunda = service.criar(request("ideia-1"), principal());

        assertEquals("proj-1", primeira.id());
        assertEquals(primeira.id(), segunda.id());
        verify(projetoRepository, never()).save(any());
        verify(orientacaoService, never()).requireAtiva(any());
    }

    @Test
    void ideiaPendenteNaoViraProjeto() {
        when(projetoRepository.findByIdeiaIdAndAtivoTrue("ideia-1")).thenReturn(Optional.empty());
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(ideiaRepository.findByIdAndAtivoTrue("ideia-1"))
                .thenReturn(Optional.of(ideia("ideia-1", StatusIdeia.PENDENTE)));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.criar(request("ideia-1"), principal()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(projetoRepository, never()).save(any());
    }

    @Test
    void statusInvalidoRetorna400() {
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.criar(requestComStatus(null, "FOO"), principal()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void roiAnualizadoSegueFormulaDoApp() {
        BigDecimal roi = service.calcularRoi(BigDecimal.valueOf(100_000), BigDecimal.valueOf(10_000));
        assertEquals(new BigDecimal("20.00"), roi);
    }

    @Test
    void atualizarProgressoAlteraEtapaEPercentual() {
        ProjetoDocument doc = projeto("proj-1", null);
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));
        when(projetoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.atualizarProgresso("proj-1",
                new ProgressoRequest("EXECUCAO", "EM_ANDAMENTO", 40, "piloto ok"));

        assertEquals("EM_ANDAMENTO", response.status());
        assertEquals(40, response.progressoPercentual());
        assertEquals("EXECUCAO", response.etapa());
        assertEquals("piloto ok", response.resultadosObtidos());
    }

    @Test
    void concluirViaProgressoPreencheCemPorCento() {
        ProjetoDocument doc = projeto("proj-1", null);
        doc.setProgressoPercentual(40);
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));
        when(projetoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.atualizarProgresso("proj-1",
                new ProgressoRequest(null, "CONCLUIDO", null, null));

        assertEquals("CONCLUIDO", response.status());
        assertEquals(100, response.progressoPercentual());
    }

    @Test
    void naoReabreProjetoConcluido() {
        ProjetoDocument doc = projeto("proj-1", null);
        doc.setStatus("CONCLUIDO");
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.atualizarProgresso("proj-1",
                        new ProgressoRequest(null, "EM_ANDAMENTO", 50, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void atualizarMantemStatusQuandoOmitido() {
        ProjetoDocument doc = projeto("proj-1", "ideia-1");
        doc.setStatus("EM_ANDAMENTO");
        doc.setProgressoPercentual(40);
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(projetoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.atualizar("proj-1", request("ideia-outra"));

        assertEquals("ideia-1", response.ideiaId());
        assertEquals("EM_ANDAMENTO", response.status());
        assertEquals(40, response.progressoPercentual());
    }

    @Test
    void atualizarNaoTrocaIdeiaOrigem() {
        ProjetoDocument doc = projeto("proj-1", "ideia-1");
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));
        when(orientacaoService.requireAtiva("ori-1")).thenReturn(new OrientacaoDocument());
        when(projetoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.atualizar("proj-1", request("ideia-outra"));

        assertEquals("ideia-1", response.ideiaId());
        assertEquals("ideia-1", doc.getIdeiaId());
    }

    @Test
    void excluirFazSoftDelete() {
        ProjetoDocument doc = projeto("proj-1", null);
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));
        when(projetoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.excluir("proj-1");

        ArgumentCaptor<ProjetoDocument> captor = ArgumentCaptor.forClass(ProjetoDocument.class);
        verify(projetoRepository).save(captor.capture());
        assertFalse(captor.getValue().isAtivo());
        assertNull(captor.getValue().getIdeiaId());
    }

    @Test
    void excluirAnulaIdeiaIdParaLiberarIndiceUnico() {
        ProjetoDocument doc = projeto("proj-1", "ideia-1");
        when(projetoRepository.findByIdAndAtivoTrue("proj-1")).thenReturn(Optional.of(doc));
        when(projetoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.excluir("proj-1");

        ArgumentCaptor<ProjetoDocument> captor = ArgumentCaptor.forClass(ProjetoDocument.class);
        verify(projetoRepository).save(captor.capture());
        assertFalse(captor.getValue().isAtivo());
        assertNull(captor.getValue().getIdeiaId());
    }

    @Test
    void obterInexistenteRetorna404() {
        when(projetoRepository.findByIdAndAtivoTrue("sumiu")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.obter("sumiu"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    private static ProjetoRequest request(String ideiaId) {
        return requestComStatus(ideiaId, null);
    }

    private static ProjetoRequest requestComStatus(String ideiaId, String status) {
        return new ProjetoRequest(
                "Telemetria da frota",
                "Reduzir combustível",
                "Descrição",
                "OPERACOES",
                "ori-1",
                ideiaId,
                null,
                status,
                BigDecimal.valueOf(100_000),
                BigDecimal.valueOf(10_000),
                LocalDate.parse("2026-12-31"),
                null,
                null
        );
    }

    private static IdeiaDocument ideia(String id, StatusIdeia status) {
        IdeiaDocument doc = new IdeiaDocument();
        doc.setId(id);
        doc.setOrientacaoId("ori-1");
        doc.setStatus(status);
        return doc;
    }

    private static ProjetoDocument projeto(String id, String ideiaId) {
        ProjetoDocument doc = new ProjetoDocument();
        doc.setId(id);
        doc.setTitulo("Telemetria da frota");
        doc.setOrientacaoId("ori-1");
        doc.setIdeiaId(ideiaId);
        doc.setStatus("PLANEJADO");
        doc.setProgressoPercentual(0);
        return doc;
    }

    private static UsuarioPrincipal principal() {
        UsuarioDocument usuario = new UsuarioDocument();
        usuario.setId("user-gestor");
        usuario.setEmail("ana.oliveira@aguiabranca.com.br");
        usuario.setSenhaHash("hash");
        usuario.setPerfil(PerfilUsuario.GESTOR);
        usuario.setAtivo(true);
        return new UsuarioPrincipal(usuario);
    }
}
