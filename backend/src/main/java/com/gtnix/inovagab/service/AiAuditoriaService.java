package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.AiScoreRunDocument;
import com.gtnix.inovagab.repository.AiScoreRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class AiAuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AiAuditoriaService.class);

    static final int MAX_SAIDA = 4000;

    private final AiScoreRunRepository repository;

    public AiAuditoriaService(AiScoreRunRepository repository) {
        this.repository = repository;
    }

    /**
     * Guarda o hash do prompt, não o prompt: o texto do colaborador já está na ideia e não precisa ser duplicado.
     * Falha de auditoria nunca derruba a triagem.
     */
    public void registrar(Execucao execucao) {
        try {
            AiScoreRunDocument doc = new AiScoreRunDocument();
            doc.setTipo(execucao.tipo());
            doc.setIdeiaId(execucao.ideiaId());
            doc.setOrientacaoId(execucao.orientacaoId());
            doc.setGestorUserId(execucao.gestorUserId());
            doc.setModelo(execucao.modelo());
            doc.setPromptVersao(execucao.promptVersao());
            doc.setInputHash(execucao.prompt() == null ? null : sha256(execucao.prompt()));
            doc.setSaidaBruta(truncar(execucao.saidaBruta()));
            doc.setLatenciaMs(execucao.latenciaMs());
            doc.setFallback(execucao.fallback());
            doc.setErro(truncar(execucao.erro()));
            doc.setCriadoEm(Instant.now());
            repository.save(doc);
        } catch (Exception ex) {
            log.warn("Falha ao registrar auditoria de IA: {}", ex.getMessage());
        }
    }

    static String sha256(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static String truncar(String texto) {
        if (texto == null || texto.length() <= MAX_SAIDA) {
            return texto;
        }
        return texto.substring(0, MAX_SAIDA);
    }

    public record Execucao(
            String tipo,
            String ideiaId,
            String orientacaoId,
            String gestorUserId,
            String modelo,
            String promptVersao,
            String prompt,
            String saidaBruta,
            long latenciaMs,
            boolean fallback,
            String erro
    ) {
    }
}
