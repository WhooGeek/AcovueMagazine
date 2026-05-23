package com.AcovueMagazine.Comment.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCountResDTO {

    private Long postSeq;
    private Long commentCount;

    public static CommentCountResDTO from(Long postSeq, Long commentCount) {
        return new CommentCountResDTO(postSeq, commentCount);
    }
}
