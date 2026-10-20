package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.ProjetoDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.domain.StatusProjeto;
import com.gtnix.inovagab.dto.projeto.ProgressoRequest;
import com.gtnix.inovagab.dto.projeto.ProjetoRequest;
import com.gtnix.inovagab.dto.projeto.ProjetoResponse;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.ProjetoRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class ProjetoService {

    static final BigDecimal DOZE = BigDecimal.valueOf(12);

    private final ProjetoRepository projetoRepository;
    private final OrientacaoService orientacaoService;
    private final IdeiaRepository ideiaRepository;
    private final GamificacaoService gamificacaoService;

    public ProjetoService(
            ProjetoRepository projetoRepository,
            OrientacaoService orientacaoService,
            IdeiaRepository ideiaRepository,
            GamificacaoService gamificacaoService
    ) {
        this.projetoRepository = projetoRepository;
        this.orientacaoService = orientacaoService;
        this.ideiaRepository = ideiaRepository;
        this.gamificacaoService = gamificacaoService;
    }

    public List<ProjetoResponse> listar() {
        return projetoRepository.findByAtivoTrue().stream().map(this::toResponse).toList();
    }

    public ProjetoResponse obter(String id) {
        return projetoRepository.findByIdAndAtivoTrue(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    public ProjetoResponse criar(ProjetoRequest request, UsuarioPrincipal principal) {
        String ideiaId = blankToNull(request.ideiaId());
        if (ideiaId != null) {
            ProjetoResponse existente = projetoExistenteDaIdeia(ideiaId);
            if (existente != null) {
                return existente;
            }
        }

        orientacaoService.requireAtiva(request.orientacaoId());
        IdeiaDocument ideia = exigirIdeiaConversivel(ideiaId, request.orientacaoId());

        ProjetoDocument doc = new ProjetoDocument();
        applyRequest(doc, request, true);
        doc.setResponsavelUserId(principal.getUserId());
        Instant now = Instant.now();
        doc.setDataCriacao(now);
        doc.setDataAtualizacao(now);

        try {
            ProjetoDocument saved = projetoRepository.save(doc);
            marcarIdeiaConvertida(ideia);
            if (ideia != null) {
                gamificacaoService.onIdeiaConvertida(ideia, saved);
            }
            return toResponse(saved);
        } catch (DuplicateKeyException ex) {
            ProjetoResponse existente = projetoExistenteDaIdeia(ideiaId);
            if (existente != null) {
                return existente;
            }
            throw new ApiException(HttpStatus.CONFLICT, "Projeto já existe para esta ideia");
        }
    }

    public ProjetoResponse atualizar(String id, ProjetoRequest request) {
        ProjetoDocument doc = projetoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        orientacaoService.requireAtiva(request.orientacaoId());
        StatusProjeto anterior = parseStatus(doc.getStatus());
        applyRequest(doc, request, false);
        doc.setDataAtualizacao(Instant.now());
        ProjetoDocument saved = projetoRepository.save(doc);
        notificarSeConcluido(anterior, saved);
        return toResponse(saved);
    }

    public void excluir(String id) {
        ProjetoDocument doc = projetoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        doc.marcarExcluido();
        doc.setIdeiaId(null);
        projetoRepository.save(doc);
        gamificacaoService.onProjetoExcluido(doc);
    }

    public ProjetoResponse atualizarProgresso(String id, ProgressoRequest request) {
        ProjetoDocument doc = projetoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        StatusProjeto atual = parseStatus(doc.getStatus());
        if (request.etapa() != null) {
            doc.setEtapa(request.etapa());
        }
        if (request.status() != null) {
            StatusProjeto status = parseStatus(request.status());
            exigirTransicao(atual, status);
            doc.setStatus(status.name());
            if (status == StatusProjeto.CONCLUIDO && request.progressoPercentual() == null) {
                doc.setProgressoPercentual(100);
            }
        } else if (atual.isTerminal() && (request.progressoPercentual() != null || request.etapa() != null)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Projeto encerrado não pode ter progresso alterado");
        }
        if (request.progressoPercentual() != null) {
            doc.setProgressoPercentual(request.progressoPercentual());
        }
        if (request.resultadosObtidos() != null) {
            doc.setResultadosObtidos(request.resultadosObtidos());
        }
        doc.setDataAtualizacao(Instant.now());
        ProjetoDocument saved = projetoRepository.save(doc);
        notificarSeConcluido(atual, saved);
        return toResponse(saved);
    }

    private void notificarSeConcluido(StatusProjeto anterior, ProjetoDocument projeto) {
        if (anterior != StatusProjeto.CONCLUIDO && StatusProjeto.CONCLUIDO.name().equals(projeto.getStatus())) {
            gamificacaoService.onProjetoConcluido(projeto);
        }
    }

    private ProjetoResponse projetoExistenteDaIdeia(String ideiaId) {
        if (ideiaId == null) {
            return null;
        }
        return projetoRepository.findByIdeiaIdAndAtivoTrue(ideiaId)
                .map(this::toResponse)
                .orElse(null);
    }

    private IdeiaDocument exigirIdeiaConversivel(String ideiaId, String orientacaoId) {
        if (ideiaId == null) {
            return null;
        }
        IdeiaDocument ideia = ideiaRepository.findByIdAndAtivoTrue(ideiaId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Ideia vinculada inválida"));
        if (ideia.getOrientacaoId() != null && !ideia.getOrientacaoId().equals(orientacaoId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ideia não pertence à orientação informada");
        }
        if (ideia.getStatus() == StatusIdeia.CONVERTIDA_PROJETO) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ideia já convertida em projeto");
        }
        if (ideia.getStatus() != StatusIdeia.APROVADA) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ideia deve estar aprovada para virar projeto");
        }
        return ideia;
    }

    private void marcarIdeiaConvertida(IdeiaDocument ideia) {
        if (ideia == null) {
            return;
        }
        ideia.setStatus(StatusIdeia.CONVERTIDA_PROJETO);
        ideia.setDataAtualizacao(Instant.now());
        ideiaRepository.save(ideia);
    }

    private void applyRequest(ProjetoDocument doc, ProjetoRequest request, boolean criacao) {
        StatusProjeto status;
        if (request.status() == null || request.status().isBlank()) {
            status = criacao ? StatusProjeto.PLANEJADO : parseStatus(doc.getStatus());
        } else {
            status = parseStatus(request.status());
            if (!criacao) {
                exigirTransicao(parseStatus(doc.getStatus()), status);
            }
        }
        Integer progresso = request.progressoPercentual();
        if (progresso == null) {
            progresso = criacao ? 0 : doc.getProgressoPercentual();
        }
        if (status == StatusProjeto.CONCLUIDO && (progresso == null || progresso < 100)) {
            progresso = 100;
        }

        doc.setTitulo(request.titulo());
        doc.setObjetivo(request.objetivo());
        doc.setDescricao(request.descricao());
        doc.setArea(request.area());
        doc.setOrientacaoId(request.orientacaoId());
        if (criacao) {
            doc.setIdeiaId(blankToNull(request.ideiaId()));
        }
        doc.setEtapa(blankToNull(request.etapa()) != null ? request.etapa() : status.name());
        doc.setStatus(status.name());
        doc.setInvestimento(request.investimento());
        doc.setRetorno(request.retorno());
        doc.setPrazo(request.prazo());
        doc.setProgressoPercentual(progresso);
        doc.setResultadosObtidos(request.resultadosObtidos());
    }

    private void exigirTransicao(StatusProjeto atual, StatusProjeto proximo) {
        if (!atual.canTransitionTo(proximo)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transição de status inválida");
        }
    }

    private StatusProjeto parseStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return StatusProjeto.PLANEJADO;
        }
        try {
            return StatusProjeto.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status de projeto inválido");
        }
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    /**
     * ROI anualizado em percentual, igual ao app:
     * ((retorno mensal × 12) − investimento) / investimento × 100
     */
    BigDecimal calcularRoi(BigDecimal investimento, BigDecimal retorno) {
        if (investimento == null || retorno == null || investimento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return retorno.multiply(DOZE)
                .subtract(investimento)
                .divide(investimento, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private ProjetoResponse toResponse(ProjetoDocument doc) {
        return new ProjetoResponse(
                doc.getId(),
                doc.getTitulo(),
                doc.getObjetivo(),
                doc.getDescricao(),
                doc.getArea(),
                doc.getOrientacaoId(),
                doc.getIdeiaId(),
                doc.getEtapa(),
                doc.getStatus(),
                doc.getInvestimento(),
                doc.getRetorno(),
                calcularRoi(doc.getInvestimento(), doc.getRetorno()),
                doc.getPrazo(),
                doc.getProgressoPercentual(),
                doc.getResultadosObtidos(),
                doc.getResponsavelUserId(),
                doc.getDataCriacao(),
                doc.getDataAtualizacao()
        );
    }
}
