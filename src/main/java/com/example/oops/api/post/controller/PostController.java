package com.example.oops.api.post.controller;

import com.example.oops.api.post.application.discussionimpl.PostAddService;
import com.example.oops.api.post.application.discussionimpl.PostDelService;
import com.example.oops.api.post.application.discussionimpl.PostGetService;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.FreePostRequestDto;
import com.example.oops.api.post.dtos.MadMovieRequestDto;
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

    @PostMapping("/postMove")
    public ResponseEntity<ApiResponseEntity> createMovePost(@RequestPart("dto") @Valid MadMovieRequestDto madMovieRequestDto, Authentication authentication, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ApiResponseEntity.successResponseEntity(postService.saveMadMovePost(jwtTokenProvider.getLoginId(authentication), madMovieRequestDto,file));
    }

    @PostMapping("/freePost")
    public ResponseEntity<ApiResponseEntity> createFreePost(@RequestPart("dto") @Valid FreePostRequestDto madMovieRequestDto, Authentication authentication, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ApiResponseEntity.successResponseEntity(postService.saveFreePost(jwtTokenProvider.getLoginId(authentication), madMovieRequestDto,file));
    }

    @GetMapping("/get/{boardType}")
    public ResponseEntity<ApiResponseEntity> getDiscussion(@PathVariable BoardType boardType,@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponseEntity.successResponseEntity(postGetService.getPostList(boardType,pageable));
    }

    @GetMapping("/get/MovUpList")
    public ResponseEntity<ApiResponseEntity> getMovUpList() {
        return ApiResponseEntity.successResponseEntity(postGetService.getMonthlyPopularMadmoviePosts());
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
}
