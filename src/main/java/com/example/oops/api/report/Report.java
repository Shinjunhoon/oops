package com.example.oops.api.report;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 기본 생성자 필요
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder // 빌더 패턴 사용
@EntityListeners(AuditingEntityListener.class) // 생성일자 자동 기록을 위함
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private Long postId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportType reportType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reasonDetail;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(nullable = false)
    private Long reporterUserId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
