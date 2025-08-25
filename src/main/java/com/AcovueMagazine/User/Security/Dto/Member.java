package com.AcovueMagazine.User.Security.Dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // provider 에는 google이 들어감
    private String provider;

    // providerId 에는 구글 로그인 한 유저의 고유 ID값이 들어감
    private String providerId;

}
