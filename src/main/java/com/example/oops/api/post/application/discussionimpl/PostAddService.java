package com.example.oops.api.post.application.discussionimpl;


import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.dtos.FreePostRequestDto;
import com.example.oops.api.post.dtos.MadMovieRequestDto;
import com.example.oops.api.post.dtos.discussionDto.DiscussionRequestDto;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.s3.S3FileService;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class PostAddService implements com.example.oops.api.post.application.PostAddService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final S3FileService s3FileService;
    


    @Transactional
    public Long savePost(Long userId, DiscussionRequestDto discussionRequestDto, MultipartFile multipartFile) throws IOException {

        String ImageUrl = s3FileService.uploadVideoWithTransferManager(multipartFile);

        User user = userRepository.findById(userId).orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));

        boolean isAdmin = user.getLoginInfo().getRoles()
                .stream()
                .anyMatch(role -> role.equals("ROLE_ADMIN"));



        Post post = Post.builder()
                .title(discussionRequestDto.getTitle())
                .user(user)
                .line1(discussionRequestDto.getLine1())
                .line2(discussionRequestDto.getLine2())
                .argument1(discussionRequestDto.getArgument1())
                .argument2(discussionRequestDto.getArgument2())
                .boardType(discussionRequestDto.getBoardType())
                .imageUrl(ImageUrl)
                .champion1(discussionRequestDto.getChampion1())
                .champion2(discussionRequestDto.getChampion2())
                .title(discussionRequestDto.getTitle())
                .tier(discussionRequestDto.getTier())
                .isNotice(isAdmin)
                .build();
         postRepository.save(post);
        return post.getId();
    }
    @Transactional
    public Long saveMadMovePost(Long userId, MadMovieRequestDto madMovieRequestDto, MultipartFile multipartFile) throws IOException {

        String videoUrlToSave;



        // 1. 💡 파일 유무에 따라 분기 처리
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // A. 파일 업로드 방식: S3에 업로드하고 반환된 URL을 사용
            videoUrlToSave = s3FileService.uploadVideoWithTransferManager(multipartFile);

        } else if (madMovieRequestDto.getVideoUrl() != null && !madMovieRequestDto.getVideoUrl().trim().isEmpty()) {
            // B. URL 입력 방식: DTO의 videoUrl을 그대로 사용
            videoUrlToSave = madMovieRequestDto.getVideoUrl().trim();

        } else {
            // C. 둘 다 없는 경우: 필수 정보 누락 처리 (클라이언트에서 이미 검증했더라도 서버에서 재검증)
            throw new OopsException(ErrorCode.REQUIRED_VIDEO_SOURCE_MISSING);
            // *ErrorCode는 프로젝트에 맞게 정의
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));

        boolean isAdmin = user.getLoginInfo().getRoles()
                .stream()
                .anyMatch(role -> role.equals("ROLE_ADMIN"));


        // 2. 결정된 URL을 Post 엔티티에 저장
        Post post = Post.builder()
                .title(madMovieRequestDto.getTitle())
                .user(user)
                .content(madMovieRequestDto.getContent())
                .boardType(madMovieRequestDto.getBoardType())
                .imageUrl(videoUrlToSave) // videoUrlToSave 변수 사용
                .isNotice(isAdmin)
                .build();

        postRepository.save(post);
        return post.getId();
    }
    @Transactional
    public Long saveFreePost(Long userId, FreePostRequestDto madMovieRequestDto, MultipartFile multipartFile) throws IOException {

        String videoUrlToSave;

        // 1. 💡 파일 유무에 따라 분기 처리
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // A. 파일 업로드 방식: S3에 업로드하고 반환된 URL을 사용
            videoUrlToSave = s3FileService.uploadVideoWithTransferManager(multipartFile);

        } else if (madMovieRequestDto.getVideoUrl() != null && !madMovieRequestDto.getVideoUrl().trim().isEmpty()) {
            // B. URL 입력 방식: DTO의 videoUrl을 그대로 사용
            videoUrlToSave = madMovieRequestDto.getVideoUrl().trim();

        } else {
            // C. 둘 다 없는 경우: 필수 정보 누락 처리 (클라이언트에서 이미 검증했더라도 서버에서 재검증)
            throw new OopsException(ErrorCode.REQUIRED_VIDEO_SOURCE_MISSING);
            // *ErrorCode는 프로젝트에 맞게 정의
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));
        boolean isAdmin = user.getLoginInfo().getRoles()
                .stream()
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        // 2. 결정된 URL을 Post 엔티티에 저장
        Post post = Post.builder()
                .title(madMovieRequestDto.getTitle())
                .content(madMovieRequestDto.getContent())
                .user(user)
                .boardType(madMovieRequestDto.getBoardType())
                .imageUrl(videoUrlToSave) // videoUrlToSave 변수 사용
                .isNotice(isAdmin)
                .build();

        postRepository.save(post);
        return post.getId();
    }
}
