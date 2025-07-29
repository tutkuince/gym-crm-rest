package com.epam.gymcrm.api.controller;

import com.epam.gymcrm.api.payload.request.LoginRequest;
import com.epam.gymcrm.domain.service.AuthService;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.GlobalExceptionHandler;
import com.epam.gymcrm.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_shouldReturn200_whenLoginSuccessful() throws Exception {
        doNothing().when(authService).login(any(LoginRequest.class));

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "ali.veli")
                        .param("password", "pass1123")
                )
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturn404_whenUserNotFound() throws Exception {
        doThrow(new NotFoundException("User not found"))
                .when(authService)
                .login(any(LoginRequest.class));

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "notfound")
                        .param("password", "pass1123")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void login_shouldReturn400_whenCredentialsInvalid() throws Exception {
        doThrow(new BadRequestException("Invalid credentials"))
                .when(authService)
                .login(any(LoginRequest.class));

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "ali.veli")
                        .param("password", "wrong-pass")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void login_shouldReturn400_whenUserNotActive() throws Exception {
        doThrow(new BadRequestException("User is not active"))
                .when(authService)
                .login(any(LoginRequest.class));

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "ali.veli")
                        .param("password", "pass1123")
                )
                .andExpect(status().isBadRequest());
    }
}