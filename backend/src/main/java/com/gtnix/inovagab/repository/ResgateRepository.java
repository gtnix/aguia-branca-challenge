package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.ResgateDocument;
import com.gtnix.inovagab.domain.StatusResgate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResgateRepository extends MongoRepository<ResgateDocument, String> {

    List<ResgateDocument> findByUserIdOrderBySolicitadoEmDesc(String userId);

    List<ResgateDocument> findByStatusOrderBySolicitadoEmAsc(StatusResgate status);

    List<ResgateDocument> findAllByOrderBySolicitadoEmDesc();

    long countByAprovadorUserIdAndStatusIn(String aprovadorUserId, List<StatusResgate> status);
}
