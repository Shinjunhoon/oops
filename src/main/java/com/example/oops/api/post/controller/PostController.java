package com.example.oops.api.post.controller;

import com.example.oops.api.post.application.impl.PostAddService;
import com.example.oops.api.post.application.impl.PostGetService;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dto.DiscussionRequestDto;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostAddService postService;
    private final PostGetService postGetService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/post")
    public ResponseEntity<ApiResponseEntity> createPost(@RequestBody DiscussionRequestDto discussionRequestDto, Authentication authentication) {
       return ApiResponseEntity.successResponseEntity(postService.savePost(jwtTokenProvider.getLoginId(authentication), discussionRequestDto));
    }

    @GetMapping("/get/{boardType}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType,@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponseEntity.successResponseEntity(postGetService.getDiscussionList(boardType,pageable));
    }
}
