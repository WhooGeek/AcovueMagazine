package com.AcovueMagazine.Member.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "members")
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_nickname", nullable = false)
    private String memberNickname;

    @Column(name = "member_password", nullable = false)
    private String memberPassword;

    @Column(name = "member_email", nullable = false)
    private String memberEmail;

    @Column(name = "member_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column(name = "member_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @Column(name = "member_login_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberLoginStatus memberLoginStatus;

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
    }

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    // OAuth 로그인 시 유저가 어디에서 로그인하는건지 알기위한 엔티티
    // 일반 로그인은 null, google, naver, kakao
    private String provider;

    // 소셜 로그인 해주는곳에서 제공해주는 고유 아이디
    private String providerId;

    @Builder
    public Members(String memberName, String memberNickname, String memberEmail,
                   String memberPassword, MemberRole memberRole, MemberStatus memberStatus,
                   MemberLoginStatus memberLoginStatus, String provider, String providerId) {
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberRole = memberRole;
        this.memberStatus = memberStatus;
        this.memberLoginStatus = memberLoginStatus;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void inActivate() {
        this.memberStatus = MemberStatus.INACTIVE;
    }

    public void updateNickname(String memberNickname) {
        this.memberNickname = memberNickname;
    }

    public void updatePassword(String encodedPassword) {
        this.memberPassword = encodedPassword;
    }
}
