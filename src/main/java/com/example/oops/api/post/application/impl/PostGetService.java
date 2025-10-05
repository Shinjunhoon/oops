package com.example.oops.api.post.application.impl;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dto.DiscussionListResponseDto;
import com.example.oops.api.post.dto.DiscussionResponseDto;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostGetService implements com.example.oops.api.post.application.PostGetService {

    private final PostRepository postRepository;

    @Override
    public Page<DiscussionListResponseDto> getDiscussionList(BoardType boardType, Pageable pageable) {
        return postRepository.findByBoardType(boardType,pageable);
    }

    @Override
    public DiscussionResponseDto getDiscussionPost(BoardType boardType, Long postId) {

        Post post = postRepository.findByBoardTypeAndId(boardType, postId)
                // 1. Optional<Post>에서 Post 객체를 안전하게 추출. 없으면 예외 발생.
                .orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));

        return post.toResponseDto();
    }
}
