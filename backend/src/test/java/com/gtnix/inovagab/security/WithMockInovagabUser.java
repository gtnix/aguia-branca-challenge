package com.gtnix.inovagab.security;

import com.gtnix.inovagab.domain.PerfilUsuario;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockInovagabUserSecurityContextFactory.class)
public @interface WithMockInovagabUser {
    PerfilUsuario perfil() default PerfilUsuario.LIDER;

    String userId() default "user-lider";

    String email() default "marcos.silva@aguiabranca.com.br";
}
