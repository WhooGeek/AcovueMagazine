package com.AcovueMagazine.Like.DTO;

import com.AcovueMagazine.Like.Entity.MagazineLike;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagazineLikeResDTO {

    private Long likeSeq;
    private Long userSeq;
    private Long magazineSeq;
    private boolean liked;

    public static MagazineLikeResDTO fromEntity(MagazineLike like) {
        if (like == null) {
            return new MagazineLikeResDTO(null, null, null, false);
        }
        return new MagazineLikeResDTO(
                like.getLikeSeq(),
                like.getUser().getUserSeq(),
                like.getMagazine().getMagazineSeq(),
                true
        );
    }
}

