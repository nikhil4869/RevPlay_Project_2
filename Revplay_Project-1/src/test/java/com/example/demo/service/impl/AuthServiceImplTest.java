package com.example.demo.service.impl;


import com.example.demo.config.JwtUtil;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ForgotPasswordRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.validation.AgeValidator;
import com.example.demo.validation.PasswordValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private Role role;
    private User user;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Strong@123");
        registerRequest.setConfirmPassword("Strong@123");
        registerRequest.setRole("USER");
        registerRequest.setDateOfBirth(LocalDate.of(2000, 1, 1));

        role = new Role();
        role.setName("USER");

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPass");
        user.setEnabled(true);
        user.setRole(role);
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));
    }

    // =========================
    // REGISTER
    // =========================

    @Test
    void register_Success() {
        try (MockedStatic<PasswordValidator> passwordMock = mockStatic(PasswordValidator.class);
             MockedStatic<AgeValidator> ageMock = mockStatic(AgeValidator.class)) {

            when(userRepository.existsByEmail("test@example.com"))
                    .thenReturn(false);

            passwordMock.when(() -> PasswordValidator.isStrong("Strong@123"))
                    .thenReturn(true);

            ageMock.when(() -> AgeValidator.isNotFutureDate(registerRequest.getDateOfBirth()))
                    .thenReturn(true);

            ageMock.when(() -> AgeValidator.isAdult(registerRequest.getDateOfBirth()))
                    .thenReturn(true);

            when(roleRepository.findByNameIgnoreCase("USER"))
                    .thenReturn(Optional.of(role));

            when(passwordEncoder.encode("Strong@123"))
                    .thenReturn("encodedPass");

            String result = authService.register(registerRequest);

            assertEquals("User registered successfully", result);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void register_PasswordMismatch() {
        registerRequest.setConfirmPassword("WrongPass");

        assertThrows(BadRequestException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void register_WeakPassword() {
        try (MockedStatic<PasswordValidator> passwordMock = mockStatic(PasswordValidator.class)) {

            when(userRepository.existsByEmail("test@example.com"))
                    .thenReturn(false);

            passwordMock.when(() -> PasswordValidator.isStrong("Strong@123"))
                    .thenReturn(false);

            assertThrows(BadRequestException.class,
                    () -> authService.register(registerRequest));
        }
    }

    @Test
    void register_UnderAge() {
        try (MockedStatic<PasswordValidator> passwordMock = mockStatic(PasswordValidator.class);
             MockedStatic<AgeValidator> ageMock = mockStatic(AgeValidator.class)) {

            when(userRepository.existsByEmail("test@example.com"))
                    .thenReturn(false);

            passwordMock.when(() -> PasswordValidator.isStrong("Strong@123"))
                    .thenReturn(true);

            ageMock.when(() -> AgeValidator.isNotFutureDate(registerRequest.getDateOfBirth()))
                    .thenReturn(true);

            ageMock.when(() -> AgeValidator.isAdult(registerRequest.getDateOfBirth()))
                    .thenReturn(false);

            assertThrows(BadRequestException.class,
                    () -> authService.register(registerRequest));
        }
    }

    // =========================
    // LOGIN
    // =========================

    @Test
    void login_Success() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("rawPass", "encodedPass"))
                .thenReturn(true);

        when(jwtUtil.generateToken("test@example.com", "USER"))
                .thenReturn("mockToken");

        AuthResponse response = authService.login("test@example.com", "rawPass");

        assertEquals("mockToken", response.getToken());
        assertEquals("USER", response.getRole());
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.login("test@example.com", "pass"));
    }

    @Test
    void login_DisabledUser() {
        user.setEnabled(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class,
                () -> authService.login("test@example.com", "pass"));
    }

    @Test
    void login_WrongPassword() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encodedPass"))
                .thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> authService.login("test@example.com", "wrong"));
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Test
    void resetPassword_Success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");
        request.setDateOfBirth("2000-01-01");
        request.setNewPassword("Strong@123");

        try (MockedStatic<PasswordValidator> passwordMock = mockStatic(PasswordValidator.class)) {

            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(user));

            passwordMock.when(() -> PasswordValidator.isStrong("Strong@123"))
                    .thenReturn(true);

            when(passwordEncoder.encode("Strong@123"))
                    .thenReturn("newEncoded");

            authService.resetPassword(request);

            verify(userRepository).save(user);
            assertTrue(user.isEnabled());
        }
    }

    @Test
    void resetPassword_UserNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> authService.resetPassword(request));
    }

    @Test
    void resetPassword_InvalidDOB() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");
        request.setDateOfBirth("1999-01-01");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> authService.resetPassword(request));
    }
}