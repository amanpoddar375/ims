package com.postanything.service;

import com.postanything.dto.AuthResponse;
import com.postanything.dto.LoginRequest;
import com.postanything.dto.RegisterRequest;
import com.postanything.entity.User;
import com.postanything.enums.Role;
import com.postanything.exception.BadRequestException;
import com.postanything.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public AuthResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    User user = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null);
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new BadRequestException("Username already exists");
    }
    if (userRepository.existsByEmail(request.email())) {
      throw new BadRequestException("Email already exists");
    }
    LocalDateTime now = LocalDateTime.now();
    User user = User.builder()
        .username(request.username())
        .password(passwordEncoder.encode(request.password()))
        .email(request.email())
        .role(Role.USER)
        .enabled(true)
        .createdAt(now)
        .updatedAt(now)
        .build();
    userRepository.save(user);
    return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null);
  }
}
