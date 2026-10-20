package com.gtnix.inovagab.dto.auth;

import com.gtnix.inovagab.domain.PerfilUsuario;

public record UsuarioResponse(
        String id,
        String nome,
        String email,
        PerfilUsuario perfil,
        String area,
        String divisao,
        String equipe
) {
}
