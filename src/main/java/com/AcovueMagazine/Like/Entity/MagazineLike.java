package com.AcovueMagazine.Like.Entity;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MagazineLike")
public class MagazineLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_seq", nullable = false)
    private Long likeSeq;

    @ManyToOne
    @JoinColumn(name = "user_seq", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_seq", nullable = false)
    private Magazine magazine;

    public MagazineLike(Users user, Magazine magazine) {
        this.user = user;
        this.magazine = magazine;
    }

    public static MagazineLike create(Users user, Magazine magazine) {
        return new MagazineLike(user, magazine);
    }
}
