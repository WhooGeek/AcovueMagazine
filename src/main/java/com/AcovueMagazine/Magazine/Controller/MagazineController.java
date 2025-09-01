package com.AcovueMagazine.Magazine.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Magazine.DTO.MagazineReqDTO;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Service.MagazineService;
import com.AcovueMagazine.User.Entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/magazine")
@RequiredArgsConstructor
public class MagazineController {

    private final MagazineService magazineService;

    // 매거진 조회
    @GetMapping("/findAll")
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

    // 매거진 삭제
    @DeleteMapping("/delete/{magazineId}")
    public ApiResponse<?> deleteMagazine(@PathVariable Long magazineId, @RequestBody Users users) {
        MagazineResDTO magazine = magazineService.deleteMagazine(magazineId, users);

        return ResponseUtil.successResponse("매거진 삭제를 성공적으로 수행하였습니다", magazine).getBody();
    }


}
