package com.example.oops.api.post.domain;


import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.DiscussionResponseDto;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.vote.domain.enums.VoteType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    private String title;
    private String content;


    private String argument1;
    private String argument2;
    //토론 필드 영역
    private int upVoteCount;
    private int downVoteCount;

    @Enumerated(EnumType.STRING)
    private Line line1;

    @Enumerated(EnumType.STRING)
    private Line line2;
    @CreatedDate
    private LocalDateTime createdAt;

    public void increaseUpVoteCount(VoteType voteType) {
        if(voteType == VoteType.UP) {
            this.upVoteCount++;
        }else if(voteType == VoteType.DOWN) {
            this.downVoteCount++;
        }
    }

    public void decreaseUpVoteCount(VoteType voteType) {
        if(voteType == VoteType.UP && this.upVoteCount > 0) {
            this.upVoteCount--;
        }
        else if(voteType == VoteType.DOWN && this.downVoteCount > 0) {
            this.downVoteCount--;
        }
    }

    public void updateUpVoteCount(VoteType oldType,VoteType newType) {
        decreaseUpVoteCount(oldType);
        increaseUpVoteCount(newType);
    }
    public DiscussionResponseDto toResponseDto(boolean isAuthor) {
        return new DiscussionResponseDto(
                this.id,
                this.title,
                this.content,
                this.argument1,
                this.argument2,
                this.upVoteCount, // 가정: 엔티티에 직접 카운트 필드가 있다고 가정
                this.downVoteCount,
                this.createdAt,
                isAuthor
        );
    }
}
