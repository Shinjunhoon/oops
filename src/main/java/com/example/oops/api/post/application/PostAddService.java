package com.example.oops.api.post.application;

import com.example.oops.api.post.dtos.discussionDto.DiscussionRequestDto;

public interface PostAddService {
    public abstract Long savePost(Long userId, DiscussionRequestDto discussionRequestDto);
}
