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

    private final S3FileService s3FileService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponseEntity> createPost(@RequestBody @Valid DiscussionRequestDto discussionRequestDto, Authentication authentication) {
       return ApiResponseEntity.successResponseEntity(postService.savePost(jwtTokenProvider.getLoginId(authentication), discussionRequestDto));
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestParam("file") MultipartFile file) {
        try {
            // 3. 서비스 호출 및 업로드 실행
            String s3Key = s3FileService.uploadVideoWithTransferManager(file);

            // 4. 성공 응답 반환
            return ResponseEntity.ok("동영상 업로드 성공. S3 Key: " + s3Key);

        } catch (RuntimeException e) {
            // S3 서비스 내부 오류 처리
            return ResponseEntity.internalServerError().body("서버 오류: S3 업로드 실패 (" + e.getMessage() + ")");
        } catch (IOException e) {
            // 파일 스트림 처리 오류 (예: 서버에서 파일 읽기 실패)
            return ResponseEntity.internalServerError().body("파일 처리 오류: " + e.getMessage());
        }
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
