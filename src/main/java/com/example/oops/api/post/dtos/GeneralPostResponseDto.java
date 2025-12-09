package com.example.oops.api.post.dtos;

import com.example.oops.api.comment.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class GeneralPostResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdOn;
    private String videoUrl;
    private String userName;
    List<CommentResponseDto> comments;
    private Long userId;
    private Boolean isNotice;
}
