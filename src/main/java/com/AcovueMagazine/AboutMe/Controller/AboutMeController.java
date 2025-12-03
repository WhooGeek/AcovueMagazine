package com.AcovueMagazine.AboutMe.Controller;

import com.AcovueMagazine.AboutMe.Dto.AboutMeReqDto;
import com.AcovueMagazine.AboutMe.Service.AboutMeService;
import com.AcovueMagazine.Common.Response.ApiResponse;
import com.AcovueMagazine.Common.Response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aboutMe/")
@RequiredArgsConstructor
public class AboutMeController {

    private final AboutMeService aboutMeService;

    @PutMapping("/update")
    public ApiResponse<?> updateAboutMe(@RequestBody AboutMeReqDto aboutMeReqDto){

        AboutMeReqDto aboutMe = aboutMeService.updateAboutMe(aboutMeReqDto);

        return ResponseUtil.successResponse("About Me 수정이 완료되었습니다.", aboutMe).getBody();

    }

}
