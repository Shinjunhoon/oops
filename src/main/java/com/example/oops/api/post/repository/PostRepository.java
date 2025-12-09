package com.example.oops.api.post.repository;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.DesPostListTopFive.PostFiveResponseDto;
import com.example.oops.api.post.dtos.MadMovieListResponseDto;
import com.example.oops.api.post.dtos.PostListResponseDto;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
// PostRepository.java

    @Query(value = "SELECT new com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto(" +
            "p.id, p.title, p.upVoteCount, p.downVoteCount, p.createdAt, " +
            "p.champion1, p.champion2, p.tier, p.viewCount, p.isNotice) " +
            "FROM Post p " +
            "WHERE p.boardType = :boardType " +
            "ORDER BY p.isNotice DESC, p.createdAt DESC",
            countQuery = "SELECT count(p) FROM Post p WHERE p.boardType = :boardType")
    Page<DiscussionListResponseDto> findByBoardType(BoardType boardType, Pageable pageable);


    Optional<Post> findByBoardTypeAndId(BoardType boardType, Long id);

    @Query("SELECT new com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto(" +
            "p.id, p.title, p.upVoteCount, p.downVoteCount, p.createdAt, p.champion1, p.champion2, p.tier,p.viewCount,p.isNotice) " +
            "FROM Post p WHERE p.boardType = :boardType " +
            "ORDER BY (p.upVoteCount + p.downVoteCount) DESC")
    Page<DiscussionListResponseDto> findByBoardTypeOrderByTotalVotesDesc(@Param("boardType") BoardType boardType, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);

    List<Post> findByUserId(Long userId);

    @Query(value = "SELECT new com.example.oops.api.post.dtos.MadMovieListResponseDto(" +
            "p.id, p.title, p.upVoteCount, p.downVoteCount, p.createdAt, p.viewCount, p.user.userInfo.nickname, p.isNotice) " +
            "FROM Post p " +
            "WHERE p.boardType = :boardType " +
            "ORDER BY p.isNotice DESC, p.createdAt DESC",
            countQuery = "SELECT count(p) FROM Post p WHERE p.boardType = :boardType")
    Page<MadMovieListResponseDto> findMadMovieListByBoardType(@Param("boardType") BoardType boardType, Pageable pageable);



    @Query(value = "SELECT new com.example.oops.api.post.dtos.PostListResponseDto(p.id, p.title,p.createdAt,p.user.userInfo.nickname,p.viewCount,p.isNotice) " +
            "FROM Post p " +
            "WHERE p.boardType = :boardType " +
            "ORDER BY p.isNotice DESC, p.createdAt DESC",
            countQuery = "SELECT count(p) FROM Post p WHERE p.boardType = :boardType")
    Page<PostListResponseDto> findGeneralListByBoardType(@Param("boardType") BoardType boardType, Pageable pageable);



    @Query(value = "SELECT new com.example.oops.api.post.dtos.MadMovieListResponseDto(" +
            "p.id, p.title, p.upVoteCount, p.downVoteCount, p.createdAt, p.viewCount,p.user.userInfo.nickname,p.isNotice ) " +
            "FROM Post p " +
            "WHERE p.boardType = :boardType " +
            "AND p.createdAt >= :startOfMonth " +
            "AND p.upVoteCount > 0 " +
            "ORDER BY p.upVoteCount DESC, p.id DESC",
            countQuery = "SELECT count(p) FROM Post p " +
                    "WHERE p.boardType = :boardType " +
                    "AND p.createdAt >= :startOfMonth " +
                    "AND p.upVoteCount > 0")
    List<MadMovieListResponseDto> findTop5MonthlyPopularMadmovieDtos(
            @Param("boardType") BoardType boardType,
            @Param("startOfMonth") LocalDateTime startOfMonth
    );

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);




    @Query("SELECT new com.example.oops.api.post.dtos.DesPostListTopFive.PostFiveResponseDto(" +
            "p.title, p.id, p.createdAt, p.user.userInfo.nickname, p.viewCount, COUNT(c)) " +
            "FROM Post p LEFT JOIN p.comments c " +
            "WHERE p.boardType = :boardType " +
            "GROUP BY p.id, p.title, p.createdAt, p.user.userInfo.nickname, p.viewCount " +
            "ORDER BY p.createdAt DESC")
    List<PostFiveResponseDto> findTop5ByBoardTypeOrderByCreatedAtDesc(@Param("boardType") BoardType boardType, Pageable pageable);


}
