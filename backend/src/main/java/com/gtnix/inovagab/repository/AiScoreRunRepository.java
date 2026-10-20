package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.AiScoreRunDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AiScoreRunRepository extends MongoRepository<AiScoreRunDocument, String> {

    List<AiScoreRunDocument> findByTipo(String tipo);

    List<AiScoreRunDocument> findByTipoAndOrientacaoId(String tipo, String orientacaoId);
}
