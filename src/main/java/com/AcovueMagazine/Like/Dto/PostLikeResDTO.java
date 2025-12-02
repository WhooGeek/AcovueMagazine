package com.AcovueMagazine.Like.Dto;

import com.AcovueMagazine.Like.Entity.PostLike;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResDTO {

    private Long likeSeq;
    private Long userSeq;
    private Long postSeq;
    private boolean liked;

    public static PostLikeResDTO fromEntity(PostLike like) {
        if (like == null) {
            return new PostLikeResDTO(null, null, null, false);
        }
        return new PostLikeResDTO(
                like.getLikeSeq(),
                like.getMembers().getMember_seq(),
                like.getPost().getPostSeq(),
                true
        );
    }
}

