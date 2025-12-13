package com.example.oops.api.post.domain;

import com.example.oops.api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
// 🚨 핵심: 복합 유니크 인덱스를 사용하여 DB 레벨에서 중복 카운트를 방지하고 조회 속도를 높입니다.
@Table(name = "view_log", indexes = {
        @Index(name = "idx_view_log_post_user_time",
                columnList = "post_id, user_identifier, recorded_at") // 조회 성능 향상용 인덱스
})
public class ViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Post 엔티티와 관계 설정 (Lazy Loading)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 🚨 식별자 필드: IP 주소 (비로그인) 또는 User ID (로그인)를 저장
    @Column(name = "user_identifier", nullable = false, length = 255)
    private String userIdentifier;

    @CreatedDate
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    // 편의 메서드
    public static ViewLog create(Post post, String userIdentifier) {
        return ViewLog.builder()
                .post(post)
                .userIdentifier(userIdentifier)
                .build();
    }
}