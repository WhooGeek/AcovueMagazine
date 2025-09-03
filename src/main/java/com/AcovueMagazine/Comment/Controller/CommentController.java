package com.AcovueMagazine.Comment.Controller;

import com.AcovueMagazine.Comment.DTO.CommentReqDTO;
import com.AcovueMagazine.Comment.DTO.CommentResDTO;
import com.AcovueMagazine.Comment.Service.CommentService;
import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/magazine/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 + 대댓글 조회
    @GetMapping("/find/{magazineId}")
    public ApiResponse<?> getComment(@PathVariable Long magazineId){

        List<CommentResDTO> comments = commentService.getComment(magazineId);

        return ResponseUtil.successResponse("댓글을 성공적으로 조회하였습니다.", comments).getBody();
    }

    // 댓글, 대댓글 등록
    @PostMapping("/create/{magazineId}")
    public ApiResponse<?> createComment(@PathVariable Long magazineId, @RequestBody CommentReqDTO commentReqDTO){

        CommentResDTO comment = commentService.createComment(magazineId, commentReqDTO);

        return ResponseUtil.successResponse("댓글을 성공적으로 조회하였습니다.", comment).getBody();
    }

    //댓글, 대댓글 수정
    @PutMapping("/update/{magazineId}/{commentSeq}")
    public ApiResponse<?> updateComment(@PathVariable Long magazineId, @PathVariable Long commentSeq, @RequestBody CommentReqDTO commentReqDTO){

        CommentResDTO comment = commentService.updateComment(magazineId, commentSeq, commentReqDTO);

        return ResponseUtil.successResponse("댓글을 성공적으로 수정하였습니다.",comment).getBody();
    }

    //댓글, 대댓글 삭제
    @DeleteMapping("/delete/{magazineId}/{commentSeq}")
    public ApiResponse<?> deleteComment(@PathVariable Long magazineId, @PathVariable Long commentSeq, @RequestBody CommentReqDTO commentReqDTO){

        CommentResDTO comment = commentService.deleteComment(magazineId, commentSeq, commentReqDTO);

        return ResponseUtil.successResponse("댓글을 성공적으로 삭제하였습니다.",comment).getBody();
    }
}
