package com.example.oops.api.post.application;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.dto.PostRequestDto;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Post savePost(Long userId, PostRequestDto postRequestDto) {

        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("유저가 없습니다."));

        Post post = new Post();
        post.setBoardType(postRequestDto.getBoardType());
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setUser(user);
        return postRepository.save(post);
    }
}
