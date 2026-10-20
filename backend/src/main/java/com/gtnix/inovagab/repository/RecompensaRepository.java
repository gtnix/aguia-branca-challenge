package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.RecompensaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RecompensaRepository extends MongoRepository<RecompensaDocument, String> {

    List<RecompensaDocument> findByAtivoTrueOrderByCustoAsasAsc();

    Optional<RecompensaDocument> findByIdAndAtivoTrue(String id);
}
