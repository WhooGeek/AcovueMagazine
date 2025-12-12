package com.AcovueMagazine.Post.Entity;

import com.AcovueMagazine.Member.Entity.Members;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_seq", nullable = false)
    private Long postSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq", nullable = false)
    private Members members;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Lob // Text 타입 지정
    @Column(name = "post_content", nullable = false)
    private String postContent;

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @PrePersist
    public void prePersist(){
        this.regDate = LocalDateTime.now();
    }

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_category")
    private PostType postCategory;

    public Post(Members members, String postTitle, String postContent, PostType postCategory) {
        this.members = members;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postCategory = postCategory;
    }

    public void updateTitle(String title) {
        this.postTitle = title;
    }

    public void updateContent(String content) {
        this.postContent = content;
    }



}
