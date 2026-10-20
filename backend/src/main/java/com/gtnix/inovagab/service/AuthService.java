package com.gtnix.inovagab.service;

import com.gtnix.inovagab.document.UsuarioDocument;
import com.gtnix.inovagab.dto.auth.LoginRequest;
import com.gtnix.inovagab.dto.auth.LoginResponse;
import com.gtnix.inovagab.dto.auth.UsuarioResponse;
import com.gtnix.inovagab.security.JwtService;
import com.gtnix.inovagab.security.UsuarioPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        var principal = (UsuarioPrincipal) auth.getPrincipal();
        UsuarioDocument u = principal.getUsuario();
        String token = jwtService.generateToken(u.getId(), u.getEmail(), u.getPerfil());
        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                toResponse(u)
        );
    }

    public UsuarioResponse me(UsuarioPrincipal principal) {
        return toResponse(principal.getUsuario());
    }

    private UsuarioResponse toResponse(UsuarioDocument u) {
        return new UsuarioResponse(
                u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getArea(), u.getDivisao(), u.getEquipe());
    }
}
