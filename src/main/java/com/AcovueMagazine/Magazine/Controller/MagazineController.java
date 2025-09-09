package com.AcovueMagazine.Magazine.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Magazine.DTO.MagazineReqDTO;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Service.MagazineService;
import com.AcovueMagazine.User.Entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/magazine")
@RequiredArgsConstructor
public class MagazineController {

    private final MagazineService magazineService;

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
    public ApiResponse<?> searchMagazine(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "true") boolean newestFirst
    ){
        List<MagazineResDTO> searchResults = magazineService.searchMagzine(keyword, start, end, newestFirst);
        return ResponseUtil.successResponse("매거진 검색을 성공적으로 수행하였습니다.", searchResults).getBody();
    }

    /**
     * Retrieves all magazines and wraps them in a success ApiResponse.
     *
     * @return an ApiResponse containing a List of MagazineResDTO on success
     */
    @GetMapping("/find/all")
    public ApiResponse<?> getMagazineList() {
        List<MagazineResDTO> magazines = magazineService.getAllMagazines();
        return ResponseUtil.successResponse("매거진 전체 조회를 성공적으로 수행하였습니다.", magazines).getBody();
    }

    // 매거진 상세조회
    @GetMapping("/find/{magazineId}")
    public ApiResponse<?> getMagazineById(@PathVariable Long magazineId) {
        MagazineResDTO magazine = magazineService.getMagazine(magazineId);
        return ResponseUtil.successResponse("매거진 상세조회를 성공적으로 수행하였습니다.", magazine).getBody();
    }

    // 매거진 등록
    @PostMapping("/create")
    public ApiResponse<?> createMagazine(@RequestBody MagazineReqDTO magazineReqDTO) {
        MagazineResDTO magazine = magazineService.createMagazine(magazineReqDTO);
        return ResponseUtil.successResponse("매거진 생성을 성공적으로 수행하였습니다.", magazine).getBody();
    }

    // 매거진 수정
    @PutMapping("/update/{magazineId}")
    public ApiResponse<?> updateMagazine(@PathVariable Long magazineId, @RequestBody MagazineReqDTO magazineReqDTO) {
        MagazineResDTO magazine = magazineService.updateMagazine(magazineReqDTO, magazineId);
        return ResponseUtil.successResponse("매거진 수정이 성공적으로 수행되었습니다.", magazine).getBody();
    }

    // 매거진 삭제
    @DeleteMapping("/delete/{magazineId}")
    public ApiResponse<?> deleteMagazine(@PathVariable Long magazineId, @RequestBody Users users) {
        MagazineResDTO magazine = magazineService.deleteMagazine(magazineId, users);

        return ResponseUtil.successResponse("매거진 삭제를 성공적으로 수행하였습니다", magazine).getBody();
    }


}
