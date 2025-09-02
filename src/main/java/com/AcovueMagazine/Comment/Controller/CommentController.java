package com.AcovueMagazine.Comment.Controller;

import com.AcovueMagazine.Comment.DTO.CommentResDTO;
import com.AcovueMagazine.Comment.Service.CommentService;
import com.AcovueMagazine.Common.Response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return ApiResponse.ofSuccess("댓글을 성공적으로 조회하였습니다.", comments);
    }
}
