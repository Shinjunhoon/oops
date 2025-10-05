package com.example.oops.api.post.domain;


import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.vote.domain.enums.VoteType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
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
}
