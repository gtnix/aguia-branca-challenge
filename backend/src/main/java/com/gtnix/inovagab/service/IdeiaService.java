package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.ApoioDocument;
import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusAnaliseIa;
import com.gtnix.inovagab.domain.StatusIdeia;
import com.gtnix.inovagab.dto.ai.IdeaScoreItem;
import com.gtnix.inovagab.dto.ideia.AnaliseIaResponse;
import com.gtnix.inovagab.dto.ideia.AvaliacaoRequest;
import com.gtnix.inovagab.dto.ideia.EstimativaRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaRequest;
import com.gtnix.inovagab.dto.ideia.IdeiaResponse;
import com.gtnix.inovagab.dto.ideia.PrioridadeRequest;
import com.gtnix.inovagab.repository.ApoioRepository;
import com.gtnix.inovagab.repository.IdeiaRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class IdeiaService {

    private final IdeiaRepository ideiaRepository;
    private final OrientacaoService orientacaoService;
    private final UsuarioRepository usuarioRepository;
    private final GamificacaoService gamificacaoService;
    private final ApoioRepository apoioRepository;
    private final EmbeddingService embeddingService;

    public IdeiaService(
            IdeiaRepository ideiaRepository,
            OrientacaoService orientacaoService,
            UsuarioRepository usuarioRepository,
            GamificacaoService gamificacaoService,
            ApoioRepository apoioRepository,
            EmbeddingService embeddingService
    ) {
        this.ideiaRepository = ideiaRepository;
        this.orientacaoService = orientacaoService;
        this.usuarioRepository = usuarioRepository;
        this.gamificacaoService = gamificacaoService;
        this.apoioRepository = apoioRepository;
        this.embeddingService = embeddingService;
    }

    public List<IdeiaResponse> listar(
            UsuarioPrincipal principal,
            StatusIdeia status,
            String area,
            String orientacaoId
    ) {
        Stream<IdeiaDocument> stream;
        if (principal.getPerfil() == PerfilUsuario.OPERADOR) {
            stream = ideiaRepository.findByAutorUserIdAndAtivoTrue(principal.getUserId()).stream();
        } else {
            stream = ideiaRepository.findByAtivoTrue().stream();
        }
        if (status != null) {
            stream = stream.filter(i -> i.getStatus() == status);
        }
        if (area != null && !area.isBlank()) {
            stream = stream.filter(i -> area.equalsIgnoreCase(i.getArea()));
        }
        if (orientacaoId != null && !orientacaoId.isBlank()) {
            stream = stream.filter(i -> orientacaoId.equals(i.getOrientacaoId()));
        }
        // Fila por ordem de chegada para quem avalia: ordenar pelo score da IA ancoraria a decisão.
        Comparator<IdeiaDocument> porData = Comparator.comparing(
                IdeiaDocument::getDataCriacao, Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<IdeiaDocument> comparator = principal.getPerfil() == PerfilUsuario.OPERADOR
                ? porData.reversed()
                : porData;
        List<IdeiaDocument> docs = stream.sorted(comparator).toList();
        Map<String, String> nomes = nomesPorAutor(docs);
        boolean exibirIa = podeVerIa(principal);
        return docs.stream().map(doc -> toResponse(doc, nomes, exibirIa)).toList();
    }

    public IdeiaResponse obter(String id, UsuarioPrincipal principal) {
        IdeiaDocument doc = requireAccessible(id, principal);
        return toResponse(doc, podeVerIa(principal));
    }

    public IdeiaResponse criar(IdeiaRequest request, UsuarioPrincipal principal) {
        OrientacaoDocument orientacao = orientacaoService.requireAtiva(request.orientacaoId());
        IdeiaDocument doc = new IdeiaDocument();
        doc.setAutorUserId(principal.getUserId());
        applyRequest(doc, request);
        doc.setStatus(StatusIdeia.PENDENTE);
        List<IdeiaDocument> existentes = ideiaRepository.findByAtivoTrue();
        SimilaridadeIdeias.encontrarDuplicada(request.titulo(), existentes)
                .ifPresent(doc::setDuplicadaDe);
        embeddingService.gerar(request.titulo(), request.descricao()).ifPresent(vetor -> {
            doc.setEmbedding(vetor);
            if (doc.getDuplicadaDe() == null) {
                EmbeddingService.encontrarSemelhante(vetor, existentes).ifPresent(doc::setDuplicadaDe);
            }
        });
        Instant now = Instant.now();
        doc.setDataCriacao(now);
        doc.setDataAtualizacao(now);
        IdeiaDocument salva = ideiaRepository.save(doc);
        gamificacaoService.onIdeiaCriada(salva, orientacao);
        return toResponse(salva, false);
    }

    public IdeiaResponse atualizar(String id, IdeiaRequest request, UsuarioPrincipal principal) {
        IdeiaDocument doc = requireOwned(id, principal);
        if (doc.getStatus() != StatusIdeia.PENDENTE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ideia não pode ser editada neste status");
        }
        orientacaoService.requireAtiva(request.orientacaoId());
        boolean textoMudou = !Objects.equals(doc.getTitulo(), request.titulo())
                || !Objects.equals(doc.getDescricao(), request.descricao());
        boolean conteudoMudou = textoMudou
                || !Objects.equals(doc.getArea(), request.area())
                || !Objects.equals(doc.getOrientacaoId(), request.orientacaoId())
                || !Objects.equals(doc.getProblema(), request.problema())
                || !Objects.equals(doc.getPublicoAfetado(), request.publicoAfetado())
                || !Objects.equals(doc.getFrequencia(), request.frequencia())
                || !Objects.equals(doc.getBeneficio(), request.beneficio());
        applyRequest(doc, request);
        if (conteudoMudou) {
            RubricaIdeia.limpar(doc);
        }
        if (textoMudou) {
            doc.setEmbedding(embeddingService.gerar(request.titulo(), request.descricao()).orElse(null));
        }
        doc.setDataAtualizacao(Instant.now());
        return toResponse(ideiaRepository.save(doc), false);
    }

    public void excluir(String id, UsuarioPrincipal principal) {
        IdeiaDocument doc = requireOwned(id, principal);
        if (doc.getStatus() != StatusIdeia.PENDENTE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Somente ideias pendentes podem ser excluídas");
        }
        doc.marcarExcluido();
        ideiaRepository.save(doc);
        gamificacaoService.onIdeiaExcluida(doc);
    }

    public IdeiaResponse priorizar(String id, PrioridadeRequest request) {
        IdeiaDocument doc = requireAtiva(id);
        requireAvaliavel(doc);
        doc.setPrioridade(request.prioridade());
        doc.setDataAtualizacao(Instant.now());
        return toResponse(ideiaRepository.save(doc), true);
    }

    public IdeiaResponse salvarEstimativa(String id, EstimativaRequest request, UsuarioPrincipal gestor) {
        IdeiaDocument doc = requireAtiva(id);
        requireAvaliavel(doc);
        Instant agora = Instant.now();
        doc.setImpacto(request.impacto());
        doc.setEsforco(request.esforco());
        doc.setMotivoAjusteEstimativa(textoOuNulo(request.motivoAjuste()));
        doc.setEstimativaPorUserId(gestor.getUserId());
        doc.setEstimativaEm(agora);
        RubricaIdeia.recalcular(doc);
        doc.setDataAtualizacao(agora);
        return toResponse(ideiaRepository.save(doc), true);
    }

    public IdeiaResponse avaliar(String id, AvaliacaoRequest request, UsuarioPrincipal avaliador) {
        if (request.status() != StatusIdeia.APROVADA && request.status() != StatusIdeia.REPROVADA) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status de avaliação inválido");
        }
        if (request.status() == StatusIdeia.REPROVADA
                && (request.feedback() == null || request.feedback().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Feedback obrigatório ao reprovar");
        }
        IdeiaDocument doc = requireAtiva(id);
        requireAvaliavel(doc);
        Instant agora = Instant.now();
        doc.setStatus(request.status());
        doc.setFeedbackReprovacao(request.status() == StatusIdeia.REPROVADA ? request.feedback().trim() : null);
        doc.setAvaliadorUserId(avaliador.getUserId());
        doc.setDataAvaliacao(agora);
        doc.setDataAtualizacao(agora);
        if (request.concordaIa() != null && doc.getScoreIa() != null) {
            doc.setConcordaIa(request.concordaIa());
            doc.setMotivoDiscordanciaIa(request.concordaIa() ? null : textoOuNulo(request.motivoDiscordanciaIa()));
        }
        IdeiaDocument salva = ideiaRepository.save(doc);
        gamificacaoService.onIdeiaAvaliada(salva, avaliador.getUserId());
        return toResponse(salva, true);
    }

    public IdeiaResponse apoiar(String id, UsuarioPrincipal principal) {
        IdeiaDocument doc = requireAtiva(id);
        if (principal.getUserId().equals(doc.getAutorUserId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Não é possível apoiar a própria ideia");
        }
        if (apoioRepository.existsByIdeiaIdAndUserId(id, principal.getUserId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Você já apoiou esta ideia");
        }
        ApoioDocument apoio = new ApoioDocument();
        apoio.setIdeiaId(id);
        apoio.setUserId(principal.getUserId());
        apoio.setCriadoEm(Instant.now());
        try {
            apoioRepository.save(apoio);
        } catch (DuplicateKeyException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "Você já apoiou esta ideia");
        }
        doc.setApoios(doc.getApoios() + 1);
        IdeiaDocument salva = ideiaRepository.save(doc);
        gamificacaoService.onApoio(salva, principal.getUserId());
        return toResponse(salva, true);
    }

    public IdeiaResponse marcarFeedbackUtil(String id, boolean util, UsuarioPrincipal principal) {
        IdeiaDocument doc = requireOwned(id, principal);
        if (doc.getStatus() == StatusIdeia.PENDENTE || doc.getDataAvaliacao() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ideia ainda não foi avaliada");
        }
        if (doc.getFeedbackUtil() != null) {
            throw new ApiException(HttpStatus.CONFLICT, "Avaliação já respondida");
        }
        doc.setFeedbackUtil(util);
        doc.setDataAtualizacao(Instant.now());
        IdeiaDocument salva = ideiaRepository.save(doc);
        if (util) {
            gamificacaoService.onFeedbackUtil(salva);
        }
        return toResponse(salva, false);
    }

    public List<IdeiaDocument> findPendentesByIds(List<String> ids, String orientacaoId) {
        return ids.stream()
                .map(ideiaRepository::findByIdAndAtivoTrue)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .filter(doc -> java.util.Objects.equals(orientacaoId, doc.getOrientacaoId()))
                .filter(doc -> doc.getStatus() == StatusIdeia.PENDENTE)
                .toList();
    }

    /**
     * Grava a recomendação em uma ideia do lote. Recebe o documento (já validado como pendente e da
     * orientação pedida), nunca um id vindo do modelo: a IA não consegue escrever em outra ideia.
     *
     * @param analise {@code null} quando o Gemini falhou; uma análise anterior válida é preservada.
     */
    IdeaScoreItem aplicarAnalise(IdeiaDocument doc, AnaliseIa analise, String modelo) {
        Instant agora = Instant.now();
        if (analise != null) {
            doc.setCriteriosIa(analise.criterios());
            doc.setImpactoSugeridoIa(analise.impactoSugerido());
            doc.setEsforcoSugeridoIa(analise.esforcoSugerido());
            doc.setRiscosIa(analise.riscos());
            doc.setPerguntasIa(analise.perguntas());
            doc.setJustificativaIa(analise.resumo());
            doc.setScoreIaModelo(modelo);
            doc.setScoreIaEm(agora);
        } else if (doc.getScoreIaStatus() != StatusAnaliseIa.IA) {
            doc.setScoreIaModelo(modelo);
            doc.setScoreIaEm(agora);
        }
        RubricaIdeia.recalcular(doc);
        doc.setDataAtualizacao(agora);
        IdeiaDocument salva = ideiaRepository.save(doc);
        return new IdeaScoreItem(
                salva.getId(),
                salva.getScoreIa(),
                salva.getJustificativaIa(),
                salva.getScoreIaStatus(),
                RubricaIdeia.faixa(salva.getScoreIa())
        );
    }

    public IdeiaDocument buscarAtiva(String id) {
        return requireAtiva(id);
    }

    private void applyRequest(IdeiaDocument doc, IdeiaRequest request) {
        doc.setTitulo(request.titulo());
        doc.setDescricao(request.descricao());
        doc.setTipo(request.tipo());
        doc.setArea(request.area());
        doc.setOrientacaoId(request.orientacaoId());
        doc.setProblema(textoOuNulo(request.problema()));
        doc.setPublicoAfetado(textoOuNulo(request.publicoAfetado()));
        doc.setFrequencia(textoOuNulo(request.frequencia()));
        doc.setBeneficio(textoOuNulo(request.beneficio()));
    }

    private static String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private static boolean podeVerIa(UsuarioPrincipal principal) {
        return principal.getPerfil() != PerfilUsuario.OPERADOR;
    }

    private IdeiaDocument requireAtiva(String id) {
        return ideiaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ideia não encontrada"));
    }

    private void requireAvaliavel(IdeiaDocument doc) {
        if (doc.getStatus() != StatusIdeia.PENDENTE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ideia não está pendente");
        }
    }

    private IdeiaDocument requireOwned(String id, UsuarioPrincipal principal) {
        IdeiaDocument doc = ideiaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ideia não encontrada"));
        if (!doc.getAutorUserId().equals(principal.getUserId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Ideia de outro operador");
        }
        return doc;
    }

    private IdeiaDocument requireAccessible(String id, UsuarioPrincipal principal) {
        IdeiaDocument doc = ideiaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ideia não encontrada"));
        if (principal.getPerfil() == PerfilUsuario.OPERADOR
                && !doc.getAutorUserId().equals(principal.getUserId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Ideia de outro operador");
        }
        return doc;
    }

    private Map<String, String> nomesPorAutor(List<IdeiaDocument> docs) {
        List<String> ids = docs.stream()
                .map(IdeiaDocument::getAutorUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<String, String> nomes = new HashMap<>();
        usuarioRepository.findAllById(ids).forEach(usuario -> {
            if (nomeValido(usuario.getNome())) {
                nomes.put(usuario.getId(), usuario.getNome());
            }
        });
        return nomes;
    }

    private IdeiaResponse toResponse(IdeiaDocument doc, boolean exibirIa) {
        String autorNome = usuarioRepository.findById(doc.getAutorUserId())
                .map(UsuarioDocument::getNome)
                .filter(IdeiaService::nomeValido)
                .orElse(doc.getAutorUserId());
        return toResponse(doc, autorNome, exibirIa);
    }

    private IdeiaResponse toResponse(IdeiaDocument doc, Map<String, String> nomes, boolean exibirIa) {
        String autorNome = nomes.getOrDefault(doc.getAutorUserId(), doc.getAutorUserId());
        return toResponse(doc, autorNome, exibirIa);
    }

    private IdeiaResponse toResponse(IdeiaDocument doc, String autorNome, boolean exibirIa) {
        return new IdeiaResponse(
                doc.getId(),
                doc.getAutorUserId(),
                autorNome,
                doc.getTitulo(),
                doc.getDescricao(),
                doc.getTipo(),
                doc.getArea(),
                doc.getOrientacaoId(),
                doc.getStatus(),
                doc.getPrioridade(),
                doc.getImpacto(),
                doc.getEsforco(),
                exibirIa ? doc.getScoreIa() : null,
                exibirIa ? doc.getJustificativaIa() : null,
                exibirIa ? doc.getScoreIaFallback() : null,
                doc.getFeedbackReprovacao(),
                doc.getAvaliadorUserId(),
                doc.getFeedbackUtil(),
                doc.getDuplicadaDe(),
                doc.getApoios(),
                doc.getDataCriacao(),
                doc.getDataAtualizacao(),
                doc.getProblema(),
                doc.getPublicoAfetado(),
                doc.getFrequencia(),
                doc.getBeneficio(),
                exibirIa ? analiseIa(doc) : null
        );
    }

    private static AnaliseIaResponse analiseIa(IdeiaDocument doc) {
        if (doc.getScoreIaStatus() == null && doc.getEstimativaOrigem() == null) {
            return null;
        }
        List<AnaliseIaResponse.CriterioIaResponse> criterios = doc.getCriteriosIa() == null
                ? List.of()
                : doc.getCriteriosIa().stream()
                .map(c -> new AnaliseIaResponse.CriterioIaResponse(
                        c.getCriterio(),
                        c.getNota(),
                        RubricaIdeia.PESOS.getOrDefault(c.getCriterio(), 0),
                        c.getEvidencia()))
                .toList();
        return new AnaliseIaResponse(
                doc.getScoreIaStatus(),
                RubricaIdeia.faixa(doc.getScoreIa()),
                criterios,
                doc.getRiscosIa() == null ? List.of() : doc.getRiscosIa(),
                doc.getPerguntasIa() == null ? List.of() : doc.getPerguntasIa(),
                doc.getImpactoSugeridoIa(),
                doc.getEsforcoSugeridoIa(),
                doc.getEstimativaOrigem(),
                doc.getMotivoAjusteEstimativa(),
                doc.getScoreIaModelo(),
                doc.getScoreIaEm(),
                doc.getConcordaIa(),
                doc.getMotivoDiscordanciaIa()
        );
    }

    private static boolean nomeValido(String nome) {
        return nome != null && !nome.isBlank();
    }
}
