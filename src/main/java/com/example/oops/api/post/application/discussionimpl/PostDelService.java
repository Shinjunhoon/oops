package com.example.oops.api.post.application.discussionimpl;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostDelService implements com.example.oops.api.post.application.PostDelService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;

    @Override
    public String deletePost(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. JwtTokenProvider를 통해 현재 로그인된 사용자의 ID(PK)를 얻습니다.
        //    (토큰 프로바이더 내부에서 인증되지 않은 사용자는 예외 처리됨)
        long currentUserId = jwtTokenProvider.getLoginId(authentication); // ✨ 이 메서드를 활용합니다.

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));

        if (post.getUser().getId() != currentUserId) {
            throw new OopsException(ErrorCode.NO_AUTHORITY);
        }

        postRepository.delete(post);
        return "성공";
    }

}
