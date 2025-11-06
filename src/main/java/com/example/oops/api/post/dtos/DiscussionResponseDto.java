package com.example.oops.api.post.dtos;

import com.example.oops.api.comment.CommentResponseDto;
import com.example.oops.api.post.domain.enums.Champion;
import com.example.oops.api.user.domain.enums.Line;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class DiscussionResponseDto {
    private Long id;
    private String title;
    private String argument1;
    private String argument2;
    private int upVoteCount;
    private int downVoteCount;
    private LocalDateTime createdAt;
    private String imageUrl;
    private Line line1;
    private Line line2;
    private Champion champion1;
    private Champion champion2;
    List<CommentResponseDto> comments;
    private Long userId;
    private String userName;
    private String koreanName1;
    private String koreanName2;
}
