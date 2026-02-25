package com.example.demo.service;

import com.example.demo.dto.auth.ForgotPasswordRequest;
import com.example.demo.dto.auth.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String login(String email, String password);
    void resetPassword(ForgotPasswordRequest request);


}
