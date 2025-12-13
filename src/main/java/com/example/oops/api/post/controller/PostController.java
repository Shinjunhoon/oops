package com.example.oops.api.post.controller;

import com.example.oops.api.post.application.discussionimpl.PostAddService;
import com.example.oops.api.post.application.discussionimpl.PostDelService;
import com.example.oops.api.post.application.discussionimpl.PostGetService;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.domain.enums.GameCategory;
import com.example.oops.api.post.dtos.FreePostRequestDto;
import com.example.oops.api.post.dtos.MadMovieRequestDto;
import com.example.oops.api.post.dtos.ViewCountRequest;
import com.example.oops.api.post.dtos.discussionDto.DiscussionRequestDto;
import com.example.oops.api.s3.S3FileService;
import com.example.oops.api.user.domain.User;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.ApiResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/postMove")
    public ResponseEntity<ApiResponseEntity> createMovePost(@RequestPart("dto") @Valid MadMovieRequestDto madMovieRequestDto, Authentication authentication, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ApiResponseEntity.successResponseEntity(postService.saveMadMovePost(jwtTokenProvider.getLoginId(authentication), madMovieRequestDto,file));
    }

    @PostMapping("/freePost")
    public ResponseEntity<ApiResponseEntity> createFreePost(@RequestPart("dto") @Valid FreePostRequestDto madMovieRequestDto, Authentication authentication, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ApiResponseEntity.successResponseEntity(postService.saveFreePost(jwtTokenProvider.getLoginId(authentication), madMovieRequestDto,file));
    }

    @GetMapping("/get/{boardType}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType, @RequestParam(required = false) GameCategory category, @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponseEntity.successResponseEntity(postGetService.getPostList(boardType,category,pageable));
    }

    @GetMapping("/get/MovUpList")
    public ResponseEntity<ApiResponseEntity> getMovUpList(@RequestParam(required = false) GameCategory category) {
        return ApiResponseEntity.successResponseEntity(postGetService.getMonthlyPopularMadmoviePosts(category));
    }

    @GetMapping("/get/{boardType}/des")
    public ResponseEntity<ApiResponseEntity> getDiscussionDes(@PathVariable BoardType boardType,@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponseEntity.successResponseEntity(postGetService.getDiscussionPostVoteDES(boardType,pageable));
    }

    @GetMapping("/get/{boardType}/{postId}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType,@PathVariable Long postId) {
        return ApiResponseEntity.successResponseEntity(postGetService.getDiscussionPost(boardType,postId));
   }

    @DeleteMapping("del/{postId}")
    public ResponseEntity<ApiResponseEntity> deleteDiscussion(@PathVariable Long postId, Authentication authentication) {
        return ApiResponseEntity.successResponseEntity(postDelService.deletePost(postId, jwtTokenProvider.getLoginId(authentication)));
    }

    @GetMapping("/getMyPost")
    public ResponseEntity<ApiResponseEntity> getMyPost(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,Authentication authentication) {
        return ApiResponseEntity.successResponseEntity(postGetService.getMyPostResponse(jwtTokenProvider.getLoginId(authentication),pageable));
    }

    @GetMapping("/getDesPost/{boardType}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType) {
        return ApiResponseEntity.successResponseEntity(postGetService.getPostDesList(boardType));
    }

    @PostMapping("/view-count")
    public ResponseEntity<Void> incrementViewCount(
            @RequestBody ViewCountRequest requestDto,
            HttpServletRequest request,
            // 🚨 [수정]: @AuthenticationPrincipal을 사용하여 로그인 사용자 정보 주입
            // JWT가 유효하면 User 객체(Principal)가 주입되고, 익명이면 null이 주입될 수 있습니다.
            @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : #this") User user
    ) {
        Long postId = requestDto.getPostId();
        String userIdentifier;

        // 1. JWT 기반 로그인 사용자 식별
        if (user != null) {
            // 🚨 로그인 사용자: User ID를 식별자로 사용 (예: "USER_123")
            // IP보다 훨씬 정확하며, 여러 기기에서 접속해도 동일한 사용자임을 보장
            userIdentifier = "USER_" + user.getId();
        } else {
            // 2. 비로그인 사용자 (익명): IP 주소를 식별자로 사용
            // 로드 밸런서가 없으므로 getRemoteAddr() 사용
            userIdentifier = "IP_" + request.getRemoteAddr();
        }

        // 🚨 디버깅/로그 목적으로 식별자 확인
        System.out.println("View Count Request received. Identifier: " + userIdentifier);

        // 3. 조회수 증가 서비스 호출 (60분 제한)
        postGetService.incrementViewCount(postId, userIdentifier, 10L);

        return ResponseEntity.ok().build();
    }
}

