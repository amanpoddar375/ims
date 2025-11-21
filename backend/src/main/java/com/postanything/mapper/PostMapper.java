package com.postanything.mapper;

import com.postanything.dto.PostRequest;
import com.postanything.dto.PostResponse;
import com.postanything.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

  public Post toEntity(PostRequest request) {
    Post post = new Post();
    post.setTitle(request.title());
    post.setDescription(request.description());
    post.setType(request.type());
    post.setPriority(request.priority());
    post.setAttachmentUrl(request.attachmentUrl());
    return post;
  }

  public PostResponse toResponse(Post post) {
    return new PostResponse(
        post.getId(),
        post.getTitle(),
        post.getDescription(),
        post.getType(),
        post.getStatus(),
        post.getPriority(),
        post.getAttachmentUrl(),
        post.getAuthor() != null ? post.getAuthor().getId() : null,
        post.getAuthor() != null ? post.getAuthor().getUsername() : null,
        post.getAssignedTo() != null ? post.getAssignedTo().getId() : null,
        post.getAssignedTo() != null ? post.getAssignedTo().getUsername() : null,
        post.getCreatedAt(),
        post.getUpdatedAt(),
        post.getResolvedAt()
    );
  }
}
