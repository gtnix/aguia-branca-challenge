package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.OrientacaoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrientacaoRepository extends MongoRepository<OrientacaoDocument, String> {

    List<OrientacaoDocument> findByAtivoTrue();

    Optional<OrientacaoDocument> findByIdAndAtivoTrue(String id);

    List<OrientacaoDocument> findByCriadoPorUserId(String criadoPorUserId);
}
