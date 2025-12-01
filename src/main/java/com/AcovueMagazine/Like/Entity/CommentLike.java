package com.AcovueMagazine.Like.Entity;

import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Member.Entity.Members;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_seq", nullable = false)
    private Long likeSeq;

    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Members members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_seq", nullable = false)
    private Comment comment;

    public CommentLike(Members members, Comment comment) {
        this.members = members;
        this.comment = comment;
    }

    public static CommentLike create(Members members, Comment comment) {
        return new CommentLike(members, comment);
    }
}
