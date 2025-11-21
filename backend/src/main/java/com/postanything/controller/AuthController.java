package com.postanything.controller;

import com.postanything.dto.ApiResponse;
import com.postanything.dto.AuthResponse;
import com.postanything.dto.LoginRequest;
import com.postanything.dto.RegisterRequest;
import com.postanything.entity.User;
import com.postanything.service.AuthService;
import com.postanything.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);
    return ResponseEntity.ok(ApiResponse.success(response, "Registered successfully"));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<AuthResponse>> me(Authentication authentication) {
    User user = userService.getCurrentUser(authentication);
    AuthResponse response = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null);
    return ResponseEntity.ok(ApiResponse.success(response, "User info"));
  }
}
