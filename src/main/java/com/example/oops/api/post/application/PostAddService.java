package com.example.oops.api.post.application;

import com.example.oops.api.post.dto.DiscussionRequestDto;

public interface PostAddService {
    public abstract Long savePost(Long userId, DiscussionRequestDto discussionRequestDto);
}
