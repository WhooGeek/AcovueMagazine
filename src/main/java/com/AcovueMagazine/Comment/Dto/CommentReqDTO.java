package com.AcovueMagazine.Comment.Dto;

import com.AcovueMagazine.Comment.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReqDTO {

    private Long userSeq;
    private Long commentSeq;
    private String commentContent;
    private Long magazineSeq;
    private Long parentSeq;

    public static CommentResDTO fromEntity(Comment comment) {
        CommentResDTO commentDTO = new CommentResDTO();
        commentDTO.setCommentSeq(comment.getCommentSeq());
        commentDTO.setCommentContent(comment.getCommentContent());
        commentDTO.setParentSeq(comment.getParent() != null ? comment.getParent().getCommentSeq() : null);
        commentDTO.setChildren(new ArrayList<>());
        return commentDTO;
    }

    public void updateTitle(String commentContent) {
        this.commentContent = commentContent;
    }
}
