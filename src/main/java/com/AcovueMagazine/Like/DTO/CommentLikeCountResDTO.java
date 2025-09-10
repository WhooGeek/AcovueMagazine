package com.AcovueMagazine.Like.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentLikeCountResDTO {

    private Long commentSeq;
    private Long likeCount;

    public static CommentLikeCountResDTO from(Long commentSeq,Long likeCount){
        return new CommentLikeCountResDTO(commentSeq,likeCount);
    }
}
