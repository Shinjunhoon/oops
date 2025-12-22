package com.example.oops.api.vote.application;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.api.vote.domain.Vote;
import com.example.oops.api.vote.domain.enums.VoteType;
import com.example.oops.api.vote.repository.VoteRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Transactional
    public String toggleVote(Long userId, Long postId, VoteType voteType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new OopsException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));

        if(post.getBoardType() != BoardType.DISCUSSION && post.getBoardType() != BoardType.MAD) {
            throw new OopsException(ErrorCode.INVALID_VOTE_BOARD);
        }

        Optional<Vote> existingVote = voteRepository.findByUserAndPost(user,post);

        // 투표 취소
        if(existingVote.isPresent()){
            Vote vote = existingVote.get();
            if(vote.getVoteType() == voteType){
                voteRepository.delete(existingVote.get());
                post.decreaseUpVoteCount(voteType);
            }
            // 투표 다른것으로 업데이트
            else{
                post.updateUpVoteCount(vote.getVoteType(),voteType);
                vote.setVoteType(voteType);
            }
        }
        else{
            Vote vote = new Vote(user, post, voteType);
            voteRepository.save(vote);
            post.increaseUpVoteCount(voteType);
        }
        return "성공";
    }
}
