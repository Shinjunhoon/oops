package com.example.oops.api.post.controller;

import com.example.oops.api.post.application.discussionimpl.PostAddService;
import com.example.oops.api.post.application.discussionimpl.PostDelService;
import com.example.oops.api.post.application.discussionimpl.PostGetService;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.discussionDto.DiscussionRequestDto;
import com.example.oops.api.s3.S3FileService;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.ApiResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostAddService postService;

    private final PostGetService postGetService;

    private final PostDelService postDelService;

    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/post")
    public ResponseEntity<ApiResponseEntity> createPost(@RequestPart("dto") @Valid DiscussionRequestDto discussionRequestDto,Authentication authentication,@RequestParam("file") MultipartFile file) throws IOException {
       return ApiResponseEntity.successResponseEntity(postService.savePost(jwtTokenProvider.getLoginId(authentication), discussionRequestDto,file));
    }

    @GetMapping("/get/{boardType}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType,@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponseEntity.successResponseEntity(postGetService.getDiscussionList(boardType,pageable));
    }

    @GetMapping("/get/{boardType}/{postId}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType,@PathVariable Long postId) {
        return ApiResponseEntity.successResponseEntity(postGetService.getDiscussionPost(boardType,postId));
   }

   @DeleteMapping("del/{postId}")
    public ResponseEntity<ApiResponseEntity> deleteDiscussion(@PathVariable Long postId) {
        return ApiResponseEntity.successResponseEntity(postDelService.deletePost(postId));
   }
}
