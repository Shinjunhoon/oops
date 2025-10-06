package com.example.oops.api.post.application;

import com.example.oops.api.post.domain.Post;
import org.springframework.security.core.Authentication;

public interface PostDelService {
    public abstract String deletePost(Long postId);
}
