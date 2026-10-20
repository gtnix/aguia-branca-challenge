package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.OrientacaoDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.dto.orientacao.EncerramentoCampanhaResponse;
import com.gtnix.inovagab.dto.orientacao.OrientacaoRequest;
import com.gtnix.inovagab.dto.orientacao.OrientacaoResponse;
import com.gtnix.inovagab.repository.OrientacaoRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrientacaoService {

    private final OrientacaoRepository orientacaoRepository;
    private final GamificacaoService gamificacaoService;
    private final UsuarioRepository usuarioRepository;
    private final Clock clock;

    public OrientacaoService(
            OrientacaoRepository orientacaoRepository,
            GamificacaoService gamificacaoService,
            UsuarioRepository usuarioRepository,
            Clock clock
    ) {
        this.orientacaoRepository = orientacaoRepository;
        this.gamificacaoService = gamificacaoService;
        this.usuarioRepository = usuarioRepository;
        this.clock = clock;
    }

    public List<OrientacaoResponse> listar(boolean apenasAtivas) {
        List<OrientacaoDocument> docs = apenasAtivas
                ? orientacaoRepository.findByAtivoTrue()
                : orientacaoRepository.findAll();
        return docs.stream().map(this::toResponse).toList();
    }

    public OrientacaoResponse obter(String id, UsuarioPrincipal principal) {
        OrientacaoDocument doc = orientacaoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));
        if (!doc.isAtivo() && principal.getPerfil() != PerfilUsuario.LIDER) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada");
        }
        return toResponse(doc);
    }

    public OrientacaoResponse criar(OrientacaoRequest request, UsuarioPrincipal principal) {
        OrientacaoDocument doc = new OrientacaoDocument();
        applyRequest(doc, request);
        doc.setCriadoPorUserId(principal.getUserId());
        Instant now = Instant.now();
        doc.setDataCriacao(now);
        doc.setDataAtualizacao(now);
        return toResponse(orientacaoRepository.save(doc));
    }

    public OrientacaoResponse atualizar(String id, OrientacaoRequest request) {
        OrientacaoDocument doc = orientacaoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));
        applyRequest(doc, request);
        doc.setDataAtualizacao(Instant.now());
        return toResponse(orientacaoRepository.save(doc));
    }

    public OrientacaoResponse definirAtivo(String id, boolean ativo) {
        OrientacaoDocument doc = orientacaoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));
        if (ativo) {
            doc.setAtivo(true);
            doc.setDeletedAt(null);
        } else {
            doc.setAtivo(false);
        }
        doc.setDataAtualizacao(Instant.now());
        return toResponse(orientacaoRepository.save(doc));
    }

    public void excluir(String id) {
        OrientacaoDocument doc = orientacaoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));
        if (doc.getDeletedAt() != null) {
            return;
        }
        doc.marcarExcluido();
        doc.setDataAtualizacao(Instant.now());
        orientacaoRepository.save(doc);
    }

    public EncerramentoCampanhaResponse encerrarCampanha(String id) {
        OrientacaoDocument doc = orientacaoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Orientação não encontrada"));
        if (doc.getCampanhaFim() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Orientação não tem campanha patrocinada");
        }
        if (doc.getCampanhaEncerradaEm() != null) {
            throw new ApiException(HttpStatus.CONFLICT, "Campanha já encerrada");
        }
        List<EncerramentoCampanhaResponse.Vencedor> vencedores = gamificacaoService.encerrarCampanha(doc).stream()
                .map(v -> new EncerramentoCampanhaResponse.Vencedor(
                        v.userId(),
                        usuarioRepository.findById(v.userId()).map(UsuarioDocument::getNome).orElse(v.userId()),
                        v.xpCampanha(),
                        v.bonusAsas()))
                .toList();
        Instant agora = clock.instant();
        doc.setCampanhaEncerradaEm(agora);
        doc.setDataAtualizacao(agora);
        orientacaoRepository.save(doc);
        return new EncerramentoCampanhaResponse(doc.getId(), doc.getCampanha(), agora, vencedores);
    }

    public OrientacaoDocument requireAtiva(String orientacaoId) {
        return orientacaoRepository.findByIdAndAtivoTrue(orientacaoId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Orientação estratégica inválida ou inativa"));
    }

    private void applyRequest(OrientacaoDocument doc, OrientacaoRequest request) {
        doc.setTitulo(request.titulo());
        doc.setDescricao(request.descricao());
        doc.setCategoria(request.categoria());
        doc.setCampanha(request.campanha());
        doc.setCampanhaFim(request.campanhaFim());
        doc.setCampanhaBonusAsas(request.campanhaFim() != null ? request.campanhaBonusAsas() : null);
        doc.setCampanhaTopN(request.campanhaFim() != null ? request.campanhaTopN() : null);
        doc.setDivisao(request.divisao());
    }

    private OrientacaoResponse toResponse(OrientacaoDocument doc) {
        return new OrientacaoResponse(
                doc.getId(),
                doc.getTitulo(),
                doc.getDescricao(),
                doc.getCategoria(),
                doc.getCampanha(),
                doc.getDataCriacao(),
                doc.getDataAtualizacao(),
                doc.isAtivo(),
                doc.getCriadoPorUserId(),
                doc.getCampanhaFim(),
                doc.getCampanhaBonusAsas(),
                doc.getCampanhaTopN(),
                doc.getCampanhaEncerradaEm(),
                doc.isCampanhaAtiva(LocalDate.now(clock)),
                doc.getDivisao()
        );
    }
}
