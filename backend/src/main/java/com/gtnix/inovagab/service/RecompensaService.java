package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.RecompensaDocument;
import com.gtnix.inovagab.document.ResgateDocument;
import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.NivelGamificacao;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.StatusResgate;
import com.gtnix.inovagab.domain.TipoEvento;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.DecisaoResgateRequest;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.RecompensaRequest;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.RecompensaResponse;
import com.gtnix.inovagab.dto.gamificacao.RecompensaDtos.ResgateResponse;
import com.gtnix.inovagab.repository.RecompensaRepository;
import com.gtnix.inovagab.repository.ResgateRepository;
import com.gtnix.inovagab.repository.UsuarioRepository;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import com.gtnix.inovagab.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final ResgateRepository resgateRepository;
    private final UsuarioRepository usuarioRepository;
    private final GamificacaoService gamificacaoService;
    private final Clock clock;

    public RecompensaService(
            RecompensaRepository recompensaRepository,
            ResgateRepository resgateRepository,
            UsuarioRepository usuarioRepository,
            GamificacaoService gamificacaoService,
            Clock clock
    ) {
        this.recompensaRepository = recompensaRepository;
        this.resgateRepository = resgateRepository;
        this.usuarioRepository = usuarioRepository;
        this.gamificacaoService = gamificacaoService;
        this.clock = clock;
    }

    // ---------------------------------------------------------------------
    // Catálogo
    // ---------------------------------------------------------------------

    public List<RecompensaResponse> listar(UsuarioPrincipal principal, boolean incluirInativas) {
        List<RecompensaDocument> docs = incluirInativas && principal.getPerfil() == PerfilUsuario.LIDER
                ? recompensaRepository.findAll()
                : recompensaRepository.findByAtivoTrueOrderByCustoAsasAsc();
        int saldo = gamificacaoService.saldoAsas(principal.getUserId());
        NivelGamificacao nivel = NivelGamificacao.fromXp(gamificacaoService.xpTotal(principal.getUserId()));
        return docs.stream().map(d -> toResponse(d, saldo, nivel)).toList();
    }

    public RecompensaResponse criar(RecompensaRequest request) {
        RecompensaDocument doc = new RecompensaDocument();
        aplicar(doc, request);
        Instant agora = clock.instant();
        doc.setDataCriacao(agora);
        doc.setDataAtualizacao(agora);
        return toResponse(recompensaRepository.save(doc), 0, NivelGamificacao.INICIANTE);
    }

    public RecompensaResponse atualizar(String id, RecompensaRequest request) {
        RecompensaDocument doc = recompensaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recompensa não encontrada"));
        aplicar(doc, request);
        doc.setDataAtualizacao(clock.instant());
        return toResponse(recompensaRepository.save(doc), 0, NivelGamificacao.INICIANTE);
    }

    private static void aplicar(RecompensaDocument doc, RecompensaRequest request) {
        doc.setTitulo(request.titulo().trim());
        doc.setDescricao(request.descricao());
        doc.setCategoria(request.categoria().trim());
        doc.setParceiro(request.parceiro());
        doc.setCustoAsas(request.custoAsas());
        doc.setEstoque(request.estoque());
        doc.setNivelMinimo(request.nivelMinimo() != null ? request.nivelMinimo() : NivelGamificacao.INICIANTE);
        if (request.ativo() != null) {
            doc.setAtivo(request.ativo());
            if (request.ativo()) {
                doc.setDeletedAt(null);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Resgates
    // ---------------------------------------------------------------------

    /**
     * Debita as Asas na hora (evento RESGATE negativo), reserva o estoque e entra já APROVADO:
     * saldo, estoque e nível são a regra pública. Sincronizado para dois pedidos simultâneos
     * não gastarem o mesmo saldo nem a última unidade.
     */
    public synchronized ResgateResponse solicitar(String recompensaId, UsuarioPrincipal principal) {
        RecompensaDocument recompensa = recompensaRepository.findByIdAndAtivoTrue(recompensaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recompensa não encontrada"));
        int saldo = gamificacaoService.saldoAsas(principal.getUserId());
        NivelGamificacao nivel = NivelGamificacao.fromXp(gamificacaoService.xpTotal(principal.getUserId()));
        String motivo = motivoBloqueio(recompensa, saldo, nivel);
        if (motivo != null) {
            throw new ApiException(HttpStatus.CONFLICT, motivo);
        }

        UsuarioDocument usuario = usuarioRepository.findById(principal.getUserId()).orElse(principal.getUsuario());
        Instant agora = clock.instant();
        ResgateDocument resgate = new ResgateDocument();
        resgate.setRecompensaId(recompensa.getId());
        resgate.setRecompensaTitulo(recompensa.getTitulo());
        resgate.setUserId(usuario.getId());
        resgate.setUserNome(usuario.getNome());
        resgate.setCustoAsas(recompensa.getCustoAsas());
        resgate.setStatus(StatusResgate.APROVADO);
        resgate.setSolicitadoEm(agora);
        resgate.setDecididoEm(agora);
        resgate = resgateRepository.save(resgate);

        recompensa.setEstoque(recompensa.getEstoque() - 1);
        recompensa.setDataAtualizacao(clock.instant());
        recompensaRepository.save(recompensa);

        gamificacaoService.registrar(new GamificacaoService.Lancamento(
                "RESGATE:" + resgate.getId() + ":" + usuario.getId(),
                usuario.getId(),
                TipoEvento.RESGATE,
                0,
                -recompensa.getCustoAsas(),
                resgate.getId(),
                recompensa.getCategoria(),
                "Resgate: " + recompensa.getTitulo()));
        return toResponse(resgate);
    }

    public List<ResgateResponse> meus(UsuarioPrincipal principal) {
        return resgateRepository.findByUserIdOrderBySolicitadoEmDesc(principal.getUserId()).stream()
                .map(RecompensaService::toResponse)
                .toList();
    }

    public List<ResgateResponse> listar(StatusResgate status) {
        List<ResgateDocument> docs = status != null
                ? resgateRepository.findByStatusOrderBySolicitadoEmAsc(status)
                : resgateRepository.findAllByOrderBySolicitadoEmDesc();
        return docs.stream().map(RecompensaService::toResponse).toList();
    }

    public synchronized ResgateResponse decidir(String id, DecisaoResgateRequest request, UsuarioPrincipal principal) {
        ResgateDocument resgate = resgateRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resgate não encontrado"));
        if (principal.getUserId().equals(resgate.getUserId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Não é possível decidir o próprio resgate");
        }
        StatusResgate novo = request.status();
        if (novo == StatusResgate.NEGADO || !resgate.getStatus().canTransitionTo(novo)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Transição inválida: " + resgate.getStatus() + " → " + novo);
        }
        if (novo != StatusResgate.ENTREGUE && novo != StatusResgate.APROVADO) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Transição inválida: " + resgate.getStatus() + " → " + novo);
        }

        Instant agora = clock.instant();
        resgate.setStatus(novo);
        String observacao = request.observacao() != null ? request.observacao().trim() : null;
        if (observacao != null && !observacao.isEmpty()) {
            resgate.setObservacao(observacao);
        }
        if (novo == StatusResgate.ENTREGUE) {
            resgate.setEntregueEm(agora);
            resgate.setAprovadorUserId(principal.getUserId());
        } else {
            resgate.setAprovadorUserId(principal.getUserId());
            resgate.setDecididoEm(agora);
        }
        return toResponse(resgateRepository.save(resgate));
    }

    // ---------------------------------------------------------------------
    // Mapeamento
    // ---------------------------------------------------------------------

    static String motivoBloqueio(RecompensaDocument r, int saldo, NivelGamificacao nivel) {
        if (r.getEstoque() <= 0) {
            return "Esgotado";
        }
        NivelGamificacao minimo = r.getNivelMinimo() != null ? r.getNivelMinimo() : NivelGamificacao.INICIANTE;
        if (!nivel.atinge(minimo)) {
            return "Disponível a partir do nível " + minimo.getLabel();
        }
        if (saldo < r.getCustoAsas()) {
            return "Faltam " + (r.getCustoAsas() - saldo) + " Asas";
        }
        return null;
    }

    private static RecompensaResponse toResponse(RecompensaDocument d, int saldo, NivelGamificacao nivel) {
        NivelGamificacao minimo = d.getNivelMinimo() != null ? d.getNivelMinimo() : NivelGamificacao.INICIANTE;
        String motivo = d.isAtivo() ? motivoBloqueio(d, saldo, nivel) : "Indisponível";
        return new RecompensaResponse(d.getId(), d.getTitulo(), d.getDescricao(), d.getCategoria(), d.getParceiro(),
                d.getCustoAsas(), d.getEstoque(), minimo.name(), minimo.getLabel(), d.isAtivo(), motivo == null, motivo);
    }

    static ResgateResponse toResponse(ResgateDocument r) {
        return new ResgateResponse(r.getId(), r.getRecompensaId(), r.getRecompensaTitulo(), r.getUserId(),
                r.getUserNome(), r.getCustoAsas(), r.getStatus(), r.getObservacao(), r.getAprovadorUserId(),
                r.getSolicitadoEm(), r.getDecididoEm(), r.getEntregueEm());
    }
}
