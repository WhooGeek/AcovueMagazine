package com.AcovueMagazine.Like.Entity;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Member.Entity.Members;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "magazine_like")
public class MagazineLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_like_seq", nullable = false)
    private Long likeSeq;

    @ManyToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Members members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_seq", nullable = false)
    private Magazine magazine;

    public MagazineLike(Members members, Magazine magazine) {
        this.members = members;
        this.magazine = magazine;
    }

    public static MagazineLike create(Members members, Magazine magazine) {
        return new MagazineLike(members, magazine);
    }
}
