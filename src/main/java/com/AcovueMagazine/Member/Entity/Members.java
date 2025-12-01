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
    private Long member_seq;

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

    //provider = Google
    private String provider;

    // providerId = 구글 로그인 한 유저의 고유 ID가 들어감
    private String providerId;

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
