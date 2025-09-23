package com.AcovueMagazine.Like.Dto;

import com.AcovueMagazine.Like.Entity.CommentLike;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeResDTO {

    private Long likeSeq;
    private Long userSeq;
    private Long commentSeq;
    private boolean liked;


    public static CommentLikeResDTO fromEntity(CommentLike like) {
        if (like == null) {
            return new CommentLikeResDTO(null, null, null, false);
        }
        return new CommentLikeResDTO(
                like.getLikeSeq(),
                like.getMembers().getMember_seq(),
                like.getComment().getCommentSeq(),
                true
        );
    }
}

