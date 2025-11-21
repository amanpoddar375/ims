package com.postanything.service;

import com.postanything.dto.PageResponse;
import com.postanything.dto.PostRequest;
import com.postanything.dto.PostResponse;
import com.postanything.dto.StatusUpdateRequest;
import com.postanything.entity.Post;
import com.postanything.entity.User;
import com.postanything.enums.PostStatus;
import com.postanything.enums.Role;
import com.postanything.exception.BadRequestException;
import com.postanything.exception.ForbiddenException;
import com.postanything.exception.NotFoundException;
import com.postanything.mapper.PostMapper;
import com.postanything.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;
  private final UserService userService;

  @Transactional
  public PostResponse create(PostRequest request, User author) {
    Post post = postMapper.toEntity(request);
    LocalDateTime now = LocalDateTime.now();
    post.setAuthor(author);
    post.setStatus(PostStatus.DRAFT);
    post.setCreatedAt(now);
    post.setUpdatedAt(now);
    postRepository.save(post);
    return postMapper.toResponse(post);
  }

  @Transactional
  public PostResponse update(Long id, PostRequest request, User actor) {
    Post post = findEditable(id, actor);
    post.setTitle(request.title());
    post.setDescription(request.description());
    post.setType(request.type());
    post.setPriority(request.priority());
    post.setAttachmentUrl(request.attachmentUrl());
    post.setUpdatedAt(LocalDateTime.now());
    return postMapper.toResponse(post);
  }

  @Transactional
  public void delete(Long id, User actor) {
    Post post = findEditable(id, actor);
    postRepository.delete(post);
  }

  @Transactional(readOnly = true)
  public PageResponse<PostResponse> list(User actor, int page, int size) {
    PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Post> posts = userService.isAdmin(actor)
        ? postRepository.findAll(pageable)
        : postRepository.findVisible(actor, pageable);
    return toPage(posts.map(postMapper::toResponse));
  }

  @Transactional(readOnly = true)
  public PostResponse get(Long id, User actor) {
    Post post = postRepository.findVisibleById(id, actor, userService.isAdmin(actor))
        .orElseThrow(() -> new NotFoundException("Post not found"));
    return postMapper.toResponse(post);
  }

  @Transactional
  public PostResponse updateStatus(Long id, StatusUpdateRequest request, User actor) {
    Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    validateStatusTransition(post, request, actor);
    post.setStatus(request.newStatus());
    if (request.newStatus() == PostStatus.RESOLVED) {
      post.setResolvedAt(LocalDateTime.now());
    }
    if (request.newStatus() == PostStatus.CLOSED && post.getResolvedAt() == null) {
      post.setResolvedAt(LocalDateTime.now());
    }
    post.setUpdatedAt(LocalDateTime.now());
    return postMapper.toResponse(post);
  }

  @Transactional
  public PostResponse assign(Long id, Long assigneeId, User actor) {
    if (!userService.isAdmin(actor)) {
      throw new ForbiddenException("Only admins can assign posts"); // SECURITY: admin-only guard
    }
    Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    User assignee = userService.findById(assigneeId);
    post.setAssignedTo(assignee);
    post.setUpdatedAt(LocalDateTime.now());
    return postMapper.toResponse(post);
  }

  private Post findEditable(Long id, User actor) {
    Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    if (userService.isAdmin(actor)) {
      return post;
    }
    if (!post.getAuthor().getId().equals(actor.getId())) {
      throw new ForbiddenException("Not allowed to modify this post");
    }
    if (post.getStatus() != PostStatus.DRAFT) {
      throw new BadRequestException("Only DRAFT posts can be edited or deleted by author");
    }
    return post;
  }

  private void validateStatusTransition(Post post, StatusUpdateRequest request, User actor) {
    Role role = actor.getRole();
    PostStatus current = post.getStatus();
    PostStatus target = request.newStatus();

    if (target == PostStatus.REJECTED && (request.reason() == null || request.reason().isBlank())) {
      throw new BadRequestException("Reason required for REJECTED status");
    }

    if (role == Role.USER) {
      if (!post.getAuthor().getId().equals(actor.getId())) {
        throw new ForbiddenException("Users can only transition their own posts");
      }
      switch (current) {
        case DRAFT -> {
          if (target == PostStatus.SUBMITTED) return;
        }
        case RESOLVED -> {
          if (target == PostStatus.CLOSED) return;
        }
        case REJECTED -> {
          if (target == PostStatus.DRAFT) return;
        }
        default -> {}
      }
      throw new ForbiddenException("User not allowed to perform this transition");
    }

    if (role == Role.ADMIN) {
      switch (current) {
        case DRAFT -> {
          if (target == PostStatus.SUBMITTED || target == PostStatus.CLOSED) return;
        }
        case SUBMITTED -> {
          if (target == PostStatus.UNDER_REVIEW || target == PostStatus.CLOSED) return;
        }
        case UNDER_REVIEW -> {
          if (target == PostStatus.RESOLVED || target == PostStatus.REJECTED || target == PostStatus.CLOSED) return;
        }
        case RESOLVED -> {
          if (target == PostStatus.CLOSED) return;
        }
        case REJECTED -> {
          if (target == PostStatus.DRAFT || target == PostStatus.CLOSED) return;
        }
        case CLOSED -> throw new BadRequestException("Closed posts cannot transition");
      }
      throw new BadRequestException("Invalid status transition");
    }

    throw new ForbiddenException("Unsupported role");
  }

  private PageResponse<PostResponse> toPage(Page<PostResponse> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  @Transactional(readOnly = true)
  public java.util.Map<String, Object> stats() {
    java.util.List<Post> posts = postRepository.findAll();
    long total = posts.size();
    long drafts = posts.stream().filter(p -> p.getStatus() == PostStatus.DRAFT).count();
    long submitted = posts.stream().filter(p -> p.getStatus() == PostStatus.SUBMITTED).count();
    long underReview = posts.stream().filter(p -> p.getStatus() == PostStatus.UNDER_REVIEW).count();
    long resolved = posts.stream().filter(p -> p.getStatus() == PostStatus.RESOLVED).count();
    long closed = posts.stream().filter(p -> p.getStatus() == PostStatus.CLOSED).count();
    long rejected = posts.stream().filter(p -> p.getStatus() == PostStatus.REJECTED).count();
    return java.util.Map.of(
        "total", total,
        "draft", drafts,
        "submitted", submitted,
        "underReview", underReview,
        "resolved", resolved,
        "closed", closed,
        "rejected", rejected);
  }
}
