package com.AcovueMagazine.Magazine.Controller;


import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import com.AcovueMagazine.Magazine.DTO.Magazine;
import com.AcovueMagazine.Magazine.DTO.MagazineReqDTO;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Service.MagazineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseUtil.successResponse("데이터를 성공적으로 조회하였습니다.", magazines).getBody();
    }


}
