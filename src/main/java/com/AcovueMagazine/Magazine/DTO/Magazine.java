package com.AcovueMagazine.Magazine.DTO;

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
    private Long magazine_seq;

    @JoinColumn(name = "user_seq", nullable = false)
    private Long user_seq;

    @Column(name = "magazine_title", nullable = false)
    private String magazine_title;

    @Column(name = "magazine_content", nullable = false)
    private String magazine_content;
}
