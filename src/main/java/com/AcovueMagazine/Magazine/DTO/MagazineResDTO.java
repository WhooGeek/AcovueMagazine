package com.AcovueMagazine.Magazine.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagazineResDTO {

    private Long magazine_seq;
    private Long user_id;
    private String magazine_title;
    private String magazine_content;

    // Entity -> DTO
    public static MagazineResDTO fromEntity(Magazine magazine) {
        return new MagazineResDTO(
                magazine.getUser_seq(),
                magazine.getMagazine_seq(),
                magazine.getMagazine_title(),
                magazine.getMagazine_content()
        );
    }
}
