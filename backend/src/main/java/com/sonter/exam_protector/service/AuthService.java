package com.sonter.exam_protector.service;

import com.sonter.exam_protector.dto.request.LoginRequest;
import com.sonter.exam_protector.dto.request.RegisterRequest;
import com.sonter.exam_protector.dto.response.AuthResponse;
import com.sonter.exam_protector.model.User;
import com.sonter.exam_protector.model.enums.Role;
import com.sonter.exam_protector.repository.UserRepository;
import com.sonter.exam_protector.security.JwtProvider;
import com.sonter.exam_protector.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFullName(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        );
    }

    @Transactional
    public User registerUser(RegisterRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        
        if (signUpRequest.getRole() != null) {
            user.setRole(Role.valueOf(signUpRequest.getRole()));
        } else {
            user.setRole(Role.STUDENT);
        }

        return userRepository.save(user);
    }
}
