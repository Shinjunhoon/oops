package com.example.oops.api.post.application.impl;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dto.DiscussionListResponseDto;
import com.example.oops.api.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostGetService implements com.example.oops.api.post.application.PostGetService {

    private final PostRepository postRepository;

    @Override
    public Page<DiscussionListResponseDto> getDiscussionList(BoardType boardType, Pageable pageable) {
        return postRepository.findByBoardType(boardType,pageable);
    }
}
