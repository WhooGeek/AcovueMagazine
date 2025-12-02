package com.AcovueMagazine.Post.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Post.Dto.PostReqDto;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Entity.PostType;
import com.AcovueMagazine.Post.Service.PostService;
import com.AcovueMagazine.Member.Entity.Members;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Searches magazines matching the optional criteria and returns the results wrapped in an ApiResponse.
     *
     * <p>Searches by an optional text keyword and an optional date-time range (start/end). Date-time
     * parameters are parsed using ISO-8601 date-time format. Results are ordered by creation date;
     * set {@code newestFirst=false} to return oldest-first.</p>
     *
     * @param keyword     optional text to match against magazine fields (title/content/etc.)
     * @param start       optional inclusive start of the date-time range (ISO-8601)
     * @param end         optional inclusive end of the date-time range (ISO-8601)
     * @param newestFirst if true (default) sort results newest-first; if false sort oldest-first
     * @return an ApiResponse whose data is a List of MagazineResDTO matching the search criteria
     */
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
    @PutMapping("/update/{magazineId}")
    public ApiResponse<?> updateMagazine(@PathVariable Long magazineId, @RequestBody PostReqDto magazineReqDTO) {
        PostResDto magazine = postService.updateMagazine(magazineReqDTO, magazineId);
        return ResponseUtil.successResponse("매거진 수정이 성공적으로 수행되었습니다.", magazine).getBody();
    }

    // 매거진 삭제
    @DeleteMapping("/delete/{magazineId}")
    public ApiResponse<?> deleteMagazine(@PathVariable Long magazineId, @RequestBody Members members) {
        PostResDto magazine = postService.deleteMagazine(magazineId, members);

        return ResponseUtil.successResponse("매거진 삭제를 성공적으로 수행하였습니다", magazine).getBody();
    }


}
