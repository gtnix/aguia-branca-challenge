package com.gtnix.inovagab.controller;

import com.gtnix.inovagab.dto.auth.LoginRequest;
import com.gtnix.inovagab.dto.auth.LoginResponse;
import com.gtnix.inovagab.dto.auth.UsuarioResponse;
import com.gtnix.inovagab.security.SecurityUtils;
import com.gtnix.inovagab.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse me() {
        return authService.me(SecurityUtils.currentUser());
    }
}
