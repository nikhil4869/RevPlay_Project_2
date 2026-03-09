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

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

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

          logger.info("AuthServiceImpl initialized");
}

    @Override
    public String register(RegisterRequest request) {

        logger.info("Register request received for email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {

            logger.warn("Registration attempt with existing email={}", request.getEmail());

            throw new DuplicateResourceException("Email already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {

            logger.warn("Password mismatch during registration for email={}", request.getEmail());

            throw new BadRequestException("Passwords do not match");
        }

        if (!PasswordValidator.isStrong(request.getPassword())) {

            logger.warn("Weak password attempt during registration for email={}", request.getEmail());

            throw new BadRequestException("Password not strong enough");
        }

        if (!AgeValidator.isNotFutureDate(request.getDateOfBirth())) {
            logger.warn("Invalid date of birth attempt (current/future) for email={}", request.getEmail());
            throw new BadRequestException("date is not valid");
        }

        if (!AgeValidator.isAdult(request.getDateOfBirth())) {
            logger.warn("Underage registration attempt for email={}", request.getEmail());
            throw new BadRequestException("age should not less than 13");
        }

        Role role = roleRepository
                .findByNameIgnoreCase(request.getRole())
                .orElseThrow(() -> new BadRequestException("Invalid role"));

        logger.debug("Role found for registration role={}", role.getName());

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(role);

        userRepository.save(user);

        logger.info("User registered successfully email={}", request.getEmail());

        return "User registered successfully";
    }

    @Override
    public AuthResponse login(String email, String password) {

        logger.info("Login attempt for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found email={}", email);
                    return new UnauthorizedException("Email not registered");
                });

        if (!user.isEnabled()) {

            logger.warn("Login attempt for disabled account email={}", email);

            throw new UnauthorizedException(
                    "Account is deactivated. Use forgot password to reactivate."
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {

            logger.warn("Invalid password attempt for email={}", email);

            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );

        logger.info("Login successful email={} role={}", email, user.getRole().getName());

        return new AuthResponse(token, user.getRole().getName());
    }

    @Override
    public void resetPassword(ForgotPasswordRequest request) {

        logger.info("Password reset requested for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {

                    logger.warn("Password reset failed - user not found email={}", request.getEmail());

                    return new BadRequestException("User not found");
                });

        if (!user.getDateOfBirth().toString()
                .equals(request.getDateOfBirth())) {

            logger.warn("Password reset failed due to DOB mismatch email={}", request.getEmail());

            throw new BadRequestException("Invalid date of birth");
        }

        if (!PasswordValidator.isStrong(request.getNewPassword())) {

            logger.warn("Weak password during reset for email={}", request.getEmail());

            throw new BadRequestException("Password not strong enough");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setEnabled(true);

        userRepository.save(user);

        logger.info("Password reset successful and account activated email={}", request.getEmail());
    }

}