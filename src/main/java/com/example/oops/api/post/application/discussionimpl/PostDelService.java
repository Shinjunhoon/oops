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

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PostDelService implements com.example.oops.api.post.application.PostDelService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;

    @Override
    public String deletePost(Long postId,Long userId) {


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = Objects.equals(post.getUser().getId(), userId);

        if (!isOwner && !isAdmin) {
            throw new OopsException(ErrorCode.NO_AUTHORITY);
        }

        postRepository.delete(post);
        return "성공";
    }

}
