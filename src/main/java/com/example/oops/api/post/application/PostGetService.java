package com.example.oops.api.post.application;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.domain.enums.GameCategory;
import com.example.oops.api.post.dtos.DesPostListTopFive.PostFiveResponseDto;
import com.example.oops.api.post.dtos.MadMovieListResponseDto;
import com.example.oops.api.post.dtos.PostListResponseDto;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import com.example.oops.api.post.dtos.DiscussionResponseDto;
import com.example.oops.api.post.dtos.discussionDto.MyPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostGetService {
    public Page<?> getPostList(BoardType boardType, GameCategory gameCategory ,Pageable pageable);
    public abstract <T> T getDiscussionPost(BoardType boardType, Long postId);
    public abstract Page<DiscussionListResponseDto> getDiscussionPostVoteDES(BoardType boardType, Pageable pageable);
    public abstract Page<MyPostResponse> getMyPostResponse(Long userId, Pageable pageable);
    public List<MadMovieListResponseDto> getMonthlyPopularMadmoviePosts(GameCategory gameCategory);

    public void incrementViewCount(Long postId, String userIdentifier, long timeLimitMinutes);
    public abstract List<PostFiveResponseDto> getPostDesList(BoardType boardType);

}
