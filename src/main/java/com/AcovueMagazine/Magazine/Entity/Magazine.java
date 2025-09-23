package com.AcovueMagazine.Magazine.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Magazine")
public class Magazine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_seq", nullable = false)
    private Long magazineSeq;

    @ManyToOne
    @JoinColumn(name = "user_seq", nullable = false)
    private Users user;

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

    public Magazine(Users user, String magazineTitle, String magazineContent) {
        this.user = user;
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
