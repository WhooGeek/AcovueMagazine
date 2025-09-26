package com.AcovueMagazine.Member.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "members")
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_seq", nullable = false)
    private Long member_seq;

    @Column(name="oauth_id", nullable = false)
    private String oauth_id;

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

    //provider = Google
    private String provider;

    // providerId = 구글 로그인 한 유저의 고유 ID가 들어감
    private String providerId;


}
