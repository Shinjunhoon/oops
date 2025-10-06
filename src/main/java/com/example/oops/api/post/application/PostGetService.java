package com.example.oops.api.post.application;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import com.example.oops.api.post.dtos.DiscussionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostGetService {
    public abstract Page<DiscussionListResponseDto> getDiscussionList(BoardType boardType, Pageable pageable);
    public abstract DiscussionResponseDto getDiscussionPost(BoardType boardType, Long postId);


}
