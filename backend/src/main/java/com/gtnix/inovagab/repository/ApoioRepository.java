package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.ApoioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApoioRepository extends MongoRepository<ApoioDocument, String> {

    boolean existsByIdeiaIdAndUserId(String ideiaId, String userId);
}
