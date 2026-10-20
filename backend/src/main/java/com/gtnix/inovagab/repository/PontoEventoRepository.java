package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.PontoEventoDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import com.gtnix.inovagab.domain.TipoEvento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface PontoEventoRepository extends MongoRepository<PontoEventoDocument, String> {

    boolean existsByChave(String chave);

    List<PontoEventoDocument> findByUserIdOrderByCriadoEmDesc(String userId);

    List<PontoEventoDocument> findByUserIdAndVistoFalseOrderByCriadoEmDesc(String userId);

    List<PontoEventoDocument> findByTemporadaIdAndPerfil(String temporadaId, PerfilUsuario perfil);

    List<PontoEventoDocument> findByPerfil(PerfilUsuario perfil);

    List<PontoEventoDocument> findByRefIdAndEstornadoFalse(String refId);

    long countByUserIdAndTipoAndCriadoEmAfter(String userId, TipoEvento tipo, Instant desde);
}
