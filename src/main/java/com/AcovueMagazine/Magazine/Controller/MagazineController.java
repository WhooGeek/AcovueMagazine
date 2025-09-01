package com.AcovueMagazine.Magazine.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Magazine.DTO.Magazine;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Service.MagazineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/magazine")
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


}
