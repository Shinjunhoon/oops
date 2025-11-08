package com.example.oops.api.post.repository;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT new com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto(p.id, p.title, p.upVoteCount, p.downVoteCount, p.createdAt,p.champion1,p.champion2,p.tier) " +
            "FROM Post p " +
            "WHERE p.boardType = :boardType",
            countQuery = "SELECT count(p) FROM Post p WHERE p.boardType = :boardType")
     Page<DiscussionListResponseDto> findByBoardType(BoardType boardType, Pageable pageable);

    Optional<Post> findByBoardTypeAndId(BoardType boardType, Long id);

    @Query("SELECT new com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto(" +
            "p.id, p.title, p.upVoteCount, p.downVoteCount, p.createdAt, p.champion1, p.champion2, p.tier) " +
            "FROM Post p WHERE p.boardType = :boardType " +
            "ORDER BY (p.upVoteCount + p.downVoteCount) DESC")
    Page<DiscussionListResponseDto> findByBoardTypeOrderByTotalVotesDesc(@Param("boardType") BoardType boardType, Pageable pageable);


}
