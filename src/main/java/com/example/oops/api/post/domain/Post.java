package com.example.oops.api.post.domain;


import com.example.oops.api.comment.Comment;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.domain.enums.Champion;
import com.example.oops.api.post.domain.enums.Tier;
import com.example.oops.api.post.dtos.DesPostListTopFive.PostFiveResponseDto;
import com.example.oops.api.post.dtos.DiscussionResponseDto;
import com.example.oops.api.post.dtos.GeneralPostResponseDto;
import com.example.oops.api.post.dtos.MadMovieResponseDto;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.vote.domain.Vote;
import com.example.oops.api.vote.domain.enums.VoteType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 500)
    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private Tier tier;


    @Column(length = 500)
    private String argument1;

    @Column(length = 500)
    private String argument2;
    //토론 필드 영역
    private int upVoteCount;
    private int downVoteCount;

    @Enumerated(EnumType.STRING)
    private Line line1;

    @Enumerated(EnumType.STRING)
    private Line line2;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Champion champion1;

    @Enumerated(EnumType.STRING)
    private Champion champion2;

    @CreatedDate
    private LocalDateTime createdAt;

    private int viewCount = 0;

    private String imageUrl;

    private boolean isNotice;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("createAt DESC ")
    private List<Comment> comments = new ArrayList<>();

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
    public DiscussionResponseDto toResponseDto() {
        return new DiscussionResponseDto(
                this.id,
                this.title,
                this.argument1,
                this.argument2,
                this.upVoteCount, // 가정: 엔티티에 직접 카운트 필드가 있다고 가정
                this.downVoteCount,
                this.createdAt,
                this.imageUrl,
                this.line1,
                this.line2,
                this.champion1,
                this.champion2,
                null,
                this.user.getId(),
                this.user.getUserInfo().getNickname(),
                this.champion1.getKoreanName(),
                this.champion2.getKoreanName(),
                this.viewCount,
                this.isNotice
        );
    }
    public MadMovieResponseDto toMadMovieResponseDto() {
        return new MadMovieResponseDto(
                this.id,
                this.title,
                this.upVoteCount, // 가정: 엔티티에 직접 카운트 필드가 있다고 가정
                this.downVoteCount,
                this.createdAt,
                this.imageUrl,
                null,
                this.user.getId(),
                this.user.getUserInfo().getNickname(),
                this.viewCount,
                this.isNotice,
                this.content
        );
    }

    public GeneralPostResponseDto toGeneralPostResponseDto() {
        return new GeneralPostResponseDto(
                this.id,
                this.title,
                this.content,
                this.createdAt,
                this.imageUrl,
                this.user.getUserInfo().getNickname(),
                null,
                this.user.getId(),
                this.isNotice
        );
    }

}
