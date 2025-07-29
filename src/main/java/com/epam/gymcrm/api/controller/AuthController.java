package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.ChangePasswordRequest;
import com.epam.gymcrm.api.payload.request.LoginRequest;
import com.epam.gymcrm.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
