package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.ProjetoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjetoRepository extends MongoRepository<ProjetoDocument, String> {

    List<ProjetoDocument> findByAtivoTrue();

    Optional<ProjetoDocument> findByIdAndAtivoTrue(String id);

    Optional<ProjetoDocument> findByIdeiaIdAndAtivoTrue(String ideiaId);

    List<ProjetoDocument> findByOrientacaoIdAndAtivoTrue(String orientacaoId);

    List<ProjetoDocument> findByResponsavelUserIdAndAtivoTrue(String responsavelUserId);
}
