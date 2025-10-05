package com.example.oops.api.vote.domain;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.user.domain.User;

import java.io.Serializable;
import java.util.Objects;

public class VoteId implements Serializable {
    private User user;
    private Post post;

    public VoteId(){}

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        VoteId voteId = (VoteId) o;
        return Objects.equals(user, voteId.user) && Objects.equals(post, voteId.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post);
    }
}
