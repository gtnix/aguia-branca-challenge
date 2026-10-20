package com.gtnix.inovagab.repository;

import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.domain.PerfilUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<UsuarioDocument, String> {

    Optional<UsuarioDocument> findByEmailAndAtivoTrue(String email);

    boolean existsByEmail(String email);

    List<UsuarioDocument> findByPerfilAndAtivoTrue(PerfilUsuario perfil);
}
