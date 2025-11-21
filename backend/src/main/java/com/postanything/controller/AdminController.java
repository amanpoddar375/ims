package com.postanything.controller;

import com.postanything.dto.ApiResponse;
import com.postanything.dto.PageResponse;
import com.postanything.dto.PostResponse;
import com.postanything.entity.User;
import com.postanything.service.PostService;
import com.postanything.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final PostService postService;
  private final UserService userService;

  @GetMapping("/posts")
  public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> posts(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size,
                                                                        Authentication authentication) {
    User admin = userService.getCurrentUser(authentication);
    PageResponse<PostResponse> response = postService.list(admin, page, size);
    return ResponseEntity.ok(ApiResponse.success(response, "All posts"));
  }

  @GetMapping("/users")
  public ResponseEntity<ApiResponse<?>> users() {
    return ResponseEntity.ok(ApiResponse.success(userService.all(), "Users fetched"));
  }

  @PatchMapping("/posts/{id}/assign")
  public ResponseEntity<ApiResponse<PostResponse>> assign(@PathVariable Long id, @RequestParam Long assigneeId, Authentication authentication) {
    User admin = userService.getCurrentUser(authentication);
    PostResponse response = postService.assign(id, assigneeId, admin);
    return ResponseEntity.ok(ApiResponse.success(response, "Post assigned"));
  }

  @GetMapping("/stats")
  public ResponseEntity<ApiResponse<?>> stats() {
    return ResponseEntity.ok(ApiResponse.success(postService.stats(), "Stats"));
  }
}
