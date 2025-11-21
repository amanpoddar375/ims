package com.postanything.dto;

import com.postanything.enums.PostStatus;
import com.postanything.enums.PostType;
import com.postanything.enums.Priority;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    String title,
    String description,
    PostType type,
    PostStatus status,
    Priority priority,
    String attachmentUrl,
    Long authorId,
    String authorUsername,
    Long assignedToId,
    String assignedToUsername,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime resolvedAt
) {}
