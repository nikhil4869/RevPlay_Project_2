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
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.ForgotPasswordRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);
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

        logger.debug("Registration attempt for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed - Email already registered: {}", request.getEmail());
            throw new DuplicateResourceException("Email already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.warn("Registration failed - Passwords do not match for email: {}", request.getEmail());
            throw new BadRequestException("Passwords do not match");
        }

        if (!PasswordValidator.isStrong(request.getPassword())) {
            logger.warn("Registration failed - Weak password for email: {}", request.getEmail());
            throw new BadRequestException("Password not strong enough");
        }

        if (!AgeValidator.isAdult(request.getDateOfBirth())) {
            logger.warn("Registration failed - Underage user attempt: {}", request.getEmail());
            throw new BadRequestException("User must be at least 13 years old");
        }

        Role role = roleRepository
                .findByNameIgnoreCase(request.getRole())
                .orElseThrow(() -> {
                    logger.warn("Registration failed - Invalid role: {}", request.getRole());
                    return new BadRequestException("Invalid role");
                });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(role);

        userRepository.save(user);

        logger.info("User registered successfully. Email: {}, Role: {}",
                request.getEmail(), role.getName());

        return "User registered successfully";
    }
    
    @Override
    public AuthResponse login(String email, String password) {

        logger.debug("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - Invalid credentials for email: {}", email);
                    return new UnauthorizedException("Invalid credentials");
                });

        if (!user.isEnabled()) {
            logger.warn("Login attempt on disabled account. Email: {}", email);
            throw new UnauthorizedException(
                    "Account is deactivated. Use forgot password to reactivate."
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed - Incorrect password for email: {}", email);
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );

        logger.info("Login successful. Email: {}, Role: {}",
                email, user.getRole().getName());

        return new AuthResponse(token, user.getRole().getName());
    }
    @Override
    public void resetPassword(ForgotPasswordRequest request) {

        logger.debug("Password reset attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Password reset failed - User not found: {}", request.getEmail());
                    return new BadRequestException("User not found");
                });

        if (!user.getDateOfBirth().toString()
                .equals(request.getDateOfBirth())) {
            logger.warn("Password reset failed - Invalid DOB for email: {}", request.getEmail());
            throw new BadRequestException("Invalid date of birth");
        }

        if (!PasswordValidator.isStrong(request.getNewPassword())) {
            logger.warn("Password reset failed - Weak password for email: {}", request.getEmail());
            throw new BadRequestException("Password not strong enough");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setEnabled(true);

        userRepository.save(user);

        logger.info("Password reset successful. Account reactivated. Email: {}",
                request.getEmail());
    }



}
