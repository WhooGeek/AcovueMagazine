package com.AcovueMagazine.Magazine.Entity;

import com.AcovueMagazine.User.Entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Magazine")
public class Magazine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_seq", nullable = false)
    private Long magazine_seq;

    @ManyToOne
    @JoinColumn(name = "user_seq", nullable = false)
    private Users user;

    @Column(name = "magazine_title", nullable = false)
    private String magazine_title;

    @Column(name = "magazine_content", nullable = false)
    private String magazine_content;

}
