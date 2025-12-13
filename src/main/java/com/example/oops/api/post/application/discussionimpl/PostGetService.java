package com.example.oops.api.post.application.discussionimpl;

import com.example.oops.api.ViewLog.ViewLogRepository;
import com.example.oops.api.comment.CommentResponseDto;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.ViewLog;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.domain.enums.GameCategory;
import com.example.oops.api.post.dtos.*;
import com.example.oops.api.post.dtos.DesPostListTopFive.PostFiveResponseDto;
import com.example.oops.api.post.dtos.discussionDto.DiscussionListResponseDto;
import com.example.oops.api.post.dtos.discussionDto.MyPostResponse;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostGetService implements com.example.oops.api.post.application.PostGetService {

    private final PostRepository postRepository;
    private final ViewLogRepository viewLogRepository;

    public Page<?> getPostList(BoardType boardType, GameCategory gameCategory ,Pageable pageable) {

        // 1. BoardType에 따른 분기 처리
        if (boardType == BoardType.DISCUSSION) {

            // --- 🏆 토론 게시판 로직 ---

            // 1-1. 토론 전용 Repository 메서드 호출 (챔피언 정보 포함)
            Page<DiscussionListResponseDto> discussionPage = postRepository.findByBoardType(boardType, pageable);

            // 1-2. DTO 목록을 순회하며 한글 이름을 설정합니다. (챔피언 정보가 필수이므로)
            discussionPage.getContent().forEach(dto -> {
                // 이 로직은 DEBATE 타입에만 적용되어야 합니다.
                if (dto.getChampion1() != null) {
                    dto.setKoreanName1(dto.getChampion1().getKoreanName());
                }
                if (dto.getChampion2() != null) {
                    dto.setKoreanName2(dto.getChampion2().getKoreanName());
                }
            });

            return discussionPage;

        } else if (boardType == BoardType.MAD) {

            // --- 🎬 매드무비 게시판 로직 ---

            // 2-1. 매드무비 전용 Repository 메서드 호출 (챔피언 정보 제외)
            // 이 메서드는 MadMovieListResponseDto를 반환합니다.
            Page<MadMovieListResponseDto> madMoviePage = postRepository.findMadMovieListByBoardTypeAndCategory(boardType,gameCategory, pageable);



            Page<MadMovieListResponseDto> madMoviePageWithComments = madMoviePage.map(postDto -> {
                // 댓글 DTO 변환
                List<CommentResponseDto> commentResponseDtos = postDto.getId() != null ?
                        postRepository.findById(postDto.getId())
                                .map(Post::getComments)
                                .orElse(List.of())
                                .stream()
                                .map(CommentResponseDto::of)
                                .toList()
                        : List.of();

                postDto.setComments(commentResponseDtos);
                return postDto;
            });
            return madMoviePageWithComments;

        } else if (boardType == BoardType.FREE) { // 📢 자유 게시판 로직 추가

            // --- 📝 자유 게시판 로직 ---

            // 3-1. 자유 게시판 전용 Repository 메서드 호출
            // PostListResponseDto는 일반적인 게시판 목록 응답 DTO라고 가정합니다.
            Page<PostListResponseDto> freePage = postRepository.findGeneralListByBoardType(boardType, pageable);

            // 3-2. 자유 게시판은 추가적인 DTO 설정 로직이 필요 없다고 가정합니다.

            return freePage;

        } else {
            // 지원하지 않는 BoardType 처리
            throw new IllegalArgumentException("지원하지 않는 게시판 타입입니다: " + boardType);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    @Transactional // ⭐ 필수: DB 변경(조회수 증가)을 위해 트랜잭션 적용
    public <T> T getDiscussionPost(BoardType boardType, Long postId) { // 메서드 이름을 일반화하여 getPostDetail로 변경했습니다.

        // 1. 게시글 조회 (없으면 예외 발생)
        Post post = postRepository.findByBoardTypeAndId(boardType, postId)
                .orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));

        // ⭐ 2. [핵심] 조회수 증가 로직
        // BoardType과 무관하게 모든 상세 조회에 대해 viewCount를 1 증가시킵니다.
        postRepository.incrementViewCount(postId);


        // 3. DTO 변환 및 반환
        if (boardType == BoardType.MAD) {
            // --- 🎬 매드무비 게시판 로직 ---
            MadMovieResponseDto madMovieResponseDto = post.toMadMovieResponseDto();

            List<CommentResponseDto> commentResponseDtos = post.getComments().stream()
                    .map(CommentResponseDto::of)
                    .toList();

            madMovieResponseDto.setComments(commentResponseDtos);

            return (T) madMovieResponseDto;

        } else if (boardType == BoardType.FREE) {
            // --- 📝 자유 게시판 로직 ---
            // FREE 타입일 경우 GeneralPostResponseDto 반환
            // toGeneralPostResponseDto() 메서드는 Post 엔티티에 정의되어 있어야 합니다.
            GeneralPostResponseDto responseDto = post.toGeneralPostResponseDto();

            List<CommentResponseDto> commentResponseDtos = post.getComments().stream()
                    .map(CommentResponseDto::of)
                    .toList();

            responseDto.setComments(commentResponseDtos);

            return (T) responseDto;

        } else if (boardType == BoardType.DISCUSSION) {
            // --- 🏆 토론 게시판 로직 ---
            // DISCUSSION 타입일 경우 DiscussionResponseDto 반환
            DiscussionResponseDto responseDto = post.toResponseDto();

            List<CommentResponseDto> commentResponseDtos = post.getComments().stream()
                    .map(CommentResponseDto::of)
                    .toList();

            responseDto.setComments(commentResponseDtos);

            return (T) responseDto;
        } else {
            // 지원하지 않는 BoardType 처리
            throw new IllegalArgumentException("지원하지 않는 게시판 타입입니다: " + boardType);
        }
        // 트랜잭션 종료 시, 변경된 post 엔티티의 viewCount가 DB에 반영됩니다.
    }

    @Override
    public Page<DiscussionListResponseDto> getDiscussionPostVoteDES(BoardType boardType, Pageable pageable) {

        Page<DiscussionListResponseDto> discussionPage = postRepository.findByBoardTypeOrderByTotalVotesDesc(boardType, pageable);

        discussionPage.getContent().forEach(dto -> {
            // DiscussionListResponseDto의 setKoreanNames() 메서드 사용 (혹은 직접 Setter 호출)
            if (dto.getChampion1() != null) {
                dto.setKoreanName1(dto.getChampion1().getKoreanName());
            }
            if (dto.getChampion2() != null) {
                dto.setKoreanName2(dto.getChampion2().getKoreanName());
            }
        });
        return discussionPage;
    }

    @Override
    public Page<MyPostResponse> getMyPostResponse(Long userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findByUserId(userId, pageable);
        return postPage.map(MyPostResponse::of);
    }

    @Override
    public List<MadMovieListResponseDto> getMonthlyPopularMadmoviePosts(GameCategory gameCategory) {
        Pageable top3 = PageRequest.of(0, 3);
        // 1. 이번 달의 첫 날을 계산 (로직 동일)
        LocalDateTime startOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate().atStartOfDay();

        // 2. DTO를 반환하는 리포지토리 메서드 호출
        return postRepository.findTopMonthlyPopularMadmovieDtos(gameCategory,BoardType.MAD, startOfMonth,top3);
    }

    public List<PostFiveResponseDto> getPostDesList(BoardType boardType) {
        return postRepository.findTop5ByBoardTypeOrderByCreatedAtDesc(boardType, PageRequest.of(0, 5));
    }


    @Transactional
    public void incrementViewCount(Long postId, String userIdentifier, long timeLimitMinutes) {
        // 1. 중복 확인을 위한 시간 제한 설정
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(timeLimitMinutes);

        // 2. ViewLog에서 최근 기록이 있는지 확인
        boolean hasRecentView = viewLogRepository.existsByPostIdAndUserIdentifierAndRecordedAtAfter(
                postId,
                userIdentifier,
                timeLimit
        );


        if (!hasRecentView) {
            // 3. 중복이 아닌 경우:

            // Post 엔티티를 로드
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

            // a. Post 엔티티의 조회수 증가 (Post 엔티티에 메서드를 추가해야 함)
            postRepository.incrementViewCount(postId);

            // b. ViewLog 기록 추가
            ViewLog newLog = ViewLog.create(post, userIdentifier);
            viewLogRepository.save(newLog);

            // postRepository.save(post); // @Transactional 덕분에 자동 반영되지만 명시적으로 호출 가능
        }
        // 중복인 경우 (hasRecentView == true)는 아무 작업도 하지 않고 종료됩니다.
    }



}