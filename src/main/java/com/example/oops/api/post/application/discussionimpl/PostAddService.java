package com.example.oops.api.post.application.discussionimpl;


import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.dtos.discussionDto.DiscussionRequestDto;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostAddService implements com.example.oops.api.post.application.PostAddService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long savePost(Long userId, DiscussionRequestDto discussionRequestDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));

        Post post = Post.builder()
                .title(discussionRequestDto.getTitle())
                .content(discussionRequestDto.getContent())
                .user(user)
                .line1(discussionRequestDto.getLine1())
                .line2(discussionRequestDto.getLine2())
                .argument1(discussionRequestDto.getArgument1())
                .argument2(discussionRequestDto.getArgument2())
                .boardType(discussionRequestDto.getBoardType())
                .build();
         postRepository.save(post);
        return post.getId();
    }
}
