package com.example.oops.api.vote.controller;

import com.example.oops.api.vote.application.VoteService;
import com.example.oops.api.vote.domain.enums.VoteType;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/vote")
@RequiredArgsConstructor
@RestController
public class VoteController {

    private final VoteService voteService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{postId}")
    public ResponseEntity<String > votePost(@PathVariable Long postId, @RequestBody VoteType voteType, Authentication authentication) {
        return ResponseEntity.ok(voteService.toggleVote(jwtTokenProvider.getLoginId(authentication),postId,voteType));
    }
}
