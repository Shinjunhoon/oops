package com.example.oops.api.like;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;


    @Transactional
    public String toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("게시글 없음"));


        Optional<Like> likeOpt = likeRepository.findByUserAndPost(user, post);

        if (likeOpt.isPresent()) {
            likeRepository.delete(likeOpt.get());
            post.downLike();
        } else {
            likeRepository.save(new Like(user, post));
            post.uPLike();
        }

        return "요청 성공";
    }
}
