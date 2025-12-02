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
@Table(name = "Magazine")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_seq", nullable = false)
    private Long magazineSeq;

    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Members members;

    @Column(name = "magazine_title", nullable = false)
    private String magazineTitle;

    @Column(name = "magazine_content", nullable = false)
    private String magazineContent;

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @PrePersist
    public void prePersist(){
        this.regDate = LocalDateTime.now();
    }

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    @Column(name = "magazine_category")
    private PostType magazineCategory;

    public Post(Members members, String magazineTitle, String magazineContent) {
        this.members = members;
        this.magazineTitle = magazineTitle;
        this.magazineContent = magazineContent;
    }

    public void updateTitle(String title) {
        this.magazineTitle = title;
    }

    public void updateContent(String content) {
        this.magazineContent = content;
    }



}
