package com.sonter.exam_protector.service;

import com.sonter.exam_protector.dto.request.LoginRequest;
import com.sonter.exam_protector.dto.request.RegisterRequest;
import com.sonter.exam_protector.dto.response.AuthResponse;
import com.sonter.exam_protector.model.User;
import com.sonter.exam_protector.model.enums.Role;
import com.sonter.exam_protector.repository.UserRepository;
import com.sonter.exam_protector.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.STUDENT)
                .build();

        userRepository.save(user);

        String token = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(token)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(token)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse refresh(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(token)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
