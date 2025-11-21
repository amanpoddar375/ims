package com.postanything.entity;

import com.postanything.enums.PostStatus;
import com.postanything.enums.PostType;
import com.postanything.enums.Priority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 5000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostStatus status;

  @Enumerated(EnumType.STRING)
  private Priority priority;

  @Column
  private String attachmentUrl;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to_id")
  private User assignedTo;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime resolvedAt;
}
