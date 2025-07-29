package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.LoginRequest;
import com.epam.gymcrm.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(@Valid @ModelAttribute LoginRequest loginRequest) {
        authService.login(loginRequest);
        return ResponseEntity.ok().build();
    }
}
