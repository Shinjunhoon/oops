package com.example.oops.api.post.application;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.dto.DiscussionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostGetService {
    public abstract Page<DiscussionListResponseDto> getDiscussionList(BoardType boardType, Pageable pageable);


}
