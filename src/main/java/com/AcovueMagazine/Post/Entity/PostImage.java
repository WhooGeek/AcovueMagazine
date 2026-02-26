package com.AcovueMagazine.Post.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String imageUrl; // S3에서 받은 URL 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_seq")
    private Post post;

    @Builder
    public PostImage(String imageUrl, Post post) {
        this.imageUrl = imageUrl;
        this.post = post;
    }
}
