package com.AcovueMagazine.Like.Entity;

import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.User.Entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CommentLike")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_seq", nullable = false)
    private Long likeSeq;

    @ManyToOne
    @JoinColumn(name = "user_seq", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_seq", nullable = false)
    private Comment comment;

    public CommentLike(Users user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public static CommentLike create(Users user, Comment comment) {
        return new CommentLike(user, comment);
    }
}
