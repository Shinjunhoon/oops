package com.example.oops.api.post.application;

import com.example.oops.api.post.dtos.discussionDto.DiscussionRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostAddService {
    public abstract Long savePost(Long userId, DiscussionRequestDto discussionRequestDto, MultipartFile multipartFile) throws IOException;
}
