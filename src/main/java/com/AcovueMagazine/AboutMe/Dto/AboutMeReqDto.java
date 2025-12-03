package com.AcovueMagazine.AboutMe.Dto;

import com.AcovueMagazine.AboutMe.Entity.AboutMe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AboutMeReqDto {

    private Long memberSeq;
    private String about_me_content;

    public static AboutMeReqDto fromEntity(AboutMe aboutMe) {
        return new AboutMeReqDto(
                aboutMe.getMembers().getMember_seq(),
                aboutMe.getAboutMeContent()
        );
    }
}
