package com.postanything.dto;

import com.postanything.enums.PostStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest(
    @NotNull PostStatus newStatus,
    @Size(max = 500) String reason // VALIDATION: required when status is REJECTED
) {}
