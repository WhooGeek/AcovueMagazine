package com.AcovueMagazine.Like.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MagazineLikeCountResDTO {

    private Long MagazineSeq;
    private Long MagazineLikeCount;

    public static MagazineLikeCountResDTO from(Long magazineSeq, Long magazineLikeCount) {
        return new MagazineLikeCountResDTO(magazineSeq, magazineLikeCount);
    }
}
