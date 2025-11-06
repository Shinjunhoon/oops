package com.example.oops.api.comment;


import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.ApiResponseEntity;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/createComment")
    public ResponseEntity<ApiResponseEntity> createComment(@RequestBody CommentCreateRequestDto commentCreateRequestDto, Authentication auth) {
      return ApiResponseEntity.successResponseEntity(commentService.createComment(commentCreateRequestDto,jwtTokenProvider.getLoginId(auth)));

    }

    @PostMapping("/updateComment")
    public ResponseEntity<ApiResponseEntity> updateComment(@RequestBody CommentUpdateRequestDto commentUpdateRequestDto, Authentication auth) {
        return ApiResponseEntity.successResponseEntity(commentService.updateComment(commentUpdateRequestDto,jwtTokenProvider.getLoginId(auth)));
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<ApiResponseEntity> deleteComment(@RequestBody CommentDeleteRequestDto commentDeleteRequestDto,  Authentication auth) {
        return ApiResponseEntity.successResponseEntity(commentService.deleteComment(commentDeleteRequestDto,jwtTokenProvider.getLoginId(auth)));
    }
}
