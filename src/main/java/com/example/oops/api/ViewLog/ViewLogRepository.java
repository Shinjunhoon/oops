package com.example.oops.api.ViewLog;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {

    /**
     * 특정 Post에 대해, 특정 사용자 식별자(IP 또는 User ID)가
     * 특정 시간 이후에 조회 기록을 남겼는지 확인합니다.
     */
    @Query("SELECT vl FROM ViewLog vl " +
            "WHERE vl.post.id = :postId " +
            "AND vl.userIdentifier = :identifier " +
            "AND vl.recordedAt >= :timeLimit")
    Optional<ViewLog> findRecentView(
            @Param("postId") Long postId,
            @Param("identifier") String identifier,
            @Param("timeLimit") LocalDateTime timeLimit);

    // exists() 메서드를 사용하여 존재 여부만 빠르게 확인할 수도 있습니다.
//    boolean existsByPostIdAndUserIdentifierAndRecordedAtAfter(
//            Long postId,
//            String userIdentifier,
//            LocalDateTime timeLimit);


    Optional<ViewLog> findByPostIdAndUserIdentifier(Long postId, String userIdentifier);
}