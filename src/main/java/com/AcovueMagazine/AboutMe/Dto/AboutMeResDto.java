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
public class AboutMeResDto {

    private String about_me_content;

    public static AboutMeResDto fromEntity(AboutMe aboutMe) {
        return new AboutMeResDto(
                aboutMe.getAboutMeContent()
        );
    }
}
