package com.example.oops.api.like;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.vote.domain.VoteId;
import com.example.oops.api.vote.domain.enums.VoteType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_like")
@IdClass(VoteId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}

