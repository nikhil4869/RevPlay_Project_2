package com.example.demo.controller;

import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.auth.AuthRequest;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ForgotPasswordRequest;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        String token = authService.login(
                request.getEmail(),
                request.getPassword());

        return new AuthResponse(token);
    }
    
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.resetPassword(request);
        return "Password reset successful. You can login now.";
    }


}
