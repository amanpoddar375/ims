package com.postanything.controller;

import com.postanything.dto.ApiResponse;
import com.postanything.dto.PageResponse;
import com.postanything.dto.PostRequest;
import com.postanything.dto.PostResponse;
import com.postanything.dto.StatusUpdateRequest;
import com.postanything.entity.User;
import com.postanything.service.PostService;
import com.postanything.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> list(
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    User currentUser = userService.getCurrentUser(authentication);
    PageResponse<PostResponse> response = postService.list(currentUser, page, size);
    return ResponseEntity.ok(ApiResponse.success(response, "Posts fetched"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PostResponse>> get(@PathVariable Long id, Authentication authentication) {
    User user = userService.getCurrentUser(authentication);
    PostResponse response = postService.get(id, user);
    return ResponseEntity.ok(ApiResponse.success(response, "Post fetched"));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PostResponse>> create(@Valid @RequestBody PostRequest request, Authentication authentication) {
    User user = userService.getCurrentUser(authentication);
    PostResponse response = postService.create(request, user);
    return ResponseEntity.ok(ApiResponse.success(response, "Post created"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<PostResponse>> update(@PathVariable Long id, @Valid @RequestBody PostRequest request, Authentication authentication) {
    User user = userService.getCurrentUser(authentication);
    PostResponse response = postService.update(id, request, user);
    return ResponseEntity.ok(ApiResponse.success(response, "Post updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication authentication) {
    User user = userService.getCurrentUser(authentication);
    postService.delete(id, user);
    return ResponseEntity.ok(ApiResponse.success(null, "Post deleted"));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<PostResponse>> updateStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody StatusUpdateRequest request,
                                                                 Authentication authentication) {
    User user = userService.getCurrentUser(authentication);
    PostResponse response = postService.updateStatus(id, request, user);
    return ResponseEntity.ok(ApiResponse.success(response, "Status updated"));
  }
}
