package com.postanything.service;

import com.postanything.dto.PostRequest;
import com.postanything.dto.StatusUpdateRequest;
import com.postanything.entity.Post;
import com.postanything.entity.User;
import com.postanything.enums.PostStatus;
import com.postanything.enums.PostType;
import com.postanything.enums.Priority;
import com.postanything.enums.Role;
import com.postanything.exception.BadRequestException;
import com.postanything.exception.ForbiddenException;
import com.postanything.mapper.PostMapper;
import com.postanything.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

  private PostRepository postRepository;
  private PostMapper postMapper;
  private UserService userService;
  private PostService postService;
  private User user;
  private User admin;

  @BeforeEach
  void setup() {
    postRepository = mock(PostRepository.class);
    postMapper = new PostMapper();
    userService = mock(UserService.class);
    postService = new PostService(postRepository, postMapper, userService);

    user = User.builder().id(2L).username("user1").role(Role.USER).build();
    admin = User.builder().id(1L).username("admin").role(Role.ADMIN).build();
  }

  @Test
  void userCanSubmitDraft() {
    Post post = basePost(PostStatus.DRAFT, user);
    when(postRepository.findById(10L)).thenReturn(Optional.of(post));

    StatusUpdateRequest req = new StatusUpdateRequest(PostStatus.SUBMITTED, null);
    Post updated = new Post();
    updated.setStatus(PostStatus.SUBMITTED);
    when(postRepository.save(any())).thenReturn(updated);

    PostStatus result = postService.updateStatus(10L, req, user).status();
    assertEquals(PostStatus.SUBMITTED, result);
  }

  @Test
  void userCannotMoveDraftToClosed() {
    Post post = basePost(PostStatus.DRAFT, user);
    when(postRepository.findById(10L)).thenReturn(Optional.of(post));
    StatusUpdateRequest req = new StatusUpdateRequest(PostStatus.CLOSED, null);
    assertThrows(ForbiddenException.class, () -> postService.updateStatus(10L, req, user));
  }

  @Test
  void adminCanCloseAnyStatus() {
    Post post = basePost(PostStatus.SUBMITTED, user);
    when(postRepository.findById(10L)).thenReturn(Optional.of(post));
    StatusUpdateRequest req = new StatusUpdateRequest(PostStatus.CLOSED, null);
    PostStatus status = postService.updateStatus(10L, req, admin).status();
    assertEquals(PostStatus.CLOSED, status);
  }

  @Test
  void rejectedRequiresReason() {
    Post post = basePost(PostStatus.UNDER_REVIEW, admin);
    when(postRepository.findById(10L)).thenReturn(Optional.of(post));
    StatusUpdateRequest req = new StatusUpdateRequest(PostStatus.REJECTED, "");
    assertThrows(BadRequestException.class, () -> postService.updateStatus(10L, req, admin));
  }

  @Test
  void onlyAuthorEditsDraft() {
    Post post = basePost(PostStatus.DRAFT, user);
    when(postRepository.findById(10L)).thenReturn(Optional.of(post));
    when(userService.isAdmin(user)).thenReturn(false);

    PostRequest request = new PostRequest("title123", "description long enough", PostType.ISSUE, Priority.HIGH, null);
    assertDoesNotThrow(() -> postService.update(10L, request, user));
  }

  @Test
  void nonAuthorCannotEditDraft() {
    Post post = basePost(PostStatus.DRAFT, user);
    when(postRepository.findById(10L)).thenReturn(Optional.of(post));
    User other = User.builder().id(5L).role(Role.USER).build();
    assertThrows(ForbiddenException.class, () -> postService.update(10L, new PostRequest("title123", "description long enough", PostType.ISSUE, Priority.LOW, null), other));
  }

  private Post basePost(PostStatus status, User author) {
    return Post.builder()
        .id(10L)
        .title("Sample")
        .description("Sample description long enough")
        .type(PostType.ISSUE)
        .status(status)
        .author(author)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
