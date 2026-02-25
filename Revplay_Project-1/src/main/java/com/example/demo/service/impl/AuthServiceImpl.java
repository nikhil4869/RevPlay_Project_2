package com.example.demo.service.impl;

import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.validation.AgeValidator;
import com.example.demo.validation.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.config.JwtUtil;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.dto.auth.ForgotPasswordRequest;



@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
          this.userRepository = userRepository;
          this.roleRepository = roleRepository;
          this.passwordEncoder = passwordEncoder;
          this.jwtUtil = jwtUtil;
}


    @Override
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (!PasswordValidator.isStrong(request.getPassword())) {
            throw new BadRequestException("Password not strong enough");
        }

        if (!AgeValidator.isAdult(request.getDateOfBirth())) {
            throw new BadRequestException("User must be at least 13 years old");
        }

        Role role = roleRepository
                .findByNameIgnoreCase(request.getRole())
                .orElseThrow(() -> new BadRequestException("Invalid role"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully";
    }
    
    @Override
    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is deactivated. Use forgot password to reactivate.");
        }


        return jwtUtil.generateToken(user.getEmail());
    }
    
    @Override
    public void resetPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!user.getDateOfBirth().toString()
                .equals(request.getDateOfBirth())) {
            throw new BadRequestException("Invalid date of birth");
        }

        if (!PasswordValidator.isStrong(request.getNewPassword())) {
            throw new BadRequestException("Password not strong enough");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setEnabled(true);

        userRepository.save(user);
    }



}
