package com.AcovueMagazine.Like.Entity;

import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Member.Entity.Members;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_seq", nullable = false)
    private Long likeSeq;

    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Members members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_seq", nullable = false)
    private Post post;

    public PostLike(Members members, Post post) {
        this.members = members;
        this.post = post;
    }

    public static PostLike create(Members members, Post post) {
        return new PostLike(members, post);
    }
}
