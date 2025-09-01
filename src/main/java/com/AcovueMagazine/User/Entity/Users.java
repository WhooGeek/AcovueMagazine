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
    private Long user_Seq;

    @Column(name = "user_id", nullable = false)
    private String user_id;

    @Column(name = "user_name", nullable = false)
    private String user_name;

    @Column(name = "user_nickname", nullable = false)
    private String user_nickname;

    @Column(name = "user_email", nullable = false)
    private String user_email;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus user_status;



}
