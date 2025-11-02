package com.example.oops.api.post.application.discussionimpl;


import com.example.oops.api.post.domain.Post;
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
                .build();
         postRepository.save(post);
        return post.getId();
    }
}
