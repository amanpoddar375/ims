package com.postanything.service;

import com.postanything.entity.User;
import com.postanything.enums.Role;
import com.postanything.exception.NotFoundException;
import com.postanything.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getCurrentUser(Authentication authentication) {
    return findByUsername(authentication.getName());
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("User not found"));
  }

  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found"));
  }

  public boolean isAdmin(User user) {
    return user.getRole() == Role.ADMIN;
  }

  public List<User> all() {
    return userRepository.findAll();
  }
}
