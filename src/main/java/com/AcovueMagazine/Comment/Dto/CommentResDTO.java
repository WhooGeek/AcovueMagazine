package com.AcovueMagazine.Comment.Dto;

import com.AcovueMagazine.Comment.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResDTO {
    private Long commentSeq;
    private Long userSeq;
    private String userName;
    private String commentContent;
    private List<CommentResDTO> children;

    private Long parentSeq; // 부모 댓글 시퀀스

    // Entity -> DTO
    public static CommentResDTO fromEntity(Comment comment) {
        CommentResDTO commentResDTO = new CommentResDTO();
        commentResDTO.setCommentSeq(comment.getCommentSeq());
        commentResDTO.setCommentContent(comment.getCommentContent());
        commentResDTO.setUserName(comment.getMember().getMemberName());
        commentResDTO.setChildren(new ArrayList<>());
        return commentResDTO;
    }

    // 댓글 - 대댓글 연결용 Setter
    public void setComments(List<CommentResDTO> comments) {
        this.children = comments;
    }
}
