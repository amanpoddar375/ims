package com.postanything.dto;

import com.postanything.enums.PostType;
import com.postanything.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record PostRequest(
    @NotBlank @Size(min = 5, max = 200) String title,
    @NotBlank @Size(min = 10, max = 5000) String description,
    @NotNull PostType type,
    Priority priority,
    @URL(message = "Invalid attachment URL") String attachmentUrl // VALIDATION: must be a valid URL when provided
) {}
