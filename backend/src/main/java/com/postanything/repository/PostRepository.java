package com.postanything.repository;

import com.postanything.entity.Post;
import com.postanything.entity.User;
import com.postanything.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query("""
      SELECT p FROM Post p
      WHERE (p.author = :currentUser OR p.status <> com.postanything.enums.PostStatus.DRAFT)
      """)
  Page<Post> findVisible(User currentUser, Pageable pageable);

  @Query("""
      SELECT p FROM Post p
      WHERE p.id = :id AND (p.author = :currentUser OR p.status <> com.postanything.enums.PostStatus.DRAFT OR :isAdmin = true)
      """)
  Optional<Post> findVisibleById(Long id, User currentUser, boolean isAdmin);
}
