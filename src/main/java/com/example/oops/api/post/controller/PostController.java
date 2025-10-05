package com.example.oops.api.post.controller;

import com.example.oops.api.post.application.PostService;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.dto.PostRequestDto;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.error.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/post")
    public ResponseEntity<ApiResponseEntity> createPost(@RequestBody PostRequestDto postRequestDto, Authentication authentication) {
       return ApiResponseEntity.successResponseEntity(postService.savePost(jwtTokenProvider.getLoginId(authentication), postRequestDto));
    }
}
