package com.example.oops.api.comment;

import com.example.oops.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreateAtAsc(Long postId);

    List<Comment> findByUser(User user);
}
