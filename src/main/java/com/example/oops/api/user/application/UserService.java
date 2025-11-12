package com.example.oops.api.user.application;

import com.example.oops.api.comment.Comment;
import com.example.oops.api.comment.CommentRepository;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.dto.UserIdCheckRequestDto;
import com.example.oops.api.user.dto.UserInfoDto;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.api.vote.domain.Vote;
import com.example.oops.api.vote.repository.VoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements com.example.oops.api.user.application.impl.UserService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @Override
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return new UserInfoDto(Objects.requireNonNull(user).getUserInfo(),userId);
    }

    @Override
    public boolean checkUserId(UserIdCheckRequestDto userIdCheckRequestDto) {
        return !userRepository.existsByLoginInfo_Username(userIdCheckRequestDto.getUserName());

    }

    @Transactional
    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        List<Vote> votes = voteRepository.findByUser(user);

        List<Comment> comments = commentRepository.findByUser(user);

        // 2️⃣ 각 투표에 대해 Post의 투표 수 감소
        for (Vote vote : votes) {
            Post post = vote.getPost();
            post.decreaseUpVoteCount(vote.getVoteType());
            postRepository.save(post);
        }
        commentRepository.deleteAll(comments);
        voteRepository.deleteAll(votes);
        userRepository.delete(Objects.requireNonNull(user));
        return "User deleted";
    }
}
