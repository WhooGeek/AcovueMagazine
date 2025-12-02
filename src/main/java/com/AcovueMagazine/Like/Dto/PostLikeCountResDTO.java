package com.AcovueMagazine.Like.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeCountResDTO {

    private Long postSeq;
    private Long postLikeCount;

    public static PostLikeCountResDTO from(Long postSeq, Long magazineLikeCount) {
        return new PostLikeCountResDTO(postSeq, magazineLikeCount);
    }
}
