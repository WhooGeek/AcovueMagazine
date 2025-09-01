package com.AcovueMagazine.User.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Seq", nullable = false)
    private Long userSeq;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_nickname", nullable = false)
    private String userNickname;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_roll", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoll userRoll;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;



}
