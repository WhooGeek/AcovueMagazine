package com.AcovueMagazine.AboutMe.Controller;

import com.AcovueMagazine.AboutMe.Dto.AboutMeReqDto;
import com.AcovueMagazine.AboutMe.Dto.AboutMeResDto;
import com.AcovueMagazine.AboutMe.Service.AboutMeService;
import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AboutMeController {

    private final AboutMeService aboutMeService;

    // AboutMe 조회 기능
    @GetMapping("/aboutMe")
    public ApiResponse<?> getAboutMe(){

        AboutMeResDto aboutMe = aboutMeService.getAboutMe();

        return ResponseUtil.successResponse("성공적으로 About Me가 조회되었습니다.", aboutMe).getBody();
    }

    // AboutMe Update 기능
    @PutMapping("/aboutMe/update")
    public ApiResponse<?> updateAboutMe(@RequestBody AboutMeReqDto aboutMeReqDto){

        AboutMeReqDto aboutMe = aboutMeService.updateAboutMe(aboutMeReqDto);

        return ResponseUtil.successResponse("About Me 수정이 완료되었습니다.", aboutMe).getBody();
    }


}
