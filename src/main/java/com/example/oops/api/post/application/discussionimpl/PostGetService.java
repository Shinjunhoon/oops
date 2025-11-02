package com.example.oops.api.post.application.discussionimpl;

import com.example.oops.api.comment.CommentResponseDto;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import com.example.oops.api.post.dtos.DiscussionResponseDto;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostGetService implements com.example.oops.api.post.application.PostGetService {

    private final PostRepository postRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Page<DiscussionListResponseDto> getDiscussionList(BoardType boardType, Pageable pageable) {
        return postRepository.findByBoardType(boardType,pageable);
    }

    @Override
    public DiscussionResponseDto getDiscussionPost(BoardType boardType, Long postId) {

        Post post = postRepository.findByBoardTypeAndId(boardType, postId)
                .orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));

        log.info("postUserId:{}",post.getUser().getId());

        DiscussionResponseDto responseDto = post.toResponseDto();

        List<CommentResponseDto> commentResponseDtos = post.getComments().stream()
                .map(CommentResponseDto::of)
                .toList();

        responseDto.setComments(commentResponseDtos);

        return responseDto;
    }
}
