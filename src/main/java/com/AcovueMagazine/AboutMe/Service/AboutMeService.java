package com.AcovueMagazine.AboutMe.Service;

import com.AcovueMagazine.AboutMe.Dto.AboutMeReqDto;
import com.AcovueMagazine.AboutMe.Dto.AboutMeResDto;
import com.AcovueMagazine.AboutMe.Entity.AboutMe;
import com.AcovueMagazine.AboutMe.Repository.AboutMeRepository;
import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.RestApiException;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AboutMeService {

    private final AboutMeRepository aboutMeRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // About Me 조회 기능
    public AboutMeResDto getAboutMe() {

        AboutMe aboutMe = aboutMeRepository.findByaboutMeSeq(1);

        return AboutMeResDto.fromEntity(aboutMe);
    }

    // About me 수정 기능
    public AboutMeReqDto updateAboutMe(AboutMeReqDto aboutMeReqDto) {

        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RestApiException(ErrorCode.ACCESS_TOKEN_NULL);
        }

        // memberSeq 꺼내기
        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            throw new RestApiException(ErrorCode.INVALID_TOKEN);
        }


        AboutMe aboutMe = aboutMeRepository.findByaboutMeSeq(1);

        // 제목 수정이 있으면 저장
        if (aboutMeReqDto.getAbout_me_content() != null && !aboutMeReqDto.getAbout_me_content().isBlank()) {
            aboutMe.updateAboutMeContent(aboutMeReqDto.getAbout_me_content());
        }

        return AboutMeReqDto.fromEntity(aboutMe);
    }


}
