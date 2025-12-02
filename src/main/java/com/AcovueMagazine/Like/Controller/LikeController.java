package com.AcovueMagazine.Like.Controller;

import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Like.Dto.CommentLikeCountResDTO;
import com.AcovueMagazine.Like.Dto.CommentLikeResDTO;
import com.AcovueMagazine.Like.Dto.PostLikeCountResDTO;
import com.AcovueMagazine.Like.Dto.PostLikeResDTO;
import com.AcovueMagazine.Like.Service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    //매거진 좋아요 토글 기능
    @PostMapping("post/{postSeq}/{userSeq}")
    public ApiResponse<?> togglePostLike(@PathVariable Long postSeq, @PathVariable Long userSeq){

        PostLikeResDTO like = likeService.togglePostLike(postSeq, userSeq);

        return ResponseUtil.successResponse("매거진 좋아요를 성공적으로 등록/삭제하였습니다.", like).getBody();
    }

    //댓글 좋아요 토글 기능
    @PostMapping("comment/{commentSeq}/{userSeq}")
    public ApiResponse<?> toggleCommentLike(@PathVariable Long commentSeq, @PathVariable Long userSeq){

        CommentLikeResDTO like = likeService.toggleCommentLike(commentSeq, userSeq);

        return ResponseUtil.successResponse("댓글 좋아요를 성공적으로 등록/삭제하였습니다.", like).getBody();
    }

    // 댓글 조아요 조회 기능
    @GetMapping("comment/{commentSeq}")
    public ApiResponse<?> getCommentLike(@PathVariable Long commentSeq){

        CommentLikeCountResDTO commentLikeCount = likeService.commentLikeCount(commentSeq);

        return ResponseUtil.successResponse("댓글 좋아요를 성공적으로 조회하였습니다.", commentLikeCount).getBody();
    }

    // 매거진 좋아요 조회 기능
    @GetMapping("magazine/{postSeq}")
    public ApiResponse<?> getPostLike(@PathVariable Long postSeq){
        PostLikeCountResDTO postLikeCount = likeService.postLikeCount(postSeq);

        return ResponseUtil.successResponse("매거진 좋아요를 성공적으로 조회하였습니다.", postLikeCount).getBody();
    }


}
