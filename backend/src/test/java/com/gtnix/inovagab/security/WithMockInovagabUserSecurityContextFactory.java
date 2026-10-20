package com.gtnix.inovagab.security;

import com.gtnix.inovagab.document.UsuarioDocument;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockInovagabUserSecurityContextFactory implements WithSecurityContextFactory<WithMockInovagabUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockInovagabUser annotation) {
        UsuarioDocument usuario = new UsuarioDocument();
        usuario.setId(annotation.userId());
        usuario.setNome("Usuário teste");
        usuario.setEmail(annotation.email());
        usuario.setSenhaHash("hash");
        usuario.setPerfil(annotation.perfil());
        usuario.setAtivo(true);

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
