package com.example.oops.api.like;

import com.example.oops.api.vote.domain.enums.VoteType;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/like")
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/{postId}")
    public ResponseEntity<String > votePost(@PathVariable Long postId, Authentication authentication) {
        return ResponseEntity.ok(likeService.toggleLike(postId,jwtTokenProvider.getLoginId(authentication)));
    }
}
