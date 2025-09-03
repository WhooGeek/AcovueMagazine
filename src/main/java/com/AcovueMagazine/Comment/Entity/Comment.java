package com.AcovueMagazine.Comment.Entity;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.User.Entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name="comment_seq")
    private Long commentSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false)
    private Users user;

    @Column(name="comment_content")
    private String commentContent;

    @Enumerated(EnumType.STRING)
    @Column(name="comment_status", nullable = false)
    private CommentStatus commentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_seq")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "magazine_seq", nullable = false)
    private Magazine magazine;

    public Comment(Users user, Magazine magazine, String commentContent, Comment parent) {
        this.user = user;
        this.magazine = magazine;
        this.commentContent = commentContent;
        this.parent = parent;
        this.commentStatus = CommentStatus.ACTIVE; // 기본값 설정
    }

    public void updateContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
