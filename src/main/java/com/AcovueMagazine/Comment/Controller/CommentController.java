package com.AcovueMagazine.Comment.Controller;

import com.AcovueMagazine.Comment.Dto.CommentReqDTO;
import com.AcovueMagazine.Comment.Dto.CommentResDTO;
import com.AcovueMagazine.Comment.Service.CommentService;
import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 + 대댓글 조회
    @GetMapping("/find/{postId}")
    public ApiResponse<?> getComment(@PathVariable Long postId){

        List<CommentResDTO> comments = commentService.getComment(postId);

        return ResponseUtil.successResponse("댓글을 성공적으로 조회하였습니다.", comments).getBody();
    }

    // 댓글, 대댓글 등록
    @PostMapping("/create/{postId}")
    public ApiResponse<?> createComment(@PathVariable Long postId, @RequestBody CommentReqDTO commentReqDTO){

        CommentResDTO comment = commentService.createComment(postId, commentReqDTO);

        return ResponseUtil.successResponse("댓글을 성공적으로 조회하였습니다.", comment).getBody();
    }

    //댓글, 대댓글 수정
    @PutMapping("/update/{postId}/{commentSeq}")
    public ApiResponse<?> updateComment(@PathVariable Long postId, @PathVariable Long commentSeq, @RequestBody CommentReqDTO commentReqDTO){

        CommentResDTO comment = commentService.updateComment(postId, commentSeq, commentReqDTO);

        return ResponseUtil.successResponse("댓글을 성공적으로 수정하였습니다.",comment).getBody();
    }

    //댓글, 대댓글 삭제
    @DeleteMapping("/delete/{postId}/{commentSeq}")
    public ApiResponse<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentSeq, @RequestBody CommentReqDTO commentReqDTO){

        CommentResDTO comment = commentService.deleteComment(postId, commentSeq, commentReqDTO);

        return ResponseUtil.successResponse("댓글을 성공적으로 삭제하였습니다.",comment).getBody();
    }
}
