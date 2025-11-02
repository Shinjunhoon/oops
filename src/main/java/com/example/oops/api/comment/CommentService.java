package com.example.oops.api.comment;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.application.UserService;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.common.error.OopsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.example.oops.common.error.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public String createComment(CommentCreateRequestDto commentCreateRequestDto,Long userId) {
        Post post = postRepository.findById(commentCreateRequestDto.getPostId()).orElse(null);


        User user = userRepository.findById(userId).orElse(null);

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .userName(Objects.requireNonNull(user).getUserInfo().getNickname())
                .comment(commentCreateRequestDto.getContent())
                .line(user.getUserInfo().getLine())
                .build();

        commentRepository.save(comment);

        return "성공";
    }
    @Transactional
    public String updateComment(CommentUpdateRequestDto commentUpdateRequestDto,Long userId) {

        Comment comment = commentRepository.findById(commentUpdateRequestDto.getCommentId()).orElse(null);

        if(!Objects.requireNonNull(comment).getUser().getId().equals(userId)) {
            throw new OopsException(USER_NOT_FOUND);
        }

        comment.updateContent(commentUpdateRequestDto.getContent());

        return "성공";
    }

    @Transactional
    public boolean deleteComment(CommentDeleteRequestDto commentDeleteRequestDto,Long userId) {

        Comment comment = commentRepository.findById(commentDeleteRequestDto.getCommentId()).orElse(null);

        if(!Objects.requireNonNull(comment).getUser().getId().equals(userId)) {
            throw new OopsException(USER_NOT_FOUND);
        }

        commentRepository.delete(comment);

        return true;
    }
}
