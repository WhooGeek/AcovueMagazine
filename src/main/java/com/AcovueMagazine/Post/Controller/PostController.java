package com.AcovueMagazine.Post.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Post.Dto.PostReqDto;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Entity.CommunityCategory;
import com.AcovueMagazine.Post.Entity.PostType;
import com.AcovueMagazine.Post.Service.PostService;
import com.AcovueMagazine.Post.Service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final S3UploadService s3UploadService;


    @GetMapping("/search")
    public ApiResponse<?> searchPost(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "true") boolean newestFirst
    ){
        List<PostResDto> searchResults = postService.searchPost(keyword, start, end, newestFirst);
        return ResponseUtil.successResponse("매거진 검색을 성공적으로 수행하였습니다.", searchResults).getBody();
    }


    /**
     * Retrieves all magazines and wraps them in a success ApiResponse.
     *
     * @return an ApiResponse containing a List of MagazineResDTO on success
     */
    @GetMapping("/find/all")
    public ApiResponse<?> getPostList(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String communityCategory
    ) {

        PostType postType = (type != null) ? PostType.valueOf(type.toUpperCase()) : null;

        CommunityCategory communityCategoryEnum=
                (communityCategory != null)
                        ? CommunityCategory.valueOf(communityCategory.toUpperCase())
                        : null;

        List<PostResDto> posts = postService.getAllPosts(limit, page, postType, communityCategoryEnum);

        return ResponseUtil.successResponse("매거진 전체 조회를 성공적으로 수행하였습니다.", posts).getBody();
    }

    // 매거진 상세조회
    @GetMapping("/find/{postSeq}")
    public ApiResponse<?> getPostBySeq(@PathVariable Long postSeq) {
        PostResDto magazine = postService.getPost(postSeq);
        return ResponseUtil.successResponse("매거진 상세조회를 성공적으로 수행하였습니다.", magazine).getBody();
    }

    // 매거진 등록
    @PostMapping("/create")
    public ApiResponse<?> createPost(@RequestBody PostReqDto postReqDTO) {
        PostResDto magazine = postService.createPost(postReqDTO);
        return ResponseUtil.successResponse("매거진 생성을 성공적으로 수행하였습니다.", magazine).getBody();
    }

    // 매거진 수정
    @PutMapping("/update/{postSeq}")
    public ApiResponse<?> updatePost(@PathVariable Long postSeq, @RequestBody PostReqDto PostReqDTO) {
        PostResDto magazine = postService.updatePost(PostReqDTO, postSeq);
        return ResponseUtil.successResponse("매거진 수정이 성공적으로 수행되었습니다.", magazine).getBody();
    }

    // 매거진 삭제
    @DeleteMapping("/delete/{postSeq}")
    public ApiResponse<?> deletePost(@PathVariable Long postSeq) {
        PostResDto magazine = postService.deletePost(postSeq);

        return ResponseUtil.successResponse("매거진 삭제를 성공적으로 수행하였습니다", magazine).getBody();
    }

    // S3 사진 업로드
    @PostMapping("/image")
    public ApiResponse<?> uploadImage(@RequestParam("image")MultipartFile image) {
        // S3 업로드 > URL 리턴
        String imageUrl = s3UploadService.saveFile(image);

        Map<String, String> data = new HashMap<>();
        data.put("imageUrl", imageUrl);

        return ResponseUtil.successResponse("S3 이미지 업로드를 성공적으로 수행하였습니다.", data).getBody();

    }


}
