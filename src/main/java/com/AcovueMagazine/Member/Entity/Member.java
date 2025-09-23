package com.AcovueMagazine.Member.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="oauth_id", nullable = false)
    private Long oauth_id;

    @Column(name = "oauth_id", nullable = false)
    private String userId;

    @Column(name = "member_name", nullable = false)
    private String userName;

    @Column(name = "member_nickname", nullable = false)
    private String userNickname;

    @Column(name = "member_email", nullable = false)
    private String userEmail;

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
