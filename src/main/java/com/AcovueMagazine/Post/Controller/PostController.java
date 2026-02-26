package com.AcovueMagazine.Post.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Post.Dto.PostReqDto;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Entity.PostType;
import com.AcovueMagazine.Post.Service.PostService;
import com.AcovueMagazine.Member.Entity.Members;
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
            @RequestParam(required = false, defaultValue = "10") Integer limit, // 페이지당 개수 (기본값은 10)
            @RequestParam(required = false, defaultValue = "1") Integer page, // 페이지 번호 (기본값은 1)
            @RequestParam(required = false) String type // 게시물 타입
    ) {
        // Category -> Enum
        PostType postType = (type != null) ? PostType.valueOf(type.toUpperCase()) : null;

        List<PostResDto> posts = postService.getAllPosts(limit, page, postType);

        return ResponseUtil.successResponse("매거진 전체 조회를 성공적으로 수행하였습니다.", posts).getBody();
    }

    // 매거진 상세조회
    @GetMapping("/find/{magazineId}")
    public ApiResponse<?> getMagazineById(@PathVariable Long magazineId) {
        PostResDto magazine = postService.getMagazine(magazineId);
        return ResponseUtil.successResponse("매거진 상세조회를 성공적으로 수행하였습니다.", magazine).getBody();
    }

    // 매거진 등록
    @PostMapping("/create")
    public ApiResponse<?> createMagazine(@RequestBody PostReqDto magazineReqDTO) {
        PostResDto magazine = postService.createMagazine(magazineReqDTO);
        return ResponseUtil.successResponse("매거진 생성을 성공적으로 수행하였습니다.", magazine).getBody();
    }

    // 매거진 수정
    @PutMapping("/update/{postId}")
    public ApiResponse<?> updateMagazine(@PathVariable Long postId, @RequestBody PostReqDto PostReqDTO) {
        PostResDto magazine = postService.updateMagazine(PostReqDTO, postId);
        return ResponseUtil.successResponse("매거진 수정이 성공적으로 수행되었습니다.", magazine).getBody();
    }

    // 매거진 삭제
    @DeleteMapping("/delete/{postId}")
    public ApiResponse<?> deleteMagazine(@PathVariable Long postId, @RequestBody Members members) {
        PostResDto magazine = postService.deleteMagazine(postId, members);

        return ResponseUtil.successResponse("매거진 삭제를 성공적으로 수행하였습니다", magazine).getBody();
    }

    // S3 사진 업로드
    @PostMapping("/image")
    public ApiResponse<?> uploadImage(@RequestParam("image")MultipartFile image) {
        try{
            // S3 업로드 > URL 리턴
            String imageUrl = s3UploadService.saveFile(image);

            Map<String, String> data = new HashMap<>();
            data.put("imageUrl", imageUrl);

            return ResponseUtil.successResponse("S3 이미지 업로드를 성공적으로 수행하였습니다.", data).getBody();
        }catch (Exception e){
            throw new RuntimeException("S3 이미지 업로드 도중 오류가 발생하였습니다.: " + e.getMessage());
        }

    }


}
