package com.AcovueMagazine.AboutMe.Entity;

import com.AcovueMagazine.Member.Entity.Members;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "about_me")
public class AboutMe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "about_me_seq", nullable = false)
    private Long aboutMeSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq", nullable = false)
    private Members members;

    @Lob // Text 타입 지정
    @Column(name = "about_me_content", nullable = false)
    private String aboutMeContent;


    public void updateAboutMeContent(String aboutMeContent) {
        this.aboutMeContent = aboutMeContent;
    }
}
