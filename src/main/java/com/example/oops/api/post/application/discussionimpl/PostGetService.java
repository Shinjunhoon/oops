package com.example.oops.api.post.application.discussionimpl;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import com.example.oops.api.post.dtos.DiscussionResponseDto;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));

        return post.toResponseDto();
    }
}
