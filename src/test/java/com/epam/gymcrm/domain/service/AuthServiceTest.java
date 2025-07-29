package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.payload.request.LoginRequest;
import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private static final String USERNAME = "ali.veli";
    private static final String PASSWORD = "sifrem123";

    @Test
    void login_shouldSucceed_whenCredentialsCorrectAndActive() {
        // arrange
        UserEntity userEntity = new UserEntity("Ali", "Veli", USERNAME, PASSWORD, true);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(userEntity));

        // act & assert
        assertDoesNotThrow(() -> authService.login(new LoginRequest(USERNAME, PASSWORD)));
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void login_shouldFail_whenUserNotFound() {
        // arrange
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // act & assert
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                authService.login(new LoginRequest(USERNAME, PASSWORD))
        );
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void login_shouldFail_whenPasswordIsWrong() {
        // arrange
        UserEntity userEntity = new UserEntity("Ali", "Veli", USERNAME, PASSWORD, true);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(userEntity));

        // act & assert
        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                authService.login(new LoginRequest(USERNAME, "wrong-pass"))
        );
        assertTrue(ex.getMessage().contains("Invalid credentials"));
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void login_shouldFail_whenUserIsNotActive() {
        // arrange
        UserEntity userEntity = new UserEntity("Ali", "Veli", USERNAME, PASSWORD, false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(userEntity));

        // act & assert
        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                authService.login(new LoginRequest(USERNAME, PASSWORD))
        );
        assertTrue(ex.getMessage().contains("not active"));
        verify(userRepository).findByUsername(USERNAME);
    }
}