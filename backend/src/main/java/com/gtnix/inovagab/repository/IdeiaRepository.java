package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.IdeiaDocument;
import com.gtnix.inovagab.domain.StatusIdeia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IdeiaRepository extends MongoRepository<IdeiaDocument, String> {

    List<IdeiaDocument> findByAtivoTrue();

    List<IdeiaDocument> findByAutorUserIdAndAtivoTrue(String autorUserId);

    Optional<IdeiaDocument> findByIdAndAtivoTrue(String id);

    List<IdeiaDocument> findByAtivoTrueAndStatus(StatusIdeia status);

    List<IdeiaDocument> findByAtivoTrueAndArea(String area);

    List<IdeiaDocument> findByAtivoTrueAndOrientacaoId(String orientacaoId);
}
