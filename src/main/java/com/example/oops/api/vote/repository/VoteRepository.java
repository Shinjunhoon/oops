package com.example.oops.api.vote.repository;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndPost(User user, Post post);
}
